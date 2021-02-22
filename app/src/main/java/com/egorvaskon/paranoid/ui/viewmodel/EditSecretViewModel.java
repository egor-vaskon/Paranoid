package com.egorvaskon.paranoid.ui.viewmodel;


import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.egorvaskon.paranoid.DecodedSecret;

import java.util.ArrayList;

public class EditSecretViewModel extends ViewModel {

    private MutableLiveData<DecodedSecret> mDecodedSecretLiveData;

    public EditSecretViewModel(){
        super();

        mDecodedSecretLiveData = new MutableLiveData<>();
    }

    public LiveData<DecodedSecret> getDecodedSecret() {
        return mDecodedSecretLiveData;
    }

    public void setDecodedSecret(@NonNull DecodedSecret decodedSecret){
        mDecodedSecretLiveData.postValue(decodedSecret);
    }

}
