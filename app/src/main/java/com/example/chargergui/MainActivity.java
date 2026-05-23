package com.example.chargergui;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import ChargingStationDetails.ChargingStationStatesRepo;
import Controller_Components.ControllerRepo;
import EnumDataType.AttributeEnumType;
import EnumDataType.RegistrationStatusEnumType;
import ChargingStationDetails.ChargingStationStates;


public class MainActivity extends Activity {

    TextView Boot;

    MyClientEndpoint myClientEndpoint ;
    ControllerRepo controllerRepo ;
    private boolean bootAccepted;
    private boolean handedOffToProduct;
    private StationSignalController stationSignalController;
    private TextView bootSignalLabel;
    private View bootSignalDot;
    private View bootGreenLed;
    private View bootBlueLed;
    private View bootAmberLed;
    private View bootWhiteLed;
    private View bootRedLed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE) ;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        Boot = findViewById(R.id.boottext);
        bootSignalLabel = findViewById(R.id.bootSignalLabel);
        bootSignalDot = findViewById(R.id.bootSignalDot);
        bootGreenLed = findViewById(R.id.bootGreenLed);
        bootBlueLed = findViewById(R.id.bootBlueLed);
        bootAmberLed = findViewById(R.id.bootAmberLed);
        bootWhiteLed = findViewById(R.id.bootWhiteLed);
        bootRedLed = findViewById(R.id.bootRedLed);
        myClientEndpoint = MyClientEndpoint.getInstance() ;
        myClientEndpoint.init(getApplicationContext());
        controllerRepo = new ControllerRepo(this);
        stationSignalController = new StationSignalController();
        stationSignalController.show(StationSignal.BOOTING);
        renderBootSignal(StationSignal.BOOTING);
        Boot.setText("Connecting to CSMS...");
    }

    @Override
    protected void onStart() {
        super.onStart() ;
        StartConnection();
    }

    private void StartConnection(){
        if (bootAccepted) {
            return;
        }
        stationSignalController.show(StationSignal.BOOTING);
        renderBootSignal(StationSignal.BOOTING);
        myClientEndpoint.ConnectClientToServer(Boot, new MyClientEndpoint.ConnectionListener() {
            @Override
            public void onBootAccepted(final int interval) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (bootAccepted) {
                            return;
                        }
                        bootAccepted = true;
                        String s = String.valueOf(interval);
                        controllerRepo.updateController("OCPPCommCtrlr","HeartbeatInterval",s, AttributeEnumType.Actual.toString()) ;

                        handedOffToProduct = true;
                        Intent i = new Intent(MainActivity.this , ProductChargerActivity.class);
                        startActivity(i);
                        finish();
                    }
                });
            }

            @Override
            public void onBootRejected(final RegistrationStatusEnumType status, final int interval) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        stationSignalController.show(StationSignal.FAULT);
                        renderBootSignal(StationSignal.FAULT);
                        Boot.append("\nBoot status: " + status + "\n");
                        retryConnection(interval);
                    }
                });
            }

            @Override
            public void onConnectionFailed(final String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        stationSignalController.show(StationSignal.FAULT);
                        renderBootSignal(StationSignal.FAULT);
                        Boot.append("\n" + message + "\n");
                        retryConnection(10);
                    }
                });
            }
        });
    }

    private void retryConnection(int intervalSeconds) {
        int retrySeconds = intervalSeconds <= 0 ? 10 : intervalSeconds;
        new CountDownTimer(retrySeconds * 1000L, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                StartConnection();
            }
        }.start();
    }

    private void renderBootSignal(StationSignal signal) {
        if (bootSignalLabel != null) {
            bootSignalLabel.setText(signal.getLabel());
        }
        drawLed(bootSignalDot, signal.getColorHex(), true);
        drawLed(bootGreenLed, "#16803C", signal == StationSignal.READY);
        drawLed(bootBlueLed, "#2563EB", signal == StationSignal.AUTHORIZE || signal == StationSignal.CHARGING);
        drawLed(bootAmberLed, "#D97706", signal == StationSignal.BOOTING || signal == StationSignal.PLUG_IN);
        drawLed(bootWhiteLed, "#F8FAFC", signal == StationSignal.COMPLETE);
        drawLed(bootRedLed, "#B42318", signal == StationSignal.FAULT);
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

    @Override
    protected void onDestroy() {
        if (!handedOffToProduct && stationSignalController != null) {
            stationSignalController.close();
        }
        super.onDestroy();
    }
}











