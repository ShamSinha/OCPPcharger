package charging_viewmodel;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chargergui.CALL;
import com.example.chargergui.ChargingStationStates;
import com.example.chargergui.DisplayMessageState;
import com.example.chargergui.ImageChargeBattery;
import com.example.chargergui.ImageSetBattery;
import com.example.chargergui.MainActivity;
import com.example.chargergui.MyClientEndpoint;
import com.example.chargergui.PINauthorizeDialog;
import com.example.chargergui.R;
import com.example.chargergui.Target;

import org.json.JSONException;

import java.io.IOException;

import javax.websocket.EncodeException;

import ChargingStationRequest.StatusNotificationRequest;
import ChargingStationRequest.TransactionEventRequest;
import Controller_Components.ControllerRepo;
import Controller_Components.SampledDataCtrlr;
import AuthorizationRelated.IdTokenType;
import DataType.SampledValueType;
import DataType.TransactionType;
import AuthorizationRelated.AuthorizationStatusEnumType;
import EnumDataType.ChargingStateEnumType;
import EnumDataType.ConnectorStatusEnumType;
import EnumDataType.IdTokenEnumType;
import DisplayMessagesRelated.MessageStateEnumType;
import EnumDataType.ReadingContextEnumType;
import EnumDataType.ReasonEnumType;
import EnumDataType.TransactionEventEnumType;
import EnumDataType.TriggerReasonEnumType;
import PhysicalComponents.CSPhysicalProperties;
import UseCasesOCPP.SendRequestToCSMS;

import static java.lang.String.format;

