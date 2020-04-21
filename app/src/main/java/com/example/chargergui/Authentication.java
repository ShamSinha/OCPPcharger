package com.example.chargergui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class Authentication extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

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
