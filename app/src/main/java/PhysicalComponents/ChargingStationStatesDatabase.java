package PhysicalComponents;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;



@Database(entities = {ChargingStationStates.class} , version = 1)
public abstract class ChargingStationStatesDatabase extends RoomDatabase {

    private static PhysicalComponents.ChargingStationStatesDatabase instance;

    public abstract ChargingStationStatesDao chargingStationStatesDao();

    public static synchronized PhysicalComponents.ChargingStationStatesDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), PhysicalComponents.ChargingStationStatesDatabase.class, "chargingStationStates_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(csStatesCallback)
                    .build();
        }
        return instance;
    }

    private static PhysicalComponents.ChargingStationStatesDatabase.Callback csStatesCallback = new PhysicalComponents.ChargingStationStatesDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PhysicalComponents.ChargingStationStatesDatabase.PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {

        private ChargingStationStatesDao chargingStationStatesDao;

        private PopulateDbAsyncTask(PhysicalComponents.ChargingStationStatesDatabase db) {
            chargingStationStatesDao = db.chargingStationStatesDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            return null;
        }
    }
}
