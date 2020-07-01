package com.example.chargergui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.websocket.EncodeException;

import ChargingStationRequest.StatusNotificationRequest;
import ChargingStationRequest.TransactionEventRequest;
import AuthorizationRelated.IdTokenType;
import DataType.TransactionType;
import EnumDataType.ChargingStateEnumType;
import EnumDataType.ConnectorStatusEnumType;
import DisplayMessagesRelated.MessageStateEnumType;
import EnumDataType.ReasonEnumType;
import ChargingStationDetails.ChargingStationStates;
import TransactionRelated.TransactionEventEnumType;
import TransactionRelated.TriggerReasonEnumType;
import UseCasesOCPP.SendRequestToCSMS;
import SOCDisplayRelated.SOCdisplay;

public class CablePlugActivity extends AppCompatActivity {

    TextView textView ;
    public int counter ;
    TextView set ;
    ImageView connected;
    ImageView plug1 ;
    ImageView plug2 ;
    SendRequestToCSMS toCSMS = new SendRequestToCSMS();
    MyClientEndpoint myClientEndpoint ;

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

    }

    @Override
    protected void onStart() {
        super.onStart();

        BluetoothThreadforCablePlugInStatus() ;

        setCountdowntimer();

    }

    private void AfterCablePluggedIn(){

            plug1.setVisibility(View.INVISIBLE);
            plug2.setVisibility(View.INVISIBLE);
            connected.setVisibility(View.VISIBLE);
            set.setText("Cable Connected");
            textView.setText("You are all Set");

            try {
                StatusNotificationRequest.setConnectorStatus(ConnectorStatusEnumType.Occupied);
                send(toCSMS.createStatusNotificationRequest());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            TransactionEventRequest.eventType = TransactionEventEnumType.Updated;
            TransactionType.chargingState = ChargingStateEnumType.EVConnected;
            TransactionEventRequest.triggerReason = TriggerReasonEnumType.CablePluggedIn;
            try {
                send(toCSMS.createTransactionEventRequest());

            } catch (JSONException e) {
                e.printStackTrace();
            }

        Intent i = new Intent(CablePlugActivity.this, SOCdisplay.class);
            startActivity(i);

    }

    public void CLEARALL(){
        IdTokenType.setIdToken(null);
        IdTokenType.setType(null);
    }


    public void setCountdowntimer(){
        new CountDownTimer(TxCtrlr.getEVConnectionTimeOut()*1000, 1000){
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
                    send(toCSMS.createTransactionEventRequest());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                CLEARALL();

                Intent i = new Intent(CablePlugActivity.this , Authentication.class) ;
                startActivity(i);
            }
        }.start();
    }

    private void BluetoothThreadforCablePlugInStatus() {
        final MainActivity bs = new MainActivity();
        if (bs.BTinit()) {
            if (bs.BTconnect()) {
                bs.deviceConnected = true;
                Thread thread = new Thread(new Runnable() {
                    public void run() {
                        boolean stopThread;
                        stopThread = false;
                        while (!Thread.currentThread().isInterrupted() && !stopThread) {
                            try {
                                String string = "CABLEPLUG";
                                bs.outputStream.write(string.getBytes());

                                int byteCount = bs.inputStream.available();
                                if (byteCount > 0) {
                                    byte[] mmBuffer = new byte[1024];
                                    int numBytes; // bytes returned from read()
                                    numBytes = bs.inputStream.read(mmBuffer);
                                    final String cableplug = new String(mmBuffer,0,numBytes,"UTF-8");
                                    Handler handler = new Handler();
                                    handler.post(new Runnable() {
                                        public void run() {
                                            if(cableplug.equals("T")) {
                                                ChargingStationStates.setCablePluggedIn(true);
                                                AfterCablePluggedIn();
                                            }
                                            else if(cableplug.equals("F")){
                                                ChargingStationStates.setCablePluggedIn(false);
                                            }

                                        }
                                    });

                                }
                            } catch (IOException ex) {
                                stopThread = true;
                            }
                        }
                    }
                });

                thread.start();
            }
        }
    }

    private void send(final CALL call) {
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    myClientEndpoint.getOpenSession().getBasicRemote().sendObject(call);
                    Log.d("TAG" , "Message Sent" + CALL.getAction() + call.getPayload());
                    Log.d("TAG", myClientEndpoint.getOpenSession().getId());

                } catch (IOException | EncodeException e) {
                    Log.e("ERROR" , "IOException in BasicRemote") ;
                    e.printStackTrace();
                }
            }
        });
        thread1.start();
    }



}
