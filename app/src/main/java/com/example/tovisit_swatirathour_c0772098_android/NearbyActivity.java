package com.example.tovisit_swatirathour_c0772098_android;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.tovisit_swatirathour_c0772098_android.Room.Place;
import com.example.tovisit_swatirathour_c0772098_android.Room.PlaceRepository;
import com.example.tovisit_swatirathour_c0772098_android.volley.GetByVolley;
import com.example.tovisit_swatirathour_c0772098_android.volley.VolleySingleton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NearbyActivity extends FragmentActivity implements OnMapReadyCallback, TabLayout.OnTabSelectedListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnInfoWindowLongClickListener {

    private static final int RADIUS = 1500;
    private static final String TAG = "NearbyActivity";
    private static final int REQUEST_CODE = 1;
    private static final float DEFAULT_ZOOM_LEVEL = 13.5f;
    private static final String FAVOURITE_STRING = "Favourite Place";
    private static final String VISITED_STRING = " visited";

    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private List<Place> mPlaceList = new ArrayList<>();
    private PlaceRepository mPlaceRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ((TabLayout) findViewById(R.id.tablayout)).addOnTabSelectedListener(this);
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        checkPermissions();
        mPlaceRepository = new PlaceRepository(this.getApplication());
        loadPlaces();
    }

    private void loadPlaces() {
        mPlaceList.clear();
        mPlaceList.addAll(mPlaceRepository.fetchAllPlaces());
    }

    private void checkPermissions() {
        if (!hasLocationPermission()) {
            requestLocationPermission();
        }
    }

    /**
     * Method to request Location Permission
     */
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    /**
     * Method to check if the app has User Location Permission
     *
     * @return - True if the app has User Location Permission
     */
    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mMap != null) {
                    enableUserLocationAndZoom();
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void enableUserLocationAndZoom() {
        mMap.setMyLocationEnabled(true);
        Location location = getCurrentLocation();
        if (location != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM_LEVEL));
        }
    }

    @SuppressLint("MissingPermission")
    private Location getCurrentLocation() {
        return mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnInfoWindowLongClickListener(this);
        showLaunchNearbyPlaces();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
            return;
        }
        enableUserLocationAndZoom();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        Location location = getCurrentLocation();
        if(location != null)
        {
            String url = "";
            switch (tab.getPosition()) {
                case 0:
                    url = getPlaceUrl(location.getLatitude(), location.getLongitude(), "hospital");
                    break;
                case 1:
                    url = getPlaceUrl(location.getLatitude(), location.getLongitude(), "restaurant");
                    break;
                case 2:
                    url = getPlaceUrl(location.getLatitude(), location.getLongitude(), "cafe");
                    break;
                case 3:
                    url = getPlaceUrl(location.getLatitude(), location.getLongitude(), "museum");
                    break;
            }
            showNearbyPlaces(url);

        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    private String getPlaceUrl(double latitude, double longitude, String placeType) {
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location=" + latitude + "," + longitude);
        googlePlaceUrl.append(("&radius=" + RADIUS));
        googlePlaceUrl.append("&type=" + placeType);
        googlePlaceUrl.append("&key=" + getString(R.string.google_maps_key));
        Log.d(TAG, "getDirectionUrl: " + googlePlaceUrl);
        return googlePlaceUrl.toString();
    }

    public void showLaunchNearbyPlaces() {
        Location location = getCurrentLocation();
        if(location != null)
        {
            String url = getPlaceUrl(location.getLatitude(), location.getLongitude(), "hospital");
            showNearbyPlaces(url);
        }
    }

    private void showNearbyPlaces(String url) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        GetByVolley.getNearbyPlaces(response, mMap);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    private Place getAssociatedPlace(Marker marker) {
        for(Place place: mPlaceList)
        {
            if(place.getLat() == marker.getPosition().latitude && place.getLng() == marker.getPosition().longitude)
            {
                return place;
            }
        }
        return null;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Place place = getAssociatedPlace(marker);
        if (place == null) {
            // Insertion
            marker.hideInfoWindow();
            place = new Place(marker.getTitle(), marker.getPosition().latitude, marker.getPosition().longitude, false);
            mPlaceRepository.insertPlace(place);
            marker.setSnippet(marker.getSnippet() + FAVOURITE_STRING);
            marker.showInfoWindow();
        } else {
            // Deletion
            marker.hideInfoWindow();
            mPlaceRepository.deletePlace(place);
            marker.setSnippet(marker.getSnippet().replace(FAVOURITE_STRING, ""));
            marker.showInfoWindow();
            place = null;
        }
        loadPlaces();
    }

    @Override
    public void onInfoWindowLongClick(Marker marker) {
        Place place = getAssociatedPlace(marker);
        boolean cond;
        if(place != null) {
            if (!place.isVisited()) {
                // Insertion
                marker.hideInfoWindow();
                marker.setSnippet(marker.getSnippet() + VISITED_STRING);
                marker.showInfoWindow();
                cond = true;
            } else {
                // Deletion
                marker.hideInfoWindow();
                marker.setSnippet(marker.getSnippet().replace(VISITED_STRING, ""));
                marker.showInfoWindow();
                cond = false;
            }
            place.setVisited(cond);
            mPlaceRepository.updatePlace(place);
        }
    }

}