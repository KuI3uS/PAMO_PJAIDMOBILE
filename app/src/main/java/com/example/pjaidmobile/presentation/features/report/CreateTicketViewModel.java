package com.example.pjaidmobile.presentation.features.report;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pjaidmobile.data.model.Location;
import com.example.pjaidmobile.data.model.TicketRequest;
import com.example.pjaidmobile.data.model.TicketResponse;
import com.example.pjaidmobile.domain.usecase.CreateTicketUseCase;
import com.example.pjaidmobile.util.LocationProvider;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel responsible for handling the logic of creating a ticket.
 * It communicates with the domain layer, manages location fetching, and exposes LiveData
 * to observe submission results and possible errors.
 */
@HiltViewModel
public class CreateTicketViewModel extends ViewModel {

    private final CreateTicketUseCase createTicketUseCase;
    private final LocationProvider locationProvider;

    private final MutableLiveData<Location> locationLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> submitSuccess = new MutableLiveData<>();

    @Inject
    public CreateTicketViewModel(CreateTicketUseCase createTicketUseCase, LocationProvider locationProvider) {
        this.createTicketUseCase = createTicketUseCase;
        this.locationProvider = locationProvider;
    }

    /**
     * Triggers fetching of the current device location.
     * Updates LiveData with result or error message.
     */
    public void getCurrentLocation() {
        locationProvider.getCurrentLocation(new LocationProvider.LocationCallback() {
            @Override
            public void onLocationReceived(double lat, double lng) {
                locationLiveData.postValue(new Location(lat, lng));
            }

            @Override
            public void onLocationError(String message) {
                errorMessage.postValue("Błąd lokalizacji: " + message);
            }
        });
    }

    public LiveData<Location> getLocationLiveData() {
        return locationLiveData;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getSubmitSuccess() {
        return submitSuccess;
    }


    /**
     * Handles submission of a ticket form. Validates input and sends request via use case.
     *
     * @param title         Ticket title
     * @param description   Ticket description
     * @param isDowntime    True if issue is a downtime, false otherwise
     * @param lat           Optional latitude
     * @param lon           Optional longitude
     * @param username      Name of the user submitting the ticket
     * @param serialNumber  Device serial number (if available)
     */
    public void onSubmitClicked(String title, String description, boolean isDowntime, Double lat, Double lon, String username, String serialNumber) {
        if (title.isEmpty()) {
            errorMessage.setValue("Tytuł nie może być pusty");
            return;
        }

        if (description.isEmpty()) {
            errorMessage.setValue("Opis nie może być pusty");
            return;
        }

        String status = isDowntime ? "PRZESTOJ" : "NOWE";

        TicketRequest request = new TicketRequest(
                title,
                description,
                status,
                1L,
                username,
                lat != null ? lat : 53.1234804,
                lon != null ? lon : 18.004378
        );

        createTicketUseCase.execute(request, new Callback<>() {
            @Override
            public void onResponse(Call<TicketResponse> call, Response<TicketResponse> response) {
                if (response.isSuccessful()) {
                    submitSuccess.postValue(true);
                } else {
                    errorMessage.postValue("Błąd zgłoszenia: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<TicketResponse> call, Throwable t) {
                errorMessage.postValue("Błąd sieci: " + t.getMessage());
            }
        });
    }
}
