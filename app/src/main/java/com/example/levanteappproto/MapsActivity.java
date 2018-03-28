package com.example.levanteappproto;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MapsActivity extends AppCompatActivity {

    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    LatLng mCurrLoc = new LatLng(-22,-47);
    MarkerOptions mMarkerOpt;
    Marker mMarker;
    Intent data;
    private boolean locationFound = false;

    private LatLng mDefaultLocation = new LatLng(-22.012251, -47.900835);
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 16f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        getLocationPermission();

        data = new Intent();

        Button send = (Button) findViewById(R.id.mapSendButton);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (locationFound) {
                    data.putExtra("markLat",mMarker.getPosition().latitude);
                    data.putExtra("markLon",mMarker.getPosition().longitude);
                    setResult(RESULT_OK,data);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        Log.d(TAG, "Shazam! ->getDeviceLocation: [DEEBUG] entrou");

        try {
            if (mLocationPermissionGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        Log.d(TAG, "Shazam! ->onComplete: [DEEBUG] task is successfull. task: " + task);
                        Log.d(TAG, "Shazam! ->onComplete: [DEEBUG] task.getResult: " + task.getResult());
                        if (task.isSuccessful() && task.getResult() != null) {
                            Location loc = (Location)task.getResult();
                            locationFound = true;
                            mCurrLoc = new LatLng(loc.getLatitude(), loc.getLongitude());

                            moveCamera(mCurrLoc,DEFAULT_ZOOM);
                            mMarkerOpt = new MarkerOptions()
                                    .position(mCurrLoc)
                                    .title("Localização da Ocorrência");
                            mMarker = mMap.addMarker(mMarkerOpt);
                        } else {
                            Log.d(TAG, "Shazam! ->[DEEBUG] exception: " + task.getException());
                            locationFound = false;
                            Toast.makeText(MapsActivity.this,"Não pôde encontrar localização atual! Ligue a localização do dispositivo.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e) {
        }
    }

    private void moveCamera(LatLng loc, float zoom) {
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.builder()
            .target(loc)
            .zoom(zoom)
            .build()));
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        Log.d(TAG, "Shazam! ->initMap: chamada");
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                Log.d(TAG, "Shazam! ->onMapReady: chamada. mMap = " + mMap);

                if (mLocationPermissionGranted) {
                    getDeviceLocation();
                    if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    {
                        mMap.setMyLocationEnabled(true);
                    }
                    mMap.getUiSettings().setCompassEnabled(true);

                    // Marker follows camera
                    mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                        @Override
                        public void onCameraMove() {
                            mMarker.setPosition(mMap.getCameraPosition().target);
                        }
                    });
                }
            }
        });
    }

    private void getLocationPermission() {
        String permissions[] = {FINE_LOCATION,COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();
            }
        }
        else {
            ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0;i < grantResults.length;i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                    // inicializar o mapa
                    initMap();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        data.putExtra("markLat",0.0);
        data.putExtra("markLon",0.0);
        setResult(RESULT_CANCELED,data);
        finish();
    }
}
