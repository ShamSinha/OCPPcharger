package SOCDisplayRelated;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SocDao {

    @Insert
    public void insert(SocEntity socEntity);

    @Update
    public void update(SocEntity socEntity);

    @Query("DELETE FROM SocEntity")
    void deleteAll();

    @Query("SELECT COUNT(*) FROM SocEntity")
    public int getSize();

    @Query("SELECT * FROM SocEntity LIMIT 1")
    LiveData<SocEntity> getSoc();


}
