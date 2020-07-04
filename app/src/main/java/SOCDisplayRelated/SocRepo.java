package SOCDisplayRelated;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocRepo {

    private SocDao socDao;
    private LiveData<List<SocEntities>> allSoc ;

    public SocRepo(Context context){
        SocDatabase database = SocDatabase.getInstance(context);
        socDao = database.socDao() ;
        allSoc = socDao.getAllSoc();
    }

    public void insert(final SocEntities soc){
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            public void run() {
                socDao.insert(soc);
            }
        });
        executorService.shutdown();

    }
    public void update(final SocEntities soc){
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            public void run() {
                socDao.update(soc);
            }
        });
        executorService.shutdown();
    }

    public LiveData<List<SocEntities>> getAllSoc(){
        return allSoc;
    }
    public void deleteAll(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            public void run() {
                socDao.deleteAll();
            }
        });
        executorService.shutdown();
    }

}

