package com.egorvaskon.paranoid.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class QuizViewModel extends ViewModel {

    private MutableLiveData<List<String>> mAnswers;

    public QuizViewModel(){
        mAnswers = new MutableLiveData<>();
    }

    public LiveData<List<String>> getAnswers() {
        return mAnswers;
    }

    public void setAnswers(List<String> answers) {
        mAnswers.postValue(answers);
    }

    public void reset(){
        mAnswers.postValue(null);
    }
}
