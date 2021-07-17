package com.example.chargergui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.galarzaa.androidthings.Rc522;

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
    ImageButton imageButton ;
    boolean stopThread = false;
    boolean stopThread1 = false ;
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
    final MainActivity bs = new MainActivity();
    MyClientEndpoint myClientEndpoint ;

    private Rc522 mRc522;



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

        DisplayMessageState.setMessageState(MessageStateEnumType.Idle);

        try {
            PeripheralManager manager = PeripheralManager.getInstance();
            resetPin = manager.openGpio("BCM25");
            SpiDevice spiDevice = manager.openSpiDevice("SPI0.0") ;
            mRc522 = new Rc522(spiDevice, resetPin);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Override
    protected void onStart() {
        super.onStart();

        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !stopThread) {

                    if( IsCableConnectedBeforeAuthorized()){
                        CablePlugInImage.setVisibility(View.VISIBLE);
                        cablepluggedinText.setVisibility(View.VISIBLE);
                    }
                    else {
                        CablePlugInImage.setVisibility(View.GONE);
                        cablepluggedinText.setVisibility(View.GONE);
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
                toCSMS.sendStatusNotificationRequest();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            TransactionEventRequest.eventType = TransactionEventEnumType.Started;
            TransactionType.chargingState = ChargingStateEnumType.EVConnected;
            TransactionEventRequest.triggerReason = TriggerReasonEnumType.CablePluggedIn;
            try {
                toCSMS.sendTransactionEventRequest();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return true ;
        }
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
                        toCSMS.sendTransactionEventRequest();
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
