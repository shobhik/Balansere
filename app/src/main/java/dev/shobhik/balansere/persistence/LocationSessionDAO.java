package dev.shobhik.balansere.persistence;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import dev.shobhik.balansere.model.LocationSession;

@Dao
public interface LocationSessionDAO {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertLocationSession(LocationSession... sessions);

    @Update
    public void updateLocationSession(LocationSession users);

    @Delete
    public void deleteLocationSession(LocationSession users);


    @Query("SELECT * FROM location_sessions")
    public LocationSession[] queryAllLocationSessions();

    @Query("SELECT * FROM location_sessions WHERE id is :num")
    public void queryLocationSessionByID(int num);

    @Query("SELECT * FROM location_sessions WHERE session_name LIKE :name")
    public void queryLocationSessionByName(String name);


}
