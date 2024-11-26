package com.example.babybook;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.google.firebase.Timestamp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChildDetailsActivity extends AppCompatActivity {

    private String currentParentId;
    private String childId, LastName, FirstName,Sex, Address,Birthday; // Store the child ID
    private GestureDetector gestureDetector;
    private ImageView imageViewMenuBCG;
    private ImageView imageViewMenuHepatitisB;
    private ImageView imageViewMenuDPT;
    private ImageView imageViewMenuBoosters1;
    private ImageView imageViewMenuOPVIPV;
    private ImageView imageViewMenuBoosters2;
    private ImageView imageViewMenuInfluenzaB;
    private ImageView imageViewMenuRotavirus;
    private ImageView imageViewMenuMeasles;
    private ImageView imageViewMenuMMR;
    private ImageView imageViewMenuBoosters3;


    private TextView textViewBCG, textViewHepatitisB, textViewDPT, textViewBoosters1, textViewOPVIPV,
            textViewBoosters2, textViewHInfluenzaB, textViewRotavirus, textViewMeasles,
            textViewMMR, textViewBoosters3;

    private CardView cardViewBCG, cardViewHepatitisB, cardViewDPT, cardViewBoosters1, cardViewOPVIPV,
            cardViewBoosters2, cardViewHInfluenzaB, cardViewRotavirus, cardViewMeasles,
            cardViewMMR, cardViewBoosters3;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_details);


        imageViewMenuBCG = findViewById(R.id.imageViewMenuBCG);
        imageViewMenuHepatitisB = findViewById(R.id.imageViewMenuHepatitisB);
        imageViewMenuDPT = findViewById(R.id.imageViewMenuDPT);
        imageViewMenuBoosters1 = findViewById(R.id.imageViewMenuBoosters1);
        imageViewMenuOPVIPV = findViewById(R.id.imageViewMenuOPVIPV);
        imageViewMenuBoosters2 = findViewById(R.id.imageViewMenuBoosters2);
        imageViewMenuInfluenzaB = findViewById(R.id.imageViewMenuInfluenzaB);
        imageViewMenuRotavirus = findViewById(R.id.imageViewMenuRotavirus);
        imageViewMenuMeasles = findViewById(R.id.imageViewMenuMeasles);
        imageViewMenuMMR = findViewById(R.id.imageViewMenuMMR);
        imageViewMenuBoosters3 = findViewById(R.id.imageViewAddBoosters3);

        // Initialize TextViews
        textViewBCG = findViewById(R.id.textViewBCG);
        textViewHepatitisB = findViewById(R.id.textViewHepatitisB);
        textViewDPT = findViewById(R.id.textViewDPT);
        textViewBoosters1 = findViewById(R.id.textViewBoosters1);
        textViewOPVIPV = findViewById(R.id.textViewOPVIPV);
        textViewBoosters2 = findViewById(R.id.textViewBoosters2);
        textViewHInfluenzaB = findViewById(R.id.textViewHInfluenzaB);
        textViewRotavirus = findViewById(R.id.textViewRotavirus);
        textViewMeasles = findViewById(R.id.textViewMeasles);
        textViewMMR = findViewById(R.id.textViewMMR);
        textViewBoosters3 = findViewById(R.id.textViewBoosters3);

        // Initialize CardViews
        cardViewBCG = findViewById(R.id.cardViewBCG);
        cardViewHepatitisB = findViewById(R.id.cardViewHepatitisB);
        cardViewDPT = findViewById(R.id.cardViewDPT);
        cardViewBoosters1 = findViewById(R.id.cardViewBoosters1);
        cardViewOPVIPV = findViewById(R.id.cardViewOPVIPV);
        cardViewBoosters2 = findViewById(R.id.cardViewBoosters2);
        cardViewHInfluenzaB = findViewById(R.id.cardViewHInfluenzaB);
        cardViewRotavirus = findViewById(R.id.cardViewRotavirus);
        cardViewMeasles = findViewById(R.id.cardViewMeasles);
        cardViewMMR = findViewById(R.id.cardViewMMR);
        cardViewBoosters3 = findViewById(R.id.cardViewBoosters3);


   

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        getSupportActionBar().setTitle("Health Record");// DOCTOR SIDE

        // Retrieve child ID from the intent
        childId = getIntent().getStringExtra("CHILD_ID");
        FirstName = getIntent().getStringExtra("FirstName");
        LastName = getIntent().getStringExtra("LastName");
        Sex = getIntent().getStringExtra("Sex");
        Address= getIntent().getStringExtra("Address");
        Birthday = getIntent().getStringExtra("Birthday");



        // Initially set all CardViews to GONE
        setCardViewsVisibility(View.GONE);

        // Set click listeners for TextViews
        textViewBCG.setOnClickListener(v -> showCardView(cardViewBCG));
        textViewHepatitisB.setOnClickListener(v -> showCardView(cardViewHepatitisB));
        textViewDPT.setOnClickListener(v -> showCardView(cardViewDPT));
        textViewBoosters1.setOnClickListener(v -> showCardView(cardViewBoosters1));
        textViewOPVIPV.setOnClickListener(v -> showCardView(cardViewOPVIPV));
        textViewBoosters2.setOnClickListener(v -> showCardView(cardViewBoosters2));
        textViewHInfluenzaB.setOnClickListener(v -> showCardView(cardViewHInfluenzaB));
        textViewRotavirus.setOnClickListener(v -> showCardView(cardViewRotavirus));
        textViewMeasles.setOnClickListener(v -> showCardView(cardViewMeasles));
        textViewMMR.setOnClickListener(v -> showCardView(cardViewMMR));
        textViewBoosters3.setOnClickListener(v -> showCardView(cardViewBoosters3));

        // Set click listeners for ImageViews
        imageViewMenuBCG.setOnClickListener(v -> hideCardView(cardViewBCG));
        imageViewMenuHepatitisB.setOnClickListener(v -> hideCardView(cardViewHepatitisB));
        imageViewMenuDPT.setOnClickListener(v -> hideCardView(cardViewDPT));
        imageViewMenuBoosters1.setOnClickListener(v -> hideCardView(cardViewBoosters1));
        imageViewMenuOPVIPV.setOnClickListener(v -> hideCardView(cardViewOPVIPV));
        imageViewMenuBoosters2.setOnClickListener(v -> hideCardView(cardViewBoosters2));
        imageViewMenuInfluenzaB.setOnClickListener(v -> hideCardView(cardViewHInfluenzaB));
        imageViewMenuRotavirus.setOnClickListener(v -> hideCardView(cardViewRotavirus));
        imageViewMenuMeasles.setOnClickListener(v -> hideCardView(cardViewMeasles));
        imageViewMenuMMR.setOnClickListener(v -> hideCardView(cardViewMMR));
        imageViewMenuBoosters3.setOnClickListener(v -> hideCardView(cardViewBoosters3));


        // Find the CardView by its ID
        CardView medicalRecordCardView = findViewById(R.id.medicalrecordcardview);

        // Set an OnClickListener to navigate to ViewMedicalRecord.java
        medicalRecordCardView.setOnClickListener(v -> {
            Intent intent = new Intent(ChildDetailsActivity.this, com.example.babybook.ListOfMedicalRecord.class);

            // Optionally, pass some data using intent if needed
            intent.putExtra("childId", childId); // Pass the child ID if needed
            intent.putExtra("FirstName", FirstName); // Pass the child ID
            intent.putExtra("LastName", LastName);
            intent.putExtra("Sex", Sex);
            intent.putExtra("Address", Address);
            intent.putExtra("Birthday", Birthday);

            startActivity(intent);
        });

        // Get the current parent ID
        currentParentId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Fetch current user ID from FirebaseAuth

        findViewById(R.id.imageViewAddBCG).setOnClickListener(v -> showAddImageDialog("BCG", R.array.dose_options_bcg));
        findViewById(R.id.imageViewAddHepatitisB).setOnClickListener(v -> showAddImageDialog("Hepatitis B", R.array.dose_options_hepatitis_b));
        findViewById(R.id.imageViewAddDPT).setOnClickListener(v -> showAddImageDialog("DPT", R.array.dose_options_dpt));
        findViewById(R.id.imageViewAddBoosters1).setOnClickListener(v -> showAddImageDialog("BOOSTERS", R.array.dose_options_boosters1));
        findViewById(R.id.imageViewAddOPVIPV).setOnClickListener(v -> showAddImageDialog("OPV/IPV", R.array.dose_options_opv_ipv));
        findViewById(R.id.imageViewAddBoosters2).setOnClickListener(v -> showAddImageDialog("BOOSTERS 1", R.array.dose_options_boosters2));
        findViewById(R.id.imageViewAddHInfluenzaB).setOnClickListener(v -> showAddImageDialog("H. Influenza B", R.array.dose_options_h_influenza_b));
        findViewById(R.id.imageViewAddRotavirus).setOnClickListener(v -> showAddImageDialog("ROTAVIRUS", R.array.dose_options_rotavirus));
        findViewById(R.id.imageViewAddMeasles).setOnClickListener(v -> showAddImageDialog("MEASLES", R.array.dose_options_measles));
        findViewById(R.id.imageViewAddMMR).setOnClickListener(v -> showAddImageDialog("MMR", R.array.dose_options_mmr));
        findViewById(R.id.imageViewAddBoosters3).setOnClickListener(v -> showAddImageDialog("BOOSTERS 2", R.array.dose_options_boosters3));
        // Find the CardView
        CardView cardViewHome = findViewById(R.id.cardViewHome);

        // Set OnClickListener for the CardView
        cardViewHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate to ParentDashboardActivity
                Intent intent = new Intent(ChildDetailsActivity.this, DoctorDashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // Optionally, finish the current activity
            }
        });
   /*     gestureDetector = new GestureDetector(this, new ChildDetailsActivity.SwipeGestureDetector());

        findViewById(R.id.rootlayout).setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return true;
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload the vaccine data when the activity resumes
        loadVaccinesFromFirestore("BCG");
        loadVaccinesFromFirestore("Hepatitis B");
        loadVaccinesFromFirestore("DPT");
        loadVaccinesFromFirestore("BOOSTERS");
        loadVaccinesFromFirestore("OPV/IPV");
        loadVaccinesFromFirestore("BOOSTERS 1");
        loadVaccinesFromFirestore("H. Influenza B");
        loadVaccinesFromFirestore("ROTAVIRUS");
        loadVaccinesFromFirestore("MEASLES");
        loadVaccinesFromFirestore("MMR");
        loadVaccinesFromFirestore("BOOSTERS 2");

    }




    private void showAddImageDialog(String vaccineName, int doseOptionsArrayId) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_add_image, null);

        TextView textViewTitle = dialogView.findViewById(R.id.textViewTitle);
        textViewTitle.setText("Add Details for " + vaccineName);

        Spinner spinnerDose = dialogView.findViewById(R.id.spinnerDose);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                doseOptionsArrayId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDose.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        EditText editTextType = dialogView.findViewById(R.id.editTextType);
        switch (vaccineName) {
            case "BCG":
                editTextType.setText("BCG");
                break;
            case "Hepatitis B":
                editTextType.setText("Hepatitis B");
                break;
            case "DPT":
                editTextType.setText("DPT");
                break;
            case "BOOSTERS":
                editTextType.setText("BOOSTERS");
                break;
            case "OPV/IPV":
                editTextType.setText("OPV/IPV");
                break;
            case "BOOSTERS 1":
                editTextType.setText("BOOSTERS 1");
                break;
            case "H. Influenza B":
                editTextType.setText("H. Influenza B");
                break;
            case "ROTAVIRUS":
                editTextType.setText("ROTAVIRUS");
                break;
            case "MEASLES":
                editTextType.setText("MEASLES");
                break;
            case "MMR":
                editTextType.setText("MMR");
                break;
            case "BOOSTERS 2":
                editTextType.setText("BOOSTERS 2");
                break;
            default:
                editTextType.setText(""); // Clear text if no match

                break;
        }

        EditText editTextLocation = dialogView.findViewById(R.id.editTextLocation);
        EditText editTextDate = dialogView.findViewById(R.id.editTextDate);
        EditText editTextReaction = dialogView.findViewById(R.id.editTextReaction);
        Button buttonUpload = dialogView.findViewById(R.id.buttonUpload);


        // Set the current date in editTextDate
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String currentDate = day + "/" + (month + 1) + "/" + year;
        editTextDate.setText(currentDate);

        editTextDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(ChildDetailsActivity.this,
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        editTextDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1);
                    }, year, month, day);
            datePickerDialog.show();
        });

        buttonUpload.setOnClickListener(v -> {
            String dose = spinnerDose.getSelectedItem().toString().trim();
            String type = editTextType.getText().toString().trim();
            String location = editTextLocation.getText().toString().trim();
            String date = editTextDate.getText().toString().trim();
            String reaction = editTextReaction.getText().toString().trim();

            if (type.isEmpty() || location.isEmpty() || date.isEmpty() || reaction.isEmpty()) {
                Toast.makeText(ChildDetailsActivity.this, "All fields must be filled", Toast.LENGTH_SHORT).show();
            } else {
                saveVaccineToFirestore(vaccineName, dose, type, location, date, reaction);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void saveVaccineToFirestore(String vaccineName, String dose, String type, String location, String date, String reaction) {
        // Define the dose limits for each vaccine type
        int doseLimit = getDoseLimitForVaccine(type);

        // If the vaccine has a dose limit, check if the limit is reached
        if (doseLimit > 0) {
            checkIfDoseLimitReached(type, doseLimit, vaccineName, dose, location, date, reaction);
        } else {
            // If there's no dose limit (such as for "BCG" dose 1), save the vaccine details directly
            saveVaccineDetails(vaccineName, dose, type, location, date, reaction);
        }
    }

    // Method to get the dose limit for each vaccine type
    private int getDoseLimitForVaccine(String type) {
        switch (type) {
            case "BCG":
                return 1;
            case "Hepatitis B":
                return 4;
            case "DPT":
                return 3;
            case "BOOSTERS":
                return 2;
            case "OPV/IPV":
                return 2;
            case "BOOSTERS 1":
                return 2;
            case "H. Influenza B":
                return 4;
            case "ROTAVIRUS":
                return 1;
            case "MEASLES":
                return 1;
            case "MMR":
                return 2;
            case "BOOSTERS 2":
                return 2;
            default:
                return 0; // No dose limit (default case)
        }
    }

    // Method to check if the dose limit has been reached for a specific vaccine type
    private void checkIfDoseLimitReached(String type, int doseLimit, String vaccineName, String dose, String location, String date, String reaction) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("vaccines")
                .whereEqualTo("type", type)
                .whereEqualTo("parentId", currentParentId)
                .whereEqualTo("childId", childId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Check how many documents with the same type exist for this child
                        int existingDoses = 0;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (type.equals(document.getString("type"))) {
                                existingDoses++;
                            }
                        }

                        // If the dose limit is reached, show a message and prevent adding more doses
                        if (existingDoses >= doseLimit) {
                            Toast.makeText(ChildDetailsActivity.this, type + "  has already been completed.", Toast.LENGTH_SHORT).show();
                        } else {
                            // Otherwise, save the vaccine details
                            saveVaccineDetails(vaccineName, dose, type, location, date, reaction);
                        }
                    } else {
                        // Handle Firestore query failure
                        Toast.makeText(ChildDetailsActivity.this, "Error checking existing records", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Method to save vaccine details to Firestore
    private void saveVaccineDetails(String vaccineName, String dose, String type, String location, String date, String reaction) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> vaccine = new HashMap<>();
        vaccine.put("name", vaccineName);
        vaccine.put("dose", dose);
        vaccine.put("type", type);
        vaccine.put("location", location);
        vaccine.put("date", date);
        vaccine.put("reaction", reaction);
        vaccine.put("parentId", currentParentId);
        vaccine.put("childId", childId);
        vaccine.put("timestamp", Timestamp.now());

        String collectionName = "vaccines";
        String documentId = db.collection(collectionName).document().getId(); // Generate a unique document ID

        db.collection(collectionName).document(documentId).set(vaccine)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ChildDetailsActivity.this, "Details uploaded successfully", Toast.LENGTH_SHORT).show();
                    loadVaccinesFromFirestore(vaccineName); // Reload to show new data
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ChildDetailsActivity.this, "Error uploading details", Toast.LENGTH_SHORT).show();
                });
    }


    private void loadVaccinesFromFirestore(String vaccineName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String collectionName = "vaccines";

        db.collection(collectionName)
                .whereEqualTo("name", vaccineName)
                .whereEqualTo("parentId", currentParentId)
                .whereEqualTo("childId", childId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        TableLayout tableLayout = getTableLayout(vaccineName);
                        if (tableLayout != null) {
                            tableLayout.removeAllViews();

                            // Add table headers
                            TableRow headerRow = new TableRow(ChildDetailsActivity.this);
                            headerRow.addView(createHeaderTextView("Dose"));
                            headerRow.addView(createHeaderTextView("Type"));
                            headerRow.addView(createHeaderTextView("Location"));
                            headerRow.addView(createHeaderTextView("Date"));
                            headerRow.addView(createHeaderTextView("Reaction"));
                            tableLayout.addView(headerRow);

                            // Store documents in a list for sorting
                            List<QueryDocumentSnapshot> documents = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                documents.add(document);
                            }

                            // Sort documents by dose number
                            Collections.sort(documents, (doc1, doc2) -> {
                                int dose1 = Integer.parseInt(doc1.getString("dose"));
                                int dose2 = Integer.parseInt(doc2.getString("dose"));
                                return Integer.compare(dose1, dose2);
                            });

                            // Add sorted rows to the table
                            for (QueryDocumentSnapshot document : documents) {
                                String dose = document.getString("dose");
                                String type = document.getString("type");
                                String location = document.getString("location");
                                String date = document.getString("date");
                                String reaction = document.getString("reaction");
                                String docId = document.getId(); // Get document ID

                                TableRow row = new TableRow(ChildDetailsActivity.this);
                                row.addView(createTextView(dose));
                                row.addView(createTextView(type));
                                row.addView(createTextView(location));
                                row.addView(createTextView(date));
                                row.addView(createTextView(reaction));

                                // Check if the vaccine type is completed (based on its dose limit)
                                checkVaccineCompletionDialog(type, dose);

                                // Set click listener for the row
                                row.setOnClickListener(v -> {
                                    // Handle row click
                                    showDetailsDialog(docId, dose, type, location, date, reaction);
                                });

                                tableLayout.addView(row);
                            }
                        }
                    } else {
                        Log.e("FirestoreError", "Error loading details: ", task.getException()); // Log the error
                        Toast.makeText(ChildDetailsActivity.this, "Error loading details", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Method to check vaccine completion based on type and dose
    private void checkVaccineCompletionDialog(String type, String dose) {
        int doseLimit = getDoseLimitForVaccine(type);  // Get dose limit for the vaccine type
        if (doseLimit > 0) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("vaccines")
                    .whereEqualTo("type", type)
                    .whereEqualTo("parentId", currentParentId)
                    .whereEqualTo("childId", childId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            int existingDoses = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (type.equals(document.getString("type"))) {
                                    existingDoses++;
                                }
                            }

                            // If the dose limit is reached and the dialog hasn't been shown yet
                            if (existingDoses >= doseLimit && !hasCompletedDialogShown(type)) {
                                showCompletedDialog(type, doseLimit);  // Show the dialog
                                setCompletedDialogShown(type);  // Mark the dialog as shown
                            }

                            // Check for different vaccine types to show/hide their respective TextViews
                            switch (type) {
                                case "BCG":
                                    updateVaccineTextView(R.id.textViewBCGComplete, existingDoses, doseLimit);
                                    break;
                                case "Hepatitis B":
                                    updateVaccineTextView(R.id.textViewHepatitisBComplete, existingDoses, doseLimit);
                                    break;
                                case "DPT":
                                    updateVaccineTextView(R.id.textViewDPTComplete, existingDoses, doseLimit);
                                    break;
                                case "BOOSTERS":
                                    updateVaccineTextView(R.id.textViewBoosters1Complete, existingDoses, doseLimit);
                                    break;
                                case "OPV/IPV":
                                    updateVaccineTextView(R.id.textViewOPVIPVComplete, existingDoses, doseLimit);
                                    break;
                                case "BOOSTERS 1":
                                    updateVaccineTextView(R.id.textViewBoosters2Complete, existingDoses, doseLimit);
                                    break;
                                case "H. Influenza B":
                                    updateVaccineTextView(R.id.textViewHInfluenzaBComplete, existingDoses, doseLimit);
                                    break;
                                case "ROTAVIRUS":
                                    updateVaccineTextView(R.id.textViewRotavirusComplete, existingDoses, doseLimit);
                                    break;
                                case "MEASLES":
                                    updateVaccineTextView(R.id.textViewMeaslesComplete, existingDoses, doseLimit);
                                    break;
                                case "MMR":
                                    updateVaccineTextView(R.id.textViewMMRComplete, existingDoses, doseLimit);
                                    break;
                                case "BOOSTERS 2":
                                    updateVaccineTextView(R.id.textViewBoosters3Complete, existingDoses, doseLimit);
                                    break;
                            }
                        }
                    });
        }
    }

    // Helper method to update the visibility of TextViews for each vaccine
    private void updateVaccineTextView(int textViewId, int existingDoses, int doseLimit) {
        TextView textView = findViewById(textViewId);
        if (existingDoses >= doseLimit) {
            textView.setVisibility(View.VISIBLE);  // Show the TextView
        } else {
            textView.setVisibility(View.GONE);  // Hide the TextView
        }
    }


    // Method to show the "Completed" dialog with a customized message
    private void showCompletedDialog(String type, int doseLimit) {
        String message = type + " has reached its " + doseLimit + " dose(s). Completed.";

        new AlertDialog.Builder(ChildDetailsActivity.this)
                .setTitle("Vaccine Status")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setCancelable(false) // Optional, prevent dialog from being dismissed by tapping outside
                .show();
    }

    // Method to check if the "Completed" dialog has already been shown for the given vaccine type
    private boolean hasCompletedDialogShown(String type) {
        SharedPreferences preferences = getSharedPreferences("VaccineDialogPrefs", MODE_PRIVATE);
        return preferences.getBoolean(type + "_completed", false);  // Returns true if dialog shown for this type
    }

    // Method to mark that the "Completed" dialog has been shown for the given vaccine type
    private void setCompletedDialogShown(String type) {
        SharedPreferences preferences = getSharedPreferences("VaccineDialogPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(type + "_completed", true);  // Set the flag for this vaccine type
        editor.apply();
    }




    private void showDetailsDialog(String documentId, String dose, String type, String location, String date, String reaction) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_view_details, null); // Your dialog layout

        // Find EditText fields in the dialog view
        EditText editTextDose = dialogView.findViewById(R.id.editTextDose);
        EditText editTextType = dialogView.findViewById(R.id.editTextType);
        EditText editTextLocation = dialogView.findViewById(R.id.editTextLocation);
        EditText editTextDate = dialogView.findViewById(R.id.editTextDate);
        EditText editTextReaction = dialogView.findViewById(R.id.editTextReaction);

        // Set the values to the EditTexts
        editTextDose.setText(dose);
        editTextType.setText(type);
        editTextLocation.setText(location);
        editTextDate.setText(date);
        editTextReaction.setText(reaction);

        builder.setView(dialogView)
                .setTitle("Edit Vaccine Details")
                .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle the "Edit" button click
                        String newDose = editTextDose.getText().toString();
                        String newType = editTextType.getText().toString();
                        String newLocation = editTextLocation.getText().toString();
                        String newDate = editTextDate.getText().toString();
                        String newReaction = editTextReaction.getText().toString();

                        // Perform the update action here
                        updateVaccineDetails(documentId, newDose, newType, newLocation, newDate, newReaction);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateVaccineDetails(String documentId, String dose, String type, String location, String date, String reaction) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> updates = new HashMap<>();
        updates.put("dose", dose);
        updates.put("type", type);
        updates.put("location", location);
        updates.put("date", date);
        updates.put("reaction", reaction);

        db.collection("vaccines").document(documentId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ChildDetailsActivity.this, "Details updated successfully", Toast.LENGTH_SHORT).show();
                    loadVaccinesFromFirestore("BCG");
                    loadVaccinesFromFirestore("Hepatitis B");
                    loadVaccinesFromFirestore("DPT");
                    loadVaccinesFromFirestore("BOOSTERS");
                    loadVaccinesFromFirestore("OPV/IPV");
                    loadVaccinesFromFirestore("BOOSTERS 1");
                    loadVaccinesFromFirestore("H. Influenza B");
                    loadVaccinesFromFirestore("ROTAVIRUS");
                    loadVaccinesFromFirestore("MEASLES");
                    loadVaccinesFromFirestore("MMR");
                    loadVaccinesFromFirestore("BOOSTERS 2");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ChildDetailsActivity.this, "Error updating details", Toast.LENGTH_SHORT).show();
                });
    }


    private TableLayout getTableLayout(String vaccineName) {
        switch (vaccineName) {
            case "BCG":
                return findViewById(R.id.tableLayoutBCG);
            case "Hepatitis B":
                return findViewById(R.id.tableLayoutHepatitisB);
            case "DPT":
                return findViewById(R.id.tableLayoutDPT);
            case "BOOSTERS":
                return findViewById(R.id.tableLayoutBoosters1);
            case "OPV/IPV":
                return findViewById(R.id.tableLayoutOPVIPV);
            case "BOOSTERS 1":
                return findViewById(R.id.tableLayoutBoosters2);
            case "H. Influenza B":
                return findViewById(R.id.tableLayoutHInfluenzaB);
            case "ROTAVIRUS":
                return findViewById(R.id.tableLayoutRotavirus);
            case "MEASLES":
                return findViewById(R.id.tableLayoutMeasles);
            case "MMR":
                return findViewById(R.id.tableLayoutMMR);
            case "BOOSTERS 2":
                return findViewById(R.id.tableLayoutBoosters3);

            default:
                return null;


        }
    }

    private TextView createTextView(String text) {
        TextView textView = new TextView(ChildDetailsActivity.this);
        textView.setText(text);
        textView.setPadding(16, 16, 16, 16);
        textView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        return textView;
    }

    private TextView createHeaderTextView(String text) {
        TextView textView = createTextView(text);
        textView.setTypeface(null, android.graphics.Typeface.BOLD);
        textView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        return textView;
    }

// Swipe Gesture Detector
  /*  private class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffX = e2.getX() - e1.getX();
            float diffY = e2.getY() - e1.getY();
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX < 0) { // Change this line to check for left swipe
                        // Swipe left
                        navigateToChildDetailsActivity2();
                    }
                }
                return true;
            }
            return false;
        }
    }*/


    private void navigateToChildDetailsActivity2() {
        Intent intent = new Intent(ChildDetailsActivity.this, ChildDetailsActivity2.class);
        intent.putExtra("CHILD_ID", childId); // Pass the child ID to the next activity
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    // Helper method to show a specific CardView and hide others
    private void showCardView(CardView cardViewToShow) {
        setCardViewsVisibility(View.GONE); // Hide all CardViews first
        cardViewToShow.setVisibility(View.VISIBLE); // Show the selected CardView
    }

    // Helper method to hide all CardViews
    private void setCardViewsVisibility(int visibility) {
        cardViewBCG.setVisibility(visibility);
        cardViewHepatitisB.setVisibility(visibility);
        cardViewDPT.setVisibility(visibility);
        cardViewBoosters1.setVisibility(visibility);
        cardViewOPVIPV.setVisibility(visibility);
        cardViewBoosters2.setVisibility(visibility);
        cardViewHInfluenzaB.setVisibility(visibility);
        cardViewRotavirus.setVisibility(visibility);
        cardViewMeasles.setVisibility(visibility);
        cardViewMMR.setVisibility(visibility);
        cardViewBoosters3.setVisibility(visibility);
    }
    // Helper method to hide the selected CardView
    private void hideCardView(CardView cardViewToHide) {
        cardViewToHide.setVisibility(View.GONE); // Hide the specific CardView
    }

}
