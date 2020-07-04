package com.example.chargergui;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebsocketRepo {

    private WebsocketDao websocketDao ;

    public WebsocketRepo(Context context){
        WebsocketDatabase websocketDatabase = WebsocketDatabase.getInstance(context);
        websocketDao = websocketDatabase.websocketDao();
    }

    public LiveData<Boolean> isCallResultArrived(String messageId){
        return websocketDao.isCallResultArrived(messageId) ;
    }

    public LiveData<Boolean> isCallResultSent(String messageId){
        return websocketDao.isCallResultSent(messageId) ;
    }

    public void insertCallSent(final WebsocketEntities.CallSent callSent){
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            public void run() {
                websocketDao.insertCallSent(callSent);
            }
        });
        executorService.shutdown();
    }

    public void insertCallArrived(final WebsocketEntities.CallArrived callArrived){
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            public void run() {
                websocketDao.insertCallArrived(callArrived);
            }
        });
        executorService.shutdown();
    }

    public void updateCallSent(final Boolean a , final String messageId){
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            public void run() {
                websocketDao.updateCallSent(a,messageId);
            }
        });
        executorService.shutdown();
    }

    public void updateCallArrived(final Boolean a , final String messageId){
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            public void run() {
                websocketDao.updateCallArrived(a,messageId);
            }
        });
        executorService.shutdown();
    }
}
