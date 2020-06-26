package ChargingRelated;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {ChargeEntity.Charging.class,ChargeEntity.Cost.class} , version = 1)

public abstract class ChargeDatabase extends RoomDatabase {
    private static ChargingRelated.ChargeDatabase instance ;

    public abstract ChargeDao ChargeDao();

    public static synchronized ChargingRelated.ChargeDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(), ChargingRelated.ChargeDatabase.class,"charge_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(chargeCallback)
                    .build();
        }
        return instance ;
    }

    private static ChargingRelated.ChargeDatabase.Callback chargeCallback = new ChargingRelated.ChargeDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new ChargingRelated.ChargeDatabase.PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void,Void,Void> {

        private ChargeDao chargeDao ;
        private PopulateDbAsyncTask(ChargingRelated.ChargeDatabase db){
            chargeDao = db.ChargeDao();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            return null ;
        }
    }

}