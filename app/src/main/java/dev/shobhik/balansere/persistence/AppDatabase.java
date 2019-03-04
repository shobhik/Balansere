package dev.shobhik.balansere.persistence;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import dev.shobhik.balansere.model.LocationSession;
import dev.shobhik.balansere.model.User;

@Database(entities = {LocationSession.class, User.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract LocationSessionDAO locationSessionDAO();
    public abstract UserDAO userDAO();

}
