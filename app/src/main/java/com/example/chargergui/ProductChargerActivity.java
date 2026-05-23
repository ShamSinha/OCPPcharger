package com.example.chargergui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.Collections;
import java.util.Locale;

import AuthorizationRelated.AdditionalInfoType;
import AuthorizationRelated.AuthorizationStatusEnumType;
import AuthorizationRelated.IdTokenType;
import ChargingStationRequest.StatusNotificationRequest;
import ChargingStationRequest.TransactionEventRequest;
import Controller_Components.SampledDataCtrlr;
import DataType.EVSEType;
import DataType.SampledValueType;
import DataType.TransactionType;
import DataType.UnitOfMeasureType;
import DisplayMessagesRelated.MessageStateEnumType;
import EnumDataType.ChargingStateEnumType;
import EnumDataType.ConnectorStatusEnumType;
import EnumDataType.IdTokenEnumType;
import EnumDataType.MeasurandEnumType;
import EnumDataType.ReadingContextEnumType;
import EnumDataType.ReasonEnumType;
import Hardware.GpioProcessor;
import Hardware.Permissions;
import Hardware.SerialRfidReader;
import Hardware.Utils;
import TransactionRelated.TransactionEventEnumType;
import TransactionRelated.TriggerReasonEnumType;
import UseCasesOCPP.SendRequestToCSMS;

public class ProductChargerActivity extends Activity {
    private static final String TAG = "ProductCharger";
    private static final int CABLE_PIN = 3;
    private static final int AUTH_RESPONSE_DELAY_MS = 2000;
    private static final int OCPP_CHAIN_DELAY_MS = 750;
    private static final int TARGET_SOC = 80;
    private static final float INITIAL_SOC = 42f;
    private static final float VOLTAGE = 230f;
    private static final float CURRENT = 16f;
    private static final float TARIFF_PER_KWH = 6f;
    private static final int DEFAULT_EV_CONNECTION_TIMEOUT_SECONDS = 60;
    private static final boolean CABLE_PERMANENTLY_ATTACHED = true;
    private static final int UPI_PAYMENT_REQUEST_CODE = 7401;
    private static final String STATION_ID = "CS01";
    private static final String OWNER_UPI_ID = "station.owner@upi";
    private static final String OWNER_UPI_NAME = "Demo EV Charging Station";

    private enum ProductState {
        READY,
        AUTHORIZING,
        WAIT_FOR_PLUG,
        CHARGING,
        STOP_AUTHORIZING,
        SUSPENDED,
        BILLING,
        FAULT
    }

    private ProductState state = ProductState.READY;
    private final Handler handler = new Handler();
    private final SendRequestToCSMS toCSMS = new SendRequestToCSMS();
    private final SerialRfidReader serialRfidReader = new SerialRfidReader();

    private StationSignalController signalController;
    private MyClientEndpoint myClientEndpoint;
    private TextView signalLabel;
    private TextView title;
    private TextView subtitle;
    private TextView detail;
    private TextView socMetric;
    private TextView energyMetric;
    private TextView costMetric;
    private TextView timeMetric;
    private ImageView batteryIndicator;
    private View signalDot;
    private View stationGreenLed;
    private View stationBlueLed;
    private View stationAmberLed;
    private View stationWhiteLed;
    private View stationRedLed;
    private View rfidScannerLed;
    private LinearLayout authPanel;
    private LinearLayout metricsPanel;
    private LinearLayout paymentPanel;
    private EditText pinInput;
    private Button pinButton;
    private Button primaryButton;
    private Button secondaryButton;
    private ImageView upiQrImage;
    private TextView upiIdText;

    private GpioProcessor.Gpio cablePin;
    private Thread cableThread;
    private Thread rfidThread;
    private volatile boolean stopThreads;
    private boolean authorizing;
    private float soc = INITIAL_SOC;
    private float energyKwh;
    private float csmsCost;
    private int elapsedSeconds;
    private boolean costFromCsms;
    private String activeIdToken = "";
    private IdTokenEnumType activeIdTokenType;
    private String pendingIdToken = "";
    private IdTokenEnumType pendingIdTokenType;
    private boolean evSideCablePlugged;
    private Uri currentUpiUri;
    private String billingMessage = "";
    private String paymentReference = "";

    private final Runnable chargingTick = new Runnable() {
        @Override
        public void run() {
            if (state != ProductState.CHARGING) {
                return;
            }

            elapsedSeconds++;
            updateMeterSnapshot();
            renderMetrics();

            if (soc >= TARGET_SOC) {
                finishCharging(ReasonEnumType.SOCLimitReached, TriggerReasonEnumType.EnergyLimitReached);
                return;
            }

            int txInterval = Math.max(1, Math.min(5, SampledDataCtrlr.TxUpdatedInterval));
            if (elapsedSeconds % txInterval == 0) {
                sendPeriodicMeterValues();
            }
            handler.postDelayed(this, 1000);
        }
    };

