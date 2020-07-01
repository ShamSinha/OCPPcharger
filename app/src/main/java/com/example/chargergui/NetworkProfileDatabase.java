package com.example.chargergui;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import EnumDataType.OCPPInterfaceEnumType;
import EnumDataType.OCPPTransportEnumType;
import EnumDataType.OCPPVersionEnumType;


@Database(entities = {NetworkProfile.class} , version = 1)
public abstract class NetworkProfileDatabase extends RoomDatabase {

    private static NetworkProfileDatabase instance ;

    public abstract NetworkProfileDao networkProfileDao();

    public static synchronized NetworkProfileDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),NetworkProfileDatabase.class,"networkProfile_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(networkProfileCallback)
                    .build();
        }
        return instance ;
    }

    private static NetworkProfileDatabase.Callback networkProfileCallback = new NetworkProfileDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            new NetworkProfileDatabase.PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void,Void,Void> {

        private NetworkProfileDao networkProfileDao;

        private PopulateDbAsyncTask(NetworkProfileDatabase db) {
            networkProfileDao = db.networkProfileDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            NetworkProfile.NetworkConnectionProfileType connectionData ;
            connectionData = new NetworkProfile.NetworkConnectionProfileType(OCPPVersionEnumType.OCPP20.name(), OCPPTransportEnumType.JSON.name(),"",30, OCPPInterfaceEnumType.Wireless0.name());

            NetworkProfile networkProfile = new NetworkProfile(connectionData);
            networkProfile.setConfigurationSlot(1);
            networkProfileDao.insert(networkProfile);
            return null;
        }
    }
    
    
    
    
    
    
    
}
