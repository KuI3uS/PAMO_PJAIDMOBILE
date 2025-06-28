package com.example.pjaidmobile.data.model;

/**
 * A class representing a request to create a new ticket (ticket).
 * Passed, for example, as the body of a POST request to the API.
 */

public class TicketRequest {
    private String title;
    private String description;
    private String status;
    private Long deviceId;
    private String userName;

    private Double latitude;
    private Double longitude;

    public TicketRequest(String title, String description, String status, Long deviceId, String userId, Double latitude, Double longitude) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.deviceId = deviceId;
        this.userName = userId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public Long getDeviceId() { return deviceId; }
    public String getUserName() { return userName; }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
