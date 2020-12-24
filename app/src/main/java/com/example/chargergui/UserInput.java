package com.example.chargergui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;


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
    Button reset;
    Button fullCharge;
    ImageButton AMOUNT ;
    ImageButton CHARGE ;
    TextView Rate ;
    TextView CurrentSOC ;


    private UserInputViewModel inputViewModel;

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
        reset = findViewById(R.id.resetInput);
        fullCharge = findViewById(R.id.full);
        AMOUNT = findViewById(R.id.imageButtonrupee);
        CHARGE = findViewById(R.id.imageButtonbattery);
        Rate = findViewById(R.id.rate1);
        CurrentSOC = findViewById(R.id.soc);

        startCharging.setEnabled(false);

        inputViewModel = new ViewModelProvider(this).get(UserInputViewModel.class);
    }

    public void OnclickBatteryImage(View view){
        openChargeDialog(inputViewModel.getSoc());
    }

    public void OnclickAmountImage(View view){
        openAmountDialog(inputViewModel.getMaximumValidAmount());
    }

    public void OnclickStart(View view){
        Intent i = new Intent(UserInput.this , ChargingDisplay.class);
        startActivity(i);
    }

    public void OnclickReset(View view){
        inputViewModel.reset();
    }

    public void OnclickFull(View view){

    }

    public void openAmountDialog(int maximum_amount){
        AmountDialog amountDialog = new AmountDialog();
        Bundle data = new Bundle();//create bundle instance
        data.putInt("MAX_COST", maximum_amount);

        amountDialog.setArguments(data);//Set bundle data to fragment
        amountDialog.show(getSupportFragmentManager(),"");
    }

    public void openChargeDialog(double InitialSOC){
        ChargeDialog chargeDialog = new ChargeDialog();
        Bundle data = new Bundle();//create bundle instance
        data.putDouble("Initial_SOC", InitialSOC);

        chargeDialog.setArguments(data);//Set bundle data to fragment
        chargeDialog.show(getSupportFragmentManager(),"");
    }

    public void applyTextAmount(int i) {
        amountText.setText(String.format(getString(R.string.amo), inputViewModel.getCurrency() ,String.valueOf(i)));
    }

    public void applyTextCharge(double j) {
        chargeText.setText(String.format(String.valueOf(j)," % Charge"));
    }

    public void applyTextInvalid() {
        chargeText.setText( "     Invalid Charge!\n Enter Valid Charge Value") ;
    }







}