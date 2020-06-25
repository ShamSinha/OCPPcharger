package AuthorizationRelated;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;



@Database(entities = {IdTokenInfoEntity.IdTokenInfo.class} , version = 1)
public abstract class IdTokenInfoDatabase extends RoomDatabase {

    private static AuthorizationRelated.IdTokenInfoDatabase instance;

    public abstract IdTokenInfoDao idTokenInfoDao();

    public static synchronized AuthorizationRelated.IdTokenInfoDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), AuthorizationRelated.IdTokenInfoDatabase.class, "idTokenInfo_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(idTokenInfoCallback)
                    .build();
        }
        return instance;
    }

    private static AuthorizationRelated.IdTokenInfoDatabase.Callback idTokenInfoCallback = new AuthorizationRelated.IdTokenInfoDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new AuthorizationRelated.IdTokenInfoDatabase.PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {

        private IdTokenInfoDao idTokenInfoDao;

        private PopulateDbAsyncTask(AuthorizationRelated.IdTokenInfoDatabase db) {
            idTokenInfoDao = db.idTokenInfoDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            return null;
        }
    }
}