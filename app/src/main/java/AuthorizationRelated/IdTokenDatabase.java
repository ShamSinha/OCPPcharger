package AuthorizationRelated;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;



@Database(entities = {IdTokenEntities.IdTokenInfo.class, IdTokenEntities.IdToken.class} , version = 1)
public abstract class IdTokenDatabase extends RoomDatabase {

    private static IdTokenDatabase instance;

    public abstract IdTokenDao idTokenDao();

    public static synchronized IdTokenDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), IdTokenDatabase.class, "idToken_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(idTokenCallback)
                    .build();
        }
        return instance;
    }

    private static IdTokenDatabase.Callback idTokenCallback = new IdTokenDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new IdTokenDatabase.PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {

        private IdTokenDao idTokenDao;

        private PopulateDbAsyncTask(IdTokenDatabase db) {
            idTokenDao = db.idTokenDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            return null;
        }
    }
}