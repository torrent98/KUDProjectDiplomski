package com.example.kudproject.maps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.kudproject.R;
import com.example.kudproject.members.AllMembersActivity;
import com.example.kudproject.performance.Performance;
import com.example.kudproject.performance.PerformanceRVAdapter;
import com.example.kudproject.performance.Performances;
import com.example.kudproject.performance.PerformancesRecyclerViewInterface;
import com.example.kudproject.user.UserSettings;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Navigation extends AppCompatActivity implements OnMapReadyCallback, PerformancesRecyclerViewInterface {

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            init();

        }

    }

    //-------Recycler View Performances--------------//

    private RecyclerView perfRV;
    private ArrayList<Performance> perfList;
    private PerformanceRVAdapter performanceRVAdapter;

    DatabaseReference databaseReference;

    //-------Recycler View Performances-------------//

    private static final String TAG = "Navigation";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    private EditText mSearchAdress;
    private ImageView gps;

    private Boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        //mSearchAdress = findViewById(R.id.input_search);
        gps = findViewById(R.id.gps);

        //-----------------------------Perf adapter and getPerfList--------------------------------//

        perfRV = findViewById(R.id.rv_horizontal_perf);

        perfList = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Performance");

        // on below line initializing our adapter class.
        performanceRVAdapter = new PerformanceRVAdapter(perfList,this,this);

        // setting layout manager to recycler view on below line.
        perfRV.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL,false));

        // setting adapter to recycler view on below line.
        perfRV.setAdapter(performanceRVAdapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                perfList.clear();

                for (DataSnapshot data : snapshot.getChildren())
                {

                    Performance performance = data.getValue(Performance.class);
                    performance.setKey(data.getKey());

                    perfList.add(performance);
                    //key = data.getKey();
                }
                performanceRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                performanceRVAdapter.notifyDataSetChanged();
            }
        });

        //-----------------------------Perf adapter and getPerfList--------------------------------//


        BottomNavigationView nav = findViewById(R.id.bottom_nav);
        nav.setSelectedItemId(R.id.nav_map);

        nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener(){

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.nav_members:
                        startActivity(new Intent(getApplicationContext(), AllMembersActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.nav_map:
                        return true;

                    case R.id.nav_perf:
                        startActivity(new Intent(getApplicationContext(), Performances.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.nav_settings:
                        startActivity(new Intent(getApplicationContext(), UserSettings.class));
                        overridePendingTransition(0,0);
                        return true;
                }

                return false;
            }

        });

        getLocationPermission();

    }

    private void init(){
        Log.d(TAG, "init: initializing");

//        mSearchAdress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//
//                if(actionId == EditorInfo.IME_ACTION_SEARCH ||
//                    actionId == EditorInfo.IME_ACTION_DONE ||
//                    event.getAction() == KeyEvent.ACTION_DOWN ||
//                    event.getAction() == KeyEvent.KEYCODE_ENTER){
//
//                    geoLocate();
//
//                }
//
//                return false;
//            }
//        });

        geoLocate();

        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation();
            }
        });

        hideSoftKeyboard();

    }

    private void geoLocate() {

        Log.d(TAG, "geoLocate: geolocating");



        //String searchString = mSearchAdress.getText().toString();

        Geocoder geocoder = new Geocoder(Navigation.this);

        List<Address> list = new ArrayList<>();

//        try {
//            list = geocoder.getFromLocationName(searchString, 1);
//        }catch (IOException e){
//            Log.e(TAG, "geoLocate: IOException: " + e.getMessage());
//        }

        if(list.size() > 0){
            Address address = list.get(0);
            Log.d(TAG, "geoLocate: found a location: " + address.toString());

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));

        }

    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the device current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {

            if(mLocationPermissionGranted){

                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()), DEFAULT_ZOOM, "My Location");

                        }else{
                            Log.d(TAG, "onComplete: current location is null!");
                            Toast.makeText(Navigation.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

        }catch (SecurityException e){
            Log.e(TAG, "getDevicLocation: SecurityException: " + e.getMessage());
        }

    }

    private void moveCamera(LatLng latLng, float zoom, String title){

        Log.d(TAG, "moveCamera: moving camera to lat:" + latLng.latitude + ", lng:" + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if(!title.equals("My Location")){
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);

            mMap.addMarker(options);
        }

        hideSoftKeyboard();

    }

    private void initMap(){
        Log.d(TAG, "initMap: initializing map");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(Navigation.this);

    }

    private void getLocationPermission(){

        Log.d(TAG, "getLocationPermission: getting location permissions");

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(), COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        Log.d(TAG, "onRequestPermissionsResult: called");
        mLocationPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionGranted = true;
                    //inicijalizacija mape
                    initMap();
                }
            }
        }

    }

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onItemClick(int position) {

        Performance performance = perfList.get(position);

//        Intent intent = new Intent(Navigation.this, ViewPerformanceDetails.class);
//
//        intent.putExtra("performance", performance);
//
//        startActivity(intent);

        String adresa = getPerformanceAdress(performance);

        Log.d(TAG, "geoLocate: geolocating");

        //String searchString = mSearchAdress.getText().toString();

        Geocoder geocoder = new Geocoder(Navigation.this);

        List<Address> list = new ArrayList<>();

        try {
            list = geocoder.getFromLocationName(adresa, 1);
        }catch (IOException e){
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage());
        }

        if(list.size() > 0){
            Address address = list.get(0);
            Log.d(TAG, "geoLocate: found a location: " + address.toString());

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));

        }

    }

    private String getPerformanceAdress(Performance performance) {

        String adresica = performance.getLokacija();

        return adresica;
    }
}