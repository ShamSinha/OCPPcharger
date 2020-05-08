package com.example.chargergui;

import android.app.Activity;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;

import javax.websocket.EncodeException;

import ChargingStationRequest.TransactionEventRequest;
import DataType.IdTokenType;
import DataType.TransactionType;
import EnumDataType.AuthorizationStatusEnumType;
import EnumDataType.ChargingStateEnumType;
import EnumDataType.IdTokenEnumType;
import EnumDataType.TransactionEventEnumType;
import EnumDataType.TriggerReasonEnumType;
import UseCasesOCPP.SendRequestToCSMS;

public class Authorization2 extends Activity {
    ProgressBar progressBar ;
    ImageButton imageButton ;
    boolean stopThread = false;
    TextView CablePluginStatus;
    TextView Carddetect;
    TextView AuthStatusRfid ;
    ImageView CableIn ;
    TextView InstructPlugin ;
    SendRequestToCSMS toCSMS = new SendRequestToCSMS();
    final MainActivity bs = new MainActivity();
    MyClientEndpoint myClientEndpoint ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE) ;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_rfid_auth);
        imageButton.findViewById(R.id.backbutton2);
        progressBar = (ProgressBar) findViewById(R.id.pb);
        Carddetect = (TextView) findViewById(R.id.tagstatus);
        AuthStatusRfid = (TextView) findViewById(R.id.authstatusrfid) ;
        InstructPlugin = (TextView) findViewById(R.id.instructplugintext2) ;

        InstructPlugin.setVisibility(View.GONE);
        AuthStatusRfid.setVisibility(View.GONE);
        Carddetect.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        CableIn = (ImageView) findViewById(R.id.cableconnectedview) ;
        CablePluginStatus = (TextView) findViewById(R.id.textView19);
        CableIn.setVisibility(View.GONE);
        CablePluginStatus.setVisibility(View.GONE);

        myClientEndpoint = MyClientEndpoint.getInstance() ;

    }
    @Override
    protected void onStart() {
        super.onStart();

        bs.BluetoothThreadforCablePlug();

        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !stopThread) {
                    Authorization1 a = new Authorization1();
                    if( a.IsCableConnectedBeforeAuthorized()){
                    stopThread = true ;
                    }
                }
            }
        });
        thread1.start();

    }

    public void AfterHavingRFID(String rfid){

        progressBar.setVisibility(View.VISIBLE);
        Carddetect.setVisibility(View.VISIBLE);

        IdTokenType.setType(IdTokenEnumType.ISO14443);
        IdTokenType.setIdToken(rfid);
        try {
            send(toCSMS.createAuthorizeRequest());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getResponse();
    }


    public void GetRFIDthread(){
        if(bs.BTinit()){
            if(bs.BTconnect()){
                bs.deviceConnected=true;
                stopThread = false;

                Thread thread  = new Thread(new Runnable()
                {
                    public void run()
                    {
                        while(!Thread.currentThread().isInterrupted() && !stopThread)
                        {
                            try
                            {
                                String string = "RFID";
                                bs.outputStream.write(string.getBytes());

                                int byteCount = bs.inputStream.available();
                                if(byteCount > 0)
                                {
                                    byte[] mmBuffer = new byte[1024];
                                    int numBytes; // bytes returned from read()
                                    numBytes = bs.inputStream.read(mmBuffer);
                                    final String ch=new String(mmBuffer,"UTF-8");
                                    Handler handler = new Handler();
                                    handler.post(new Runnable() {
                                        public void run()
                                        {
                                            AfterHavingRFID(ch);
                                        }
                                    });

                                }
                            }
                            catch (IOException ex)
                            {
                                stopThread = true;
                            }
                        }
                    }
                });

                thread.start();
            }
        }
    }

    public void OnClickBack2(View view){
        IdTokenType.setType(null);
        IdTokenType.setIdToken(null);
        Intent i = new Intent(Authorization2.this, Authentication.class);
        startActivity(i);
    }

    private void send(final CALL call) {
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    myClientEndpoint.getOpenSession().getBasicRemote().sendObject(call);
                    Log.d("TAG" , "Message Sent" + CALL.getAction() + call.getPayload());


                } catch (IOException | EncodeException e) {
                    Log.e("ERROR" , "IOException in BasicRemote") ;
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    });
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
                                    AuthStatusRfid.setVisibility(View.VISIBLE);
                                    AuthStatusRfid.setText("Authorization\nSuccessful");

                                    if(!ChargingStationStates.isEVSideCablePluggedIn){
                                        InstructPlugin.setVisibility(View.VISIBLE);
                                    }

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
                            progressBar.setVisibility(View.GONE);
                            AuthStatusRfid.setVisibility(View.VISIBLE);
                            AuthStatusRfid.setText(String.format("%s\nCARD", myClientEndpoint.getIdInfo().getStatus()));
                        }
                    }
                });

            }
        });
        thread.start();
    }


}
