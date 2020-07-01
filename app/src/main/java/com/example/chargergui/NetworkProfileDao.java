package com.example.chargergui;

import androidx.room.Insert;
import androidx.room.Query;

public interface NetworkProfileDao {

    @Insert
    public void insert(NetworkProfile networkProfile) ;

    @Query("SELECT* FROM NetworkProfile WHERE configurationSlot = :configurationSlot")
    public NetworkProfile getNetworkProfile(int configurationSlot) ;

}
