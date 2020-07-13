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
    @Query("SELECT* FROM TransactionEventRequest WHERE SeqNo = (SELECT MAX(SeqNo) FROM TransactionEventRequest)")
    public LiveData<TransactionEntities.EventRequestAndResponse> getEventReqAndResp();

    @Transaction
    @Query("DELETE FROM TransactionEventRequest WHERE SeqNo = (SELECT MAX(SeqNo) FROM TransactionEventRequest)")
    public void deleteEvent();

    @Insert
    public void insertSEQNO(TransactionEntities.SEQNO seqno);

    @Query("UPDATE SEQNO SET SeqNo = CASE WHEN(SeqNo < 2147483647) THEN SeqNo + 1 ELSE 0 END WHERE id =1 ")
    public void updateSeqNo();

    @Query("SELECT*FROM SEQNO WHERE id = 1 ")
    public long getSeqNo();



}
