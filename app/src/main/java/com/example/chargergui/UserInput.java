package com.example.chargergui;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;

import android.view.View;

import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import Controller_Components.ControllerRepo;
import DisplayMessagesRelated.MessageStateEnumType;
import ChargingRelated.ChargingDisplay;
import SOCDisplayRelated.SOCdisplay;


public class UserInput extends AppCompatActivity implements AmountDialog.AmountDialogListener , ChargeDialog.ChargeDialogListener  {

    TextView amountText;
    TextView chargeText ;
    Button startCharging ;
    TextView Rate ;
    TextView CurrentSOC ;
    String str ;
    String str2 ;
    String maximum_cost ;
    String currency ;
    ControllerRepo controllerRepo ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE) ;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user_input);

        DisplayMessageState.setMessageState(MessageStateEnumType.Idle);


        amountText = findViewById(R.id.textView7);
        chargeText = findViewById(R.id.textView8);
        startCharging = findViewById(R.id.startchargingbutton);
        Rate = findViewById(R.id.rate1);
        CurrentSOC = findViewById(R.id.soc);
        Intent i = getIntent();
        str = i.getStringExtra("ch");
        CurrentSOC.setText(String.format("Charge\n %s%%", str));
        str2 = i.getStringExtra("rate");
        Rate.setText(str2);
        startCharging.setEnabled(false);
        SOCdisplay soCdisplay = new SOCdisplay();
        maximum_cost = String.valueOf(estimatedcost(100,Float.parseFloat(str),soCdisplay.getBatteryCapacity()));

        final ImageButton AMOUNT = findViewById(R.id.imageButtonrupee);
        AMOUNT.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openDialog(maximum_cost);
            }
        });

        final ImageButton CHARGE = findViewById(R.id.imageButtonbattery);
        CHARGE.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openDialog2(str);
            }
        });

        startCharging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserInput.this , ChargingDisplay.class);
                intent.putExtra("currentsoc",str);
                startActivity(intent);
            }
        });



    }

    public void openDialog(String maximum_cost){
        AmountDialog amountDialog = new AmountDialog();
        Bundle data = new Bundle();//create bundle instance
        data.putString("maximumCost", maximum_cost );//put string to pass with a key value

        amountDialog.setArguments(data);//Set bundle data to fragment

        amountDialog.show(getSupportFragmentManager(),"");

    }

    public void openDialog2(String s){
        ChargeDialog chargeDialog = new ChargeDialog();
        Bundle data = new Bundle();//create bundle instance
        data.putString("currentsoc", s);//put string to pass with a key value

        chargeDialog.setArguments(data);//Set bundle data to fragment
        chargeDialog.show(getSupportFragmentManager(),"");


    }

    public void applyTexts(int i) {
        amountText.setText(String.format(getString(R.string.amo), TariffCostCtrlr.getCurrency() ,String.valueOf(i)));
        //float l = estimatedcharge(i) ;
        //chargeText.setText(String.format(getString(R.string.estimated_charge), String.valueOf(l)));

    }

    public void applyTexts2(float j) {
        chargeText.setText(String.format(String.valueOf(j)," % Charge"));
        //int cost = estimatedcost(j);
        //amounttext.setText(String.format(getString(R.string.amo), String.valueOf(cost)));

    }

    public void applyTextsinvalid() {
        chargeText.setText( "     Invalid Charge!\n Enter Valid Charge Value") ;

    }


    public float estimatedcharge(int amount, float currentsoc , int BatteryCapacity){  // in Wh
        float l ;
        l = 5 ;
        return l ;
    }
    public int estimatedcost(float targetsoc , float currentsoc , int BatteryCapacity){
        int cost ;
        cost = 500 ;
        return cost ;

    }





}