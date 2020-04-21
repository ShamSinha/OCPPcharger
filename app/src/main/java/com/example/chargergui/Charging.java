package com.example.chargergui;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;

import javax.websocket.EncodeException;

import ChargingStationRequest.TransactionEventRequest;
import Controller_Components.SampledDataCtrlr;
import Controller_Components.TariffCostCtrlr;
import DataType.SampledValueType;
import DataType.TransactionType;
import EnumDataType.ChargingStateEnumType;
import EnumDataType.ReadingContextEnumType;
import EnumDataType.TransactionEventEnumType;
import EnumDataType.TriggerReasonEnumType;
import UseCasesOCPP.SendRequestToCSMS;

import static java.lang.String.*;

public class Charging extends AppCompatActivity {
    ImageView BatteryCharge ;
    Button stopCharging;
    TextView voltage;
    TextView current ;
    TextView TimeSpent;
    TextView Charge ;
    TextView Miles ;
    TextView updatedCost ;
    Button WantToChargeMore ;
    Button Payment ;
    float SOC ;
    float Voltage = 0;
    float Current = 0;
    float Energy = 0 ;
    String currentsoc ;
    Handler mHandler = new Handler();
    int count = 0 ;
    SendRequestToCSMS toCSMS1 = new SendRequestToCSMS();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charging);
        Payment = (Button) findViewById(R.id.paybutton);
        BatteryCharge = (ImageView) findViewById(R.id.imageViewcharge);
        Intent intent = getIntent();
        currentsoc = intent.getStringExtra("currentsoc");
        SOC = Float.parseFloat(currentsoc);
        ImageChargeBattery imageChargeBattery = new ImageChargeBattery(SOC, BatteryCharge);

        voltage = (TextView) findViewById(R.id.voltage);
        current = (TextView) findViewById(R.id.current);
        Charge = (TextView) findViewById(R.id.charge);
        Miles = (TextView) findViewById(R.id.miles);
        updatedCost = (TextView) findViewById(R.id.costupdated) ;


        voltage.setText(R.string.initialzero); // 0.00
        current.setText(R.string.initialzero); // 0.00
        Charge.setText(currentsoc);
        updatedCost.setText(format("%s 0.00", TariffCostCtrlr.Currency));



        TimeSpent = (TextView) findViewById(R.id.spent);
        WantToChargeMore.setVisibility(View.INVISIBLE);
        Payment.setVisibility(View.INVISIBLE);
        stopCharging = (Button) findViewById(R.id.stopbutton);
        WantToChargeMore = (Button) findViewById(R.id.button4);

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
                toCSMS1.sendTransactionEventRequest() ;
                mHandler.postDelayed(this,1000* SampledDataCtrlr.TxUpdatedInterval) ;
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (EncodeException e) {
                e.printStackTrace();
            }
        }
    };


    public void AfterChargingComplete(){

        if(SOC >= Target.SOC){

            TransactionEventRequest.eventType = TransactionEventEnumType.Updated ;
            TransactionEventRequest.triggerReason = TriggerReasonEnumType.MeterValuePeriodic ;
            TransactionType.chargingState = ChargingStateEnumType.Charging;
            SampledValueType.value = Energy ;
            SampledValueType.context = ReadingContextEnumType.TransactionEnd ;
            try {
                toCSMS1.sendTransactionEventRequest();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (EncodeException e) {
                e.printStackTrace();
            }

            StopAtFinish();

            try {
                TransactionEventRequest.eventType = TransactionEventEnumType.Updated ;
                TransactionEventRequest.triggerReason = TriggerReasonEnumType.ChargingStateChanged ;
                TransactionType.chargingState = ChargingStateEnumType.SuspendedEVSE;
                toCSMS1.sendTransactionEventRequest();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (EncodeException e) {
                e.printStackTrace();
            }
        }
    }

    public void StopAtFinish(){
        ChargingStationStates.setEnergyTransfer(false);
        ImageSetBattery imageSetBattery = new ImageSetBattery(SOC,BatteryCharge);
        WantToChargeMore.setVisibility(View.VISIBLE);
        Payment.setVisibility(View.VISIBLE);
        stopCharging.setVisibility(View.GONE);
        voltage.setVisibility(View.GONE);
        current.setVisibility(View.GONE);
        StopSendingMeterValues();
    }

    public void OnClickStop(View view ) throws IOException, EncodeException, JSONException {
        ChargingStationStates.setEnergyTransfer(false);
        ImageSetBattery imageSetBattery = new ImageSetBattery(SOC,BatteryCharge);
        WantToChargeMore.setVisibility(View.VISIBLE);
        Payment.setVisibility(View.VISIBLE);
        stopCharging.setVisibility(View.GONE);
        voltage.setVisibility(View.GONE);
        current.setVisibility(View.GONE);
        StopSendingMeterValues();


        TransactionEventRequest.eventType = TransactionEventEnumType.Updated ;
        TransactionEventRequest.triggerReason = TriggerReasonEnumType.MeterValuePeriodic ;
        TransactionType.chargingState = ChargingStateEnumType.Charging;
        SampledValueType.value = Energy ;
        SampledValueType.context = ReadingContextEnumType.TransactionEnd ;
        try {
            toCSMS1.sendTransactionEventRequest();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (EncodeException e) {
            e.printStackTrace();
        }

        TransactionEventRequest.eventType = TransactionEventEnumType.Updated ;
        TransactionEventRequest.triggerReason = TriggerReasonEnumType.ChargingStateChanged ;
        TransactionType.chargingState = ChargingStateEnumType.SuspendedEVSE;
        toCSMS1.sendTransactionEventRequest();

    }


    public void TimerForTimeSpent(){
        Thread t = new Thread(){
            @Override
            public void run(){
                while(!isInterrupted()){
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
                                if(!ChargingStationStates.isEnergyTransfer){
                                    interrupt();
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

    public void BluetoothThreadMeter() {
        final MainActivity bs = new MainActivity();

        if (bs.BTinit()) {
            if (bs.BTconnect()) {
                bs.deviceConnected = true;
                final Handler handler = new Handler();
                Thread thread = new Thread(new Runnable() {
                    public void run() {
                        boolean stopThread;
                        stopThread = false;
                        while (!Thread.currentThread().isInterrupted() && !stopThread) {
                            try {
                                final String string  = "GetMeterValue";
                                bs.outputStream.write(string.getBytes());

                                int byteCount = bs.inputStream.available();
                                if (byteCount > 0) {

                                    byte[] mmBuffer = new byte[1024];
                                    int numBytes; // bytes returned from read()
                                    numBytes = bs.inputStream.read(mmBuffer);
                                    final String variable = new String(mmBuffer, 0,numBytes ,"UTF-8");
                                    //variable = V-220-I-15.6-SOC-45.6-E-10.6;
                                    handler.post(new Runnable() {
                                        public void run() {

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
                                                AfterChargingComplete();

                                              // Energy in KWh
                                                Energy = Float.parseFloat(output[7]) ;


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

    public void StopSendingMeterValues(){
        mHandler.removeCallbacks(sendTransReq); // stop runnable
    }

    public void StartSendingMeterValues(){
        sendTransReq.run();
    }







}
