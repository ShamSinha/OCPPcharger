package DisplayMessagesRelated;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;


@Database(entities = {MessageInfoEntity.MessageInfo.class} , version = 1)
public abstract class MessageInfoDatabase extends RoomDatabase {

    private static MessageInfoDatabase instance;

    public abstract MessageInfoDao messageInfoDao();

    public static synchronized MessageInfoDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), MessageInfoDatabase.class, "messageInfo_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

}
            