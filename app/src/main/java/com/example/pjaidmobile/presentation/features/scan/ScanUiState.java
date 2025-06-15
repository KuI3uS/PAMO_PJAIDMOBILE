package com.example.pjaidmobile.presentation.features.scan;

import com.example.pjaidmobile.data.model.Device;

/**
 * Interface representing the various UI states during QR scanning.
 * Implemented as a sealed-like class hierarchy.
 */
public interface ScanUiState {
    class Idle implements ScanUiState {
        private Idle() {
        }
    }

    class Scanning implements ScanUiState {
        private Scanning() {
        }
    }

    class FetchingDevice implements ScanUiState {
        private FetchingDevice() {
        }
    }

    class ScanCancelled implements ScanUiState {
        private ScanCancelled() {
        }
    }

    class DeviceFound implements ScanUiState {
        public final Device device;

        public DeviceFound(Device device) {
            this.device = device;
        }
    }

    class DeviceNotFound implements ScanUiState {
        private DeviceNotFound() {
        }
    }

    class Error implements ScanUiState {
        public final String message;

        public Error(String message) {
            this.message = message;
        }
    }

    ScanUiState IDLE = new Idle();
    ScanUiState FETCHING_DEVICE = new FetchingDevice();
    ScanUiState DEVICE_NOT_FOUND = new DeviceNotFound();
}
