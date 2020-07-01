package ChargingStationDetails;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;



@Database(entities = {ChargingStationStates.class} , version = 1)
public abstract class ChargingStationStatesDatabase extends RoomDatabase {

    private static ChargingStationDetails.ChargingStationStatesDatabase instance;

    public abstract ChargingStationStatesDao chargingStationStatesDao();

    public static synchronized ChargingStationDetails.ChargingStationStatesDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), ChargingStationDetails.ChargingStationStatesDatabase.class, "chargingStationStates_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(csStatesCallback)
                    .build();
        }
        return instance;
    }

    private static ChargingStationDetails.ChargingStationStatesDatabase.Callback csStatesCallback = new ChargingStationDetails.ChargingStationStatesDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new ChargingStationDetails.ChargingStationStatesDatabase.PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {

        private ChargingStationStatesDao chargingStationStatesDao;

        private PopulateDbAsyncTask(ChargingStationDetails.ChargingStationStatesDatabase db) {
            chargingStationStatesDao = db.chargingStationStatesDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            return null;
        }
    }
}
