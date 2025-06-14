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

    public void initializeMap(Bundle savedInstanceState, Runnable onMapReadyCallback) {
        this.onMapReady = onMapReadyCallback;
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(map -> {
            googleMap = map;
            enableUserLocation();
            if (onMapReady != null) onMapReady.run();
        });
    }

    public void updateMapWithLocation(double lat, double lng) {
        if (googleMap != null) {
            LatLng userLocation = new LatLng(lat, lng);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
            googleMap.addMarker(new MarkerOptions().position(userLocation).title("Your location"));
        } else {
            Log.w(TAG, "GoogleMap not initialized yet");
        }
    }

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

    public void onSaveInstanceState(Bundle outState, String mapViewKey) {
        Bundle mapViewBundle = outState.getBundle(mapViewKey);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(mapViewKey, mapViewBundle);
        }
        mapView.onSaveInstanceState(mapViewBundle);
    }
}
