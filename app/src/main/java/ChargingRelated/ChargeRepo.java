package ChargingRelated;

import android.content.Context;
import android.os.AsyncTask;

public class ChargeRepo {
    
    private ChargeDao chargeDao;
    
    public ChargeRepo(Context context){
        ChargeDatabase database = ChargeDatabase.getInstance(context);
        chargeDao = database.ChargeDao() ;
    }

    public void insertCharge(ChargeEntity.Charging charging){
        new ChargeRepo.InsertChargingAsyncTask(chargeDao).execute(charging);

    }
    public void updateCharge(ChargeEntity.Charging charging){
        new ChargeRepo.UpdateChargingAsyncTask(chargeDao).execute(charging);
    }

    public ChargeEntity.Charging getCharge(String transactionId){
        return chargeDao.getCharge(transactionId) ;
    }

    public void insertCost(ChargeEntity.Cost cost){
        new ChargeRepo.InsertCostAsyncTask(chargeDao).execute(cost);

    }
    public void updateCost(ChargeEntity.Cost cost){
        new ChargeRepo.UpdateCostAsyncTask(chargeDao).execute(cost);
    }

    public ChargeEntity.Cost getCost(String transactionId){
        return chargeDao.getCost(transactionId) ;
    }

    private static class InsertChargingAsyncTask extends AsyncTask<ChargeEntity.Charging,Void,Void> {
        private ChargeDao chargeDao;

        private InsertChargingAsyncTask(ChargeDao chargeDao){
            this.chargeDao = chargeDao ;
        }
        @Override
        protected Void doInBackground(ChargeEntity.Charging... Charges) {
            chargeDao.insertCharge(Charges[0]);
            return null;
        }
    }

    private static class UpdateChargingAsyncTask extends AsyncTask<ChargeEntity.Charging,Void,Void>{
        private ChargeDao chargeDao;

        private UpdateChargingAsyncTask(ChargeDao chargeDao){
            this.chargeDao = chargeDao ;
        }
        @Override
        protected Void doInBackground(ChargeEntity.Charging... Charges) {
            chargeDao.updateCharge(Charges[0]);
            return null;
        }
    }

    private static class InsertCostAsyncTask extends AsyncTask<ChargeEntity.Cost,Void,Void> {
        private ChargeDao chargeDao;

        private InsertCostAsyncTask(ChargeDao chargeDao){
            this.chargeDao = chargeDao ;
        }
        @Override
        protected Void doInBackground(ChargeEntity.Cost... costs) {
            chargeDao.insertCost(costs[0]);
            return null;
        }
    }

    private static class UpdateCostAsyncTask extends AsyncTask<ChargeEntity.Cost,Void,Void>{
        private ChargeDao chargeDao;

        private UpdateCostAsyncTask(ChargeDao chargeDao){
            this.chargeDao = chargeDao ;
        }
        @Override
        protected Void doInBackground(ChargeEntity.Cost...costs) {
            chargeDao.updateCost(costs[0]);
            return null;
        }
    }

}
