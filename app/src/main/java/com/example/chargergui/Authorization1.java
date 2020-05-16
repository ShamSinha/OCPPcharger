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

import org.json.JSONException;

import java.io.IOException;

import javax.websocket.EncodeException;

import ChargingStationRequest.StatusNotificationRequest;
import ChargingStationRequest.TransactionEventRequest;
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
    TextView dateTime ;
    TextView CablePluginStatus;
    TextView AuthStatusText ;
    ImageView CableIn ;
    Button button ;
    ProgressBar progressBar ;
    boolean stopThread  ;
    ImageButton imageButton ;
    SendRequestToCSMS toCSMS = new SendRequestToCSMS();
    MyClientEndpoint myClientEndpoint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE) ;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_authorization1);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        progressBar.setVisibility(View.INVISIBLE);
        PIN = (EditText) findViewById(R.id.enterpin);
        dateTime = (TextView) findViewById(R.id.date_time);
        dateTime2 dT = new dateTime2();
        dateTime.setText(dT.dateTime());
        button = (Button) findViewById(R.id.authorize);
        CableIn = (ImageView) findViewById(R.id.cableconnectedview) ;
        CablePluginStatus = (TextView) findViewById(R.id.textView19);
        AuthStatusText = (TextView) findViewById(R.id.authtext);



        AuthStatusText.setVisibility(View.GONE);
        CableIn.setVisibility(View.GONE);
        CablePluginStatus.setVisibility(View.GONE);
        stopThread = false ;

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
                        stopThread = true ;
                    }
                }

            }
        });
        thread1.start();

    }



    public boolean IsCableConnectedBeforeAuthorized(){

        if(ChargingStationStates.isEVSideCablePluggedIn && !ChargingStationStates.isAuthorized){
            CableIn.setVisibility(View.VISIBLE);
            CablePluginStatus.setVisibility(View.VISIBLE);

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

    public void OnClickBack(View view){
        IdTokenType.setType(null);
        IdTokenType.setIdToken(null);
        Intent i = new Intent(Authorization1.this, Authentication.class);
        startActivity(i);

    }

    public void OnClickAuthorize(View view) throws  JSONException {

        progressBar.setVisibility(View.VISIBLE);

        IdTokenType.setType(IdTokenEnumType.KeyCode);
        IdTokenType.setIdToken(PIN.getText().toString());
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
                        if (ChargingStationStates.isAuthorized){
                            progressBar.setVisibility(View.GONE);
                            new CountDownTimer(3000, 1000) {
                                public void onTick(long millisUntilFinished) {
                                    AuthStatusText.setVisibility(View.VISIBLE);
                                    AuthStatusText.setText("Authorization\nSuccessful");

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
                            progressBar.setVisibility(View.GONE);
                            AuthStatusText.setVisibility(View.VISIBLE);
                            AuthStatusText.setText(String.format("%s\nPIN", myClientEndpoint.getIdInfo().getStatus()));
                        }
                    }
                });

            }
        });
        thread.start();
    }

}
