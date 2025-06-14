package com.example.pjaidmobile.presentation.features.scan;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pjaidmobile.domain.usecase.GetDeviceByIdUseCase;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.HttpException;

@HiltViewModel
public class ScanQRViewModel extends ViewModel {

    private static final String UNKNOWN_API_ERROR = "Unknown API error";

    private final GetDeviceByIdUseCase getDeviceByIdUseCase;
    private final MutableLiveData<ScanUiState> uiStateMutable = new MutableLiveData<>(ScanUiState.IDLE);
    public final LiveData<ScanUiState> uiState = uiStateMutable;
    private final CompositeDisposable disposables = new CompositeDisposable();

    @Inject
    public ScanQRViewModel(GetDeviceByIdUseCase getDeviceByIdUseCase) {
        this.getDeviceByIdUseCase = getDeviceByIdUseCase;
    }

    public void fetchDevice(String scannedId) {
        uiStateMutable.setValue(ScanUiState.FETCHING_DEVICE);
        disposables.add(
                getDeviceByIdUseCase.execute(scannedId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                device -> uiStateMutable.setValue(new ScanUiState.DeviceFound(device)),
                                this::handleError
                        )
        );
    }

    private void handleError(Throwable throwable) {
        if (throwable instanceof HttpException && ((HttpException) throwable).code() == 404) {
            uiStateMutable.setValue(ScanUiState.DEVICE_NOT_FOUND);
        } else {
            String message = throwable.getMessage() != null ? throwable.getMessage() : UNKNOWN_API_ERROR;
            uiStateMutable.setValue(new ScanUiState.Error(message));
        }
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}
