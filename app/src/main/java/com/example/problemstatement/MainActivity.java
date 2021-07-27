package com.example.problemstatement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class MainActivity extends AppCompatActivity {

    TextView tvLat, tvLong;
    Button btnOffMusic, btnStart, btnStop, btnCheck;
    LocationRequest mLocationRequest;
    LocationCallback mLocationCallback;
    private GoogleMap map;
    String folderLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //==================----------------InitSetupAll---------------=================
        //=================SetupUI==================
        tvLat = findViewById(R.id.tvLat);
        tvLong = findViewById(R.id.tvLong);
        btnOffMusic = findViewById(R.id.btnMusicOff);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        btnCheck = findViewById(R.id.btnCheck);
        //=================SetupUI==================
        //==================CheckPermFunctionCall=====================
        checkPermission();
        //==================CheckPermFunctionCall=====================
        //=================ConnectGPlayService===================
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        Task<Location> task = client.getLastLocation();
        //=================ConnectGPlayService===================
        //==================MapFragmentSetup=====================
        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment)
                fm.findFragmentById(R.id.map);
        //==================MapFragmentSetup=====================
        //==================FolderCreation=====================
        folderLocation = getFilesDir().getAbsolutePath() + "/MyFolder";
        File folder = new File(folderLocation);
        if (folder.exists() == false) {
            boolean result = folder.mkdir();
            if (result == true) {
                Log.d("File Read/Write", "Folder created");
            }
        }
        //==================FolderCreation=====================
        //==================----------------InitSetupAll---------------=================



        //========GetReferenceToGoogleMapObject========
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            //-----------RunOnceLoaded----------
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                task.addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location != null){
                            //------------------PointToLocation---------------
                            LatLng lastLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            //------------------PointToLocation---------------
                            //-----------------Display----------------
                            tvLat.setText("Latitude: " + location.getLatitude());
                            tvLong.setText("Longitude: " + location.getLongitude());
                            Marker cp = map.addMarker(new
                                    MarkerOptions()
                                    .position(lastLocation)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                            //-----------------Display----------------
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocation,
                                    15));
                            UiSettings ui = map.getUiSettings();
                            ui.setZoomControlsEnabled(true);
                        }
                        else{
                            Toast.makeText(MainActivity.this, "No Last Known Location found", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            //-----------RunOnceLoaded----------
        });
        //========GetReferenceToGoogleMapObject========



        //========================ButtonHandle========================
        //===============----------LocationDetectHandle--------------==================
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
                mLocationRequest = LocationRequest.create();
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                mLocationRequest.setInterval(30000);
                mLocationRequest.setSmallestDisplacement(500);
                mLocationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        if (locationResult != null) {
                            Location data = locationResult.getLastLocation();
                            double lat = data.getLatitude();
                            double lng = data.getLongitude();

                            tvLat.setText("Latitude: " + lat);
                            tvLong.setText("Longitude: " + lng);

                            //===============-----------WriteFile-------------====================
                            try {
                                String folderLocation_I = getFilesDir().getAbsolutePath() + "/MyFolder";
                                File targetFile_I = new File(folderLocation_I, "data.txt");
                                FileWriter writer_I = new FileWriter(targetFile_I, true);
                                writer_I.write(lat + "," + lng + "\n");
                                writer_I.flush();
                                writer_I.close();
                            } catch (Exception e){
                                Toast.makeText(MainActivity.this, "Failed to write!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                            //===============-----------WriteFile-------------====================

                            //------------------PointToLocation---------------
                            LatLng lastLocation = new LatLng(lat, lng);
                            //------------------PointToLocation---------------
                            //-----------------Display----------------
                            map.clear();
                            map.addMarker(new
                                    MarkerOptions()
                                    .position(lastLocation)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                            //-----------------Display----------------
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocation,
                                    15));
                        }
                    };
                };
                client.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
            }
        });
        
        
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
                client.removeLocationUpdates(mLocationCallback);
            }
        });
        //===============----------LocationDetectHandle--------------==================

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, CordDisplay.class);
                startActivity(i);
            }
        });
        //========================ButtonHandle========================


    }


    //===============-------FuncPermissionChecker------==================
    private boolean checkPermission(){
        int permissionCheck_Coarse = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED
                || permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED) {
            return true;
        }
        else {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            return false;
        }
    }
    //===============-------FuncPermissionChecker------==================
}