package com.example.chargergui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.galarzaa.androidthings.Rc522;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;
import com.google.android.things.pio.SpiDevice;

import org.json.JSONException;

import java.io.IOException;

import AuthorizationRelated.AuthorizationStatusEnumType;
import AuthorizationRelated.IdTokenType;
import ChargingStationDetails.ChargingStationStates;
import ChargingStationRequest.StatusNotificationRequest;
import ChargingStationRequest.TransactionEventRequest;
import DataType.TransactionType;
import DisplayMessagesRelated.MessageStateEnumType;
import EnumDataType.ChargingStateEnumType;
import EnumDataType.ConnectorStatusEnumType;
import EnumDataType.IdTokenEnumType;
import TransactionRelated.TransactionEventEnumType;
import TransactionRelated.TriggerReasonEnumType;
import UseCasesOCPP.SendRequestToCSMS;

public class Authorization2 extends Activity {
    private static final String TAG = "Authorization2";
    private static final String SPI_PORT = "SPI0.0";
    private static final String RESET_PIN = "BCM25";

    ImageButton imageButton ;
    boolean stopThread = false;
    boolean stopThread1 = false ;
    boolean stopRfidThread = false;
    CardView cardView1 ;
    CardView cardView2 ;
    CardView cardView3 ;
    TextView authorizeStatus ;
    ImageView tickOrCross ;
    TextView cardProcessing ;

    ImageView CablePlugInImage ;
    TextView cablepluggedinText;

    int count ;
    SendRequestToCSMS toCSMS = new SendRequestToCSMS();
    MyClientEndpoint myClientEndpoint ;

