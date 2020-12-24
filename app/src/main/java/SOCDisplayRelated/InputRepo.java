package SOCDisplayRelated;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InputRepo {

    private InputDao inputDao;
    private LiveData<InputEntity> Input ;

    public InputRepo(Context context){
        InputDatabase database = InputDatabase.getInstance(context);
        inputDao = database.inputDao() ;
        Input = inputDao.getInput();
    }

    public void insert(final InputEntity input){
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            public void run() {
                inputDao.insert(input);
            }
        });
        executorService.shutdown();

    }
    public void update(final InputEntity input){
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            public void run() {
                inputDao.update(input);
            }
        });
        executorService.shutdown();
    }

    public LiveData<InputEntity> getInput(){
        return Input;
    }

    public void deleteAll(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            public void run() {
                inputDao.deleteAll();
            }
        });
        executorService.shutdown();
    }

    public boolean isEmptyInputTable(){
        return inputDao.getSize() == 0;
    }

}