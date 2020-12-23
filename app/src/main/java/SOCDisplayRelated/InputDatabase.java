package SOCDisplayRelated;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {InputEntity.class} , version = 1)
public abstract class InputDatabase extends RoomDatabase {

    private static InputDatabase instance ;

    public abstract InputDao inputDao();

    public static synchronized InputDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context,InputDatabase.class,"input_database")
                    .allowMainThreadQueries()
                    .addCallback(inputCallback)
                    .build();
        }
        return instance ;
    }

    public static InputDatabase.Callback inputCallback = new InputDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    public static class PopulateDbAsyncTask extends AsyncTask<Void,Void,Void> {
        private InputDao inputDao ;
        private PopulateDbAsyncTask(InputDatabase db){
            inputDao = db.inputDao();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            inputDao.insert(new InputEntity(0,0));
            return null ;
        }

    }

}
