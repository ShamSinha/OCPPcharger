package com.example.chargergui;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE) ;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        Boot = findViewById(R.id.boottext);
        myClientEndpoint = MyClientEndpoint.getInstance() ;
    }

    @Override
    protected void onStart() {
        super.onStart() ;
        StartConnection();
    }

    private void StartConnection(){
        myClientEndpoint.ConnectClientToServer(Boot);

        if(myClientEndpoint.getBootNotificationResponse().getBootStatus() == RegistrationStatusEnumType.Accepted){
            int interval = myClientEndpoint.getBootNotificationResponse().getBootInterval();
            String s = String.valueOf(interval);
            controllerRepo.updateController("OCPPCommCtrlr","HeartbeatInterval",s, AttributeEnumType.Actual.toString()) ;

            Intent i = new Intent(MainActivity.this , WelcomeAndStart.class);
            startActivity(i);
        }
        if(myClientEndpoint.getBootNotificationResponse().getBootStatus() != RegistrationStatusEnumType.Accepted) {
            new CountDownTimer(myClientEndpoint.getBootNotificationResponse().getBootInterval()*1000, 1000) {
                public void onTick(long millisUntilFinished) {
                }
                public void onFinish() {
                    StartConnection();
                }
            };
        }
    }
}
















