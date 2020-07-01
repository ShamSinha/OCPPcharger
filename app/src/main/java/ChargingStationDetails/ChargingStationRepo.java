package ChargingStationDetails;

import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChargingStationRepo {

    private ChargingStationDao chargingStationDao ;

    public ChargingStationRepo(Context context){
        ChargingStationDatabase database = ChargingStationDatabase.getInstance(context);
        chargingStationDao = database.chargingStationDao() ;
    }

    public void updateFirmware(final String firmwareVersion){
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            public void run() {
                chargingStationDao.update(firmwareVersion);
            }
        });
        executorService.shutdown();
    }
}
