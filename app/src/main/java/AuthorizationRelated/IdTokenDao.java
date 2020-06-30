package AuthorizationRelated;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

public interface IdTokenDao {

    @Insert
    public void insertIdTokenInfo(IdTokenEntities.IdTokenInfo idTokenInfo );

    @Insert
    public void insertIdToken(IdTokenEntities.IdToken idToken) ;

    @Update
    public void updateIdTokenInfo(IdTokenEntities.IdTokenInfo idTokenInfo);

    @Update
    public void updateIdToken(IdTokenEntities.IdToken idToken) ;

    @Transaction
    @Query("DELETE FROM IdToken")
    public void deleteAll() ;

    @Transaction
    @Query("SELECT * FROM IdToken WHERE transactionId = :transactionId")
    public IdTokenEntities.IdTokenAndInfo getIdTokenAndInfo(String transactionId) ;

}
