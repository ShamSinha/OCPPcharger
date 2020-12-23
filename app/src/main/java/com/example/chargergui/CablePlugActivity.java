package com.example.chargergui;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.PeripheralManager;

import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import ChargingStationRequest.StatusNotificationRequest;
import ChargingStationRequest.TransactionEventRequest;
import DataType.TransactionType;
import DisplayMessagesRelated.MessageStateEnumType;
import EnumDataType.ChargingStateEnumType;
import EnumDataType.ConnectorStatusEnumType;
import EnumDataType.ReasonEnumType;
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
    // GPIO Pin Name
    private static final String Cable = BoardDefaults.getGPIOForCable();
    private Gpio CableGPIO;
    MyGPIO myGPIO ;

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

        try {
            PeripheralManager manager = PeripheralManager.getInstance();
            CableGPIO= manager.openGpio(Cable);

        } catch (IOException e) {
            Log.w("TAG", "Unable to access GPIO", e);
        }
        try {
            myGPIO.configureInput(CableGPIO,gpioCallback1);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private GpioCallback gpioCallback1 = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio gpio) {
            // Read the active low pin state
            try {
                if (gpio.getValue()) {
                    // Pin is HIGH
                    AfterCablePluggedIn();
                } else {
                    // Pin is LOW

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Continue listening for more interrupts
            return true;
        }

        @Override
        public void onGpioError(Gpio gpio, int error) {
            Log.w("TAG", gpio + ": Error event " + error);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

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
