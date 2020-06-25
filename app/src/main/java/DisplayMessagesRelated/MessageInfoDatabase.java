package DisplayMessagesRelated;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;


@Database(entities = {MessageInfoType.MessageInfo.class} , version = 1)
public abstract class MessageInfoDatabase extends RoomDatabase {

    private static MessageInfoDatabase instance;

    public abstract MessageInfoDao messageInfoDao();

    public static synchronized MessageInfoDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), MessageInfoDatabase.class, "messageInfo_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(messageInfoCallback)
                    .build();
        }
        return instance;
    }

    private static MessageInfoDatabase.Callback messageInfoCallback = new MessageInfoDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new MessageInfoDatabase.PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {

        private MessageInfoDao messageInfoDao;

        private PopulateDbAsyncTask(MessageInfoDatabase db) {
            messageInfoDao = db.messageInfoDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            return null;
        }
    }
}
            