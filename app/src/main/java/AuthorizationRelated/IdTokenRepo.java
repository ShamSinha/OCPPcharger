package AuthorizationRelated;

import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IdTokenRepo {

    private IdTokenDao idTokenDao;

    public IdTokenRepo(Context context){
        IdTokenDatabase database = IdTokenDatabase.getInstance(context);
        idTokenDao = database.idTokenDao() ;
    }

    public void insertIdToken(final IdTokenEntities.IdToken idToken){
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            public void run() {
                idTokenDao.insertIdToken(idToken);
            }
        });
        executorService.shutdown();

    }
    public void updateIdToken(final IdTokenEntities.IdToken idToken){
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            public void run() {
                idTokenDao.updateIdToken(idToken);
            }
        });
        executorService.shutdown();
    }

    public void insertIdTokenInfo(final IdTokenEntities.IdTokenInfo idTokenInfo){
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            public void run() {
                idTokenDao.insertIdTokenInfo(idTokenInfo);
            }
        });
        executorService.shutdown();

    }
    public void updateIdTokenInfo(final IdTokenEntities.IdTokenInfo idTokenInfo){
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            public void run() {
                idTokenDao.updateIdTokenInfo(idTokenInfo);
            }
        });
        executorService.shutdown();
    }

    public IdTokenEntities.IdTokenAndInfo getIdTokenAndInfo(String transactionId){
        return idTokenDao.getIdTokenAndInfo(transactionId) ;
    }
    public void deleteIdToken(String transactionId){
         idTokenDao.deleteIdToken(transactionId);
    }
}
