package AuthorizationRelated;

import android.content.Context;
import android.os.AsyncTask;

public class IdTokenInfoRepo {
    private IdTokenInfoDao idTokenInfoDao;

    public IdTokenInfoRepo(Context context){
        IdTokenInfoDatabase database = IdTokenInfoDatabase.getInstance(context);
        idTokenInfoDao = database.idTokenInfoDao() ;
    }

    public void insert(IdTokenInfoType.IdTokenInfo idTokenInfo){
        new AuthorizationRelated.IdTokenInfoRepo.InsertIdTokenInfoAsyncTask(idTokenInfoDao).execute(idTokenInfo);

    }
    public void update(IdTokenInfoType.IdTokenInfo idTokenInfo){
        new AuthorizationRelated.IdTokenInfoRepo.UpdateIdTokenInfoAsyncTask(idTokenInfoDao).execute(idTokenInfo);
    }

    public IdTokenInfoType.IdTokenInfo getIdTokenInfo(){
        return idTokenInfoDao.getIdTokenInfo() ;
    }



    private static class InsertIdTokenInfoAsyncTask extends AsyncTask<IdTokenInfoType.IdTokenInfo,Void,Void> {
        private IdTokenInfoDao idTokenInfoDao;

        private InsertIdTokenInfoAsyncTask(IdTokenInfoDao idTokenInfoDao){
            this.idTokenInfoDao = idTokenInfoDao ;
        }
        @Override
        protected Void doInBackground(IdTokenInfoType.IdTokenInfo... idTokenInfos) {
            idTokenInfoDao.insert(idTokenInfos[0]);
            return null;
        }
    }

    private static class UpdateIdTokenInfoAsyncTask extends AsyncTask<IdTokenInfoType.IdTokenInfo,Void,Void>{
        private IdTokenInfoDao idTokenInfoDao;

        private UpdateIdTokenInfoAsyncTask(IdTokenInfoDao idTokenInfoDao){
            this.idTokenInfoDao = idTokenInfoDao ;
        }
        @Override
        protected Void doInBackground(IdTokenInfoType.IdTokenInfo... idTokenInfos) {
            idTokenInfoDao.update(idTokenInfos[0]);
            return null;
        }
    }



}