public class ChargingDisplay extends AppCompatActivity implements PINauthorizeDialog.PINauthorizeDialogListener {
    ImageView BatteryCharge ;
    Button stopCharging;
    TextView voltage;
    TextView current ;
    TextView TimeSpent;
    TextView Charge ;
    TextView Miles ;
    TextView updatedCost ;
    TextView ChargingText ;
    TextView AfterSuspend ;
    TextView SuspendTimer ;
    TextView AfterStopButton ;
    Button WantToChargeMore ;
    Button Payment ;
    ProgressBar progressBar ;
    float SOC ;
    float Voltage = 0;
    float Current = 0;
    float Energy = 0 ;
    String currentsoc ;
    int counter = TxCtrlr.getEVConnectionTimeOut() ;
    int count = 0 ;
    boolean stopThread =false;
    boolean stopThread1 = false ;
    Handler mHandler = new Handler();
    SendRequestToCSMS toCSMS1 = new SendRequestToCSMS();
    MainActivity bs ;
    MyClientEndpoint myClientEndpoint ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_charging);

        Payment =  findViewById(R.id.paybutton);
        BatteryCharge = findViewById(R.id.imageViewcharge);
        voltage =  findViewById(R.id.voltage);
        current = findViewById(R.id.current);
        Charge =  findViewById(R.id.charge);
        Miles =  findViewById(R.id.miles);
        updatedCost =  findViewById(R.id.costupdated) ;
        stopCharging =  findViewById(R.id.stopbutton);
        WantToChargeMore =  findViewById(R.id.button4);
        ChargingText =  findViewById(R.id.chargingtext);
        AfterSuspend =  findViewById(R.id.aftersuspend);
        SuspendTimer =  findViewById(R.id.suspendtimer);
        TimeSpent =  findViewById(R.id.spent);
        AfterStopButton =  findViewById(R.id.textView14) ;
        progressBar = findViewById(R.id.progressBar1);

        Intent intent = getIntent();
        currentsoc = intent.getStringExtra("currentsoc");
        SOC = Float.parseFloat(currentsoc);
        new ImageChargeBattery(SOC, BatteryCharge);

        voltage.setText(R.string.initialzero); // 0.00
        current.setText(R.string.initialzero); // 0.00
        Charge.setText(currentsoc);
        ControllerRepo controllerRepo = new ControllerRepo(ChargingDisplay.this) ;
        updatedCost.setText(format("%s 0.00", controllerRepo.getController("TariffCostCtrlr","Currency").getvalue() ));

        SuspendTimer.setVisibility(View.GONE);
        AfterSuspend.setVisibility(View.GONE);
        WantToChargeMore.setVisibility(View.GONE);
        Payment.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        myClientEndpoint = MyClientEndpoint.getInstance() ;

        DisplayMessageState.setMessageState(MessageStateEnumType.Charging);

    }

    @Override
    protected void onStart() {
        super.onStart();
        BluetoothThreadMeter();
        TimerForTimeSpent();
        StartSendingMeterValues();
    }

    private Runnable sendTransReq = new Runnable() {
        @Override
        public void run() {

            try {
                TransactionEventRequest.eventType = TransactionEventEnumType.Updated ;
                TransactionEventRequest.triggerReason = TriggerReasonEnumType.MeterValuePeriodic ;
                TransactionType.chargingState = ChargingStateEnumType.Charging;
                SampledValueType.value = Energy ;
                SampledValueType.context = ReadingContextEnumType.SamplePeriodic ;
                send(toCSMS1.createTransactionEventRequest()) ;

                updatedCost.setText(String.format("%s %s", TariffCostCtrlr.getCurrency(), myClientEndpoint.getCostUpdated().getTotalCost()));
                mHandler.postDelayed(this,1000* SampledDataCtrlr.TxUpdatedInterval) ;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


    private void AfterChargingComplete()  {

        StopSendingMeterValues();
        ChargingStationStates.setEnergyTransfer(false);
        UpdateUiAfterStop();
        stopThread = true ;

        TransactionEventRequest.eventType = TransactionEventEnumType.Updated;
        TransactionEventRequest.triggerReason = TriggerReasonEnumType.MeterValuePeriodic;
        TransactionType.chargingState = ChargingStateEnumType.Charging;
        SampledValueType.value = Energy;
        SampledValueType.context = ReadingContextEnumType.TransactionEnd;
        try {
            send(toCSMS1.createTransactionEventRequest());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TransactionEventRequest.eventType = TransactionEventEnumType.Updated;
        TransactionEventRequest.triggerReason = TriggerReasonEnumType.ChargingStateChanged;
        TransactionType.chargingState = ChargingStateEnumType.SuspendedEVSE;
        try {
            send(toCSMS1.createTransactionEventRequest());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void UpdateUiAfterStop(){
        ImageSetBattery imageSetBattery = new ImageSetBattery(SOC,BatteryCharge);
        WantToChargeMore.setVisibility(View.VISIBLE);
        Payment.setVisibility(View.VISIBLE);
        stopCharging.setVisibility(View.GONE);
        voltage.setVisibility(View.GONE);
        current.setVisibility(View.GONE);
        ChargingText.setText(" CHARGING\n      Done!");
    }

    private void UpdateUiAfterSuspend(){

        ChargingText.setText(" CHARGING\nSuspended!");
        new ImageSetBattery(SOC,BatteryCharge);
        AfterSuspend.setVisibility(View.VISIBLE);

    }

    private void OnClickStop(View view )  {
        if(IdTokenType.type == IdTokenEnumType.KeyCode){
            openDialogPIN();
        }
        if(IdTokenType.type == IdTokenEnumType.ISO14443){
            openDialogRFID();
        }
        AfterChargingComplete();
    }

    public void openDialogPIN(){

        PINauthorizeDialog piNauthorizeDialog = new PINauthorizeDialog();

        piNauthorizeDialog.show(getSupportFragmentManager(),"3");
    }

    public void openDialogRFID(){


    }

    @Override
    public void applyTexts(final String s) {

        progressBar.setVisibility(View.VISIBLE);
        new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
                count-- ;
                if(count==4){
                    progressBar.setVisibility(View.GONE);
                    AfterStopButton.setText(s);
                }
            }
            @Override
            public void onFinish() {
                if(myClientEndpoint.getIdInfo().getStatus() != AuthorizationStatusEnumType.Accepted) {
                    AfterStopButton.setText("To stop charging\n first verify your IDTOKEN");
                }
                if(myClientEndpoint.getIdInfo().getStatus() == AuthorizationStatusEnumType.Accepted){
                    AfterStopButton.setVisibility(View.GONE);
                }
            }
        }.start();
    }

    public void OnClickPayment(View view){

    }

    public void OnClickWantToChargeMore(View view){

    }

    private void TimerForTimeSpent(){
        final Thread t = new Thread(){
            @Override
            public void run(){
                while(!isInterrupted() && !stopThread){
                    try {
                        Thread.sleep(1000);  //1000ms = 1 sec
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                count++;
                                TransactionType.timeSpentCharging = count ;
                                int hours = TransactionType.timeSpentCharging / 3600;
                                int minutes = (TransactionType.timeSpentCharging % 3600) / 60;
                                int seconds = TransactionType.timeSpentCharging % 60;
                                TimeSpent.setText(getString(R.string.timespent,hours, minutes, seconds));
                                if(getString(R.string.timespent, hours, minutes, seconds).equals(myClientEndpoint.getIdInfo().getCacheExpiryDateTime())){
                                    AfterChargingComplete();
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

    private void BluetoothThreadMeter() {

        if (bs.BTinit()) {
            if (bs.BTconnect()) {
                bs.deviceConnected = true;
                final Handler handler = new Handler();
                Thread thread = new Thread(new Runnable() {
                    public void run() {
                        while (!Thread.currentThread().isInterrupted() && !stopThread) {
                            try {
                                final String string  = "METER";
                                bs.outputStream.write(string.getBytes());

                                int byteCount = bs.inputStream.available();
                                if (byteCount > 0) {

                                    byte[] mmBuffer = new byte[1024];
                                    int numBytes; // bytes returned from read()
                                    numBytes = bs.inputStream.read(mmBuffer);
                                    final String variable = new String(mmBuffer, 0,numBytes ,"UTF-8");
                                    //variable = V-220-I-15.6-SOC-45.6-E-10.6-CPEV-T-EO-T;
                                    handler.post(new Runnable() {
                                        public void run() {

                                            if (ChargingStationStates.isEVSideCablePluggedIn && ChargingStationStates.isEnergyTransfer) {
                                                String[] output = variable.split("-");

                                                // Instantaneous DC or AC RMS supply voltage
                                                voltage.setText(output[1]);
                                                Voltage = Float.parseFloat(output[1]);

                                                // Instantaneous current flow to EV
                                                current.setText(output[3]);
                                                Current = Float.parseFloat(output[3]);

                                                // State of charge of charging vehicle in percentage
                                                Charge.setText(output[5]);
                                                SOC = Float.parseFloat(output[5]);
                                                if (SOC >= Target.SOC) {
                                                    AfterChargingComplete();
                                                }
                                                Energy = Float.parseFloat(output[7]);  // Energy in KWh

                                                if (output[9].equals("T")) {
                                                    ChargingStationStates.setCablePluggedIn(true);
                                                }
                                                if (output[9].equals("F")) {
                                                    ChargingStationStates.setCablePluggedIn(false);
                                                    try {
                                                        afterCableUnplugAtEVSide();

                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    } catch (EncodeException e) {
                                                        e.printStackTrace();
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                if (output[11].equals("T") ) {
                                                    ChargingStationStates.setEnergyTransfer(true);
                                                }
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

    private void StopSendingMeterValues(){
        mHandler.removeCallbacks(sendTransReq); // stop runnable
    }

    private void StartSendingMeterValues(){
        sendTransReq.run();
    }

    private void BluetoothThreadforCablePlug() {
        if (bs.BTinit()) {
            if (bs.BTconnect()) {
                bs.deviceConnected = true;
                Thread thread = new Thread(new Runnable() {
                    public void run() {
                        while (!Thread.currentThread().isInterrupted() && !stopThread1) {
                            try {
                                String string = "CPEV";
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
                                                afterSuspendCablePluggedAtEVSide();
                                                stopThread1 = true ;
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

    private void afterSuspendCablePluggedAtEVSide(){

        ChargingStationStates.setEnergyTransfer(true);
        stopThread = false;

        AfterSuspend.setVisibility(View.INVISIBLE);
        SuspendTimer.setVisibility(View.INVISIBLE);

        TimerForTimeSpent();

        TransactionEventRequest.eventType = TransactionEventEnumType.Updated;
        TransactionEventRequest.triggerReason = TriggerReasonEnumType.CablePluggedIn;
        TransactionType.chargingState = ChargingStateEnumType.Charging;
        try {
            toCSMS1.createTransactionEventRequest();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        BluetoothThreadMeter();

        StartSendingMeterValues();
    }



    private void afterCableUnplugAtEVSide() throws IOException, EncodeException, JSONException {

        ChargingStationStates.setEnergyTransfer(false);
        StopSendingMeterValues();
        stopThread = true ;

        if(!TxCtrlr.isStopTxOnEVSideDisconnect()){ // Suspend Transaction After CableUnplug at EV side

            UpdateUiAfterSuspend();

            TransactionEventRequest.eventType = TransactionEventEnumType.Updated;
            TransactionEventRequest.triggerReason = TriggerReasonEnumType.EVCommunicationLost;

            send(toCSMS1.createTransactionEventRequest());

            if(CSPhysicalProperties.isCableIsPermanentAttached) {

                new CountDownTimer(TxCtrlr.getEVConnectionTimeOut() * 1000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        int minutes = counter / 60;
                        int seconds = counter % 60;
                        SuspendTimer.setText(getString(R.string.timersuspend, minutes, seconds));
                        counter--;

                    }
                    public void onFinish() {
                        if (!ChargingStationStates.isEVSideCablePluggedIn) {
                            SuspendTimer.setText(getString(R.string.timersuspend, 0, 0));
                            TransactionEventRequest.eventType = TransactionEventEnumType.Ended;
                            TransactionEventRequest.triggerReason = TriggerReasonEnumType.EVCommunicationLost;
                            TransactionType.stoppedReason = ReasonEnumType.EVDisconnected;
                            try {
                                send(toCSMS1.createTransactionEventRequest());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            StatusNotificationRequest.setConnectorStatus(ConnectorStatusEnumType.Available);
                            try {
                                send(toCSMS1.createStatusNotificationRequest());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Intent i = new Intent(ChargingDisplay.this, MainActivity.class);
                            startActivity(i);

                        }
                    }

                }.start() ;
            }

            BluetoothThreadforCablePlug();

        }

        if(TxCtrlr.isStopTxOnEVSideDisconnect()){   // Stop Transaction After CableUnplug at EV side
            TransactionEventRequest.eventType = TransactionEventEnumType.Ended;
            TransactionEventRequest.triggerReason = TriggerReasonEnumType.EVCommunicationLost;
            TransactionType.stoppedReason = ReasonEnumType.EVDisconnected ;
            try {
                send(toCSMS1.createTransactionEventRequest());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            StatusNotificationRequest.setConnectorStatus(ConnectorStatusEnumType.Available);
            try {
                send(toCSMS1.createStatusNotificationRequest());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void send(final CALL call) {
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    myClientEndpoint.getOpenSession().getBasicRemote().sendObject(call);
                    Log.d("TAG" , "Message Sent" + CALL.getAction());
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








