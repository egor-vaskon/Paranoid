package com.egorvaskon.paranoid.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.egorvaskon.paranoid.Key;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Single;

@Dao
public interface KeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Key key);

    @Delete
    void delete(Key key);

    @Query("DELETE FROM keys WHERE id=:id")
    void delete(long id);

    @Query("SELECT * from keys ORDER BY name ASC,question ASC")
    LiveData<List<Key>> getKeysLiveData();
    
    @Query("SELECT question FROM keys WHERE id IN (:ids) ORDER BY id ASC")
    Single<List<String>> getQuestions(List<Long> ids);

    @Query("SELECT * FROM keys WHERE id=:id")
    Single<Key> getKey(long id);
}
