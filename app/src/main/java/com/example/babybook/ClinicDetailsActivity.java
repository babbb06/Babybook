package com.example.babybook;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Map;

public class ClinicDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private TextView tvClinicName, tvDoctorName, tvDoctorSpecialization, tvClinicAddress, tvTime, tvDayMon, tvDayTue, tvDayWed, tvDayThu, tvDayFri, tvDaySat, tvDaySun;
    private TextView tvBcg, tvHepatitisB, tvDpt, tvBooster1, tvMmr, tvOpvIpv, tvBooster2, tvHInfluenzaB, tvRotavirus, tvMeasles, tvBooster3;
    private ImageView ivClinicImg;

    private GoogleMap mMap;
    private LatLng initialLocation;
    private Integer LOCATION_PERMISSION_REQUEST_CODE = 123;
    private ScrollView scrollView;
    private Double latitude, longitude;
    private String clinicPhoneNumber, clinicName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic_details);
        scrollView = findViewById(R.id.scrollView1);

        // Initialize the views
        tvClinicName = findViewById(R.id.textClinicName);
        tvTime = findViewById(R.id.texttime);
        tvDoctorName = findViewById(R.id.tvDocName);
        tvDoctorSpecialization = findViewById(R.id.tvSpecialization);
        tvClinicAddress = findViewById(R.id.tvClinicAddress);
        ivClinicImg = findViewById(R.id.ivClinicImage);

        tvDayMon = findViewById(R.id.day_mon);
        tvDayTue = findViewById(R.id.day_tue);
        tvDayWed = findViewById(R.id.day_wed);
        tvDayThu = findViewById(R.id.day_thu);
        tvDayFri = findViewById(R.id.day_fri);
        tvDaySat = findViewById(R.id.day_sat);
        tvDaySun = findViewById(R.id.day_sun);

        //  vaccines
        tvBcg = findViewById(R.id.day_bcg);
        tvHepatitisB = findViewById(R.id.day_hepatitis_b);
        tvDpt = findViewById(R.id.day_dpt);
        tvBooster1 = findViewById(R.id.day_booster1);
        tvMmr = findViewById(R.id.day_mmr);
        tvOpvIpv = findViewById(R.id.day_opv_ipv);
        tvBooster2 = findViewById(R.id.day_booster2);
        tvHInfluenzaB = findViewById(R.id.day_h_influenza_b);
        tvRotavirus = findViewById(R.id.day_rotavirus);
        tvMeasles = findViewById(R.id.day_measles);
        tvBooster3 = findViewById(R.id.day_booster3);

        // Define your button
        Button bookBtn = findViewById(R.id.Bookbtn);

