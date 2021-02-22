package com.egorvaskon.paranoid.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.egorvaskon.paranoid.Secret;
import com.egorvaskon.paranoid.SecretHeader;
import com.egorvaskon.paranoid.room.AppRepository;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

public class SecretsViewModel extends AndroidViewModel {

    public SecretsViewModel(@NonNull Application app){
        super(app);

        AppRepository.INSTANCE.open(app);
    }

    @NonNull
    public LiveData<List<SecretHeader>> getSecretsLiveData(){
        return AppRepository.INSTANCE.getSecretsLiveData();
    }

    public Single<Secret> getSecret(long id){
        return AppRepository.INSTANCE.getSecret(id);
    }

    public LiveData<SecretHeader> getSecretHeader(long id){
        return AppRepository.INSTANCE.getSecretHeaderLiveData(id);
    }

    public void addSecret(@NonNull Secret secret){
        AppRepository.INSTANCE.addSecret(secret);
    }

    public void deleteSecret(long id){
        AppRepository.INSTANCE.deleteSecret(id);
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        AppRepository.INSTANCE.close();
    }
}
