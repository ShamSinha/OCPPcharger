package charging_viewmodel;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;


import charging_viewmodel.ChargeDao;

@Database(entities = {ChargeEntity.Charging.class,ChargeEntity.Cost.class} , version = 1)

public abstract class ChargeDatabase extends RoomDatabase {
    private static charging_viewmodel.ChargeDatabase instance ;

    public abstract ChargeDao ChargeDao();

    public static synchronized charging_viewmodel.ChargeDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(), charging_viewmodel.ChargeDatabase.class,"charge_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(chargeCallback)
                    .build();
        }
        return instance ;
    }

    private static charging_viewmodel.ChargeDatabase.Callback chargeCallback = new charging_viewmodel.ChargeDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new charging_viewmodel.ChargeDatabase.PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void,Void,Void> {

        private ChargeDao chargeDao ;
        private PopulateDbAsyncTask(charging_viewmodel.ChargeDatabase db){
            chargeDao = db.ChargeDao();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            return null ;
        }
    }

}