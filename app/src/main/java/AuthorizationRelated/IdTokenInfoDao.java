package AuthorizationRelated;

import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

public interface IdTokenInfoDao {

    @Insert
    public void insert(IdTokenInfoEntity.IdTokenInfo idTokenInfo);

    @Update
    public void update(IdTokenInfoEntity.IdTokenInfo idTokenInfo);

    @Query("SELECT * FROM IdTokenInfoType.IdTokenInfo ")
    IdTokenInfoEntity.IdTokenInfo getIdTokenInfo();


}
