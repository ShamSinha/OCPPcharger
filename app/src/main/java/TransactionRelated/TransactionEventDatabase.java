package TransactionRelated;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;




@Database(entities = {TransactionEntities.TransactionEventRequest.class,TransactionEntities.TransactionEventResponse.class} , version = 1)
public abstract class TransactionEventDatabase extends RoomDatabase {

    private static TransactionEventDatabase instance;

    public abstract TransactionEventDao transactionEventDao();

    public static synchronized TransactionEventDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), TransactionEventDatabase.class, "transactionEvent_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(transactionEventCallback)
                    .build();
        }
        return instance;
    }

    private static TransactionEventDatabase.Callback transactionEventCallback = new TransactionEventDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new TransactionEventDatabase.PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {

        private TransactionEventDao transactionEventDao;

        private PopulateDbAsyncTask(TransactionEventDatabase db) {
            transactionEventDao = db.transactionEventDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            return null;
        }
    }

}

