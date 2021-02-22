package com.egorvaskon.paranoid.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.egorvaskon.paranoid.Key;
import com.egorvaskon.paranoid.Secret;

@Database(entities = {Secret.class, Key.class},version = 1)
@TypeConverters({GsonTypeConverters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract SecretDao secretDao();
    public abstract KeyDao keyDao();

}
