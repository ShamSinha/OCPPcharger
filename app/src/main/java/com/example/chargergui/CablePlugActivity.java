package com.example.chargergui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.websocket.EncodeException;

import ChargingStationRequest.StatusNotificationRequest;
import ChargingStationRequest.TransactionEventRequest;
import DataType.IdTokenInfoType;
import DataType.IdTokenType;
import DataType.TransactionType;
import EnumDataType.AuthorizationStatusEnumType;
import EnumDataType.ChargingStateEnumType;
import EnumDataType.ConnectorStatusEnumType;
import EnumDataType.ReasonEnumType;
import EnumDataType.TransactionEventEnumType;
import EnumDataType.TriggerReasonEnumType;
import UseCasesOCPP.SendRequestToCSMS;

public class CablePlugActivity extends AppCompatActivity {

    TextView textView ;
    public int counter ;
    TextView set ;
    ImageView connected;
    ImageView plug1 ;
    ImageView plug2 ;
    SendRequestToCSMS toCSMS = new SendRequestToCSMS();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cable_plug);
        textView = (TextView) findViewById(R.id.timer);
        set = (TextView) findViewById(R.id.plugin);
        set.setText("Plug In the Cable \n  Within");
        connected = (ImageView) findViewById(R.id.imageView4);
        connected.setVisibility(View.INVISIBLE);
        plug1 = (ImageView) findViewById(R.id.imageView3);
        plug2 = (ImageView) findViewById(R.id.imageView2);

    }

    @Override
    protected void onStart() {
        super.onStart();

        final MainActivity bs = new MainActivity();

        bs.BluetoothThreadforCablePlug();
    }

    @Override
    protected void onResume(){
        super.onResume();

        setCountdowntimer();

        if(ChargingStationStates.isCablePluggedIn){
            plug1.setVisibility(View.INVISIBLE);
            plug2.setVisibility(View.INVISIBLE);
            connected.setVisibility(View.VISIBLE);
            set.setText("Cable Connected");
            textView.setText("You are all Set");

            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                StatusNotificationRequest.setConnectorStatus(ConnectorStatusEnumType.Occupied);
                toCSMS.sendStatusNotificationRequest();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (EncodeException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(CALLRESULT.MessageId.equals(CALL.MessageId)) {
                TransactionEventRequest.eventType = TransactionEventEnumType.Updated;
                TransactionType.chargingState = ChargingStateEnumType.EVConnected;
                TransactionEventRequest.triggerReason = TriggerReasonEnumType.CablePluggedIn;
                try {

                    toCSMS.sendTransactionEventRequest();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (EncodeException e) {
                    e.printStackTrace();
                }

                Intent i = new Intent(CablePlugActivity.this, SOCdisplay.class);
                startActivity(i);
            }
        }
    }

    public void CLEARALL(){
        IdTokenType.setIdToken(null);
        IdTokenType.setType(null);
        IdTokenInfoType.status = AuthorizationStatusEnumType.Invalid ;
    }


    public void setCountdowntimer(){
        new CountDownTimer(90000, 1000){
            public void onTick(long millisUntilFinished){
                textView.setText(String.valueOf(counter));
                counter++;
            }
            public  void onFinish(){
                textView.setText("OOPs! You Have to Authorize Again");
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                TransactionEventRequest.eventType = TransactionEventEnumType.Ended ;
                TransactionEventRequest.triggerReason = TriggerReasonEnumType.EVConnectTimeout ;
                TransactionType.stoppedReason = ReasonEnumType.Timeout;
                try {
                    toCSMS.sendTransactionEventRequest();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (EncodeException e) {
                    e.printStackTrace();
                }

                CLEARALL();

                Intent i = new Intent(CablePlugActivity.this , Authentication.class) ;
                startActivity(i);
            }
        }.start();
    }

}
