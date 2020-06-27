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

    @Query("SELECT EXISTS(SELECT 1 FROM Controller WHERE componentName = :component)")
    int isComponent(String component);

    @Query("SELECT EXISTS(SELECT 1 FROM Controller WHERE componentName = :component AND variableName = :variable)")
    int isVariable(String component , String variable);

    @Query("UPDATE Controller SET attributeType = :attributeType AND attributeValue = :attributeValue  WHERE componentName = :component AND variableName = :variable AND mutability <> 'ReadOnly'")
    public int updateController(String component , String variable, String attributeValue , String attributeType);

}
