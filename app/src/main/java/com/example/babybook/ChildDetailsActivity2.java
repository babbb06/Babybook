package com.example.babybook;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TableLayout;
import android.widget.TableRow;
import com.google.firebase.Timestamp;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChildDetailsActivity2 extends AppCompatActivity {

    private String currentParentId;
    private String childId; // Store the child ID
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_details2);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Retrieve child ID from the intent
        childId = getIntent().getStringExtra("CHILD_ID");

        // Get the current parent ID
        currentParentId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Fetch current user ID from FirebaseAuth

        findViewById(R.id.imageViewAddPneumococcalConjugate).setOnClickListener(v -> showAddImageDialog("Pneumococcal Conjugate", R.array.dose_options_bcg));
        findViewById(R.id.imageViewAddPneumococcalPolysaccharide).setOnClickListener(v -> showAddImageDialog("Pneumococcal Polysaccharide", R.array.dose_options_hepatitis_b));
        findViewById(R.id.imageViewAddInfluenza).setOnClickListener(v -> showAddImageDialog("Influenza", R.array.dose_options_dpt));
        findViewById(R.id.imageViewAddVaricella).setOnClickListener(v -> showAddImageDialog("Varicella", R.array.dose_options_boosters1));
        findViewById(R.id.imageViewAddHepatitisB).setOnClickListener(v -> showAddImageDialog("Hepatitis B", R.array.dose_options_opv_ipv));
        findViewById(R.id.imageViewAddHPV).setOnClickListener(v -> showAddImageDialog("HPV", R.array.dose_options_boosters2));
        findViewById(R.id.imageViewAddMantouxTest).setOnClickListener(v -> showAddImageDialog("Mantoux Test", R.array.dose_options_h_influenza_b));
        findViewById(R.id.imageViewAddTyphoid).setOnClickListener(v -> showAddImageDialog("Typhoid", R.array.dose_options_rotavirus));
        findViewById(R.id.imageViewAddNewbornScreening).setOnClickListener(v -> showAddImageDialog("NEWBORN SCREENING", R.array.dose_options_measles));
        findViewById(R.id.imageViewAddNewbornHearing).setOnClickListener(v -> showAddImageDialog("NEWBORN HEARING", R.array.dose_options_mmr));


        // Initialize GestureDetector
        gestureDetector = new GestureDetector(this, new SwipeGestureDetector());

        // Set onTouchListener to the root view
        findViewById(R.id.rootlayout).setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload the vaccine data when the activity resumes
        loadVaccinesFromFirestore("Pneumococcal Conjugate");
        loadVaccinesFromFirestore("Pneumococcal Polysaccharide");
        loadVaccinesFromFirestore("Influenza");
        loadVaccinesFromFirestore("Varicella");
        loadVaccinesFromFirestore("Hepatitis B");
        loadVaccinesFromFirestore("HPV");
        loadVaccinesFromFirestore("Mantoux Test");
        loadVaccinesFromFirestore("Typhoid");
        loadVaccinesFromFirestore("NEWBORN SCREENING");
        loadVaccinesFromFirestore("NEWBORN HEARING");
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
        EditText editTextLocation = dialogView.findViewById(R.id.editTextLocation);
        EditText editTextDate = dialogView.findViewById(R.id.editTextDate);
        EditText editTextReaction = dialogView.findViewById(R.id.editTextReaction);
        Button buttonUpload = dialogView.findViewById(R.id.buttonUpload);

        editTextDate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(ChildDetailsActivity2.this,
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
                Toast.makeText(ChildDetailsActivity2.this, "All fields must be filled", Toast.LENGTH_SHORT).show();
            } else {
                saveVaccineToFirestore(vaccineName, dose, type, location, date, reaction);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void saveVaccineToFirestore(String vaccineName, String dose, String type, String location, String date, String reaction) {
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
                    Toast.makeText(ChildDetailsActivity2.this, "Details uploaded successfully", Toast.LENGTH_SHORT).show();
                    loadVaccinesFromFirestore(vaccineName); // Reload to show new data
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ChildDetailsActivity2.this, "Error uploading details", Toast.LENGTH_SHORT).show();
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
                            TableRow headerRow = new TableRow(ChildDetailsActivity2.this);
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

                                TableRow row = new TableRow(ChildDetailsActivity2.this);
                                row.addView(createTextView(dose));
                                row.addView(createTextView(type));
                                row.addView(createTextView(location));
                                row.addView(createTextView(date));
                                row.addView(createTextView(reaction));

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
                        Toast.makeText(ChildDetailsActivity2.this, "Error loading details", Toast.LENGTH_SHORT).show();
                    }
                });
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
                    Toast.makeText(ChildDetailsActivity2.this, "Details updated successfully", Toast.LENGTH_SHORT).show();
                    // Reload the specific vaccine data after update
                    loadVaccinesFromFirestore("Pneumococcal Conjugate"); // Replace with actual vaccine name if necessary
                    loadVaccinesFromFirestore("Pneumococcal Polysaccharide"); // Repeat for other vaccines as needed
                    loadVaccinesFromFirestore("Influenza");
                    loadVaccinesFromFirestore("Varicella");
                    loadVaccinesFromFirestore("Hepatitis B");
                    loadVaccinesFromFirestore("HPV");
                    loadVaccinesFromFirestore("Mantoux Test");
                    loadVaccinesFromFirestore("Typhoid");
                    loadVaccinesFromFirestore("NEWBORN SCREENING");
                    loadVaccinesFromFirestore("NEWBORN HEARING");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ChildDetailsActivity2.this, "Error updating details", Toast.LENGTH_SHORT).show();
                });
    }



    private TableLayout getTableLayout(String vaccineName) {
        switch (vaccineName) {
            case "Pneumococcal Conjugate":
                return findViewById(R.id.tableLayoutPneumococcalConjugate);
            case "Pneumococcal Polysaccharide":
                return findViewById(R.id.tableLayoutPneumococcalPolysaccharide);
            case "Influenza":
                return findViewById(R.id.tableLayoutInfluenza);
            case "Varicella":
                return findViewById(R.id.tableLayoutVaricella);
            case "Hepatitis B":
                return findViewById(R.id.tableLayoutHepatitisB);
            case "HPV":
                return findViewById(R.id.tableLayoutHPV);
            case "Mantoux Test":
                return findViewById(R.id.tableLayoutMantouxTest);
            case "Typhoid":
                return findViewById(R.id.tableLayoutTyphoid);
            case "NEWBORN SCREENING":
                return findViewById(R.id.tableLayoutNewbornScreening);
            case "NEWBORN HEARING":
                return findViewById(R.id.tableLayoutNewbornHearing);
            default:
                return null;
        }
    }

    private TextView createTextView(String text) {
        TextView textView = new TextView(ChildDetailsActivity2.this);
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

    private class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffX = e2.getX() - e1.getX();
            float diffY = e2.getY() - e1.getY();
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) { // Change this line to check for right swipe
                        // Swipe right
                        navigateToChildDetailsActivity();
                    }
                }
                return true;
            }
            return false;
        }
    }


    private void navigateToChildDetailsActivity() {
        Intent intent = new Intent(ChildDetailsActivity2.this, ChildDetailsActivity.class);
        intent.putExtra("CHILD_ID", childId); // Pass the child ID to the next activity
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
