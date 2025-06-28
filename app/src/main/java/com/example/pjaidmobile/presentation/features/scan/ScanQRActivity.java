package com.example.pjaidmobile.presentation.features.scan;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import com.example.pjaidmobile.R;
import com.example.pjaidmobile.data.model.Device;
import com.example.pjaidmobile.presentation.features.report.CreateTicketActivity;
import com.example.pjaidmobile.util.DeviceIntentHelper;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * Activity responsible for scanning QR codes using the camera.
 * It handles permission checks, initializes the scanner, and navigates to CreateTicketActivity on success.
 */
@AndroidEntryPoint
public class ScanQRActivity extends AppCompatActivity {

    private static final String TAG = "ScanQRActivity";

    private ScanQRViewModel viewModel;
    private DecoratedBarcodeView barcodeScannerView;
    private final ActivityResultLauncher<String> cameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted != null && isGranted) {
                    startScanning();
                } else {
                    Toast.makeText(this, "Brak uprawnień do kamery", Toast.LENGTH_LONG).show();
                    finish();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "ScanQRActivity started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);

        initViewModel();
        initViews();
        checkCameraPermissionOrStartScanning();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(ScanQRViewModel.class);
        viewModel.uiState.observe(this, this::handleUiState);
    }

    private void initViews() {
        barcodeScannerView = findViewById(R.id.barcodeScannerView);

        ImageButton buttonBack = findViewById(R.id.buttonBack);
        if (buttonBack != null) {
            Log.d(TAG, "Back button clicked");
            buttonBack.setOnClickListener(v -> finish());
        }
    }

    private void checkCameraPermissionOrStartScanning() {
        boolean granted = hasCameraPermission();
        Log.d(TAG, "Camera permission: " + (granted ? "granted" : "denied"));
        if (granted) {
            startScanning();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }


    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }


    private void startScanning() {
        barcodeScannerView.decodeContinuous(result -> {
            if (result == null || result.getText() == null) {
                Log.w(TAG, "Scan returned empty result");
                return;
            }

            String scannedId = result.getText();
            Log.d(TAG, "Scanned QR code: " + scannedId);

            barcodeScannerView.pause();
            viewModel.fetchDevice(scannedId);
        });

        barcodeScannerView.resume();
    }

    private void handleUiState(ScanUiState state) {
        if (state instanceof ScanUiState.DeviceFound) {
            handleDeviceFound(((ScanUiState.DeviceFound) state).device);
        } else if (state instanceof ScanUiState.DeviceNotFound) {
            handleDeviceNotFound();
        } else if (state instanceof ScanUiState.Error) {
            handleScanError(((ScanUiState.Error) state).message);
        } else if (state instanceof ScanUiState.ScanCancelled) {
            handleScanCancelled();
        }
    }

    private void handleDeviceFound(Device device) {
        Log.d(TAG, "Device found: " + device.getName());
        Intent intent = DeviceIntentHelper.createIntentWithDevice(this, CreateTicketActivity.class, device);
        startActivity(intent);
        finish();
    }

    private void handleDeviceNotFound() {
        Log.w(TAG, "Device not found in API");
        showToast(R.string.device_not_found);
        finish();
    }

    private void handleScanError(String message) {
        String errorMessage = message != null ? message : getString(R.string.unknown_api_error);
        Log.e(TAG, "API error: " + errorMessage);
        showToast(getString(R.string.api_error_prefix) + errorMessage);
        finish();
    }

    private void handleScanCancelled() {
        Log.d(TAG, "Scan cancelled");
        showToast(R.string.scan_cancelled);
        finish();
    }

    private void showToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            barcodeScannerView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeScannerView.pause();
    }
}
