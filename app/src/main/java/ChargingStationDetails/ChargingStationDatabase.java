package ChargingStationDetails;


import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {ChargingStation.class} , version = 1)
public abstract class ChargingStationDatabase extends RoomDatabase {

    private static ChargingStationDetails.ChargingStationDatabase instance;
    public abstract ChargingStationDao chargingStationDao();

    public static synchronized ChargingStationDetails.ChargingStationDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), ChargingStationDetails.ChargingStationDatabase.class, "chargingStationStates_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(csStatesCallback)
                    .build();
        }
        return instance;
    }

    private static ChargingStationDetails.ChargingStationDatabase.Callback csStatesCallback = new ChargingStationDetails.ChargingStationDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new ChargingStationDetails.ChargingStationDatabase.PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {

        private ChargingStationDao chargingStationDao;

        private PopulateDbAsyncTask(ChargingStationDetails.ChargingStationDatabase db) {
            chargingStationDao = db.chargingStationDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            chargingStationDao.insert(new ChargingStation("","SingleSocketCharger","VendorX","1",""));
            return null;
        }
    }

}
