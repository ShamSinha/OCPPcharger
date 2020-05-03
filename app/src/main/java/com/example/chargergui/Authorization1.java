package com.example.chargergui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import UseCasesOCPP.IdTokenInfoType;
import DataType.IdTokenType;
import DataType.TransactionType;
import EnumDataType.AuthorizationStatusEnumType;
import EnumDataType.ChargingStateEnumType;
import EnumDataType.ConnectorStatusEnumType;
import EnumDataType.IdTokenEnumType;
import EnumDataType.TransactionEventEnumType;
import EnumDataType.TriggerReasonEnumType;
import UseCasesOCPP.SendRequestToCSMS;


public class Authorization1 extends Activity {
    EditText PIN ;
    TextView dateTime ;
    TextView CablePluginStatus;
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
        CableIn.setVisibility(View.INVISIBLE);
        CablePluginStatus.setVisibility(View.INVISIBLE);
        stopThread = false ;

        myClientEndpoint = MyClientEndpoint.getInstance() ;
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
                toCSMS.sendStatusNotificationRequest();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (EncodeException e) {
                e.printStackTrace();
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
            } catch (IOException e) {
                e.printStackTrace();
            } catch (EncodeException e) {
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

    public void OnClickAuthorize(View view) throws IOException, EncodeException, JSONException, InterruptedException {

        progressBar.setVisibility(View.VISIBLE);

        IdTokenType.setType(IdTokenEnumType.KeyCode);
        IdTokenType.setIdToken(PIN.getText().toString());
        toCSMS.sendAuthorizeRequest();

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
            toCSMS.sendTransactionEventRequest();
        }


        if (ChargingStationStates.isAuthorized){
            progressBar.setVisibility(View.INVISIBLE);

            if(ChargingStationStates.isEVSideCablePluggedIn) {
                Toast.makeText(getApplicationContext(), "Authorization Successful", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Authorization1.this, UserInput.class);
                startActivity(i);

            }
            if(!ChargingStationStates.isEVSideCablePluggedIn){
                Toast.makeText(getApplicationContext(), "Authorization Successful\n Now Plug the Cable to the Car", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Authorization1.this, CablePlugActivity.class);
                startActivity(i);
                }
        }

        else if(myClientEndpoint.getIdInfo().getStatus()  != null) {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), myClientEndpoint.getIdInfo().getStatus() +"PIN " , Toast.LENGTH_SHORT).show();
        }
    }

}
