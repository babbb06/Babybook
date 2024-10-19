package com.example.babybook;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.babybook.adapter.ClinicAdapter;
import com.example.babybook.adapter.PostAdapter;
import com.example.babybook.model.Clinic;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SearchClinicActivity extends AppCompatActivity implements OnMapReadyCallback {

    // Declare buttons
    private Button bgc, hepatitis_b, dpt, booster_1, opv_ipv, booster_2, h_influenza_b, rotavirus, measles, mmr, booster_3;

    private RecyclerView recyclerView;
    private ClinicAdapter clinicAdapter;
    private List<Clinic> clinicList;
    private FirebaseFirestore db;
    private GoogleMap mMap;
    private LatLng initialLocation;
    private final Integer LOCATION_PERMISSION_REQUEST_CODE = 123;
    private ScrollView scrollView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private EditText etSpecialization;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_clinic);
        db = FirebaseFirestore.getInstance();


        // Initialize buttons
        bgc = findViewById(R.id.bgc);
        hepatitis_b = findViewById(R.id.hepatitis_b);
        dpt = findViewById(R.id.dpt);
        booster_1 = findViewById(R.id.booster_1);
        opv_ipv = findViewById(R.id.opv_ipv);
        booster_2 = findViewById(R.id.booster_2);
        h_influenza_b = findViewById(R.id.h_influenza_b);
        rotavirus = findViewById(R.id.rotavirus);
        measles = findViewById(R.id.measles);
        mmr = findViewById(R.id.mmr);
        booster_3 = findViewById(R.id.booster_3);
        scrollView = findViewById(R.id.scrollView);
        etSpecialization = findViewById(R.id.editTextSpecialization);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Search Clinic");
        }

        // Set onClickListeners for each button
        bgc.setOnClickListener(view -> handleButtonClick("BGC"));
        hepatitis_b.setOnClickListener(view -> handleButtonClick("Hepatitis B"));
        dpt.setOnClickListener(view -> handleButtonClick("DPT"));
        booster_1.setOnClickListener(view -> handleButtonClick("Booster 1"));
        opv_ipv.setOnClickListener(view -> handleButtonClick("OPV IPV"));
        booster_2.setOnClickListener(view -> handleButtonClick("Booster 2"));
        h_influenza_b.setOnClickListener(view -> handleButtonClick("H Influenza B"));
        rotavirus.setOnClickListener(view -> handleButtonClick("Rotavirus"));
        measles.setOnClickListener(view -> handleButtonClick("Measles"));
        mmr.setOnClickListener(view -> handleButtonClick("MMR"));
        booster_3.setOnClickListener(view -> handleButtonClick("Booster 3"));

        //Showing clinic list
        recyclerView = findViewById(R.id.recyclerViewClinics);
        clinicList = new ArrayList<>();
        clinicAdapter = new ClinicAdapter(clinicList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(clinicAdapter);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this::loadClinics);

        loadClinics();

        // Set up the custom MapTouchableWrapper
        MapTouchableWrapper mapWrapper = findViewById(R.id.map_wrapper);
        mapWrapper.setOnTouchListener(() -> {
            // Disable parent scroll when interacting with the map
            scrollView.requestDisallowInterceptTouchEvent(true);
        });

        // Initialize the map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.ivMapView);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

    }

    // Method to handle button clicks
    private void handleButtonClick(String buttonText) {
        // Implement your button click logic here
        // For example, you can show a toast message
        // Toast.makeText(this, buttonText + " clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Method to load clinic data
    private void loadClinics() {
        swipeRefreshLayout.setRefreshing(true);
        db.collection("clinics")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Clear the existing list
                        clinicList.clear();
                        swipeRefreshLayout.setRefreshing(false);
                        etSpecialization.setText("");
                        etSpecialization.clearFocus();


                        // Loop through the documents
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String clinicId = document.getId();
                            String clinicName = document.getString("clinicName");
                            String clinicPhoneNumber = document.getString("clinicPhoneNumber");
                            String clinicProfileUrl = document.getString("clinicProfileUrl");
                            List<String> schedDays = (List<String>) document.get("schedDays");
                            String schedStartTime = document.getString("schedStartTime");
                            String schedEndTime = document.getString("schedEndTime");
                            String clinicAddress = document.getString("clinicAddress");
                            String doctorId = document.getString("doctorId");
                            String doctorName = document.getString("doctorName");
                            Double latitude = document.getDouble("latitude");
                            Double longitude = document.getDouble("longitude");
                            long timestamp = document.getLong("timestamp");
                            String profileImageUrl = document.getString("profileImageUrl");
                            String specialization = document.getString("specialization");
                            Map<String, Integer> vaccines = (Map<String, Integer>) document.get("vaccines");

                            // Create a Clinic object
                            Clinic clinic = new Clinic(clinicId, clinicName, clinicPhoneNumber, clinicProfileUrl,
                                    schedDays, schedStartTime, schedEndTime, doctorId, doctorName,
                                    latitude, longitude, clinicAddress, timestamp, profileImageUrl, specialization, vaccines);

                            // Add to the list
                            clinicList.add(clinic);

                            // Add a marker for each clinic on the map
                            if (latitude != null && longitude != null) {
                                LatLng clinicLocation = new LatLng(latitude, longitude);
                                mMap.addMarker(new MarkerOptions()
                                        .position(clinicLocation)
                                        .title(clinicName)
                                        .snippet(clinicPhoneNumber));
                            }
                        }

                        // Notify adapter that the data has changed
                        clinicAdapter.notifyDataSetChanged();
                    } else {
                        // Handle error
                        Log.w("Firestore", "Error getting documents.", task.getException());
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }


    /*---------------------------------MAP----------------------------------------------------*/

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }

        initialLocation = new LatLng(14.95, 120.75); // APALIT

        // Enable zoom controls and compass
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        // Move camera to the initial location
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 13));

        // Load clinic data
        loadClinics();
    }
}
