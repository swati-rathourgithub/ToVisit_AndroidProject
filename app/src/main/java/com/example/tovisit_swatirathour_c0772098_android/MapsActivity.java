package com.example.tovisit_swatirathour_c0772098_android;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.tovisit_swatirathour_c0772098_android.Room.Place;
import com.example.tovisit_swatirathour_c0772098_android.Room.PlaceRepository;
import com.example.tovisit_swatirathour_c0772098_android.volley.GetByVolley;
import com.example.tovisit_swatirathour_c0772098_android.volley.VolleySingleton;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnInfoWindowLongClickListener, GoogleMap.OnMarkerDragListener {

    private static final float DEFAULT_ZOOM_LEVEL = 13.5f;
    private static final String FAVOURITE_STRING = "Favourite Place";
    private static final String VISITED_STRING = " Visited";
    private static final int REQUEST_CODE = 1;
    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private Marker mMarker;
    FusedLocationProviderClient fusedLocationProviderClient;
    private LocationManager mLocationManager;
    private List<Place> mPlaceList = new ArrayList<>();
    private PlaceRepository mPlaceRepository;
    private Place mPlace;
    private int mSelectedType = R.id.normal_radio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        checkPermissions();
        mPlaceRepository = new PlaceRepository(this.getApplication());
        mPlace = (Place) getIntent().getSerializableExtra("place");
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnInfoWindowLongClickListener(this);
        mMap.setOnMarkerDragListener(this);
        if(mPlace != null)
        {
            String snippet = FAVOURITE_STRING;
            if(mPlace.isVisited())
            {
                snippet += VISITED_STRING;
            }
            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(mPlace.getLat(),mPlace.getLng())).snippet(snippet).title(mPlace.getTitle()).draggable(true);
            mMarker = mMap.addMarker(markerOptions);
            mMarker.showInfoWindow();
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
            return;
        }
        enableUserLocationAndZoom();
    }


    @Override
    public void onMapLongClick(LatLng latLng) {

        String title = "";
        try {
            Geocoder geocoder = new Geocoder(this);
            Address address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0);
            title += address.getThoroughfare() + " " + address.getLocality() + " " + address.getAdminArea();
        } catch (Exception e) {
            title = getFormattedDate();
        }
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(title).snippet("").draggable(true);
        mMarker = mMap.addMarker(markerOptions);
        mMarker.showInfoWindow();
    }

    public static String getFormattedDate() {
        return new DateFormat().format("EEE, MM-dd-yyyy hh:mm", new Date()).toString();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if (mPlace == null) {
            // Insertion
            mMarker.hideInfoWindow();
            mPlace = new Place(marker.getTitle(), marker.getPosition().latitude, marker.getPosition().longitude, false);
            mPlaceRepository.insertPlace(mPlace);
            marker.setSnippet(marker.getSnippet() + FAVOURITE_STRING);
            mMarker.showInfoWindow();
        } else {
            // Deletion
            mMarker.hideInfoWindow();
            mPlaceRepository.deletePlace(mPlace);
            marker.setSnippet(marker.getSnippet().replace(FAVOURITE_STRING, ""));
            mMarker.showInfoWindow();
            mPlace = null;
        }
        loadPlaces();
    }

    @Override
    public void onInfoWindowLongClick(Marker marker) {
        mPlace = getAssociatedPlace(marker);
        boolean cond;
        if(mPlace != null) {
            if (!mPlace.isVisited()) {
                // Insertion
                mMarker.hideInfoWindow();
                marker.setSnippet(marker.getSnippet() + VISITED_STRING);
                mMarker.showInfoWindow();
                cond = true;
            } else {
                // Deletion
                mMarker.hideInfoWindow();
                marker.setSnippet(marker.getSnippet().replace(VISITED_STRING, ""));
                mMarker.showInfoWindow();
                cond = false;
            }
            mPlace.setVisited(cond);
            mPlaceRepository.updatePlace(mPlace);
        }
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
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        String title = "";
        try {
            Geocoder geocoder = new Geocoder(this);
            Address address = geocoder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1).get(0);
            title += address.getThoroughfare() + " " + address.getLocality() + " " + address.getAdminArea();
        } catch (Exception e) {
            title = getFormattedDate();
        }
        marker.hideInfoWindow();
        marker.setTitle(title);
        marker.showInfoWindow();
        if (mPlace != null) {
            mPlace.setTitle(title);
            mPlace.setLat(marker.getPosition().latitude);
            mPlace.setLng(marker.getPosition().longitude);
            mPlaceRepository.updatePlace(mPlace);
        }
    }

    public void navigateClicked(View view) {
        if(mMarker != null)
        {
            final LatLng latLng = mMarker.getPosition();
            if (latLng != null) {

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                        getDirectionUrl(latLng), null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        GetByVolley.getDirection(response, mMap, latLng);
                    }
                }, null);
                VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
            } else {
                Toast.makeText(MapsActivity.this, "Please choose a destination", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getDirectionUrl(LatLng location) {
        Location location1 = getCurrentLocation();
        StringBuilder googleDirectionUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionUrl.append("origin=" + location1.getLatitude() + "," + location1.getLongitude());
        googleDirectionUrl.append(("&destination=" + location.latitude + "," + location.longitude));
        googleDirectionUrl.append("&key=" + getString(R.string.google_maps_key));
        Log.d(TAG, "getDirectionUrl: " + googleDirectionUrl);
        return googleDirectionUrl.toString();
    }

    public void mapTypeClicked(View view) {

        final androidx.appcompat.app.AlertDialog dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog_map_type, null);

        RadioGroup group = dialogView.findViewById(R.id.radio_group);
        group.check(mSelectedType);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group1, int checkedId) {
                switch (checkedId) {
                    case R.id.normal_radio:
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        mSelectedType = R.id.normal_radio;
                        break;
                    case R.id.hybrid_radio:
                        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        mSelectedType = R.id.hybrid_radio;
                        break;
                    case R.id.satellite_radio:
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        mSelectedType = R.id.satellite_radio;
                        break;
                    case R.id.terrain_radio:
                        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        mSelectedType = R.id.terrain_radio;
                }
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(true);
        dialogBuilder.show();

    }
}