package com.egorvaskon.paranoid.room;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Room;

import com.egorvaskon.paranoid.Key;
import com.egorvaskon.paranoid.Secret;
import com.egorvaskon.paranoid.SecretHeader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public final class AppRepository {

    public static final AppRepository INSTANCE = new AppRepository();
    public static final String DB_NAME = "app_database";

    private ExecutorService mExecutor;

    private AppDatabase mAppDatabase;

    private final AtomicInteger mClientCount = new AtomicInteger(0);

    private AppRepository(){

    }

    public void open(@NonNull Context context){
        if(mClientCount.get() == 0){
            mAppDatabase = Room.databaseBuilder(context,AppDatabase.class,DB_NAME).build();
            mExecutor = Executors.newFixedThreadPool(3);
        }

        mClientCount.incrementAndGet();
    }

    public void addSecret(Secret secret){
        mExecutor.execute(() -> mAppDatabase.secretDao().insert(secret));
    }

    public Single<Secret> getSecret(long id){
        return Single.fromCallable(() -> mAppDatabase.secretDao().getSecret(id))
                .subscribeOn(Schedulers.io());
    }

    public void addKey(Key key){
        mExecutor.execute(() -> mAppDatabase.keyDao().insert(key));
    }

    public void deleteSecret(long id){
        mExecutor.execute(() -> mAppDatabase.secretDao().delete(id));
    }

    public void deleteKey(Key key){
        mExecutor.execute(() -> mAppDatabase.keyDao().delete(key));
    }

    public void deleteKey(long key){
        mExecutor.execute(() -> mAppDatabase.keyDao().delete(key));
    }

    public LiveData<SecretHeader> getSecretHeaderLiveData(long id){
        return mAppDatabase.secretDao().getSecretHeaderLiveData(id);
    }

    public LiveData<List<SecretHeader>> getSecretsLiveData(){
        return mAppDatabase.secretDao().getSecretListLiveData();
    }

    public Single<ArrayList<String>> getQuestions(@NonNull List<Long> ids){
        Single<List<String>> list = mAppDatabase.keyDao().getQuestions(ids);
        return list.map(lst -> {
            if(lst instanceof ArrayList)
                return (ArrayList<String>)lst;
            else
                return new ArrayList<>(lst);
        });
    }

    public LiveData<List<Key>> getKeysLiveData(){
        return mAppDatabase.keyDao().getKeysLiveData();
    }

    public Single<Key> getKey(long id){
        return mAppDatabase.keyDao().getKey(id);
    }

    public void close(){
        if(mClientCount.get() == 0)
            return;

        if(mClientCount.decrementAndGet() == 0){
            mExecutor.shutdown();
            try {
                if (!mExecutor.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
                    mExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                mExecutor.shutdownNow();
            }
            finally {
                mAppDatabase.close();
                mAppDatabase = null;
            }
        }
    }

}
