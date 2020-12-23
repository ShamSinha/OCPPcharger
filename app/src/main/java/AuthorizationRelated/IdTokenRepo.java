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

    public void insertIdToken(final IdTokenEntity idToken){
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            public void run() {
                idTokenDao.insertIdToken(idToken);
            }
        });
        executorService.shutdown();

    }
    public void updateIdToken(final IdTokenEntity idToken){
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            public void run() {
                idTokenDao.updateIdToken(idToken);
            }
        });
        executorService.shutdown();
    }

    public void insertIdTokenInfo(final IdTokenInfoEntity idTokenInfo){
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            public void run() {
                idTokenDao.insertIdTokenInfo(idTokenInfo);
            }
        });
        executorService.shutdown();

    }
    public void updateIdTokenInfo(final IdTokenInfoEntity idTokenInfo){
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            public void run() {
                idTokenDao.updateIdTokenInfo(idTokenInfo);
            }
        });
        executorService.shutdown();
    }

    public void deleteIdToken(){
         idTokenDao.deleteIdToken();
    }
    public void deleteIdTokenInfo(){
        idTokenDao.deleteIdTokenInfo();
    }

    public IdTokenEntity getIdToken(){
        return idTokenDao.getIdToken();
    }

    public IdTokenInfoEntity getIdTokenInfo(){
        return idTokenDao.getIdTokenInfo();
    }
}
