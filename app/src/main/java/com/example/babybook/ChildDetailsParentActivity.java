package com.example.babybook;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChildDetailsParentActivity extends AppCompatActivity {

    private String currentParentId;
    private String childId,FirstName,LastName,Sex,Address,Birthday; // Store the child ID
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
    private ImageView textViewBCGicon, textViewHepatitisBicon, textViewDPTicon, textViewBoosters1icon,
            textViewOPVIPVicon, textViewBoosters2icon, textViewHInfluenzaBicon, textViewRotavirusicon,
            textViewMeaslesicon, textViewMMRicon, textViewBoosters3icon;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_details_parents);


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

// Initialize the TextViews
        textViewBCGicon = findViewById(R.id.textViewBCGicon);
        textViewHepatitisBicon = findViewById(R.id.textViewHepatitisBicon);
        textViewDPTicon = findViewById(R.id.textViewDPTicon);
        textViewBoosters1icon = findViewById(R.id.textViewBoosters1icon);
        textViewOPVIPVicon = findViewById(R.id.textViewOPVIPVicon);
        textViewBoosters2icon = findViewById(R.id.textViewBoosters2icon);
        textViewHInfluenzaBicon = findViewById(R.id.textViewHInfluenzaBicon);
        textViewRotavirusicon = findViewById(R.id.textViewRotavirusicon);
        textViewMeaslesicon = findViewById(R.id.textViewMeaslesicon);
        textViewMMRicon = findViewById(R.id.textViewMMRicon);
        textViewBoosters3icon = findViewById(R.id.textViewBoosters3icon);


        // Find the CardView
        CardView cardViewHome = findViewById(R.id.cardViewHome);

        // Set OnClickListener for the CardView
        cardViewHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate to ParentDashboardActivity
                Intent intent = new Intent(ChildDetailsParentActivity.this, ParentDashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // Optionally, finish the current activity
            }
        });





        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        getSupportActionBar().setTitle("Health Record");//parent side

        // Retrieve child ID from the intent
        childId = getIntent().getStringExtra("CHILD_ID");
        FirstName = getIntent().getStringExtra("FirstName");
        LastName = getIntent().getStringExtra("LastName");
        Sex = getIntent().getStringExtra("Sex");
        Address= getIntent().getStringExtra("Address");
        Birthday = getIntent().getStringExtra("Birthday");

        // Find the CardView by its ID
        CardView medicalRecordCardView = findViewById(R.id.medicalrecordcv);



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




        // Set click listeners for each TextView icon to show dialog with vaccine information
        textViewBCGicon.setOnClickListener(v -> showVaccineInfo("BCG Vaccine",
                "Dose: 1 dose at birth.\nPurpose: Protects against severe forms of tuberculosis, particularly in children, such as TB meningitis and miliary TB."));
        textViewHepatitisBicon.setOnClickListener(v -> showVaccineInfo("Hepatitis B Vaccine",
                "Dose: 1 dose at birth.\nPurpose: Prevents hepatitis B infection, which can lead to chronic liver disease and liver cancer."));
        textViewDPTicon.setOnClickListener(v -> showVaccineInfo("Pentavalent Vaccine (DPT-Hep B-HIB)",
                "Dose: 3 doses at 1½, 2½, and 3½ months.\nPurpose: Combines protection against five diseases: diphtheria, pertussis (whooping cough), tetanus, hepatitis B, and Haemophilus influenzae type b (causing meningitis and pneumonia)."));
        textViewBoosters1icon.setOnClickListener(v -> showVaccineInfo("Boosters",
                "Dose: 1 booster shot at 5 years.,\n<Purpose: Boosts immunity for long-term protection against diseases like DPT."));

        textViewOPVIPVicon.setOnClickListener(v -> showVaccineInfo("Polio Vaccines/Inactivated Polio Vaccine(OPV & IPV)",
                "Dose: 3 doses at 1½, 2½, and 3½ months.\nPurpose: Provides immunity against poliovirus, preventing poliomyelitis (polio), a disease that can cause paralysis."));

        textViewBoosters2icon.setOnClickListener(v -> showVaccineInfo("Boosters",
                "Dose: 1 booster shot at 5 years.,\n<Purpose: Boosts immunity for long-term protection against diseases like DPT."));
        textViewHInfluenzaBicon.setOnClickListener(v -> showVaccineInfo("Haemophilus Influenzae Type B (Hib) Vaccine",
                "Doses: 3 or 4 doses depending on the vaccine brand, given at 2 months, 4 months, 6 months (if needed), and a booster at 12-15 months.\nPurpose: Provides immunity against Haemophilus influenzae type b, a bacterium that can cause severe infections such as meningitis, pneumonia, and bloodstream infections, especially in young children under 5 years old."));
        textViewRotavirusicon.setOnClickListener(v -> showVaccineInfo("Rotavirus Vaccine",
                "Doses: 2 doses at 2 months and 4 months.\nPurpose: Provides immunity against rotavirus, which can cause severe diarrhea, dehydration, vomiting, and fever in infants and young children, preventing hospitalizations and serious complications."));

        textViewMeaslesicon.setOnClickListener(v -> showVaccineInfo("Measles Vaccine",
                "Dose: 1 dose at 9 months.\nPurpose: Prevents measles, which can cause serious complications like pneumonia, encephalitis, and death."));
        textViewMMRicon.setOnClickListener(v -> showVaccineInfo("MMR Vaccine",
                "Dose: 2 doses at 9 months and 1 year.\nPurpose: Prevents measles, mumps, and rubella, preventing complications like pneumonia, encephalitis, and birth defects if contracted during pregnancy."));
        textViewBoosters3icon.setOnClickListener(v -> showVaccineInfo("Boosters 3",
                "Dose: 1 booster shot at 5 years.\nPurpose: Boosts immunity for long-term protection against diseases like DPT and polio."));




        // Set an OnClickListener to navigate to ViewMedicalRecord.java
        medicalRecordCardView.setOnClickListener(v -> {
            Intent intent = new Intent(ChildDetailsParentActivity.this, com.example.babybook.ViewMedicalRecordParent.class);

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

       /* gestureDetector = new GestureDetector(this, new ChildDetailsParentActivity.SwipeGestureDetector());

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
        EditText editTextLocation = dialogView.findViewById(R.id.editTextLocation);
        EditText editTextDate = dialogView.findViewById(R.id.editTextDate);
        EditText editTextReaction = dialogView.findViewById(R.id.editTextReaction);
        Button buttonUpload = dialogView.findViewById(R.id.buttonUpload);

        editTextDate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(ChildDetailsParentActivity.this,
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
                Toast.makeText(ChildDetailsParentActivity.this, "All fields must be filled", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ChildDetailsParentActivity.this, "Details uploaded successfully", Toast.LENGTH_SHORT).show();
                    loadVaccinesFromFirestore(vaccineName); // Reload to show new data
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ChildDetailsParentActivity.this, "Error uploading details", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadVaccinesFromFirestore(String vaccineName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String collectionName = "vaccines";

        db.collection(collectionName)
                .whereEqualTo("name", vaccineName)
              //  .whereEqualTo("parentId", currentParentId)
                .whereEqualTo("childId", childId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        TableLayout tableLayout = getTableLayout(vaccineName);
                        if (tableLayout != null) {
                            tableLayout.removeAllViews();

                            // Add table headers
                            TableRow headerRow = new TableRow(ChildDetailsParentActivity.this);
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

                                TableRow row = new TableRow(ChildDetailsParentActivity.this);
                                row.addView(createTextView(dose));
                                row.addView(createTextView(type));
                                row.addView(createTextView(location));
                                row.addView(createTextView(date));
                                row.addView(createTextView(reaction));

                                // Set click listener for the row
                              /*row.setOnClickListener(v -> {
                                    // Handle row click
                                    showDetailsDialog(docId, dose, type, location, date, reaction);
                                });*/

                                tableLayout.addView(row);
                            }
                        }
                    } else {
                        Log.e("FirestoreError", "Error loading details: ", task.getException()); // Log the error
                        Toast.makeText(ChildDetailsParentActivity.this, "Error loading details", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ChildDetailsParentActivity.this, "Details updated successfully", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ChildDetailsParentActivity.this, "Error updating details", Toast.LENGTH_SHORT).show();
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
        TextView textView = new TextView(ChildDetailsParentActivity.this);
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
 /*   private class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {
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
private void showVaccineInfo(String vaccineName, String vaccineDetails) {
    // Format the vaccine details using HTML to bold "Dose:" and "Purpose:"
    String formattedDetails = "<b>Dose:</b> " + extractDose(vaccineDetails) + "<br><b>Purpose:</b> " + extractPurpose(vaccineDetails);

    // Convert the formatted string to Spanned for HTML rendering
    Spanned formattedText = Html.fromHtml(formattedDetails, Html.FROM_HTML_MODE_COMPACT);

    // Show the AlertDialog with the formatted text
    new AlertDialog.Builder(this)
            .setTitle(vaccineName)
            .setMessage(formattedText)
            .setPositiveButton("OK", null)
            .show();
}

    // Helper method to extract "Dose" information from vaccine details (without the "Dose:" keyword)
    private String extractDose(String details) {
        // Extract the portion after "Dose:" (if it exists)
        int doseIndex = details.indexOf("Dose:");
        if (doseIndex != -1) {
            // Extract everything after the first "Dose:" keyword
            return details.substring(doseIndex + 5).trim();  // "+ 5" skips over the "Dose:" part
        }
        return "No dose information available.";
    }

    // Helper method to extract "Purpose" information from vaccine details
    private String extractPurpose(String details) {
        // Extract the portion after "Purpose:" (if it exists)
        int purposeIndex = details.indexOf("Purpose:");
        if (purposeIndex != -1) {
            // Extract everything after the "Purpose:" keyword
            return details.substring(purposeIndex + 8).trim();  // "+ 8" skips over the "Purpose:" part
        }
        return "No purpose information available.";
    }

    private void navigateToChildDetailsActivity2() {
        Intent intent = new Intent(ChildDetailsParentActivity.this, ChildDetailsActivity2.class);
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