// Set an onClick listener




        // Retrieve the data from the intent
        Intent intent = getIntent();
        String clinicId = intent.getStringExtra("clinicId");
        clinicName = intent.getStringExtra("clinicName"); // class level
        clinicPhoneNumber = intent.getStringExtra("clinicPhoneNumber"); // class level
        String clinicProfileUrl = intent.getStringExtra("clinicProfileUrl");
        List<String> schedDays = intent.getStringArrayListExtra("schedDays");
        String schedStartTime = intent.getStringExtra("schedStartTime");
        String schedEndTime = intent.getStringExtra("schedEndTime");
        String doctorId = intent.getStringExtra("doctorId");
        String doctorName = intent.getStringExtra("doctorName");
        latitude = intent.getDoubleExtra("latitude", 0); // class level
        longitude = intent.getDoubleExtra("longitude", 0); // class level
        String clinicAddress = intent.getStringExtra("clinicAddress");
        long timestamp = intent.getLongExtra("timestamp", 0);
        String profileImageUrl = intent.getStringExtra("profileImageUrl");
        String specialization = intent.getStringExtra("specialization");
        Map<String, Long> vaccines = (Map<String, Long>) intent.getSerializableExtra("vaccines");

        // display the clinic details
        tvClinicName.setText(clinicName);
        tvDoctorName.setText(doctorName);
        tvDoctorSpecialization.setText(specialization);
        tvClinicAddress.setText(clinicAddress);
        tvTime.setText(schedStartTime + " to " + schedEndTime);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Clinic Details");
        }

        bookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle book appointment button click
                Intent intent = new Intent(ClinicDetailsActivity.this, BookAppointmentActivity.class);
                intent.putExtra("doctorId", doctorId); // Remove `.getId()` since doctorId is already a String
                startActivity(intent);
            }
        });




        // Check which days are in the list and set backgrounds and text colors accordingly
        if (schedDays != null) {
            setDayProperties(tvDayMon, "Monday", schedDays);
            setDayProperties(tvDayTue, "Tuesday", schedDays);
            setDayProperties(tvDayWed, "Wednesday", schedDays);
            setDayProperties(tvDayThu, "Thursday", schedDays);
            setDayProperties(tvDayFri, "Friday", schedDays);
            setDayProperties(tvDaySat, "Saturday", schedDays);
            setDayProperties(tvDaySun, "Sunday", schedDays);
        }

        //display vaccines available
        updateVaccineDisplay(vaccines);



        // Load the image using Glide
        Glide.with(this)
                .load(clinicProfileUrl)
                .into(ivClinicImg);

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

    // method to set background and text color for Days
    private void setDayProperties(TextView textView, String day, List<String> schedDays) {
        if (schedDays.contains(day)) {
            textView.setBackgroundResource(R.drawable.day_background_selected);
            textView.setTextColor(getResources().getColor(android.R.color.white));
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);

        }
    }

    // method to set background and text color for vaccines
    private void updateVaccineDisplay(Map<String, Long> vaccines) {
        if (vaccines != null) {
            for (Map.Entry<String, Long> entry : vaccines.entrySet()) {
                String vaccineName = entry.getKey();
                Long quantity = entry.getValue();

                Log.d("Vaccines", "Vaccine: " + vaccineName + ", Quantity: " + quantity);

                // Update TextView based on vaccine availability
                switch (vaccineName) {
                    case "BCG":
                        setVaccineProperties(tvBcg, quantity);
                        break;
                    case "Hepatitis B":
                        setVaccineProperties(tvHepatitisB, quantity);
                        break;
                    case "DPT":
                        setVaccineProperties(tvDpt, quantity);
                        break;
                    case "Booster 1":
                        setVaccineProperties(tvBooster1, quantity);
                        break;
                    case "MMR":
                        setVaccineProperties(tvMmr, quantity);
                        break;
                    case "OPV IPV":
                        setVaccineProperties(tvOpvIpv, quantity);
                        break;
                    case "Booster 2":
                        setVaccineProperties(tvBooster2, quantity);
                        break;
                    case "H Influenza B":
                        setVaccineProperties(tvHInfluenzaB, quantity);
                        break;
                    case "Rotavirus":
                        setVaccineProperties(tvRotavirus, quantity);
                        break;
                    case "Measles":
                        setVaccineProperties(tvMeasles, quantity);
                        break;
                    case "Booster 3":
                        setVaccineProperties(tvBooster3, quantity);
                        break;
                }
            }
        } else {
            Log.d("Vaccines", "No vaccines available.");
        }
    }

    private void setVaccineProperties(TextView textView, Long quantity) {
        if (quantity > 0) {
            textView.setBackgroundResource(R.drawable.day_background_selected);
            textView.setTextColor(getResources().getColor(android.R.color.white));
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
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

        //initialLocation = new LatLng(14.95, 120.75); // APALIT
        LatLng clinicLocation = new LatLng(latitude, longitude);

        // Enable zoom controls and compass
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        // Move camera to the initial location
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(clinicLocation, 13));

        // Add a marker for the clinic on the map
        if (latitude != null && longitude != null && (latitude != 0 && longitude != 0)) {
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(clinicLocation)
                    .title(clinicName) // Assuming clinicName is accessible here
                    .snippet(clinicPhoneNumber)); // Optional: add a snippet with phone number

            // Immediately show the info window for the marker
            marker.showInfoWindow();
        }
    }

    // BACK BUTTON
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Handle back button
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
