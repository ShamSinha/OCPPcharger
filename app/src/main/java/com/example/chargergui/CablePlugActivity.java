package com.example.chargergui;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONException;

import java.util.concurrent.TimeUnit;

import ChargingStationRequest.StatusNotificationRequest;
import ChargingStationRequest.TransactionEventRequest;
import DataType.TransactionType;
import DisplayMessagesRelated.MessageStateEnumType;
import EnumDataType.ChargingStateEnumType;
import EnumDataType.ConnectorStatusEnumType;
import EnumDataType.ReasonEnumType;
import Hardware.GpioProcessor;
import SOCDisplayRelated.SOCdisplay;
import TransactionRelated.TransactionEventEnumType;
import TransactionRelated.TriggerReasonEnumType;
import UseCasesOCPP.SendRequestToCSMS;

public class CablePlugActivity extends AppCompatActivity {

    TextView textView ;
    public int counter ;
    TextView set ;
    ImageView connected;
    ImageView plug1 ;
    ImageView plug2 ;

    SendRequestToCSMS toCSMS = new SendRequestToCSMS();
    MyClientEndpoint myClientEndpoint ;

    private CablePlugViewModel cablePlugViewModel;

    GpioProcessor gpioProcessor = new GpioProcessor();
    GpioProcessor.Gpio led = gpioProcessor.getPin3();
    boolean locked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE) ;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_cable_plug);
        textView = findViewById(R.id.timer);
        set = findViewById(R.id.plugin);
        set.setText("Plug In the Cable \n  Within");
        connected = findViewById(R.id.imageView4);
        connected.setVisibility(View.INVISIBLE);
        plug1 = findViewById(R.id.imageView3);
        plug2 = findViewById(R.id.imageView2);

        myClientEndpoint = MyClientEndpoint.getInstance() ;

        DisplayMessageState.setMessageState(MessageStateEnumType.Idle);

        cablePlugViewModel = new ViewModelProvider(this).get(CablePlugViewModel.class) ;

        led.in();

    }

    @Override
    protected void onStart() {
        super.onStart();
        CheckGPIOCableIn();
        setCountdowntimer();
    }
    private void CheckGPIOCableIn(){
        final Thread t = new Thread(){
            @Override
            public void run(){
                while(!isInterrupted() && !locked){
                    try {
                        if(led.getValue() == 0){
                            locked = true ;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AfterCablePluggedIn();
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t.start();
    }

    private void AfterCablePluggedIn(){

            plug1.setVisibility(View.INVISIBLE);
            plug2.setVisibility(View.INVISIBLE);
            connected.setVisibility(View.VISIBLE);
            set.setText("Cable Connected");
            textView.setText("You are all Set");

            try {
                StatusNotificationRequest.setConnectorStatus(ConnectorStatusEnumType.Occupied);
                toCSMS.sendStatusNotificationRequest();

            } catch (JSONException e) {
                e.printStackTrace();
            }

            TransactionEventRequest.eventType = TransactionEventEnumType.Updated;
            TransactionType.chargingState = ChargingStateEnumType.EVConnected;
            TransactionEventRequest.triggerReason = TriggerReasonEnumType.CablePluggedIn;
            try {
                toCSMS.sendTransactionEventRequest(getApplicationContext());

            } catch (JSONException e) {
                e.printStackTrace();
            }

        Intent i = new Intent(CablePlugActivity.this, SOCdisplay.class);
            startActivity(i);

    }

    public void setCountdowntimer(){
        new CountDownTimer(cablePlugViewModel.getEVConnectionTimeOut()*1000, 1000){
            public void onTick(long millisUntilFinished){
                textView.setText(String.valueOf(counter));
                counter++;
            }
            public  void onFinish(){
                textView.setText("OOPs!\n You Have to Authorize Again");
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                TransactionEventRequest.eventType = TransactionEventEnumType.Ended ;
                TransactionEventRequest.triggerReason = TriggerReasonEnumType.EVConnectTimeout ;
                TransactionType.stoppedReason = ReasonEnumType.Timeout;
                try {
                    toCSMS.sendTransactionEventRequest(getApplicationContext());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent i = new Intent(CablePlugActivity.this , Authentication.class) ;
                startActivity(i);
            }
        }.start();
    }
}
