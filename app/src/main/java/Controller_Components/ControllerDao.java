package Controller_Components;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ControllerDao {

    @Insert
    public void insert(Controller controller);

    @Update
    public void update(Controller controller);

    @Query("SELECT * FROM Controller")
    List<Controller> getAllController();

    @Query("SELECT * FROM Controller WHERE variableName = :variable AND componentName = :component ")
    Controller getController(String component , String variable);
}
