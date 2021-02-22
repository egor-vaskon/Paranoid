package com.egorvaskon.paranoid.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.egorvaskon.paranoid.Key;
import com.egorvaskon.paranoid.room.AppRepository;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Single;

public class KeysViewModel extends AndroidViewModel {

    public KeysViewModel(@NonNull Application app){
        super(app);

        AppRepository.INSTANCE.open(app);
    }

    @NonNull
    public LiveData<List<Key>> getKeysLiveData(){
        return AppRepository.INSTANCE.getKeysLiveData();
    }

    public Single<ArrayList<String>> getQuestions(@NonNull List<Long> ids){
        return AppRepository.INSTANCE.getQuestions(ids);
    }

    public void addKey(@NonNull Key key){
        AppRepository.INSTANCE.addKey(key);
    }

    public void deleteKey(@NonNull Key key){
        AppRepository.INSTANCE.deleteKey(key);
    }

    public void deleteKey(long key){
        AppRepository.INSTANCE.deleteKey(key);
    }

    public Single<Key> getKey(long id){
        return AppRepository.INSTANCE.getKey(id);
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        AppRepository.INSTANCE.close();
    }
}
