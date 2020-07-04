package com.example.chargergui;


import androidx.lifecycle.LiveData;
import androidx.room.Insert;
import androidx.room.Query;

public interface WebsocketDao {

    @Insert
    public void insertCallSent(WebsocketEntities.CallSent callSent) ;

    @Query("UPDATE Callsent SET isCallResultArrived = :a WHERE MessageId = :messageId ")
    public void updateCallSent(Boolean a , String messageId) ;

    @Query("SELECT isCallResultArrived FROM CallSent WHERE MessageId = :messageId ")
    public LiveData<Boolean> isCallResultArrived(String messageId) ;


    @Insert
    public void insertCallArrived(WebsocketEntities.CallArrived callArrived) ;

    @Query("UPDATE CallArrived SET isCallResultSent = :a WHERE MessageId = :messageId ")
    public void updateCallArrived(Boolean a , String messageId) ;

    @Query("SELECT isCallResultSent FROM CallArrived WHERE MessageId = :messageId ")
    public LiveData<Boolean> isCallResultSent(String messageId) ;

}
