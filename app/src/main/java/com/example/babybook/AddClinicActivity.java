package com.example.babybook;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.babybook.model.Clinic;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import android.Manifest;
import android.widget.Toast;


public class AddClinicActivity extends AppCompatActivity implements OnMapReadyCallback {

    private LinearLayout vaccineList;
    private List<String> vaccines;
    private Map<String, Integer> vaccineMap = new HashMap<>();

    private TextInputEditText etStartTime, etEndTime, eTClinicAddress;
    private TextView tvNoPin, tvPinSuccess;
    private GoogleMap mMap;
    private LatLng initialLocation;
    private LatLng selectedLocation;
    private Integer LOCATION_PERMISSION_REQUEST_CODE = 123;
    private Button btnSubmit, btnCancel;
    private FirebaseFirestore db;
    EditText etFirstName, etLastName, etEmail, etClinicName, etClinicNumber;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;
    private ImageView selectedClinicImage;
    private boolean isImageSelected = false;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_clinic);

        NestedScrollView scrollView = findViewById(R.id.nestedScrollView);

        TextView errorTextView = findViewById(R.id.errorTextView);


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
        eTClinicAddress= findViewById(R.id.eTClinicAddress);
        tvNoPin = findViewById(R.id.tvnoPin);
        tvPinSuccess = findViewById(R.id.tvPinSuccess);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnCancel = findViewById(R.id.btnCancel);

        etFirstName = findViewById(R.id.editTextFirstName);
        etLastName = findViewById(R.id.lastnameEt);
        etEmail = findViewById(R.id.editTextEmail);
        etClinicName = findViewById(R.id.editTextClinicName);
        etClinicNumber = findViewById(R.id.etPhoneNumberClinic);
        selectedClinicImage = findViewById(R.id.ivSelectedImagefront);
        ConstraintLayout btnAddClinicImage = findViewById(R.id.btnAddImagefront);

        db = FirebaseFirestore.getInstance();

        // Fetch Doc data
        fetchUserData();

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

        btnAddClinicImage.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Profile Photo to Continue"), PICK_IMAGE_REQUEST);
        });

        btnSubmit.setOnClickListener(v -> {
            if (!validateCheckboxes()) {
                // Show error message
                Toast.makeText(getApplicationContext(), "Please select at least one day.", Toast.LENGTH_SHORT).show();
            } else {
                // Proceed with your logic, e.g., saving data
            }
            if (validateInputs()) {  // Validate all inputs before creating the clinic
                if (selectedLocation != null) {
                    createClinic(selectedLocation.latitude, selectedLocation.longitude);
                    Log.d("ClinicData", "Latitude: " + selectedLocation.latitude + ", Longitude: " + selectedLocation.longitude);
                } else {
                    Toast.makeText(this, "Please select a location on the map.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please fill in all the required fields.", Toast.LENGTH_SHORT).show();
            }
        });


        btnCancel.setOnClickListener(v -> {
            finish(); // Close the activity
        });





    }
    private boolean validateInputs() {


        if (etClinicName.getText().toString().trim().isEmpty()) {
            etClinicName.setError("Clinic name is required");
            etClinicName.requestFocus();
            return false;
        }
        if (etClinicNumber.getText().toString().trim().isEmpty()) {
            etClinicNumber.setError("Phone number is required");
            etClinicNumber.requestFocus();
            return false;
        }
        if (etStartTime.getText().toString().trim().isEmpty()) {
            etStartTime.setError("Start time is required");
            etStartTime.requestFocus();
            return false;
        }
        if (etEndTime.getText().toString().trim().isEmpty()) {
            etEndTime.setError("End time is required");
            etEndTime.requestFocus();
            return false;
        }

        if (eTClinicAddress.getText().toString().trim().isEmpty()) {
            eTClinicAddress.setError("Clinic Address is required");
            eTClinicAddress.requestFocus();
            return false;
        }


        if (selectedImageUri == null) {
            Toast.makeText(this, "Please select a clinic image", Toast.LENGTH_SHORT).show();

            return false;
        }



        return true;
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
                        String doctorProfile = document.getString("profileImageUrl"); // Optional if needed for the doctor
                        String specialization = document.getString("specialization");
                        String clinicName = etClinicName.getText().toString().trim();
                        String clinicPhoneNumber = "+63" + etClinicNumber.getText().toString().trim();
                        String schedStartTime = etStartTime.getText().toString().trim();
                        String schedEndTime = etEndTime.getText().toString().trim();
                        String clinicAddress = eTClinicAddress.getText().toString().trim();
                        List<String> selectedDays = getSelectedDays();

                        // Ensure required fields are not null
                        if (doctorName != null && selectedImageUri != null) {
                            // Upload the image to Firebase Storage
                            String imageFileName = clinicName + "_Image.jpg";
                            StorageReference storageRef = FirebaseStorage.getInstance().getReference("clinicImages/" + imageFileName);

                            storageRef.putFile(selectedImageUri)
                                    .addOnSuccessListener(taskSnapshot -> {
                                        // Get the download URL of the image
                                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                            String clinicProfileUrl = uri.toString(); // Get the image URL

                                            // Create Clinic object with clinicProfileUrl
                                            Clinic clinic = new Clinic(null, clinicName, clinicPhoneNumber, clinicProfileUrl, selectedDays, schedStartTime, schedEndTime,clinicAddress, userId, doctorName, latitude, longitude, System.currentTimeMillis(), doctorProfile, specialization, vaccineMap);

                                            // Add Clinic to Firestore
                                            db.collection("clinics").add(clinic).addOnSuccessListener(documentReference -> {
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
                                        }).addOnFailureListener(e -> {
                                            Toast.makeText(AddClinicActivity.this, "Error getting image URL", Toast.LENGTH_SHORT).show();
                                        });
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(AddClinicActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(AddClinicActivity.this, "Doctor profile or clinic image missing", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    // DAYS SCHEDULE
    private List<String> getSelectedDays() {
        List<String> selectedDays = new ArrayList<>();
        if (((CheckBox) findViewById(R.id.cbMonday)).isChecked()) selectedDays.add("Monday");
        if (((CheckBox) findViewById(R.id.cbTuesday)).isChecked()) selectedDays.add("Tuesday");
        if (((CheckBox) findViewById(R.id.cbWednesday)).isChecked()) selectedDays.add("Wednesday");
        if (((CheckBox) findViewById(R.id.cbThursday)).isChecked()) selectedDays.add("Thursday");
        if (((CheckBox) findViewById(R.id.cbFriday)).isChecked()) selectedDays.add("Friday");
        if (((CheckBox) findViewById(R.id.cbSaturday)).isChecked()) selectedDays.add("Saturday");
        if (((CheckBox) findViewById(R.id.cbSunday)).isChecked()) selectedDays.add("Sunday");
        return selectedDays;
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

        // Add to the map
        vaccineMap.put(vaccine, Integer.parseInt(quantity));
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

    // TO DISPLAY THE SELECTED IMAGE TO IMAGEVIEW
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData(); // Store image URI
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                selectedClinicImage.setImageBitmap(bitmap); // Display image
                isImageSelected = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

    private void fetchUserData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid(); // Get the current user ID

            db.collection("doctorUsers").document(userId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    // Get data from Firestore
                                    String firstName = document.getString("firstName");
                                    String lastName = document.getString("lastName");
                                    String email = document.getString("email");

                                    // Set data to EditText fields
                                    etFirstName.setText(firstName);
                                    etLastName.setText(lastName);
                                    etEmail.setText(email);
                                } else {
                                    Toast.makeText(AddClinicActivity.this, "No such document", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(AddClinicActivity.this, "Fetch failed: " + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "No user is logged in", Toast.LENGTH_SHORT).show();
        }
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

    private boolean validateCheckboxes() {
        CheckBox cbMonday = findViewById(R.id.cbMonday);
        CheckBox cbTuesday = findViewById(R.id.cbTuesday);
        CheckBox cbWednesday = findViewById(R.id.cbWednesday);
        CheckBox cbThursday = findViewById(R.id.cbThursday);
        CheckBox cbFriday = findViewById(R.id.cbFriday);
        CheckBox cbSaturday = findViewById(R.id.cbSaturday);
        CheckBox cbSunday = findViewById(R.id.cbSunday);

        // Check if at least one checkbox is checked
        boolean isAnyDayChecked = cbMonday.isChecked() || cbTuesday.isChecked() || cbWednesday.isChecked() ||
                cbThursday.isChecked() || cbFriday.isChecked() || cbSaturday.isChecked() ||
                cbSunday.isChecked();

        TextView errorTextView = findViewById(R.id.errorTextView);

        if (!isAnyDayChecked) {
            showError("Please select at least one day.");
            errorTextView.setVisibility(View.VISIBLE); // Show error message
        } else {
            errorTextView.setVisibility(View.GONE); // Hide error message
        }

        return isAnyDayChecked;
    }

    // Example of showError method
    private void showError(String message) {
        TextView errorTextView = findViewById(R.id.errorTextView);
        errorTextView.setText(message);
        errorTextView.setVisibility(View.VISIBLE);
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

