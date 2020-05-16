package com.example.chargergui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import EnumDataType.MessageStateEnumType;

public class Authentication extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE) ;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_authentication);

        DisplayMessageState.setMessageState(MessageStateEnumType.Idle);


    }
    public void OnClickPIN(View view) {
        Intent i = new Intent(Authentication.this, Authorization1.class);
        startActivity(i);
    }

    public void OnClickRFID(View view) {
        Intent i = new Intent(Authentication.this, Authorization2.class);
        startActivity(i);
    }





}
