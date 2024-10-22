package com.example.babybook;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.babybook.adapter.ClinicAdapter;
import com.example.babybook.adapter.PostAdapter;
import com.example.babybook.model.Clinic;
import com.example.babybook.model.Doctor;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
    private Button all, bgc, hepatitis_b, dpt, booster_1, opv_ipv, booster_2, h_influenza_b, rotavirus, measles, mmr, booster_3;

    private RecyclerView recyclerView;
    private ClinicAdapter clinicAdapter;
    private List<Clinic> clinicList;
    private FirebaseFirestore db;
    private GoogleMap mMap;
    private LatLng initialLocation;
    private final Integer LOCATION_PERMISSION_REQUEST_CODE = 123;
    private NestedScrollView scrollView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private EditText etVaccine;
    private Button btnSearch;
    private TextView tvNoClinics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_clinic);
        db = FirebaseFirestore.getInstance();


        // Initialize buttons
        all = findViewById(R.id.all);
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
        etVaccine = findViewById(R.id.editTextVaccine);
        btnSearch = findViewById(R.id.buttonSearch);
        tvNoClinics = findViewById(R.id.tvNoClinic);

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

        all.setOnClickListener(view -> loadClinics());

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
        mapWrapper.setOnTouchListener(event -> {
            // Disable parent scroll when interacting with the map
            scrollView.requestDisallowInterceptTouchEvent(true);
            //swipeRefreshLayout.setEnabled(false); // Disable swipe refresh
        });


        // Filter by vaccine
        btnSearch.setOnClickListener(v -> {
            String query = etVaccine.getText().toString();
            if (!TextUtils.isEmpty(query)) {
                searchClinics(query);
            } else {
                Toast.makeText(SearchClinicActivity.this, "Please enter a vaccine to search.", Toast.LENGTH_SHORT).show();
            }
        });

        // Initialize the map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.ivMapView);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

    }

    // Method to handle button clicks (FILTER VACCINE BUTTONS)
    private void handleButtonClick(String buttonText) {
        // Reset button colors to default
        resetButtonColors();

        // Change the clicked button's color to violet
        Button clickedButton = findViewById(getButtonId(buttonText));
        if (clickedButton != null) {
            clickedButton.setBackgroundResource(R.drawable.rounded_violet_btn);
            clickedButton.setTextColor(getResources().getColor(R.color.white));
        }

        // Update the EditText with the button text
        etVaccine.setText(buttonText);

        // Trigger the search button click
        btnSearch.performClick();
    }

    // Method to reset button colors to default
    private void resetButtonColors() {
        // Set the background resource to the rounded border drawable
        int defaultDrawable = R.drawable.rounded_border_black_btn; // Your drawable resource
        int defaultTextColor = getResources().getColor(R.color.black); // Black color for text

        // Reset each button's background and text color
        all.setBackgroundResource(defaultDrawable);
        all.setTextColor(defaultTextColor);

        bgc.setBackgroundResource(defaultDrawable);
        bgc.setTextColor(defaultTextColor);

        hepatitis_b.setBackgroundResource(defaultDrawable);
        hepatitis_b.setTextColor(defaultTextColor);

        dpt.setBackgroundResource(defaultDrawable);
        dpt.setTextColor(defaultTextColor);

        booster_1.setBackgroundResource(defaultDrawable);
        booster_1.setTextColor(defaultTextColor);

        opv_ipv.setBackgroundResource(defaultDrawable);
        opv_ipv.setTextColor(defaultTextColor);

        booster_2.setBackgroundResource(defaultDrawable);
        booster_2.setTextColor(defaultTextColor);

        h_influenza_b.setBackgroundResource(defaultDrawable);
        h_influenza_b.setTextColor(defaultTextColor);

        rotavirus.setBackgroundResource(defaultDrawable);
        rotavirus.setTextColor(defaultTextColor);

        measles.setBackgroundResource(defaultDrawable);
        measles.setTextColor(defaultTextColor);

        mmr.setBackgroundResource(defaultDrawable);
        mmr.setTextColor(defaultTextColor);

        booster_3.setBackgroundResource(defaultDrawable);
        booster_3.setTextColor(defaultTextColor);
    }


    // Helper method to get button ID based on text
    private int getButtonId(String buttonText) {
        switch (buttonText) {
            case "BGC":
                return R.id.bgc;
            case "Hepatitis B":
                return R.id.hepatitis_b;
            case "DPT":
                return R.id.dpt;
            case "Booster 1":
                return R.id.booster_1;
            case "OPV IPV":
                return R.id.opv_ipv;
            case "Booster 2":
                return R.id.booster_2;
            case "H Influenza B":
                return R.id.h_influenza_b;
            case "Rotavirus":
                return R.id.rotavirus;
            case "Measles":
                return R.id.measles;
            case "MMR":
                return R.id.mmr;
            case "Booster 3":
                return R.id.booster_3;
            default:
                return -1; // Invalid button
        }
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
        tvNoClinics.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(true);
        resetButtonColors();
        all.setBackgroundResource(R.drawable.rounded_violet_btn);
        all.setTextColor(getResources().getColor(R.color.white));
        db.collection("clinics")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Clear the existing list
                        clinicList.clear();
                        swipeRefreshLayout.setRefreshing(false);
                        etVaccine.setText("");
                        etVaccine.clearFocus();


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
                                BitmapDescriptor customMarker = BitmapDescriptorFactory.fromResource(R.drawable.custom_marker_clinic);

                                LatLng clinicLocation = new LatLng(latitude, longitude);
                                mMap.addMarker(new MarkerOptions()
                                                .position(clinicLocation)
                                                .title(clinic.getClinicName())
                                                .snippet(clinic.getClinicPhoneNumber()))
                                                .setIcon(customMarker);
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

    // Method to search clinics with vaccine based on the query
    private void searchClinics(String query) {
        swipeRefreshLayout.setRefreshing(true);
        String lowerCaseQuery = query.trim().toLowerCase();
        tvNoClinics.setVisibility(View.GONE);

        db.collection("clinics")
                .get()
                .addOnCompleteListener(task -> {
                    swipeRefreshLayout.setRefreshing(false);
                    if (task.isSuccessful()) {
                        clinicList.clear();
                        mMap.clear();
                        boolean foundClinics = false;

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Integer> vaccines = (Map<String, Integer>) document.get("vaccines");
                            if (vaccines != null) {
                                for (String vaccineName : vaccines.keySet()) {
                                    if (vaccineName.toLowerCase().equals(lowerCaseQuery)) {
                                        // Create a Clinic object with all necessary details
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

                                        // Create a Clinic object
                                        Clinic clinic = new Clinic(clinicId, clinicName, clinicPhoneNumber, clinicProfileUrl,
                                                schedDays, schedStartTime, schedEndTime, doctorId, doctorName,
                                                latitude, longitude, clinicAddress, timestamp, profileImageUrl, specialization, vaccines);

                                        clinicList.add(clinic);
                                        if (latitude != null && longitude != null) {
                                            BitmapDescriptor customMarker = BitmapDescriptorFactory.fromResource(R.drawable.custom_marker_clinic);

                                            LatLng clinicLocation = new LatLng(latitude, longitude);
                                            mMap.addMarker(new MarkerOptions()
                                                            .position(clinicLocation)
                                                            .title(clinic.getClinicName())
                                                            .snippet(clinic.getClinicPhoneNumber()))
                                                            .setIcon(customMarker);
                                        }
                                        foundClinics = true;
                                        break;
                                    }
                                }
                            }
                        }

                        clinicAdapter.notifyDataSetChanged();

                        if (!foundClinics) {
                            tvNoClinics.setVisibility(View.VISIBLE);
                            tvNoClinics.setText("No clinics with vaccine: " + query);
                        } else {
                            tvNoClinics.setVisibility(View.GONE);
                        }
                    } else {
                        Toast.makeText(SearchClinicActivity.this, "Error getting clinics.", Toast.LENGTH_SHORT).show();
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
