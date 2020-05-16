package com.example.chargergui;


import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.TextView ;
import android.view.View ;

import java.io.IOException;

import Controller_Components.TariffCostCtrlr;
import EnumDataType.MessageStateEnumType;

public class SOCdisplay extends Activity {
    TextView Batterytype;
    TextView Charge ;
    TextView EnergyinKWh ;
    TextView dateTime ;
    TextView Recommendation ;
    Button Standard ;
    Button Fast;
    Button Semi_Fast;
    ImageView battery ;
    String chargeValue ;
    public float standardrate = 4;
    public float semifastrate = 5;
    public float fastrate = 8;

    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE) ;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_socdisplay);
        Batterytype = findViewById(R.id.batterytype);
        Charge = findViewById(R.id.charge);
        EnergyinKWh = findViewById(R.id.energy);
        battery = findViewById(R.id.tickorcrossIMAGE);

        Recommendation = findViewById(R.id.recommend);
        Standard = findViewById(R.id.standard);
        Semi_Fast = findViewById(R.id.semi);
        Fast = findViewById(R.id.fast);

        DisplayMessageState.setMessageState(MessageStateEnumType.Idle);


    }

    @Override
    protected void onStart() {
        super.onStart();
        Recommendation.setText(R.string.hh);
        BluetoothThread();
    }

    public void onClickStandard(View view) {

        Intent i = new Intent(SOCdisplay.this, UserInput.class);
        i.putExtra("ch", chargeValue);
        i.putExtra("rate",String.format("STANDARD CHARGING\n "+ TariffCostCtrlr.getCurrency() + " %s/KWh",String.valueOf(standardrate)));
        // currentContext.startActivity(activityChangeIntent);
        startActivity(i);
    }

    public void onClickSemi(View view) {
        Intent i = new Intent(SOCdisplay.this, UserInput.class);
        i.putExtra("ch", chargeValue);
        i.putExtra("rate",String.format("SEMI-FAST CHARGING\n "+ TariffCostCtrlr.getCurrency() + " %s/KWh",String.valueOf(semifastrate)));

        // currentContext.startActivity(activityChangeIntent);
        startActivity(i);
    }

    public void onClickFast(View view) {
        Intent i = new Intent(SOCdisplay.this, UserInput.class);
        i.putExtra("ch", chargeValue);
        i.putExtra("rate",String.format("FAST CHARGING\n "+ TariffCostCtrlr.getCurrency() + " %s/KWh",String.valueOf(fastrate)));

        // currentContext.startActivity(activityChangeIntent);
        startActivity(i);
    }



    public int getBatteryCapacity(){
        return 60000; //Wh
    }

    public String getEnergy(float soc , int BatteryCapacity){
        float Energy = (soc*BatteryCapacity)/100 ;
        return String.valueOf(Energy) ;
    }

    public void BluetoothThread() {
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
                                String string = "currentSOC";
                                bs.outputStream.write(string.getBytes());

                                int byteCount = bs.inputStream.available();
                                if (byteCount > 0) {
                                    byte[] mmBuffer = new byte[1024];
                                    int numBytes; // bytes returned from read()
                                    numBytes = bs.inputStream.read(mmBuffer);
                                    final String ch = new String(mmBuffer,0,numBytes,"UTF-8");
                                    handler.post(new Runnable() {
                                        public void run() {
                                            if( Float.parseFloat(ch) <= 100 && Float.parseFloat(ch) >= 0) {
                                                chargeValue = ch;

                                                ImageSetBattery imagesetBattery = new ImageSetBattery(Double.parseDouble(chargeValue), battery);

                                                String e = getEnergy(Float.parseFloat(chargeValue), getBatteryCapacity());

                                                EnergyinKWh.setText(e);

                                                Charge.setText(chargeValue);

                                                Thread.currentThread().interrupt();
                                            }

                                        }
                                    });

                                }
                            }
                            catch (IOException ex) {
                                stopThread = true;
                            }
                        }
                    }
                });

                thread.start();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}
