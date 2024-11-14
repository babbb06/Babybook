package com.example.babybook;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.babybook.model.HealthChecklist;
import com.example.babybook.emoji.DecimalDigitsInputFilter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class AddMedicalRecord extends AppCompatActivity {

    private String childId, LastName, FirstName, Sex, Address, Birthday;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medical_record); // Replace with your XML layout file name
        FirebaseApp.initializeApp(this);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get references to EditTexts
        EditText editTextDate = findViewById(R.id.editTextDate);
        EditText editTextWeight = findViewById(R.id.editTextWeight);
        EditText editTextTemperature = findViewById(R.id.editTextTemperature);
        EditText summaryDiagnosis = findViewById(R.id.summary_diagnosis);
        EditText treatmentPlan = findViewById(R.id.treatment_plan);
        EditText followUpPlan = findViewById(R.id.follow_up_plan);
        EditText editTextsex = findViewById(R.id.sex);
        EditText editTextaddress = findViewById(R.id.address);

        // Get references to CheckBoxes
        CheckBox checkSick = findViewById(R.id.check_sick);
        CheckBox checkCough = findViewById(R.id.check_cough);
        CheckBox checkDiarrhea = findViewById(R.id.check_diarrhea);
        CheckBox checkFever = findViewById(R.id.check_fever);
        CheckBox checkMeasles = findViewById(R.id.check_measles);
        CheckBox checkEarPain = findViewById(R.id.check_ear_pain);
        CheckBox checkPallor = findViewById(R.id.check_pallor);
        CheckBox checkMalnourished = findViewById(R.id.check_malnourished);
        CheckBox checkFeeding = findViewById(R.id.check_feeding);
        CheckBox checkBreastfeeding = findViewById(R.id.check_breastfeeding);
        CheckBox checkDiarrheaCough = findViewById(R.id.check_diarrhea_cough);
        CheckBox checkImmunization = findViewById(R.id.check_immunization);
        CheckBox checkOtherProblems = findViewById(R.id.check_other_problems);

        // Get reference to the Submit button
        Button submitButton = findViewById(R.id.submit_button);

        // Retrieve child ID from the intent
        childId = getIntent().getStringExtra("childId");
        LastName = getIntent().getStringExtra("LastName");
        FirstName = getIntent().getStringExtra("FirstName");
        Sex = getIntent().getStringExtra("Sex");
        Address = getIntent().getStringExtra("Address");
        Birthday = getIntent().getStringExtra("Birthday");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        getSupportActionBar().setTitle("Medical Records Details");

        editTextTemperature.setFilters(new InputFilter[]{new DecimalDigitsInputFilter()});

        // Get today's date
        String datetoday = new SimpleDateFormat("MM-dd-yyyy").format(new Date());
        editTextDate.setText(datetoday);

        // Set an onClickListener on the submit button
        submitButton.setOnClickListener(v -> {
            // Check user authentication
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();

            if (user == null) {
                showToast("Please log in to submit a medical record.");
                return; // Exit early if not authenticated
            }

            // Validate inputs
            if (validateInputs(editTextDate, editTextWeight, editTextTemperature)) {
                createMedicalRecord(editTextDate, editTextWeight, editTextTemperature, summaryDiagnosis, treatmentPlan, followUpPlan,
                        checkSick, checkCough, checkDiarrhea, checkFever, checkMeasles, checkEarPain,
                        checkPallor, checkMalnourished, checkFeeding, checkBreastfeeding, checkDiarrheaCough,
                        checkImmunization, checkOtherProblems, user.getUid(), childId); // Pass user ID as doctorId
            }
        });
    }

    private boolean validateInputs(EditText date, EditText weight, EditText temperature) {
        if (TextUtils.isEmpty(date.getText())) {
            showToast("Date is required.");
            return false;
        }
        if (TextUtils.isEmpty(weight.getText())) {
            showToast("Weight is required.");
            return false;
        }
        if (TextUtils.isEmpty(temperature.getText())) {
            showToast("Temperature is required.");
            return false;
        }
        return true;
    }

    private void showToast(String message) {
        Toast.makeText(AddMedicalRecord.this, message, Toast.LENGTH_SHORT).show();
    }

    private void createMedicalRecord(EditText editTextDate, EditText editTextWeight, EditText editTextTemperature, EditText summaryDiagnosis,
                                     EditText treatmentPlan, EditText followUpPlan, CheckBox checkSick, CheckBox checkCough,
                                     CheckBox checkDiarrhea, CheckBox checkFever, CheckBox checkMeasles, CheckBox checkEarPain,
                                     CheckBox checkPallor, CheckBox checkMalnourished, CheckBox checkFeeding, CheckBox checkBreastfeeding,
                                     CheckBox checkDiarrheaCough, CheckBox checkImmunization, CheckBox checkOtherProblems,
                                     String doctorId, String childId) {

        // Create a unique ID for the medical record
        String medicalRecordId = UUID.randomUUID().toString();

        // Create an instance of the HealthChecklist model
        HealthChecklist healthChecklist = new HealthChecklist();
        healthChecklist.setDate(editTextDate.getText().toString());
        healthChecklist.setWeight(editTextWeight.getText().toString());
        healthChecklist.setTemperature(editTextTemperature.getText().toString());
        healthChecklist.setSex(Sex);
        healthChecklist.setLastName(LastName);
        healthChecklist.setFirstName(FirstName);
        healthChecklist.setBirthdate(Birthday);




        healthChecklist.setSummaryDiagnosis(summaryDiagnosis.getText().toString());
        healthChecklist.setTreatmentPlan(treatmentPlan.getText().toString());
        healthChecklist.setFollowUpPlan(followUpPlan.getText().toString());
        healthChecklist.setDoctorId(doctorId);
        healthChecklist.setMedicalRecordId(medicalRecordId);
        healthChecklist.setChildId(childId);

        // Set CheckBox values
        healthChecklist.setSick(checkSick.isChecked());
        healthChecklist.setCough(checkCough.isChecked());
        healthChecklist.setDiarrhea(checkDiarrhea.isChecked());
        healthChecklist.setFever(checkFever.isChecked());
        healthChecklist.setMeasles(checkMeasles.isChecked());
        healthChecklist.setEarPain(checkEarPain.isChecked());
        healthChecklist.setPallor(checkPallor.isChecked());
        healthChecklist.setMalnourished(checkMalnourished.isChecked());
        healthChecklist.setAssessFeeding(checkFeeding.isChecked());
        healthChecklist.setAssessBreastfeeding(checkBreastfeeding.isChecked());
        healthChecklist.setLongDiarrheaOrCough(checkDiarrheaCough.isChecked());
        healthChecklist.setNeedsImmunization(checkImmunization.isChecked());
        healthChecklist.setOtherProblems(checkOtherProblems.isChecked());

        // Set current timestamp
        healthChecklist.setTimestamp(com.google.firebase.Timestamp.now());

        // Reference to the parent document (e.g., child document)
        db.collection("healthRecords").document(childId) // Use the appropriate parent collection and document ID
                .collection("medicalRecords") // Subcollection name
                .document(medicalRecordId) // Set a specific document ID for the medical record
                .set(healthChecklist)
                .addOnSuccessListener(documentReference -> {
                    showToast("Medical record submitted successfully!");
                    // Navigate to the ViewMedicalRecord activity
                    Intent intent = new Intent(AddMedicalRecord.this, ViewMedicalRecord.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error submitting data: " + e.getMessage());
                    showToast("Error submitting record. Please try again.");
                });
    }
}
