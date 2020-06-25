package socdisplay_viewmodel;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import Controller_Components.Controller;

@Dao
public interface SocDao {

    @Insert
    public void insert(SocEntities socEntities);

    @Update
    public void update(SocEntities socEntities);

    @Query("DELETE FROM SocEntities")
    void deleteAll();

    @Query("SELECT * FROM SocEntities")
    LiveData<List<SocEntities>> getAllSoc();


}
