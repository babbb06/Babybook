package com.example.babybook;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.babybook.model.Clinic;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import android.Manifest;
import android.widget.Toast;


public class AddClinicActivity extends AppCompatActivity implements OnMapReadyCallback {

    private LinearLayout vaccineList;
    private List<String> vaccines;
    private TextInputEditText etStartTime, etEndTime;
    private TextView tvNoPin, tvPinSuccess;
    private GoogleMap mMap;
    private LatLng initialLocation;
    private LatLng selectedLocation;
    private Integer LOCATION_PERMISSION_REQUEST_CODE = 123;
    private Button btnSubmit;
    private FirebaseFirestore db;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_clinic);

        NestedScrollView scrollView = findViewById(R.id.nestedScrollView);  // Adjust this if you're using NestedScrollView or another scrollable parent



        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Clinic");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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

        vaccineList = findViewById(R.id.vaccineList);
        Button addButton = findViewById(R.id.addButton);
        etStartTime = findViewById(R.id.etStartTime);
        etEndTime = findViewById(R.id.etEndTime);
        tvNoPin = findViewById(R.id.tvnoPin);
        tvPinSuccess = findViewById(R.id.tvPinSuccess);
        btnSubmit = findViewById(R.id.btnSubmit);
        db = FirebaseFirestore.getInstance();

        // List of vaccines
        vaccines = new ArrayList<>();
        vaccines.add("BCG");
        vaccines.add("Hepatitis B");
        vaccines.add("DPT");
        vaccines.add("Booster 1");
        vaccines.add("OPV IPV");
        vaccines.add("Booster 2");
        vaccines.add("H Influenza B");
        vaccines.add("Rotavirus");
        vaccines.add("Measles");
        vaccines.add("MMR");
        vaccines.add("Booster 3");

        etStartTime.setOnClickListener(v -> showTimePickerDialog(true));
        etEndTime.setOnClickListener(v -> showTimePickerDialog(false));

        addButton.setOnClickListener(view -> showVaccineSelectionDialog());

        btnSubmit.setOnClickListener(v -> {
            if (selectedLocation != null) {
                createClinic(selectedLocation.latitude, selectedLocation.longitude);
                Log.d("ClinicData", "Latitude: " + selectedLocation.latitude + ", Longitude: " + selectedLocation.longitude);

            } else {
                Toast.makeText(this, "Please select a location on the map.", Toast.LENGTH_SHORT).show();
            }
        });


    }

    // STORE CLINIC DETAILS TO DB

    private void createClinic(Double latitude, Double longitude) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            db.collection("doctorUsers").document(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String doctorName = document.getString("fullName");
                        String doctorProfile = document.getString("profileImageUrl");
                        String specialization = document.getString("specialization");

                        // Ensure name and profile are not null
                        if (doctorName != null && doctorProfile != null) {
                            Clinic clinic = new Clinic(null, userId, doctorName, latitude, longitude, System.currentTimeMillis(), doctorProfile, specialization);

                            // Add the Clinic to Firestore
                            db.collection("clinics").add(clinic).addOnSuccessListener(documentReference -> {
                                // Get the auto-generated Clinic and update the Clinic document
                                String clinicId = documentReference.getId();
                                documentReference.update("clinicId", clinicId)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(AddClinicActivity.this, "Clinic created", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(AddClinicActivity.this, DoctorDashboardActivity.class));
                                            finish();

                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(AddClinicActivity.this, "Error updating clinicId", Toast.LENGTH_SHORT).show();
                                        });
                            }).addOnFailureListener(e -> {
                                Toast.makeText(AddClinicActivity.this, "Error creating Clinic", Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            Toast.makeText(AddClinicActivity.this, "Doctor profile data missing", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    private void showVaccineSelectionDialog() {
        CharSequence[] vaccineArray = vaccines.toArray(new CharSequence[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose a Vaccine")
                .setItems(vaccineArray, (dialog, which) -> {
                    String selectedVaccine = vaccineArray[which].toString();
                    showVaccineInputDialog(selectedVaccine);
                })
                .setNegativeButton("Cancel", null);

        builder.create().show();
    }

    private void showVaccineInputDialog(String vaccine) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Vaccine Details");

        // Inflate the custom dialog layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_vaccine_details, null);
        builder.setView(dialogView);

        // Get references to the TextView and EditText in the dialog
        TextView vaccineNameTextView = dialogView.findViewById(R.id.vaccineNameTextView);
        EditText quantityInput = dialogView.findViewById(R.id.quantityInput);
        ImageView buttonPlus = dialogView.findViewById(R.id.buttonPlus);
        ImageView buttonMinus = dialogView.findViewById(R.id.buttonMinus);

        // Set the vaccine name in the TextView
        vaccineNameTextView.setText(vaccine);

        // Set initial quantity
        quantityInput.setText("1");

        // Add functionality for the minus button
        buttonMinus.setOnClickListener(view -> {
            int currentQuantity = Integer.parseInt(quantityInput.getText().toString());
            if (currentQuantity > 1) {
                currentQuantity--;
                quantityInput.setText(String.valueOf(currentQuantity));
            }
        });

        // Add functionality for the plus button
        buttonPlus.setOnClickListener(view -> {
            int currentQuantity = Integer.parseInt(quantityInput.getText().toString());
            if (currentQuantity < 999) { // Set a max limit for safety
                currentQuantity++;
                quantityInput.setText(String.valueOf(currentQuantity));
            }
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
                    String quantity = quantityInput.getText().toString();
                    addVaccineToList(vaccine, quantity);
                })
                .setNegativeButton("Cancel", null);

        // Show the dialog
        builder.create().show();
    }

    private void addVaccineToList(String vaccine, String quantity) {
        // Inflate the vaccine list item layout
        View vaccineItem = getLayoutInflater().inflate(R.layout.vaccine_list_item, vaccineList, false);

        // Find the TextViews in the inflated layout
        TextView vaccineNameTextView = vaccineItem.findViewById(R.id.vaccineNameTextView);
        TextView quantityTextView = vaccineItem.findViewById(R.id.quantityTextView);

        // Set the text for the TextViews
        vaccineNameTextView.setText(vaccine);
        quantityTextView.setText(quantity);

        // Add the inflated view to the vaccine list
        vaccineList.addView(vaccineItem);
    }
    // Override onOptionsItemSelected to handle back button click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showTimePickerDialog(final boolean isStartTime) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Adjust the minute to the nearest 00 or 30
        minute = (minute < 15) ? 0 : (minute < 45) ? 30 : 0;
        if (minute == 0 && calendar.get(Calendar.HOUR_OF_DAY) == 23) {
            hour = 0; // Wrap around to 0 when it's 23:00
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {
                    String time = String.format("%02d:%02d %s",
                            selectedHour % 12 == 0 ? 12 : selectedHour % 12,
                            selectedMinute,
                            selectedHour < 12 ? "AM" : "PM");

                    if (isStartTime) {
                        etStartTime.setText(time);
                    } else {
                        etEndTime.setText(time);
                    }
                },
                hour,
                minute, // Use adjusted minute
                false // Use 12-hour format
        ) {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                // Restrict minutes to 00 or 30
                if (minute != 0 && minute != 30) {
                    view.setMinute(minute < 15 ? 0 : 30);
                }
                super.onTimeChanged(view, hourOfDay, minute);
            }
        };

        timePickerDialog.show();
    }

    /*---------------------------------MAP----------------------------------------------------*/

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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

        googleMap.setOnMapLongClickListener(latLng -> {
            googleMap.clear(); // Clear previous markers
            googleMap.addMarker(new MarkerOptions().position(latLng).title("Clinic Location"));
            selectedLocation = latLng; // Store the selected location

            Log.d("MapClick", "Location selected: " + selectedLocation.latitude + "LONG: " + selectedLocation.latitude);
            tvNoPin.setVisibility(View.GONE);
            tvPinSuccess.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, enable location
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
            } else {
                // Permission denied
            }
        }
    }


}

