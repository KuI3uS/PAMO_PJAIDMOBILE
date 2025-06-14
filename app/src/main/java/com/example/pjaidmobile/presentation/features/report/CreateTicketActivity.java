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

@AndroidEntryPoint
public class CreateTicketActivity extends AppCompatActivity {

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    private MapHandler mapHandler;
    private Double latitude = null;
    private Double longitude = null;
    private Device device;
    private CreateTicketViewModel viewModel;

    private ActivityCreateTicketBinding binding;

    private final ActivityResultLauncher<String> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (Boolean.TRUE.equals(isGranted)) {
                    mapHandler.enableUserLocation();
                    viewModel.getCurrentLocation();
                } else {
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
                }
            });

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


    private void setupMap(Bundle savedInstanceState) {
        Bundle mapBundle = null;
        if (savedInstanceState != null) {
            mapBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mapHandler.initializeMap(mapBundle, this::checkLocationPermission);
    }

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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapHandler.onSaveInstanceState(outState, MAPVIEW_BUNDLE_KEY);
    }
}
