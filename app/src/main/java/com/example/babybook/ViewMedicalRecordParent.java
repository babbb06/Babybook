package com.example.babybook;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.babybook.model.HealthChecklist;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ViewMedicalRecordParent extends AppCompatActivity {

    private TextView name,textViewWeight, textViewDate, textViewTemperature, textViewSummaryDiagnosis, textViewTreatmentPlan, textViewFollowUpPlan;
    private CheckBox checkBoxSick, checkBoxCough, checkBoxDiarrhea, checkBoxFever, checkBoxMeasles, checkBoxEarPain, checkBoxPallor, checkBoxMalnourished, checkBoxFeeding, checkBoxBreastfeeding, checkBoxDiarrheaCough, checkBoxImmunization, checkBoxOtherProblems;
    private String childId,FirstName,LastName; // This will now be used to fetch the medical record

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_medical_record_parent);

        // Get the child ID from the intent
        childId = getIntent().getStringExtra("childId");
        FirstName = getIntent().getStringExtra("FirstName");
        LastName = getIntent().getStringExtra("LastName");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        getSupportActionBar().setTitle("Medical Records Details");// PARENT SIDe



        // Initialize UI components
        textViewWeight = findViewById(R.id.textViewWeight);
        textViewDate = findViewById(R.id.textViewDate);
        textViewTemperature = findViewById(R.id.textViewTemperature);
        textViewSummaryDiagnosis = findViewById(R.id.textViewSummaryDiagnosis);
        textViewTreatmentPlan = findViewById(R.id.textViewTreatmentPlan);
        textViewFollowUpPlan = findViewById(R.id.textViewFollowUpPlan);
        name = findViewById(R.id.name);
        checkBoxSick = findViewById(R.id.checkBoxSick);
        checkBoxCough = findViewById(R.id.checkBoxCough);
        checkBoxDiarrhea = findViewById(R.id.checkBoxDiarrhea);
        checkBoxFever = findViewById(R.id.checkBoxFever);
        checkBoxMeasles = findViewById(R.id.checkBoxMeasles);
        checkBoxEarPain = findViewById(R.id.checkBoxEarPain);
        checkBoxPallor = findViewById(R.id.checkBoxPallor);
        checkBoxMalnourished = findViewById(R.id.checkBoxMalnourished);
        checkBoxFeeding = findViewById(R.id.checkBoxFeeding);
        checkBoxBreastfeeding = findViewById(R.id.checkBoxBreastfeeding);
        checkBoxDiarrheaCough = findViewById(R.id.checkBoxDiarrheaCough);
        checkBoxImmunization = findViewById(R.id.checkBoxImmunization);
        checkBoxOtherProblems = findViewById(R.id.checkBoxOtherProblems);


        fetchHealthRecordDetails();

        name.setText("Name: " + FirstName +" "+ LastName);


    }



    private void fetchHealthRecordDetails() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("medicalRecords")
                .whereEqualTo("childId", childId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            HealthChecklist checklist = document.toObject(HealthChecklist.class);

                            // Logging to check if checklist data is being fetched correctly
                            if (checklist != null) {
                                Toast.makeText(ViewMedicalRecordParent.this, "Checklist retrieved successfully", Toast.LENGTH_SHORT).show();


                            } else {
                                Toast.makeText(ViewMedicalRecordParent.this, "Checklist is null", Toast.LENGTH_SHORT).show();
                            }

                            // Set other UI elements as needed
                            textViewWeight.setText(document.getString("Weight"));
                            textViewDate.setText(document.getString("Date"));
                            textViewTemperature.setText(document.getString("Temperature"));
                            textViewSummaryDiagnosis.setText(document.getString("Summary"));
                            textViewTreatmentPlan.setText(document.getString("Treatment"));
                            textViewFollowUpPlan.setText(document.getString("Follow"));


                            // Set checkbox values based on boolean data
                            checkBoxSick.setChecked(document.getBoolean("IsSick"));
                            checkBoxCough.setChecked(document.getBoolean("HasCough"));
                            checkBoxDiarrhea.setChecked(document.getBoolean("HasDiarrhea"));
                            checkBoxFever.setChecked(document.getBoolean("HasFever"));
                            checkBoxMeasles.setChecked(document.getBoolean("HasMeasles"));
                            checkBoxEarPain.setChecked(document.getBoolean("HasEarPain"));
                            checkBoxPallor.setChecked(document.getBoolean("IsPallor"));
                            checkBoxMalnourished.setChecked(document.getBoolean("IsMalnourished"));
                            checkBoxFeeding.setChecked(document.getBoolean("IsFeeding"));
                            checkBoxBreastfeeding.setChecked(document.getBoolean("IsBreastfeeding"));
                            checkBoxDiarrheaCough.setChecked(document.getBoolean("HasDiarrheaCough"));
                            checkBoxImmunization.setChecked(document.getBoolean("HasImmunization"));
                            checkBoxOtherProblems.setChecked(document.getBoolean("HasOtherProblems"));


                        }
                    } else {
                        Toast.makeText(ViewMedicalRecordParent.this, "No record found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ViewMedicalRecordParent.this, "Failed to load record: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }





}
