package com.example.babybook;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.babybook.model.HealthChecklist;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ViewMedicalRecordParent extends AppCompatActivity {

    private TextView name,textViewWeight,textViewBirthday, textViewSex,textViewDate, textViewTemperature, textViewSummaryDiagnosis, textViewTreatmentPlan, textViewFollowUpPlan;
    private CheckBox checkBoxSick, checkBoxCough, checkBoxDiarrhea, checkBoxFever, checkBoxMeasles, checkBoxEarPain, checkBoxPallor, checkBoxMalnourished, checkBoxFeeding, checkBoxBreastfeeding, checkBoxDiarrheaCough, checkBoxImmunization, checkBoxOtherProblems;
    private String childId,FirstName,LastName,dateToday,Sex,Birthday;
    private LinearLayout layouthide;
    private ConstraintLayout layoutgone;// This will now be used to fetch the medical record

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_medical_record_parent);

        // Get the child ID from the intent
        childId = getIntent().getStringExtra("childId");
        FirstName = getIntent().getStringExtra("FirstName");
        LastName = getIntent().getStringExtra("LastName");
        dateToday = getIntent().getStringExtra("dateToday");
        Sex = getIntent().getStringExtra("Sex");
        Birthday = getIntent().getStringExtra("Birthday");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        getSupportActionBar().setTitle("Medical Records Details");// PARENT SIDe



        // Initialize UI components
        textViewWeight = findViewById(R.id.textViewWeight);
        textViewDate = findViewById(R.id.textViewDate);
        textViewSex = findViewById(R.id.textViewSex);
        textViewBirthday = findViewById(R.id.textViewBirthday);
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
        layoutgone = findViewById(R.id.layoutgone);
        layouthide= findViewById(R.id.layouthide);

        fetchHealthRecordDetails();

        name.setText(FirstName +" "+ LastName);
        textViewBirthday.setText(Birthday);


    }




    private void fetchHealthRecordDetails() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Fetch medical records for the specified childId
        db.collection("healthRecords")
                .document(childId) // Document ID for health record
                .collection("medicalRecords") // Subcollection name
                .document(childId) // Replace with the actual medical record document ID
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        HealthChecklist checklist = documentSnapshot.toObject(HealthChecklist.class);

                        // Logging to check if checklist data is being fetched correctly
                        if (checklist != null) {
                            // Set UI elements with the fetched data
                            textViewWeight.setText(documentSnapshot.getString("Weight"));
                            textViewDate.setText(documentSnapshot.getString("Date"));
                            textViewTemperature.setText(documentSnapshot.getString("Temperature"));
                            textViewSummaryDiagnosis.setText(documentSnapshot.getString("Summary"));
                            textViewTreatmentPlan.setText(documentSnapshot.getString("Treatment"));
                            textViewFollowUpPlan.setText(documentSnapshot.getString("Follow"));

                            // Set checkbox values based on boolean data
                            checkBoxSick.setChecked(documentSnapshot.getBoolean("IsSick") != null && documentSnapshot.getBoolean("IsSick"));
                            checkBoxCough.setChecked(documentSnapshot.getBoolean("HasCough") != null && documentSnapshot.getBoolean("HasCough"));
                            checkBoxDiarrhea.setChecked(documentSnapshot.getBoolean("HasDiarrhea") != null && documentSnapshot.getBoolean("HasDiarrhea"));
                            checkBoxFever.setChecked(documentSnapshot.getBoolean("HasFever") != null && documentSnapshot.getBoolean("HasFever"));
                            checkBoxMeasles.setChecked(documentSnapshot.getBoolean("HasMeasles") != null && documentSnapshot.getBoolean("HasMeasles"));
                            checkBoxEarPain.setChecked(documentSnapshot.getBoolean("HasEarPain") != null && documentSnapshot.getBoolean("HasEarPain"));
                            checkBoxPallor.setChecked(documentSnapshot.getBoolean("IsPallor") != null && documentSnapshot.getBoolean("IsPallor"));
                            checkBoxMalnourished.setChecked(documentSnapshot.getBoolean("IsMalnourished") != null && documentSnapshot.getBoolean("IsMalnourished"));
                            checkBoxFeeding.setChecked(documentSnapshot.getBoolean("IsFeeding") != null && documentSnapshot.getBoolean("IsFeeding"));
                            checkBoxBreastfeeding.setChecked(documentSnapshot.getBoolean("IsBreastfeeding") != null && documentSnapshot.getBoolean("IsBreastfeeding"));
                            checkBoxDiarrheaCough.setChecked(documentSnapshot.getBoolean("HasDiarrheaCough") != null && documentSnapshot.getBoolean("HasDiarrheaCough"));
                            checkBoxImmunization.setChecked(documentSnapshot.getBoolean("HasImmunization") != null && documentSnapshot.getBoolean("HasImmunization"));
                            checkBoxOtherProblems.setChecked(documentSnapshot.getBoolean("HasOtherProblems") != null && documentSnapshot.getBoolean("HasOtherProblems"));

                            layouthide.setVisibility(View.VISIBLE);
                            layoutgone.setVisibility(View.GONE);
                            Toast.makeText(ViewMedicalRecordParent.this, "correct", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ViewMedicalRecordParent.this, "Checklist is null", Toast.LENGTH_SHORT).show();
                            layouthide.setVisibility(View.GONE);
                            layoutgone.setVisibility(View.VISIBLE);
                        }
                    } else {
                        layouthide.setVisibility(View.GONE);
                        layoutgone.setVisibility(View.VISIBLE);
                        Toast.makeText(ViewMedicalRecordParent.this, "Checklist is null", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ViewMedicalRecordParent.this, "Failed to load record: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }







}
