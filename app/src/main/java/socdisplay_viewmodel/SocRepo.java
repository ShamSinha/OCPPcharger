package socdisplay_viewmodel;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class SocRepo {

    private SocDao socDao;
    private LiveData<List<SocEntities>> allSoc ;

    public SocRepo(Context context){
        SocDatabase database = SocDatabase.getInstance(context);
        socDao = database.socDao() ;
        allSoc = socDao.getAllSoc();
    }

    public void insert(SocEntities soc){
        new InsertSocAsyncTask(socDao).execute(soc);

    }
    public void update(SocEntities soc){
        new UpdateSocAsyncTask(socDao).execute(soc);
    }

    public LiveData<List<SocEntities>> getAllSoc(){
        return allSoc;
    }
    public void deleteAll(){
        new DeleteAllSocAsyncTask(socDao).execute();
    }

    private static class InsertSocAsyncTask extends AsyncTask<SocEntities,Void,Void> {
        private SocDao socDao;

        private InsertSocAsyncTask(SocDao socDao){
            this.socDao = socDao ;
        }
        @Override
        protected Void doInBackground(SocEntities... socs) {
            socDao.insert(socs[0]);
            return null;
        }
    }

    private static class UpdateSocAsyncTask extends AsyncTask<SocEntities,Void,Void>{
        private SocDao socDao;

        private UpdateSocAsyncTask(SocDao socDao){
            this.socDao = socDao ;
        }
        @Override
        protected Void doInBackground(SocEntities... socs) {
            socDao.update(socs[0]);
            return null;
        }
    }

    private static class DeleteAllSocAsyncTask extends AsyncTask<Void, Void, Void> {
        private SocDao socDao;

        private DeleteAllSocAsyncTask(SocDao socDao){
            this.socDao = socDao ;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            socDao.deleteAll();
            return null;
        }
    }



}

