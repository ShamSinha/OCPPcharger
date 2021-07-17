package com.example.chargergui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import Controller_Components.ControllerRepo;
import DisplayMessagesRelated.MessageInfoEntity;
import Hardware.GpioProcessor;
import Hardware.Permissions;
import Hardware.Utils;

public class WelcomeAndStart extends Activity {
    MyClientEndpoint myClientEndpoint;
    MessageInfoEntity messageInfoType ;
    ControllerRepo controllerRepo = new ControllerRepo(WelcomeAndStart.this) ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE) ;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome_and_start);
        myClientEndpoint = MyClientEndpoint.getInstance();
        messageInfoType = new MessageInfoEntity();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!Utils.rootAccess()){
            Log.d("TAG","No root access");
        }
        else{
            List<Integer> gpioList= new ArrayList<Integer>();
            gpioList.add(2);
            gpioList.add(3);
            Permissions.GivePermissionToGpio(gpioList);

            GpioProcessor gpioProcessor = new GpioProcessor();
            GpioProcessor.Gpio led = gpioProcessor.getPin2();
            led.out();
            led.high();
        }
    }

    public void onClickStart(View view){
        if(controllerRepo.getController("AuthCtrlr", "Enabled").getvalue().equals("true")) {
            Intent i = new Intent(WelcomeAndStart.this, Authentication.class);
            startActivity(i);
        }
        else{
            Intent i = new Intent(WelcomeAndStart.this, CablePlugActivity.class);
            startActivity(i);
        }
    }

}
