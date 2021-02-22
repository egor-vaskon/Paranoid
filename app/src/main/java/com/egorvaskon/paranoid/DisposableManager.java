package com.egorvaskon.paranoid;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import java.util.Stack;

import io.reactivex.rxjava3.disposables.Disposable;

public class DisposableManager {

    @NonNull
    private final Stack<Disposable> mDisposables = new Stack<>();

    public DisposableManager(){

    }

    public void pushDisposable(@NonNull Disposable disposable){
        mDisposables.push(disposable);
    }

    public Disposable popDisposable(){
        return mDisposables.pop();
    }

    public void dispose(){
        while(!mDisposables.isEmpty()){
            mDisposables.pop().dispose();
        }
    }

}
