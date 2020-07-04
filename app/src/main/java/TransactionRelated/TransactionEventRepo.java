package TransactionRelated;

import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransactionEventRepo {

    private TransactionEventDao eventDao ;

    public TransactionEventRepo(Context context){
        TransactionEventDatabase eventDatabase = TransactionEventDatabase.getInstance(context);
        eventDao = eventDatabase.transactionEventDao();
    }

    public TransactionEntities.EventRequestAndResponse getEventReqAndResp(){
        return eventDao.getEventReqAndResp() ;
    }

    public void insertEventReq(final TransactionEntities.TransactionEventRequest request){
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            public void run() {
                eventDao.insertEventReq(request);
            }
        });
        executorService.shutdown();
    }

    public void insertEventResp(final TransactionEntities.TransactionEventResponse response){
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            public void run() {
                eventDao.insertEventResp(response);
            }
        });
        executorService.shutdown();
    }
}
