package socdisplay_viewmodel;


import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {SocEntities.class} , version = 1)
public abstract class SocDatabase extends RoomDatabase {

    private static SocDatabase instance ;

    public abstract SocDao socDao();

    public static synchronized SocDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context,SocDatabase.class,"soc_database")
                    .allowMainThreadQueries()
                    .addCallback(socCallback)
                    .build();
        }
        return instance ;
    }

    public static SocDatabase.Callback socCallback = new SocDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    public static class PopulateDbAsyncTask extends AsyncTask<Void,Void,Void> {
        private SocDao socDao ;
        private PopulateDbAsyncTask(SocDatabase db){
            socDao = db.socDao();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            socDao.insert(new SocEntities(0,0,"BatteryType"));
            return null ;
        }

    }

}
