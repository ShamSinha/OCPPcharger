package PhysicalComponents;

import android.content.Context;
import android.os.AsyncTask;

public class ChargingStationStatesRepo {
    private ChargingStationStatesDao chargingStationStatesDao ;

    public ChargingStationStatesRepo(Context context){
        ChargingStationStatesDatabase database = ChargingStationStatesDatabase.getInstance(context);
        chargingStationStatesDao = database.chargingStationStatesDao() ;
    }

    public void insert(ChargingStationStates chargingStationStates){
        new InsertChargingStationStatesAsyncTask(chargingStationStatesDao).execute(chargingStationStates);
    }

    public void updateEVSideCablePluggedIn(String transactionId , boolean state){

    }

    public void updateAuthorized(String transactionId , boolean state){

    }

    public void updateDataSigned(String transactionId , boolean state){

    }

    public void updatePowerPathClosed(String transactionId , boolean state){

    }
    public void updateEnergyTransfer(String transactionId , boolean state){

    }

    private static class InsertChargingStationStatesAsyncTask extends AsyncTask<ChargingStationStates,Void,Void> {
        private ChargingStationStatesDao chargingStationStatesDao ;

        private InsertChargingStationStatesAsyncTask(ChargingStationStatesDao chargingStationStatesDao){
            this.chargingStationStatesDao = chargingStationStatesDao ;
        }
        @Override
        protected Void doInBackground(ChargingStationStates... chargingStationStates) {
            chargingStationStatesDao.insert(chargingStationStates[0]);
            return null;
        }
    }

    

}
