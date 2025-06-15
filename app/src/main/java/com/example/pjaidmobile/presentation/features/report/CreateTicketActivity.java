package com.example.pjaidmobile.presentation.features.report;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.pjaidmobile.data.model.Device;
import com.example.pjaidmobile.databinding.ActivityCreateTicketBinding;
import com.example.pjaidmobile.util.DeviceIntentHelper;
import com.example.pjaidmobile.util.MapHandler;
import com.google.android.gms.maps.MapView;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Activity responsible for creating and submitting a support ticket (e.g., device failure or downtime).
 * It allows the user to fill out a form with a title, description, select a type of issue,
 * and automatically attaches current GPS location and device info (if available).
 */
@AndroidEntryPoint
public class CreateTicketActivity extends AppCompatActivity {

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    private MapHandler mapHandler;
    private Double latitude = null;
    private Double longitude = null;
    private Device device;
    private CreateTicketViewModel viewModel;

    private ActivityCreateTicketBinding binding;

    /**
     * Request launcher for location permission. If granted, enables location layer on map
     * and fetches current coordinates from the ViewModel.
     */
    private final ActivityResultLauncher<String> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (Boolean.TRUE.equals(isGranted)) {
                    mapHandler.enableUserLocation();
                    viewModel.getCurrentLocation();
                } else {
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    /**
     * Called when the activity is first created. Initializes layout, ViewModel, map, form logic, and observers.
     *
     * @param savedInstanceState Bundle with saved state data (used for restoring MapView)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateTicketBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonBack.setOnClickListener(v -> finish());

        viewModel = new ViewModelProvider(this).get(CreateTicketViewModel.class);
        MapView mapView = binding.mapView;
        mapHandler = new MapHandler(this, mapView);

        device = DeviceIntentHelper.extractDeviceFromIntent(getIntent());

        setupMap(savedInstanceState);
        setupForm();
        observeViewModel();
        populateFormFromDevice(device);
    }

    /**
     * Configures the submit button logic and validates form input.
     * Sends the filled ticket data to the ViewModel.
     */
    private void setupForm() {
        binding.buttonSubmitTicket.setOnClickListener(v -> {
            String titleText = binding.editTextTitle.getText().toString().trim();
            String descriptionText = binding.editTextDescription.getText().toString().trim();
            boolean isDowntime = binding.toggleType.getCheckedButtonId() == binding.btnPrzestoj.getId();

            if (titleText.isEmpty()) {
                binding.editTextTitle.setError("Tytuł nie może być pusty");
                return;
            }

            if (descriptionText.isEmpty()) {
                binding.editTextDescription.setError("Opis nie może być pusty");
                return;
            }

            String username = getSharedPreferences("PJAIDPrefs", MODE_PRIVATE)
                    .getString("loggedInUsername", "Nieznany użytkownik");

            String serialNumber = device != null && device.getSerialNumber() != null
                    ? device.getSerialNumber()
                    : "Brak numeru seryjnego";

            viewModel.onSubmitClicked(
                    titleText,
                    descriptionText,
                    isDowntime,
                    latitude,
                    longitude,
                    username,
                    serialNumber
            );
        });
    }


    /**
     * Fills in the form with preloaded device information passed via Intent (e.g., from QR code scan).
     *
     * @param device Device object received from previous activity
     */
    private void populateFormFromDevice(Device device) {
        if (device != null) {
            if (device.getName() != null) {
                binding.editTextTitle.setText(getString(
                        com.example.pjaidmobile.R.string.ticket_title_prefix, device.getName()));
            }

            StringBuilder descriptionBuilder = new StringBuilder();
            if (device.getSerialNumber() != null) {
                descriptionBuilder.append("Numer seryjny: ").append(device.getSerialNumber()).append("\n");
            }
            if (device.getPurchaseDate() != null) {
                descriptionBuilder.append("Data zakupu: ").append(device.getPurchaseDate());
            }

            if (descriptionBuilder.length() > 0) {
                binding.editTextDescription.setText(descriptionBuilder.toString());
            }
        }
    }

    /**
     * Subscribes to LiveData from ViewModel to update the map location,
     * show success messages or error feedback.
     */
    private void observeViewModel() {
        viewModel.getLocationLiveData().observe(this, location -> {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            mapHandler.updateMapWithLocation(latitude, longitude);
        });

        viewModel.getErrorMessage().observe(this, error ->
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        );

        viewModel.getSubmitSuccess().observe(this, success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(this, "Zgłoszenie wysłane!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }


    /**
     * Initializes the Google Map with saved instance state and triggers permission check.
     *
     * @param savedInstanceState Bundle to restore the map state from
     */
    private void setupMap(Bundle savedInstanceState) {
        Bundle mapBundle = null;
        if (savedInstanceState != null) {
            mapBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mapHandler.initializeMap(mapBundle, this::checkLocationPermission);
    }

    /**
     * Checks if the app has permission to access location. If not, requests it.
     * On success, enables the user's location on the map and requests coordinates.
     */
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mapHandler.enableUserLocation();
            viewModel.getCurrentLocation();
        } else {
            locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapHandler.onResume();
    }

    @Override
    protected void onPause() {
        mapHandler.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapHandler.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapHandler.onLowMemory();
    }

    /**
     * Called before the activity is destroyed to save the state of the MapView.
     *
     * @param outState Bundle used to store the map’s state
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapHandler.onSaveInstanceState(outState, MAPVIEW_BUNDLE_KEY);
    }
}
