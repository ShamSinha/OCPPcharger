package com.example.chargergui;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONException;

import java.io.IOException;

import javax.websocket.EncodeException;

import AuthorizationRelated.AdditionalInfoType;
import AuthorizationRelated.IdTokenEntities;
import AuthorizationRelated.IdTokenType;
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


public class Authorization1 extends AppCompatActivity {
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

    private Authorization1ViewModel authorization1ViewModel ;
    boolean EVSideCablePluggedIn ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE) ;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_authorization1);

        PIN =  findViewById(R.id.enterpin);
        Username = findViewById(R.id.editUSER);
        dateTime = findViewById(R.id.date_time);
        dateTime2 dT = new dateTime2();
        dateTime.setText(dT.dateTime());
        button = findViewById(R.id.authorize);
        CableIn =  findViewById(R.id.cablepluginIMAGE) ;
        CablePluginStatus = findViewById(R.id.cablepluginTEXT);
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

        authorization1ViewModel = new ViewModelProvider(this).get(Authorization1ViewModel.class) ;

    }


    @Override
    protected void onStart() {
        super.onStart();
        final MainActivity bs = new MainActivity();

        bs.BluetoothThreadforCablePlug();

        final String transactionId = "" ;

        authorization1ViewModel = new ViewModelProvider(this).get(Authorization1ViewModel.class) ;
        authorization1ViewModel.isAuthorized(transactionId).observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                cardView2PIN.setVisibility(View.GONE);
                cardView3PIN.setVisibility(View.VISIBLE);
                if (aBoolean != null) {
                    if (aBoolean) {
                        authorization1ViewModel.updateAuthorized(transactionId, true);

                        TransactionEventRequest.triggerReason = TriggerReasonEnumType.Authorized;
                        if (EVSideCablePluggedIn) {
                            TransactionEventRequest.eventType = TransactionEventEnumType.Updated;
                            TransactionType.chargingState = ChargingStateEnumType.EVConnected;
                        } else {
                            TransactionEventRequest.eventType = TransactionEventEnumType.Started;
                            TransactionType.chargingState = ChargingStateEnumType.Idle;
                        }
                        try {
                            sendRequest(toCSMS.createTransactionEventRequest());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        new CountDownTimer(3000, 1000) {
                            public void onTick(long millisUntilFinished) {

                                AuthStatusText.setText("Authorized!");
                                TickorCrossPIN.setImageResource(R.drawable.ic_png_check_mark_others_cdr_check_mark_area_svg_clipart);
                            }

                            public void onFinish() {
                                if (EVSideCablePluggedIn) {
                                    Intent i = new Intent(Authorization1.this, UserInput.class);
                                    startActivity(i);
                                } else {
                                    Intent i = new Intent(Authorization1.this, CablePlugActivity.class);
                                    startActivity(i);
                                }

                            }
                        }.start();

                    } else {
                        AuthStatusText.setText(String.format("%s\nPIN", authorization1ViewModel.getIdTokenAndInfo(transactionId).getIdTokenInfo().getStatus()));
                        TickorCrossPIN.setImageResource(R.drawable.ic_cross);
                    }
                }
            }
        });

        authorization1ViewModel.isEVSideCablePluggedIn(transactionId).observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                EVSideCablePluggedIn = aBoolean ;
                if(aBoolean){
                    CablePluginStatus.setVisibility(View.VISIBLE);
                    CableIn.setVisibility(View.VISIBLE);
                }
                else{
                    CablePluginStatus.setVisibility(View.GONE);
                    CableIn.setVisibility(View.GONE);
                }
            }
        });
    }

    public void CableConnectedBeforeAuthorized() {
        StatusNotificationRequest.setConnectorStatus(ConnectorStatusEnumType.Occupied);
        try {
            sendRequest(toCSMS.createStatusNotificationRequest());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TransactionEventRequest.eventType = TransactionEventEnumType.Started;
        TransactionType.chargingState = ChargingStateEnumType.EVConnected;
        TransactionEventRequest.triggerReason = TriggerReasonEnumType.CablePluggedIn;
        try {
            sendRequest(toCSMS.createTransactionEventRequest());
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        Intent i = new Intent(Authorization1.this, Authentication.class);
        startActivity(i);
        authorization1ViewModel.deleteAll();
        authorization1ViewModel.deleteStates();
    }

    public void OnClickAuthorize(View view) throws  JSONException {
        cardView1PIN.setVisibility(View.GONE);
        cardView2PIN.setVisibility(View.VISIBLE);
        PINProcessing();
        IdTokenEntities.IdToken idToken = new IdTokenEntities.IdToken(PIN.getText().toString(),IdTokenEnumType.KeyCode.name(),Username.getText().toString());
        idToken.setTransactionId();
        authorization1ViewModel.insertIdToken(idToken);

        IdTokenType.setType(IdTokenEnumType.KeyCode);
        IdTokenType.setIdToken(PIN.getText().toString());
        AdditionalInfoType.setType("username");
        AdditionalInfoType.setAdditionalIdToken(Username.getText().toString());
        sendRequest(toCSMS.createAuthorizeRequest());
    }
    private void sendRequest(final CALL call) {
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

}
