package com.example.chargergui;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.chargergui.WebsocketDao;
import com.example.chargergui.WebsocketEntities;


@Database(entities = {WebsocketEntities.CallSent.class, WebsocketEntities.CallArrived.class} , version = 1)
public abstract class WebsocketDatabase extends RoomDatabase {


    private static WebsocketDatabase instance ;

    public abstract WebsocketDao websocketDao();

    public static synchronized WebsocketDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(), WebsocketDatabase.class,"websocket_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(websocketCallback)
                    .build();
        }
        return instance ;
    }

    private static WebsocketDatabase.Callback websocketCallback = new WebsocketDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new WebsocketDatabase.PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void,Void,Void> {

        private WebsocketDao websocketDao ;
        private PopulateDbAsyncTask(WebsocketDatabase db){
            websocketDao = db.websocketDao();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            return null ;
        }
    }

}
