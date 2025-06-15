package com.example.pjaidmobile.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Utility class for managing a Google Map instance inside a MapView.
 * Handles lifecycle events, location permissions, and camera positioning.
 */
public class MapHandler {

    private static final String TAG = "MapHandler";
    private final MapView mapView;
    private final Context context;
    private GoogleMap googleMap;
    private Runnable onMapReady;

    public MapHandler(Context context, MapView mapView) {
        this.context = context;
        this.mapView = mapView;
    }

    /**
     * Initializes the map and executes the provided callback when ready.
     *
     * @param savedInstanceState Saved instance state bundle
     * @param onMapReadyCallback Callback to be executed when the map is ready
     */
    public void initializeMap(Bundle savedInstanceState, Runnable onMapReadyCallback) {
        this.onMapReady = onMapReadyCallback;
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(map -> {
            googleMap = map;
            enableUserLocation();
            if (onMapReady != null) onMapReady.run();
        });
    }

    /**
     * Updates the map by moving the camera to a new location and placing a marker.
     *
     * @param lat Latitude of the new location
     * @param lng Longitude of the new location
     */
    public void updateMapWithLocation(double lat, double lng) {
        if (googleMap != null) {
            LatLng userLocation = new LatLng(lat, lng);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
            googleMap.addMarker(new MarkerOptions().position(userLocation).title("Your location"));
        } else {
            Log.w(TAG, "GoogleMap not initialized yet");
        }
    }

    /**
     * Enables the "My Location" layer if location permission is granted.
     */
    public void enableUserLocation() {
        if (googleMap != null && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        } else {
            Log.w(TAG, "Location permission not granted, cannot enable user location");
        }
    }

    public void onResume() {
        mapView.onResume();
    }

    public void onPause() {
        mapView.onPause();
    }

    public void onDestroy() {
        mapView.onDestroy();
    }

    public void onLowMemory() {
        mapView.onLowMemory();
    }

    /**
     * Saves the MapView instance state into the provided bundle.
     *
     * @param outState   The Bundle to save the MapView state into
     * @param mapViewKey Key under which to save the MapView's state
     */
    public void onSaveInstanceState(Bundle outState, String mapViewKey) {
        Bundle mapViewBundle = outState.getBundle(mapViewKey);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(mapViewKey, mapViewBundle);
        }
        mapView.onSaveInstanceState(mapViewBundle);
    }
}
