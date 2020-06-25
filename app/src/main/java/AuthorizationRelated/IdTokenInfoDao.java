package AuthorizationRelated;

import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

public interface IdTokenInfoDao {

    @Insert
    public void insert(IdTokenInfoType.IdTokenInfo idTokenInfo);

    @Update
    public void update(IdTokenInfoType.IdTokenInfo idTokenInfo);

    @Query("SELECT * FROM IdTokenInfoType.IdTokenInfo ")
    IdTokenInfoType.IdTokenInfo getIdTokenInfo();


}
