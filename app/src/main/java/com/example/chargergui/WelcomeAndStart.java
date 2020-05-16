package com.example.chargergui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import javax.websocket.EncodeException;

import ChargingStationResponse.SetDisplayMessageResponse;
import Controller_Components.AuthCtrlr;
import EnumDataType.DisplayMessageStatusEnumType;
import EnumDataType.MessageStateEnumType;
import UseCasesOCPP.IdTokenInfoType;
import UseCasesOCPP.MessageInfoType;

public class WelcomeAndStart extends Activity {
    MyClientEndpoint myClientEndpoint;
    MessageInfoType messageInfoType ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE) ;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome_and_start);
        myClientEndpoint = MyClientEndpoint.getInstance();
        messageInfoType = new MessageInfoType();

        DisplayMessageState.setMessageState(MessageStateEnumType.Idle);

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

    public void getRequest(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    if(myClientEndpoint.getisCALLarrived()){
                        if(CALL.getAction().equals("SetDisplayMessage")){
                            messageInfoType = myClientEndpoint.getMessageInfo();
                            if(messageInfoType.getState() != MessageStateEnumType.Idle) {
                                SetDisplayMessageResponse.setStatus(DisplayMessageStatusEnumType.NotSupportedState);
                            }
                            else{
                                SetDisplayMessageResponse.setStatus(DisplayMessageStatusEnumType.Accepted);
                                JSONObject responsePayload = new JSONObject();
                                try {
                                    responsePayload = SetDisplayMessageResponse.payload() ;
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                CALLRESULT callresult = new CALLRESULT(responsePayload);
                                try {
                                    myClientEndpoint.getOpenSession().getBasicRemote().sendObject(callresult);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (EncodeException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    }
                }
            }
        });
        thread.start();

    }
}
