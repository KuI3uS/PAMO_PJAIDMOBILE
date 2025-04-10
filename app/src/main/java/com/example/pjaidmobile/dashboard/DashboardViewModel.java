package com.example.pjaidmobile.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DashboardViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public DashboardViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Tutaj pojawią się twoje zgłoszenia");
    }

    public LiveData<String> getText() {
        return mText;
    }
}