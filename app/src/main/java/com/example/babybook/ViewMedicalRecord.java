package com.example.babybook;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.babybook.model.HealthChecklist;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ViewMedicalRecord extends AppCompatActivity {

    private TextView textViewWeight, textViewDate, textViewTemperature, textViewSummaryDiagnosis, textViewTreatmentPlan, textViewFollowUpPlan;
    private CheckBox checkBoxSick, checkBoxCough, checkBoxDiarrhea, checkBoxFever, checkBoxMeasles, checkBoxEarPain, checkBoxPallor, checkBoxMalnourished, checkBoxFeeding, checkBoxBreastfeeding, checkBoxDiarrheaCough, checkBoxImmunization, checkBoxOtherProblems;
    private String childId,FirstName; // This will now be used to fetch the medical record

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_medical_record);

        // Get the child ID from the intent
        childId = getIntent().getStringExtra("childId");
        FirstName = getIntent().getStringExtra("FirstName");


        Toast.makeText(this, "Child ID: " + childId, Toast.LENGTH_SHORT).show();

        // Initialize UI components
        textViewWeight = findViewById(R.id.textViewWeight);
        textViewDate = findViewById(R.id.textViewDate);
        textViewTemperature = findViewById(R.id.textViewTemperature);
        textViewSummaryDiagnosis = findViewById(R.id.textViewSummaryDiagnosis);
        textViewTreatmentPlan = findViewById(R.id.textViewTreatmentPlan);
        textViewFollowUpPlan = findViewById(R.id.textViewFollowUpPlan);

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




    }



    private void fetchHealthRecordDetails() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("medicalRecords")
                .whereEqualTo("childId", childId)
                .whereEqualTo("FirstName", FirstName)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            HealthChecklist checklist = document.toObject(HealthChecklist.class);

                            // Logging to check if checklist data is being fetched correctly
                            if (checklist != null) {
                                Toast.makeText(ViewMedicalRecord.this, "Checklist retrieved successfully", Toast.LENGTH_SHORT).show();


                            } else {
                                Toast.makeText(ViewMedicalRecord.this, "Checklist is null", Toast.LENGTH_SHORT).show();
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

                            // Disable all checkboxes
                            checkBoxSick.setEnabled(false);
                            checkBoxCough.setEnabled(false);
                            checkBoxDiarrhea.setEnabled(false);
                            checkBoxFever.setEnabled(false);
                            checkBoxMeasles.setEnabled(false);
                            checkBoxEarPain.setEnabled(false);
                            checkBoxPallor.setEnabled(false);
                            checkBoxMalnourished.setEnabled(false);
                            checkBoxFeeding.setEnabled(false);
                            checkBoxBreastfeeding.setEnabled(false);
                            checkBoxDiarrheaCough.setEnabled(false);
                            checkBoxImmunization.setEnabled(false);
                            checkBoxOtherProblems.setEnabled(false);
                        }
                    } else {
                        Toast.makeText(ViewMedicalRecord.this, "No record found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ViewMedicalRecord.this, "Failed to load record: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }





}
