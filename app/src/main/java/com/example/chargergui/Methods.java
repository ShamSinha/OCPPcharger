package com.example.chargergui;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.websocket.EncodeException;

public class Methods {

    public void sendResponse(final CALLRESULT callresult) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            public void run() {
                try {
                    MyClientEndpoint.getInstance().getOpenSession().getBasicRemote().sendObject(callresult);
                    Log.d("TAG", "Message Sent: " + CALL.getAction() + callresult.getPayload());

                } catch (IOException | EncodeException e) {
                    Log.e("ERROR", "IOException in BasicRemote");
                    e.printStackTrace();
                }
            }
        });
        executorService.shutdown();
    }

    public void sendRequest(final CALL call) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    MyClientEndpoint.getInstance().getOpenSession().getBasicRemote().sendObject(call);
                    Log.d("TAG", "Message Sent: " + CALL.getAction() + call.getPayload());

                } catch (IOException | EncodeException e) {
                    Log.e("ERROR", "IOException in BasicRemote");
                    e.printStackTrace();
                }
            }
        });
        executorService.shutdown();
    }


}