    private Rc522 mRc522;
    private Gpio resetPin;
    private SpiDevice spiDevice;
    private Thread rfidThread;
    private boolean cableEventSent;
    private boolean authorizationInFlight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE) ;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_rfid_auth);

        imageButton = findViewById(R.id.backbutton2);

        cardView1 = findViewById(R.id.cardview1) ;
        cardView2 = findViewById(R.id.cardview2) ;
        cardView3 = findViewById(R.id.cardview3) ;

        authorizeStatus = findViewById(R.id.authorizestatus);
        tickOrCross = findViewById(R.id.tickorcrossIMAGE);
        cardProcessing = findViewById(R.id.cardview2TEXT) ;
        cardProcessing.setText("Card Detected\nProcessing ");

        cardView2.setVisibility(View.GONE);
        cardView3.setVisibility(View.GONE);

        CablePlugInImage = findViewById(R.id.cablepluginIMAGE);
        cablepluggedinText = findViewById(R.id.cablepluginTEXT) ;

        CablePlugInImage.setVisibility(View.GONE);
        cablepluggedinText.setVisibility(View.GONE);

        myClientEndpoint = MyClientEndpoint.getInstance() ;
        myClientEndpoint.init(getApplicationContext());

        DisplayMessageState.setMessageState(MessageStateEnumType.Idle);

        initializeRfidReader();

    }

    private void initializeRfidReader() {
        try {
            PeripheralManager manager = PeripheralManager.getInstance();
            resetPin = manager.openGpio(RESET_PIN);
            spiDevice = manager.openSpiDevice(SPI_PORT);
            mRc522 = new Rc522(this, spiDevice, resetPin);
        } catch (IOException e) {
            Log.e(TAG, "Could not initialize RC522 RFID reader", e);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        stopThread = false;
        stopRfidThread = false;

        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !stopThread) {
                    final boolean cableConnected = IsCableConnectedBeforeAuthorized();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CablePlugInImage.setVisibility(cableConnected ? View.VISIBLE : View.GONE);
                            cablepluggedinText.setVisibility(cableConnected ? View.VISIBLE : View.GONE);
                        }
                    });
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        });
        thread1.start();
        startRfidPolling();

    }

    @Override
    protected void onStop() {
        super.onStop();
        stopThread = true;
        stopRfidThread = true;
        if (rfidThread != null) {
            rfidThread.interrupt();
        }
    }

    @Override
    protected void onDestroy() {
        closeQuietly(spiDevice);
        closeQuietly(resetPin);
        super.onDestroy();
    }

    public boolean IsCableConnectedBeforeAuthorized(){

        if(ChargingStationStates.isEVSideCablePluggedIn && !ChargingStationStates.isAuthorized){
            if (cableEventSent) {
                return true;
            }

            StatusNotificationRequest.setConnectorStatus(ConnectorStatusEnumType.Occupied);
            try {
                toCSMS.sendStatusNotificationRequest();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            TransactionEventRequest.eventType = TransactionEventEnumType.Started;
            TransactionType.chargingState = ChargingStateEnumType.EVConnected;
            TransactionEventRequest.triggerReason = TriggerReasonEnumType.CablePluggedIn;
            try {
                toCSMS.sendTransactionEventRequest(Authorization2.this);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            cableEventSent = true;
            return true ;
        }
        cableEventSent = false;
        return false ;
    }


    public void AfterHavingRFID(String rfid){

        cardView1.setVisibility(View.GONE);
        cardView2.setVisibility(View.VISIBLE);
        Processing();

        IdTokenType.setType(IdTokenEnumType.ISO14443);
        IdTokenType.setIdToken(rfid);
        try {
            toCSMS.sendAuthorizeRequest();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getResponse();
    }

    private void startRfidPolling() {
        if (mRc522 == null || rfidThread != null && rfidThread.isAlive()) {
            return;
        }
        rfidThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !stopRfidThread) {
                    final String rfid = readRfidUid();
                    if (rfid != null && !authorizationInFlight) {
                        authorizationInFlight = true;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AfterHavingRFID(rfid);
                            }
                        });
                        return;
                    }
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        });
        rfidThread.start();
    }

    private String readRfidUid() {
        try {
            mRc522.stopCrypto();
            if (!mRc522.request()) {
                return null;
            }
            if (!mRc522.antiCollisionDetect()) {
                return null;
            }
            byte[] uid = mRc522.getUid();
            if (uid == null || uid.length == 0) {
                return null;
            }
            mRc522.selectTag(uid);
            return toHexString(uid);
        } catch (Exception e) {
            Log.e(TAG, "RFID polling failed", e);
            return null;
        }
    }

    private String toHexString(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte value : bytes) {
            builder.append(String.format("%02X", value & 0xFF));
        }
        return builder.toString();
    }

    private void closeQuietly(AutoCloseable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Exception e) {
            Log.e(TAG, "Could not close RFID resource", e);
        }
    }

    private void Processing(){
        final Thread t = new Thread(){
            @Override
            public void run(){
                while(!isInterrupted() && !stopThread1){
                    try {
                        Thread.sleep(1000);  //1000ms = 1 sec
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                count++ ;
                                if(count%4 == 1) {
                                    cardProcessing.setText("Card Detected\nProcessing .");
                                }
                                if(count%4 == 2) {
                                    cardProcessing.setText("Card Detected\nProcessing ..");
                                }
                                if(count%4 == 3) {
                                    cardProcessing.setText("Card Detected\nProcessing ...");
                                }
                                if(count%4 == 0) {
                                    cardProcessing.setText("Card Detected\nProcessing ....");
                                }

                            }
                        });

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        };
        t.start();
    }


    public void OnClickBack2(View view){
        IdTokenType.setType(null);
        IdTokenType.setIdToken(null);
        Intent i = new Intent(Authorization2.this, Authentication.class);
        startActivity(i);
    }

    private void getResponse(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(myClientEndpoint.getIdInfo().getStatus() == AuthorizationStatusEnumType.Accepted){

                    ChargingStationStates.setAuthorized(true);

                    TransactionEventRequest.triggerReason = TriggerReasonEnumType.Authorized ;
                    if(ChargingStationStates.isEVSideCablePluggedIn) {
                        TransactionEventRequest.eventType = TransactionEventEnumType.Updated ;
                        TransactionType.chargingState = ChargingStateEnumType.EVConnected;
                    }
                    if(!ChargingStationStates.isEVSideCablePluggedIn){
                        TransactionEventRequest.eventType = TransactionEventEnumType.Started ;
                        TransactionType.chargingState =ChargingStateEnumType.Idle ;
                    }
                    try {
                        toCSMS.sendTransactionEventRequest(Authorization2.this);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        stopThread1 = true ;
                        cardView2.setVisibility(View.GONE);
                        if (ChargingStationStates.isAuthorized){
                            cardView3.setVisibility(View.VISIBLE);

                            new CountDownTimer(3000, 1000) {
                                public void onTick(long millisUntilFinished) {
                                    authorizeStatus.setText("Authorize!");
                                    tickOrCross.setImageResource(R.drawable.ic_png_check_mark_others_cdr_check_mark_area_svg_clipart);
                                }
                                public void onFinish() {
                                    if(ChargingStationStates.isEVSideCablePluggedIn) {
                                        Intent i = new Intent(Authorization2.this, UserInput.class);
                                        startActivity(i);
                                    }
                                    if(!ChargingStationStates.isEVSideCablePluggedIn){
                                        Intent i = new Intent(Authorization2.this, CablePlugActivity.class);
                                        startActivity(i);
                                    }
                                }
                            }.start();
                        }
                        else {
                            cardView3.setVisibility(View.VISIBLE);
                            authorizeStatus.setText(String.format("%s\nCARD", myClientEndpoint.getIdInfo().getStatus()));
                            tickOrCross.setImageResource(R.drawable.ic_cross);
                        }
                    }
                });

            }
        });
        thread.start();
    }


}
