package com.egorvaskon.paranoid.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.egorvaskon.paranoid.Secret;
import com.egorvaskon.paranoid.SecretHeader;

import java.util.List;

@Dao
public interface SecretDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Secret secret);

    @Update
    int update(Secret secret);

    @Query("DELETE FROM secrets WHERE id=:id")
    void delete(long id);

    @Delete
    void delete(Secret secret);

    @Query("SELECT id,name FROM secrets")
    LiveData<List<SecretHeader>> getSecretListLiveData();

    @Query("SELECT * FROM secrets WHERE id=:id")
    Secret getSecret(long id);

    @Query("SELECT id,name FROM secrets WHERE id=:id")
    LiveData<SecretHeader> getSecretHeaderLiveData(long id);

}
