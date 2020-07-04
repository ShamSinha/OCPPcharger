package TransactionRelated;

import androidx.lifecycle.LiveData;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

public interface TransactionEventDao {

    @Insert
    public void insertEventReq(TransactionEntities.TransactionEventRequest request);

    @Insert
    public void insertEventResp(TransactionEntities.TransactionEventResponse response);

    @Transaction
    @Query("SELECT* FROM TransactionEventRequest WHERE SeqNo = (SELECT MAX(SeqNo) FROM TransactionEventRequest);")
    public TransactionEntities.EventRequestAndResponse getEventReqAndResp();


}