    private final Runnable evConnectionTimeoutRunnable = new Runnable() {
        @Override
        public void run() {
            if (state == ProductState.SUSPENDED) {
                finishCharging(
                        ReasonEnumType.EVDisconnected,
                        TriggerReasonEnumType.EVCommunicationLost,
                        "EV side cable unplugged. Session stopped after connection timeout.");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_product_charger);

        bindViews();
        myClientEndpoint = MyClientEndpoint.getInstance();
        myClientEndpoint.init(getApplicationContext());
        myClientEndpoint.setCostUpdateListener(new MyClientEndpoint.CostUpdateListener() {
            @Override
            public void onCostUpdated(final String transactionId, final float totalCost) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handleCostUpdated(transactionId, totalCost);
                    }
                });
            }
        });
        signalController = new StationSignalController();

        EVSEType.setId(1);
        EVSEType.setConnectorId(1);
        DisplayMessageState.setMessageState(MessageStateEnumType.Idle);

        prepareCableInput();
        showReady();
    }

    @Override
    protected void onStart() {
        super.onStart();
        stopThreads = false;
        startRfidPolling();
        startCablePolling();
    }

    @Override
    protected void onStop() {
        stopThreads = true;
        if (cableThread != null) {
            cableThread.interrupt();
        }
        if (rfidThread != null) {
            rfidThread.interrupt();
        }
        serialRfidReader.close();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        if (myClientEndpoint != null) {
            myClientEndpoint.setCostUpdateListener(null);
        }
        if (signalController != null) {
            signalController.close();
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != UPI_PAYMENT_REQUEST_CODE) {
            return;
        }

        String response = data == null ? "" : data.getStringExtra("response");
        if (response == null) {
            response = "";
        }

        String status = upiResponseValue(response, "Status");
        if ("SUCCESS".equalsIgnoreCase(status) || response.toUpperCase(Locale.US).contains("SUCCESS")) {
            subtitle.setText("Payment reported");
            setDetail(billingSummary()
                    + "\nUPI app reported success. Confirm settlement in CSMS before closing the payment.");
        } else if ("FAILURE".equalsIgnoreCase(status)
                || "FAILED".equalsIgnoreCase(status)
                || response.toUpperCase(Locale.US).contains("FAILURE")) {
            subtitle.setText("Payment failed");
            setDetail(billingSummary() + "\nPayment failed. Scan the QR again or retry Pay.");
        } else if (resultCode == RESULT_CANCELED) {
            subtitle.setText("Payment pending");
            setDetail(billingSummary() + "\nPayment was cancelled. Scan the QR again or retry Pay.");
        } else {
            subtitle.setText("Payment pending");
            setDetail(billingSummary() + "\nPayment status is pending. Confirm settlement before release.");
        }
    }

    private void bindViews() {
        signalLabel = findViewById(R.id.productSignalLabel);
        title = findViewById(R.id.productTitle);
        subtitle = findViewById(R.id.productSubtitle);
        detail = findViewById(R.id.productDetail);
        socMetric = findViewById(R.id.productSoc);
        energyMetric = findViewById(R.id.productEnergy);
        costMetric = findViewById(R.id.productCost);
        timeMetric = findViewById(R.id.productTime);
        batteryIndicator = findViewById(R.id.productBatteryIndicator);
        signalDot = findViewById(R.id.productSignalDot);
        stationGreenLed = findViewById(R.id.stationGreenLed);
        stationBlueLed = findViewById(R.id.stationBlueLed);
        stationAmberLed = findViewById(R.id.stationAmberLed);
        stationWhiteLed = findViewById(R.id.stationWhiteLed);
        stationRedLed = findViewById(R.id.stationRedLed);
        rfidScannerLed = findViewById(R.id.rfidScannerLed);
        authPanel = findViewById(R.id.productAuthPanel);
        metricsPanel = findViewById(R.id.productMetricsPanel);
        paymentPanel = findViewById(R.id.productPaymentPanel);
        pinInput = findViewById(R.id.productPinInput);
        pinButton = findViewById(R.id.productPinButton);
        primaryButton = findViewById(R.id.productPrimaryButton);
        secondaryButton = findViewById(R.id.productSecondaryButton);
        upiQrImage = findViewById(R.id.productUpiQr);
        upiIdText = findViewById(R.id.productUpiId);

        pinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authorizeWithPin();
            }
        });
    }

    private void prepareCableInput() {
        if (!Utils.rootAccess()) {
            return;
        }
        Permissions.GivePermissionToGpio(Collections.singletonList(CABLE_PIN));
        GpioProcessor gpioProcessor = new GpioProcessor();
        cablePin = gpioProcessor.getPin(CABLE_PIN);
        cablePin.in();
    }

    private void showReady() {
        state = ProductState.READY;
        authorizing = false;
        handler.removeCallbacks(chargingTick);
        handler.removeCallbacks(evConnectionTimeoutRunnable);
        soc = INITIAL_SOC;
        energyKwh = 0;
        csmsCost = 0;
        elapsedSeconds = 0;
        costFromCsms = false;
        activeIdToken = "";
        activeIdTokenType = null;
        pendingIdToken = "";
        pendingIdTokenType = null;
        evSideCablePlugged = false;
        billingMessage = "";
        paymentReference = "";
        renderSignal(StationSignal.READY);
        showScannerReady();
        DisplayMessageState.setMessageState(MessageStateEnumType.Idle);

        title.setText("Ready");
        subtitle.setText("Tap RFID");
        setDetail(null);
        authPanel.setVisibility(View.GONE);
        metricsPanel.setVisibility(View.GONE);
        hidePaymentPanel();
        primaryButton.setVisibility(View.VISIBLE);
        primaryButton.setEnabled(true);
        primaryButton.setBackgroundResource(R.drawable.product_button_primary);
        primaryButton.setText("Start");
        primaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAuthorize();
            }
        });
        secondaryButton.setVisibility(View.GONE);
        secondaryButton.setEnabled(true);

        sendStatus(ConnectorStatusEnumType.Available);
    }

    private void showAuthorize() {
        state = ProductState.AUTHORIZING;
        renderSignal(StationSignal.AUTHORIZE);
        showScannerReady();
        DisplayMessageState.setMessageState(MessageStateEnumType.Idle);

        title.setText("Tap card");
        subtitle.setText("PIN fallback below");
        setDetail(null);
        authPanel.setVisibility(View.VISIBLE);
        metricsPanel.setVisibility(View.GONE);
        hidePaymentPanel();
        primaryButton.setVisibility(View.GONE);
        primaryButton.setEnabled(true);
        secondaryButton.setVisibility(View.VISIBLE);
        secondaryButton.setEnabled(true);
        secondaryButton.setBackgroundResource(R.drawable.product_button_secondary);
        secondaryButton.setText("Cancel");
        secondaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showReady();
            }
        });
    }

    private void showPlugIn() {
        state = ProductState.WAIT_FOR_PLUG;
        renderSignal(StationSignal.PLUG_IN);
        showScannerOff();

        title.setText("Plug in");
        subtitle.setText("Connect cable");
        setDetail(null);
        authPanel.setVisibility(View.GONE);
        metricsPanel.setVisibility(View.GONE);
        hidePaymentPanel();
        primaryButton.setVisibility(View.VISIBLE);
        primaryButton.setEnabled(true);
        primaryButton.setBackgroundResource(R.drawable.product_button_primary);
        primaryButton.setText("Cable connected");
        primaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleCableConnected();
            }
        });
        secondaryButton.setVisibility(View.VISIBLE);
        secondaryButton.setEnabled(true);
        secondaryButton.setBackgroundResource(R.drawable.product_button_secondary);
        secondaryButton.setText("Cancel");
        secondaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelTransaction();
            }
        });
    }

    private void showCharging() {
        state = ProductState.CHARGING;
        renderSignal(StationSignal.CHARGING);
        showScannerOff();
        DisplayMessageState.setMessageState(MessageStateEnumType.Charging);

        title.setText("Charging");
        subtitle.setText("Target " + TARGET_SOC + "%");
        setDetail("Live meter data is read from the controller. Cost is updated by CSMS.");
        authPanel.setVisibility(View.GONE);
        metricsPanel.setVisibility(View.VISIBLE);
        hidePaymentPanel();
        primaryButton.setVisibility(View.VISIBLE);
        primaryButton.setEnabled(true);
        primaryButton.setBackgroundResource(R.drawable.product_button_danger);
        primaryButton.setText("Stop");
        primaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showStopAuthorize();
            }
        });
        secondaryButton.setVisibility(View.GONE);
        secondaryButton.setEnabled(true);
        renderMetrics();
    }

    private void showBilling(String message) {
        state = ProductState.BILLING;
        renderSignal(StationSignal.COMPLETE);
        showScannerOff();
        DisplayMessageState.setMessageState(MessageStateEnumType.Idle);

        title.setText("Billing");
        subtitle.setText("Scan to pay");
        billingMessage = message == null ? "" : message;
        setDetail(billingDisplayText(billingMessage));
        authPanel.setVisibility(View.GONE);
        metricsPanel.setVisibility(View.GONE);
        renderUpiPayment();
        primaryButton.setVisibility(View.VISIBLE);
        primaryButton.setEnabled(true);
        primaryButton.setBackgroundResource(R.drawable.product_button_primary);
        primaryButton.setText("Pay");
        primaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchUpiPayment();
            }
        });
        secondaryButton.setVisibility(View.VISIBLE);
        secondaryButton.setEnabled(true);
        secondaryButton.setBackgroundResource(R.drawable.product_button_secondary);
        secondaryButton.setText("Charge more");
        secondaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showReady();
            }
        });
        renderMetrics();
    }

    private void showStopAuthorize() {
        if (state != ProductState.CHARGING && state != ProductState.SUSPENDED) {
            return;
        }

        state = ProductState.STOP_AUTHORIZING;
        renderSignal(StationSignal.AUTHORIZE);
        showScannerReady();
        title.setText("Verify stop");
        subtitle.setText("Present same ID");
        setDetail("Use the same RFID/PIN that started this session.");
        authPanel.setVisibility(activeIdTokenType == IdTokenEnumType.KeyCode ? View.VISIBLE : View.GONE);
        metricsPanel.setVisibility(View.VISIBLE);
        hidePaymentPanel();
        primaryButton.setVisibility(View.GONE);
        secondaryButton.setVisibility(View.VISIBLE);
        secondaryButton.setEnabled(true);
        secondaryButton.setBackgroundResource(R.drawable.product_button_secondary);
        secondaryButton.setText("Resume");
        secondaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (evSideCablePlugged) {
                    showCharging();
                    handler.postDelayed(chargingTick, 1000);
                } else {
                    showSuspended();
                    handler.removeCallbacks(evConnectionTimeoutRunnable);
                    handler.postDelayed(evConnectionTimeoutRunnable, getEvConnectionTimeoutSeconds() * 1000L);
                }
            }
        });
    }

    private void showSuspended() {
        state = ProductState.SUSPENDED;
        renderSignal(StationSignal.PLUG_IN);
        showScannerOff();
        title.setText("Charging paused");
        subtitle.setText("Cable unplugged");
        setDetail("Reconnect EV side cable within " + getEvConnectionTimeoutSeconds() + "s.");
        authPanel.setVisibility(View.GONE);
        metricsPanel.setVisibility(View.VISIBLE);
        hidePaymentPanel();
        primaryButton.setVisibility(View.VISIBLE);
        primaryButton.setEnabled(true);
        primaryButton.setBackgroundResource(R.drawable.product_button_danger);
        primaryButton.setText("Stop");
        primaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showStopAuthorize();
            }
        });
        secondaryButton.setVisibility(View.GONE);
        secondaryButton.setEnabled(true);
        renderMetrics();
    }

    private void showFault(String message) {
        state = ProductState.FAULT;
        renderSignal(StationSignal.FAULT);
        title.setText("Attention");
        subtitle.setText("Try again");
        setDetail(message);
        hidePaymentPanel();
    }

    private void authorizeWithPin() {
        String pin = pinInput.getText().toString().trim();
        if (pin.length() == 0) {
            setDetail("Enter PIN or tap RFID.");
            return;
        }

        AdditionalInfoType.setType("source");
        AdditionalInfoType.setAdditionalIdToken("product-screen");
        authorizeWithToken(pin, IdTokenEnumType.KeyCode);
    }

    private void authorizeWithToken(String token, IdTokenEnumType type) {
        if (authorizing || token == null || token.trim().length() == 0) {
            return;
        }

        final boolean stopRequest = state == ProductState.STOP_AUTHORIZING;
        authorizing = true;
        state = stopRequest ? ProductState.STOP_AUTHORIZING : ProductState.AUTHORIZING;
        pendingIdToken = token.trim();
        pendingIdTokenType = type;
        renderSignal(StationSignal.AUTHORIZE);
        showScannerReading();
        title.setText(stopRequest ? "Checking stop" : "Checking");
        subtitle.setText("Please wait");
        setDetail(null);
        authPanel.setVisibility(View.GONE);

        IdTokenType.setType(type);
        IdTokenType.setIdToken(pendingIdToken);
        try {
            toCSMS.sendAuthorizeRequest();
        } catch (JSONException e) {
            Log.e(TAG, "Authorize request failed", e);
            authorizing = false;
            showScannerRejected();
            showFault("Authorization request could not be sent.");
            return;
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (stopRequest) {
                    handleStopAuthorizationResult();
                } else {
                    handleAuthorizationResult();
                }
            }
        }, AUTH_RESPONSE_DELAY_MS);
    }

    private void handleAuthorizationResult() {
        authorizing = false;
        if (myClientEndpoint.getIdInfo().getStatus() == AuthorizationStatusEnumType.Accepted) {
            activeIdToken = pendingIdToken;
            activeIdTokenType = pendingIdTokenType;
            showScannerAccepted();
            sendAuthorizedTransactionEvent();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showPlugIn();
                }
            }, 700);
            return;
        }

        showScannerRejected();
        showFault("Authorization " + myClientEndpoint.getIdInfo().getStatus().name());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showAuthorize();
            }
        }, 2500);
    }

    private void handleStopAuthorizationResult() {
        authorizing = false;
        boolean accepted = myClientEndpoint.getIdInfo().getStatus() == AuthorizationStatusEnumType.Accepted;
        boolean sameToken = pendingIdTokenType == activeIdTokenType && pendingIdToken.equals(activeIdToken);
        if (accepted && sameToken) {
            showScannerAccepted();
            finishCharging(ReasonEnumType.Local, TriggerReasonEnumType.StopAuthorized, "Stopped by authorized IdToken.");
            return;
        }

        showScannerRejected();
        showFault("Stop denied. Present the original IdToken.");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (evSideCablePlugged) {
                    showCharging();
                    handler.postDelayed(chargingTick, 1000);
                } else {
                    showSuspended();
                    handler.removeCallbacks(evConnectionTimeoutRunnable);
                    handler.postDelayed(evConnectionTimeoutRunnable, getEvConnectionTimeoutSeconds() * 1000L);
                }
            }
        }, 2500);
    }

    private void handleCableConnected() {
        if (state != ProductState.WAIT_FOR_PLUG) {
            return;
        }

        evSideCablePlugged = true;
        state = ProductState.CHARGING;
        title.setText("Starting");
        subtitle.setText("Preparing session");
        setDetail(null);
        primaryButton.setEnabled(false);
        secondaryButton.setVisibility(View.GONE);

        sendStatus(ConnectorStatusEnumType.Occupied);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendCablePluggedTransactionEvent();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendChargingStartedTransactionEvent();
                        showCharging();
                        handler.postDelayed(chargingTick, 1000);
                    }
                }, OCPP_CHAIN_DELAY_MS);
            }
        }, OCPP_CHAIN_DELAY_MS);
    }

    private void sendCablePluggedTransactionEvent() {
        TransactionEventRequest.eventType = TransactionEventEnumType.Updated;
        TransactionEventRequest.triggerReason = TriggerReasonEnumType.CablePluggedIn;
        TransactionType.chargingState = ChargingStateEnumType.EVConnected;
        TransactionType.stoppedReason = null;
        sendTransactionEvent();
    }

    private void sendChargingStartedTransactionEvent() {
        TransactionEventRequest.eventType = TransactionEventEnumType.Updated;
        TransactionEventRequest.triggerReason = TriggerReasonEnumType.ChargingStateChanged;
        TransactionType.chargingState = ChargingStateEnumType.Charging;
        sendTransactionEvent();
    }

    private void finishCharging(ReasonEnumType reason, TriggerReasonEnumType triggerReason) {
        finishCharging(reason, triggerReason, null);
    }

    private void finishCharging(ReasonEnumType reason, TriggerReasonEnumType triggerReason, String message) {
        if (state != ProductState.CHARGING
                && state != ProductState.STOP_AUTHORIZING
                && state != ProductState.SUSPENDED) {
            return;
        }

        handler.removeCallbacks(chargingTick);
        handler.removeCallbacks(evConnectionTimeoutRunnable);
        TransactionEventRequest.eventType = TransactionEventEnumType.Ended;
        TransactionEventRequest.triggerReason = triggerReason;
        TransactionType.chargingState = ChargingStateEnumType.Idle;
        TransactionType.stoppedReason = reason;
        TransactionType.timeSpentCharging = elapsedSeconds;
        sendTransactionEvent();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendStatus(ConnectorStatusEnumType.Available);
            }
        }, OCPP_CHAIN_DELAY_MS);

        String reasonText = reason == ReasonEnumType.SOCLimitReached
                ? "Target charge reached."
                : message == null ? "Stopped by driver." : message;
        showBilling(reasonText);
    }

    private void cancelTransaction() {
        TransactionEventRequest.eventType = TransactionEventEnumType.Ended;
        TransactionEventRequest.triggerReason = TriggerReasonEnumType.EVConnectTimeout;
        TransactionType.chargingState = ChargingStateEnumType.Idle;
        TransactionType.stoppedReason = ReasonEnumType.Timeout;
        sendTransactionEvent();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showReady();
            }
        }, OCPP_CHAIN_DELAY_MS);
    }

    private void sendAuthorizedTransactionEvent() {
        TransactionEventRequest.eventType = TransactionEventEnumType.Started;
        TransactionEventRequest.triggerReason = TriggerReasonEnumType.Authorized;
        TransactionType.chargingState = ChargingStateEnumType.Idle;
        TransactionType.stoppedReason = null;
        TransactionType.timeSpentCharging = 0;
        sendTransactionEvent();
    }

    private void sendStatus(ConnectorStatusEnumType connectorStatus) {
        StatusNotificationRequest.setConnectorStatus(connectorStatus);
        try {
            toCSMS.sendStatusNotificationRequest();
        } catch (JSONException e) {
            Log.e(TAG, "StatusNotification request failed", e);
        }
    }

    private void sendTransactionEvent() {
        try {
            toCSMS.sendTransactionEventRequest(this);
        } catch (JSONException e) {
            Log.e(TAG, "TransactionEvent request failed", e);
        }
    }

    private void sendPeriodicMeterValues() {
        try {
            JSONArray sampledValues = new JSONArray();
            SampledValueType energy = new SampledValueType(energyKwh, ReadingContextEnumType.SamplePeriodic, MeasurandEnumType.EnergyActiveImportRegister);
            SampledValueType currentSoc = new SampledValueType(soc, ReadingContextEnumType.SamplePeriodic, MeasurandEnumType.SoC);
            SampledValueType voltage = new SampledValueType(VOLTAGE, ReadingContextEnumType.SamplePeriodic, MeasurandEnumType.Voltage);
            sampledValues.put(0, energy.getp(new UnitOfMeasureType("kWh", 1)));
            sampledValues.put(1, currentSoc.getp(new UnitOfMeasureType("%", 1)));
            sampledValues.put(2, voltage.getp(new UnitOfMeasureType("V", 1)));
            TransactionEventRequest.SetMeterValues(sampledValues);
        } catch (JSONException e) {
            Log.e(TAG, "Meter values could not be prepared", e);
        }

        TransactionEventRequest.eventType = TransactionEventEnumType.Updated;
        TransactionEventRequest.triggerReason = TriggerReasonEnumType.MeterValuePeriodic;
        TransactionType.chargingState = ChargingStateEnumType.Charging;
        TransactionType.stoppedReason = null;
        TransactionType.timeSpentCharging = elapsedSeconds;
        sendTransactionEvent();
    }

    private void handleCostUpdated(String transactionId, float totalCost) {
        String currentTransactionId = TransactionType.transactionId == null ? "" : TransactionType.transactionId;
        if (transactionId != null
                && transactionId.trim().length() > 0
                && currentTransactionId.length() > 0
                && !transactionId.equals(currentTransactionId)) {
            return;
        }

        csmsCost = totalCost;
        costFromCsms = true;
        if (metricsPanel.getVisibility() == View.VISIBLE) {
            renderMetrics();
        }
        if (state == ProductState.BILLING && paymentPanel.getVisibility() == View.VISIBLE) {
            setDetail(billingDisplayText(billingMessage));
            renderUpiPayment();
        }
    }

    private void handleEvSideCableUnplugged() {
        if (state != ProductState.CHARGING || !CABLE_PERMANENTLY_ATTACHED) {
            return;
        }

        evSideCablePlugged = false;
        handler.removeCallbacks(chargingTick);
        showSuspended();

        TransactionEventRequest.eventType = TransactionEventEnumType.Updated;
        TransactionEventRequest.triggerReason = TriggerReasonEnumType.EVCommunicationLost;
        TransactionType.chargingState = ChargingStateEnumType.SuspendedEVSE;
        TransactionType.stoppedReason = null;
        TransactionType.timeSpentCharging = elapsedSeconds;
        sendTransactionEvent();

        handler.removeCallbacks(evConnectionTimeoutRunnable);
        handler.postDelayed(evConnectionTimeoutRunnable, getEvConnectionTimeoutSeconds() * 1000L);
    }

    private void handleEvSideCableReconnected() {
        if (state != ProductState.SUSPENDED) {
            return;
        }

        evSideCablePlugged = true;
        handler.removeCallbacks(evConnectionTimeoutRunnable);
        TransactionEventRequest.eventType = TransactionEventEnumType.Updated;
        TransactionEventRequest.triggerReason = TriggerReasonEnumType.CablePluggedIn;
        TransactionType.chargingState = ChargingStateEnumType.Charging;
        TransactionType.stoppedReason = null;
        TransactionType.timeSpentCharging = elapsedSeconds;
        sendTransactionEvent();
        showCharging();
        handler.postDelayed(chargingTick, 1000);
    }

    private void updateMeterSnapshot() {
        energyKwh += (VOLTAGE * CURRENT) / 3600000f;
        if (soc < TARGET_SOC) {
            soc = Math.min(TARGET_SOC, soc + 0.5f);
        }
    }

    private void renderMetrics() {
        int roundedSoc = Math.round(soc);
        socMetric.setText(String.format(Locale.US, "%d%%", roundedSoc));
        batteryIndicator.setImageResource(getBatteryIndicatorResource(roundedSoc));
        energyMetric.setText(String.format(Locale.US, "%.2f kWh", energyKwh));
        costMetric.setText(String.format(Locale.US, "INR %.2f", displayedCost()));
        timeMetric.setText(formattedElapsedTime());
    }

    private float displayedCost() {
        return costFromCsms ? csmsCost : energyKwh * TARIFF_PER_KWH;
    }

    private String billingSummary() {
        String transactionId = TransactionType.transactionId == null ? "" : TransactionType.transactionId;
        if (transactionId.trim().length() == 0) {
            transactionId = buildPaymentReference();
        }
        return String.format(Locale.US,
                "Transaction %s\nEnergy %.2f kWh | Time %s\nAmount INR %.2f",
                transactionId,
                energyKwh,
                formattedElapsedTime(),
                displayedCost());
    }

    private String billingDisplayText(String message) {
        if (message == null || message.trim().length() == 0) {
            return billingSummary();
        }
        return message.trim() + "\n" + billingSummary();
    }

    private void renderUpiPayment() {
        if (paymentPanel == null) {
            return;
        }

        currentUpiUri = buildUpiPaymentUri();
        try {
            upiQrImage.setImageBitmap(SimpleQrCode.bitmap(currentUpiUri.toString(), 4, 4));
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "UPI QR payload could not be rendered", e);
            upiQrImage.setImageBitmap(null);
        }
        upiIdText.setText(String.format(Locale.US,
                "UPI ID: %s\nAmount: INR %.2f",
                OWNER_UPI_ID,
                displayedCost()));
        paymentPanel.setVisibility(View.VISIBLE);
    }

    private void hidePaymentPanel() {
        currentUpiUri = null;
        if (paymentPanel != null) {
            paymentPanel.setVisibility(View.GONE);
        }
    }

    private Uri buildUpiPaymentUri() {
        String reference = buildPaymentReference();
        return new Uri.Builder()
                .scheme("upi")
                .authority("pay")
                .appendQueryParameter("pa", OWNER_UPI_ID)
                .appendQueryParameter("pn", OWNER_UPI_NAME)
                .appendQueryParameter("tr", reference)
                .appendQueryParameter("tn", "EV charging " + reference)
                .appendQueryParameter("am", String.format(Locale.US, "%.2f", Math.max(0f, displayedCost())))
                .appendQueryParameter("cu", "INR")
                .build();
    }

    private String buildPaymentReference() {
        if (paymentReference.length() > 0) {
            return paymentReference;
        }
        String transactionId = TransactionType.transactionId == null ? "" : TransactionType.transactionId.trim();
        String cleaned = transactionId.replaceAll("[^A-Za-z0-9]", "");
        if (cleaned.length() == 0) {
            cleaned = String.valueOf(System.currentTimeMillis() % 100000000L);
        }
        if (cleaned.length() > 24) {
            cleaned = cleaned.substring(cleaned.length() - 24);
        }
        paymentReference = STATION_ID + cleaned;
        return paymentReference;
    }

    private void launchUpiPayment() {
        if (currentUpiUri == null) {
            renderUpiPayment();
        }
        Intent upiIntent = new Intent(Intent.ACTION_VIEW, currentUpiUri);
        try {
            startActivityForResult(Intent.createChooser(upiIntent, "Pay with UPI"), UPI_PAYMENT_REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            subtitle.setText("Scan QR");
            setDetail(billingSummary()
                    + "\nNo UPI app is installed on this device. Scan the QR or pay to " + OWNER_UPI_ID + ".");
        }
    }

    private String upiResponseValue(String response, String key) {
        if (response == null || response.length() == 0) {
            return "";
        }
        String[] fields = response.split("&");
        for (String field : fields) {
            String[] pair = field.split("=", 2);
            if (pair.length == 2 && key.equalsIgnoreCase(pair[0])) {
                return pair[1];
            }
        }
        return "";
    }

    private String formattedElapsedTime() {
        int minutes = elapsedSeconds / 60;
        int seconds = elapsedSeconds % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }

    private int getEvConnectionTimeoutSeconds() {
        return DEFAULT_EV_CONNECTION_TIMEOUT_SECONDS;
    }

    private int getBatteryIndicatorResource(int socValue) {
        int clampedSoc = Math.max(0, Math.min(100, socValue));
        int roundedToTen = ((clampedSoc + 5) / 10) * 10;
        if (roundedToTen <= 0) {
            return R.drawable.drawing;
        }
        if (roundedToTen == 10) {
            return R.drawable.drawing10;
        }
        if (roundedToTen == 20) {
            return R.drawable.drawing20;
        }
        if (roundedToTen == 30) {
            return R.drawable.drawing30;
        }
        if (roundedToTen == 40) {
            return R.drawable.drawing40;
        }
        if (roundedToTen == 50) {
            return R.drawable.drawing50;
        }
        if (roundedToTen == 60) {
            return R.drawable.drawing60;
        }
        if (roundedToTen == 70) {
            return R.drawable.drawing70;
        }
        if (roundedToTen == 80) {
            return R.drawable.drawing80;
        }
        if (roundedToTen == 90) {
            return R.drawable.drawing90;
        }
        return R.drawable.drawing100;
    }

    private void renderSignal(StationSignal signal) {
        signalController.show(signal);
        signalLabel.setText(signal.getLabel());

        drawLed(signalDot, signal.getColorHex(), true);
        drawLed(stationGreenLed, "#16803C", signal == StationSignal.READY);
        drawLed(stationBlueLed, "#2563EB", signal == StationSignal.AUTHORIZE || signal == StationSignal.CHARGING);
        drawLed(stationAmberLed, "#D97706", signal == StationSignal.BOOTING || signal == StationSignal.PLUG_IN);
        drawLed(stationWhiteLed, "#F8FAFC", signal == StationSignal.COMPLETE);
        drawLed(stationRedLed, "#B42318", signal == StationSignal.FAULT);
    }

    private void setDetail(String message) {
        if (message == null || message.trim().length() == 0) {
            detail.setText("");
            detail.setVisibility(View.GONE);
            return;
        }
        detail.setText(message);
        detail.setVisibility(View.VISIBLE);
    }

    private void showScannerReady() {
        signalController.showRfidReady();
        drawLed(rfidScannerLed, "#2563EB", true);
    }

    private void showScannerReading() {
        signalController.showRfidReading();
        drawLed(rfidScannerLed, "#2563EB", true);
    }

    private void showScannerAccepted() {
        signalController.blinkRfidAccepted();
        drawLed(rfidScannerLed, "#16A34A", true);
    }

    private void showScannerRejected() {
        signalController.blinkRfidRejected();
        drawLed(rfidScannerLed, "#B42318", true);
    }

    private void showScannerOff() {
        signalController.showRfidOff();
        drawLed(rfidScannerLed, "#CBD5E1", false);
    }

    private void drawLed(View led, String colorHex, boolean active) {
        if (led == null) {
            return;
        }

        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(Color.parseColor(active ? colorHex : "#CBD5E1"));
        drawable.setStroke(2, Color.parseColor(active ? "#1F2937" : "#94A3B8"));
        led.setBackgroundDrawable(drawable);
    }

    private void startCablePolling() {
        if (cablePin == null || cableThread != null && cableThread.isAlive()) {
            return;
        }

        cableThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !stopThreads) {
                    try {
                        final boolean cablePlugged = cablePin.getValue() == 0;
                        if (state == ProductState.WAIT_FOR_PLUG && cablePlugged) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    handleCableConnected();
                                }
                            });
                        } else if (state == ProductState.CHARGING && !cablePlugged) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    handleEvSideCableUnplugged();
                                }
                            });
                        } else if (state == ProductState.SUSPENDED && cablePlugged) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    handleEvSideCableReconnected();
                                }
                            });
                        }
                        Thread.sleep(250);
                    } catch (Exception e) {
                        Log.e(TAG, "Cable GPIO polling failed", e);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException interruptedException) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }
        });
        cableThread.start();
    }

    private void startRfidPolling() {
        if (rfidThread != null && rfidThread.isAlive()) {
            return;
        }

        if (Utils.rootAccess()) {
            Permissions.GivePermissionToSerial();
        }

        rfidThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !stopThreads) {
                    try {
                        if ((state != ProductState.READY
                                && state != ProductState.AUTHORIZING
                                && state != ProductState.STOP_AUTHORIZING)
                                || authorizing) {
                            Thread.sleep(250);
                            continue;
                        }
                        if (!serialRfidReader.isConnected()) {
                            Thread.sleep(1000);
                            continue;
                        }

                        final String uid = serialRfidReader.readUid();
                        if (uid == null) {
                            Thread.sleep(250);
                            continue;
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (state == ProductState.READY) {
                                    showAuthorize();
                                }
                                authorizeWithToken(uid, IdTokenEnumType.ISO14443);
                            }
                        });
                    } catch (IOException e) {
                        Log.e(TAG, "RFID serial read failed: " + e.getMessage());
                        serialRfidReader.close();
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException interruptedException) {
                            Thread.currentThread().interrupt();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        });
        rfidThread.start();
    }
}
