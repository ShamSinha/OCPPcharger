package SOCDisplayRelated;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocRepo {

    private SocDao socDao;
    private LiveData<SocEntity> Soc ;

    public SocRepo(Context context){
        SocDatabase database = SocDatabase.getInstance(context);
        socDao = database.socDao() ;
        Soc = socDao.getSoc();
    }

    public void insert(final SocEntity soc){
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            public void run() {
                socDao.insert(soc);
            }
        });
        executorService.shutdown();

    }
    public void update(final SocEntity soc){
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            public void run() {
                socDao.update(soc);
            }
        });
        executorService.shutdown();
    }

    public LiveData<SocEntity> getSoc(){
        return Soc;
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
    public boolean isEmptySocTable(){
        return socDao.getSize() == 0;
    }

}

