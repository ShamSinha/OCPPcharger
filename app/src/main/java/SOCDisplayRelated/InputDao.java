package SOCDisplayRelated;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;



@Dao
public interface InputDao {

    @Insert
    public void insert(InputEntity inputEntity);

    @Update
    public void update(InputEntity inputEntity);

    @Query("DELETE FROM InputEntity")
    void deleteAll();

    @Query("SELECT * FROM InputEntity LIMIT 1")
    LiveData<InputEntity> getInput();
}
