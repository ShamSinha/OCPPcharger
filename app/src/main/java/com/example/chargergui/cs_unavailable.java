package com.example.chargergui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import DisplayMessagesRelated.MessageStateEnumType;

public class cs_unavailable extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cs_unavailable);

        DisplayMessageState.setMessageState(MessageStateEnumType.Unavailable);


        final Button PRICE = (Button) findViewById(R.id.price);
        final Button Location = (Button) findViewById(R.id.location);
        PRICE.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });
        Location.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });




    }
}
