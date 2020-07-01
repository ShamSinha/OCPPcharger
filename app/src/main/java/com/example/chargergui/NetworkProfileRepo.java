package com.example.chargergui;

import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class NetworkProfileRepo {
    private NetworkProfileDao networkProfileDao;


    public NetworkProfileRepo(Context context){
        NetworkProfileDatabase database = NetworkProfileDatabase.getInstance(context);
        networkProfileDao = database.networkProfileDao() ;
    }

    public void insert(final NetworkProfile networkProfile){
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            public void run() {
                networkProfileDao.insert(networkProfile);
            }
        });
        executorService.shutdown();
    }

    public NetworkProfile getNetworkProfile(int configurationSlot){
        return networkProfileDao.getNetworkProfile(configurationSlot) ;
    }
}
