package com.example.babybook.medicalrecord;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.babybook.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddMedicalRecord extends AppCompatActivity {

    private String childId; // Store the child ID
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medical_record);  // Replace with your XML layout file name
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

        // Young Infant Checklist
        CheckBox checkBabySick = findViewById(R.id.check_baby_sick);
        CheckBox checkBabyFever = findViewById(R.id.check_baby_fever);
        CheckBox checkBabyJaundice = findViewById(R.id.check_baby_jaundice);
        CheckBox checkBabyWeight = findViewById(R.id.check_baby_weight);
        CheckBox checkBabyFeeding = findViewById(R.id.check_baby_feeding);

        // Get reference to the Submit button
        Button submitButton = findViewById(R.id.submit_button);

        // Retrieve child ID from the intent
        childId = getIntent().getStringExtra("CHILD_ID");

        // Fetch user data
        fetchUserData();

        // Set an onClickListener on the submit button
        submitButton.setOnClickListener(v -> {
            // Check user authentication
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();

            if (user == null) {
                Toast.makeText(AddMedicalRecord.this, "Please log in to submit a medical record.", Toast.LENGTH_SHORT).show();
                return; // Exit early if not authenticated
            }

            // Call createMedicalRecord without latitude and longitude
            createMedicalRecord(editTextDate, editTextWeight, editTextTemperature, summaryDiagnosis, treatmentPlan, followUpPlan,
                    checkSick, checkCough, checkDiarrhea, checkFever, checkMeasles, checkEarPain,
                    checkPallor, checkMalnourished, checkFeeding, checkBreastfeeding, checkDiarrheaCough,
                    checkImmunization, checkOtherProblems, checkBabySick, checkBabyFever, checkBabyJaundice,
                    checkBabyWeight, checkBabyFeeding);
        });
    }

    private void createMedicalRecord(EditText editTextDate, EditText editTextWeight, EditText editTextTemperature,
                                     EditText summaryDiagnosis, EditText treatmentPlan, EditText followUpPlan,
                                     CheckBox checkSick, CheckBox checkCough, CheckBox checkDiarrhea,
                                     CheckBox checkFever, CheckBox checkMeasles, CheckBox checkEarPain,
                                     CheckBox checkPallor, CheckBox checkMalnourished, CheckBox checkFeeding,
                                     CheckBox checkBreastfeeding, CheckBox checkDiarrheaCough,
                                     CheckBox checkImmunization, CheckBox checkOtherProblems,
                                     CheckBox checkBabySick, CheckBox checkBabyFever, CheckBox checkBabyJaundice,
                                     CheckBox checkBabyWeight, CheckBox checkBabyFeeding) {

        // Create a HashMap to store the data
        Map<String, Object> medicalRecordData = new HashMap<>();

        // Store EditText values
        medicalRecordData.put("Date", editTextDate.getText().toString());
        medicalRecordData.put("Weight", editTextWeight.getText().toString());
        medicalRecordData.put("Temperature", editTextTemperature.getText().toString());
        medicalRecordData.put("Summary of Diagnosis", summaryDiagnosis.getText().toString());
        medicalRecordData.put("Treatment Plan", treatmentPlan.getText().toString());
        medicalRecordData.put("Follow Up Plan", followUpPlan.getText().toString());
        medicalRecordData.put("Child ID", childId);  // Add the child ID to the record

        // Store CheckBox states
        medicalRecordData.put("Is the child TOO SICK?", checkSick.isChecked());
        medicalRecordData.put("COUGH or DIFFICULT BREATHING?", checkCough.isChecked());
        medicalRecordData.put("Has DIARRHOEA?", checkDiarrhea.isChecked());
        medicalRecordData.put("Has FEVER?", checkFever.isChecked());
        medicalRecordData.put("Has MEASLES in the last 3 months?", checkMeasles.isChecked());
        medicalRecordData.put("EAR PAIN/DISCHARGE?", checkEarPain.isChecked());
        medicalRecordData.put("Check for PALLOR?", checkPallor.isChecked());
        medicalRecordData.put("Child MALNOURISHED?", checkMalnourished.isChecked());
        medicalRecordData.put("Assess FEEDING?", checkFeeding.isChecked());
        medicalRecordData.put("Assess BREAST FEEDING?", checkBreastfeeding.isChecked());
        medicalRecordData.put("DIARRHOEA/COUGH > 2 WEEKS?", checkDiarrheaCough.isChecked());
        medicalRecordData.put("IMMUNIZATION needed?", checkImmunization.isChecked());
        medicalRecordData.put("Other problems?", checkOtherProblems.isChecked());

        // Young Infant Checklist
        medicalRecordData.put("Baby TOO SICK?", checkBabySick.isChecked());
        medicalRecordData.put("Baby has FEVER?", checkBabyFever.isChecked());
        medicalRecordData.put("Baby JAUNDICE?", checkBabyJaundice.isChecked());
        medicalRecordData.put("Assess Baby's Weight?", checkBabyWeight.isChecked());
        medicalRecordData.put("Baby's Feeding?", checkBabyFeeding.isChecked());

        // Save data to Firestore
        db.collection("medicalRecords")
                .add(medicalRecordData)
                .addOnSuccessListener(documentReference -> {
                    // Successfully saved
                    Toast.makeText(AddMedicalRecord.this, "Medical record submitted successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error submitting data: " + e.getMessage());
                    Toast.makeText(AddMedicalRecord.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchUserData() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            String userId = user.getUid(); // Get the current user's ID

            db.collection("Users").document(userId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Access user data
                                String userName = document.getString("name");
                                String userEmail = document.getString("email");

                                // You can now use this data as needed, e.g., display it in the UI
                                Toast.makeText(this, "User: " + userName + ", Email: " + userEmail, Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d("FetchUserData", "No such document");
                            }
                        } else {
                            Log.d("FetchUserData", "Get failed with ", task.getException());
                        }
                    });
        }
    }
}
