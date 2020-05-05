package com.example.chargergui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import Controller_Components.AuthCtrlr;

public class WelcomeAndStart extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE) ;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome_and_start);
    }

    public void onClickStart(View view){
        if(AuthCtrlr.isEnabled()) {
            Intent i = new Intent(WelcomeAndStart.this, Authentication.class);
            startActivity(i);
        }
        else{
            Intent i = new Intent(WelcomeAndStart.this, CablePlugActivity.class);
            startActivity(i);
        }
    }
}
