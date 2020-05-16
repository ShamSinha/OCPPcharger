package com.example.chargergui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import org.json.JSONException;

import java.io.IOException;

import javax.websocket.EncodeException;

import ChargingStationRequest.StatusNotificationRequest;
import ChargingStationRequest.TransactionEventRequest;
import DataType.AdditionalInfoType;
import DataType.IdTokenType;
import DataType.TransactionType;
import EnumDataType.AuthorizationStatusEnumType;
import EnumDataType.ChargingStateEnumType;
import EnumDataType.ConnectorStatusEnumType;
import EnumDataType.IdTokenEnumType;
import EnumDataType.MessageStateEnumType;
import EnumDataType.TransactionEventEnumType;
import EnumDataType.TriggerReasonEnumType;
import UseCasesOCPP.SendRequestToCSMS;


public class Authorization1 extends Activity {
    EditText PIN ;
    EditText Username ;
    TextView dateTime ;

    CardView cardView1PIN ;
    CardView cardView2PIN ;
    CardView cardView3PIN ;

    TextView CablePluginStatus;
    TextView AuthStatusText ;
    TextView PINprocessing ;
    ImageView CableIn ;
    Button button ;
    ImageView TickorCrossPIN ;

    boolean stopThread = false ;
    boolean stopThread1 = false ;
    int count ;
    SendRequestToCSMS toCSMS = new SendRequestToCSMS();
    MyClientEndpoint myClientEndpoint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE) ;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_authorization1);

        PIN = (EditText) findViewById(R.id.enterpin);
        Username = findViewById(R.id.editUSER);
        dateTime = (TextView) findViewById(R.id.date_time);
        dateTime2 dT = new dateTime2();
        dateTime.setText(dT.dateTime());
        button = (Button) findViewById(R.id.authorize);
        CableIn = (ImageView) findViewById(R.id.cablepluginIMAGE) ;
        CablePluginStatus = (TextView) findViewById(R.id.cablepluginTEXT);
        PINprocessing = findViewById(R.id.cardview2PINTEXT);
        AuthStatusText = findViewById(R.id.authorizestatusPIN);

        cardView1PIN = findViewById(R.id.EnterPINcardview);
        cardView2PIN = findViewById(R.id.cardview2PIN) ;
        cardView3PIN = findViewById(R.id.cardview3PIN) ;

        cardView2PIN.setVisibility(View.GONE);
        cardView3PIN.setVisibility(View.GONE);


        CableIn.setVisibility(View.GONE);
        CablePluginStatus.setVisibility(View.GONE);
        stopThread = false ;

        TickorCrossPIN = findViewById(R.id.tickorcrossimage);

        myClientEndpoint = MyClientEndpoint.getInstance();
        DisplayMessageState.setMessageState(MessageStateEnumType.Idle);

    }


    @Override
    protected void onStart() {
        super.onStart();
        final MainActivity bs = new MainActivity();

        bs.BluetoothThreadforCablePlug();

        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {

                while (!Thread.currentThread().isInterrupted() && !stopThread) {
                    if(IsCableConnectedBeforeAuthorized()){
                        CablePluginStatus.setVisibility(View.VISIBLE);
                        CableIn.setVisibility(View.VISIBLE);
                    }
                    else {
                        CablePluginStatus.setVisibility(View.GONE);
                        CableIn.setVisibility(View.GONE);
                    }
                }

            }
        });
        thread1.start();

    }



    public boolean IsCableConnectedBeforeAuthorized(){

        if(ChargingStationStates.isEVSideCablePluggedIn && !ChargingStationStates.isAuthorized){

            StatusNotificationRequest.setConnectorStatus(ConnectorStatusEnumType.Occupied);
            try {
                send(toCSMS.createStatusNotificationRequest());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            TransactionEventRequest.eventType = TransactionEventEnumType.Started;
            TransactionType.chargingState = ChargingStateEnumType.EVConnected;
            TransactionEventRequest.triggerReason = TriggerReasonEnumType.CablePluggedIn;
            try {
                send(toCSMS.createTransactionEventRequest());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return true ;
        }
        return false ;
    }

    private void PINProcessing(){
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
                                    PINprocessing.setText("PIN Processing .");
                                }
                                if(count%4 == 2) {
                                    PINprocessing.setText("PIN Processing ..");
                                }
                                if(count%4 == 3) {
                                    PINprocessing.setText("PIN Processing ...");
                                }
                                if(count%4 == 0) {
                                    PINprocessing.setText("PIN Processing ....");
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


    public void OnClickBack(View view){
        IdTokenType.setType(null);
        IdTokenType.setIdToken(null);
        Intent i = new Intent(Authorization1.this, Authentication.class);
        startActivity(i);

    }

    public void OnClickAuthorize(View view) throws  JSONException {
        cardView1PIN.setVisibility(View.GONE);
        cardView2PIN.setVisibility(View.VISIBLE);
        PINProcessing();
        IdTokenType.setType(IdTokenEnumType.KeyCode);
        IdTokenType.setIdToken(PIN.getText().toString());

        AdditionalInfoType.setType("username");
        AdditionalInfoType.setAdditionalIdToken(Username.getText().toString());
        send(toCSMS.createAuthorizeRequest());

        getResponse();
    }
    private void send(final CALL call) {
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                    try {
                        myClientEndpoint.getOpenSession().getBasicRemote().sendObject(call);
                        Log.d("TAG", "Message Sent: " + CALL.getAction() + call.getPayload());

                    } catch (IOException | EncodeException e) {
                        Log.e("ERROR", "IOException in BasicRemote");
                        e.printStackTrace();
                    }
                }
        });
        thread1.start();
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
                        send(toCSMS.createTransactionEventRequest());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cardView2PIN.setVisibility(View.GONE);
                        stopThread1 =  true ;
                        if (ChargingStationStates.isAuthorized){
                            cardView3PIN.setVisibility(View.VISIBLE);

                            new CountDownTimer(3000, 1000) {
                                public void onTick(long millisUntilFinished) {

                                    AuthStatusText.setText("Authorized!");
                                    TickorCrossPIN.setImageResource(R.drawable.ic_png_check_mark_others_cdr_check_mark_area_svg_clipart);

                                }
                                public void onFinish() {

                                    if(ChargingStationStates.isEVSideCablePluggedIn) {
                                        Intent i = new Intent(Authorization1.this, UserInput.class);
                                        startActivity(i);
                                    }
                                    if(!ChargingStationStates.isEVSideCablePluggedIn){
                                        Intent i = new Intent(Authorization1.this, CablePlugActivity.class);
                                        startActivity(i);
                                    }
                                }
                            }.start();
                        }
                        else {
                            cardView3PIN.setVisibility(View.VISIBLE);
                            AuthStatusText.setText(String.format("%s\nPIN", myClientEndpoint.getIdInfo().getStatus()));
                            TickorCrossPIN.setImageResource(R.drawable.ic_cross);
                        }
                    }
                });

            }
        });
        thread.start();
    }

}
