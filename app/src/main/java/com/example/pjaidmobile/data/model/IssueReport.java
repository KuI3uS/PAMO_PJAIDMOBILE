package com.example.pjaidmobile.data.model;

/**
 * A data model representing a problem (failure, fault) request for a specific device.
 * Can be used to create a new ticket and send it to the backend (e.g. as JSON).
 */

public class IssueReport {
    private String deviceId;
    private String description;

    public IssueReport(String deviceId, String description) {
        this.deviceId = deviceId;
        this.description = description;
    }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

