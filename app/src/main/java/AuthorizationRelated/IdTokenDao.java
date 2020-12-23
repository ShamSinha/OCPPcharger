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
    public void insertIdTokenInfo(IdTokenInfoEntity idTokenInfo );

    @Insert
    public void insertIdToken(IdTokenEntity idToken) ;

    @Update
    public void updateIdTokenInfo(IdTokenInfoEntity idTokenInfo);

    @Update
    public void updateIdToken(IdTokenEntity idToken) ;

    @Query("DELETE FROM IdTokenEntity")
    public void deleteIdToken();

    @Query("DELETE FROM IdTokenInfoEntity")
    public void deleteIdTokenInfo();

    @Query("SELECT * FROM IdTokenEntity LIMIT 1")
    public IdTokenEntity getIdToken() ;

    @Query("SELECT * FROM IdTokenInfoEntity LIMIT 1")
    public IdTokenInfoEntity getIdTokenInfo() ;

}
