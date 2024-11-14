package com.example.babybook;

import android.content.Intent;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ViewMedicalRecord extends AppCompatActivity {

    private TextView name,textViewWeight, textViewDate, textViewTemperature, textViewSummaryDiagnosis, textViewTreatmentPlan, textViewFollowUpPlan,textViewBirthday,textViewSex;
    private CheckBox checkBoxSick, checkBoxCough, checkBoxDiarrhea, checkBoxFever, checkBoxMeasles, checkBoxEarPain, checkBoxPallor, checkBoxMalnourished, checkBoxFeeding, checkBoxBreastfeeding, checkBoxDiarrheaCough, checkBoxImmunization, checkBoxOtherProblems;
    private String childId,FirstName,LastName,dateToday,Sex,Birthday; // This will now be used to fetch the medical record
    private FloatingActionButton fabCreatePost;
    private LinearLayout layouthide;
    private ConstraintLayout layoutgone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_medical_record);

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
        getSupportActionBar().setTitle("Medical Records Details");// DOCTOR SIDe



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

        layoutgone = findViewById(R.id.layoutgone);
        layouthide= findViewById(R.id.layouthide);
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
        fabCreatePost = findViewById(R.id.fabCreatePost);

        fetchHealthRecordDetails();

        name.setText(FirstName +" "+ LastName);
        textViewBirthday.setText(Birthday);

        // Fetch health record details from Firestore
        fetchHealthRecordDetails();

        // Set onClickListener for fabCreatePost
        fabCreatePost.setOnClickListener(v -> {
            // Create an Intent to navigate to AddMedicalRecord Activity
            Intent intent = new Intent(ViewMedicalRecord.this, AddMedicalRecord.class);
            intent.putExtra("childId", childId);
            intent.putExtra("FirstName", FirstName);
            intent.putExtra("LastName", LastName);
           intent.putExtra("dateToday", dateToday);
            intent.putExtra("Birthday", Birthday);
            intent.putExtra("Sex", Sex);

          //  intent.putExtra("Address", Address);
            startActivity(intent);
        });


    }



    private void fetchHealthRecordDetails() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Listen for real-time updates on a specific medical record using childId
        db.collection("healthRecords")
                .document(childId) // Parent document ID
                .collection("medicalRecords") // Subcollection name
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(ViewMedicalRecord.this, "No records found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        HealthChecklist checklist = documentSnapshot.toObject(HealthChecklist.class);
                        if (checklist != null) {
                            // Set UI elements with the fetched data
                            textViewWeight.setText(checklist.getWeight());
                            textViewDate.setText(checklist.getDate());
                            textViewTemperature.setText(checklist.getTemperature());
                            textViewSummaryDiagnosis.setText(checklist.getSummaryDiagnosis());
                            textViewTreatmentPlan.setText(checklist.getTreatmentPlan());
                            textViewFollowUpPlan.setText(checklist.getFollowUpPlan());

                            // Set checkbox values based on boolean data
                            checkBoxSick.setChecked(checklist.isSick());
                            checkBoxCough.setChecked(checklist.hasCough());
                            checkBoxDiarrhea.setChecked(checklist.hasDiarrhea());
                            checkBoxFever.setChecked(checklist.hasFever());
                            checkBoxMeasles.setChecked(checklist.hasMeasles());
                            checkBoxEarPain.setChecked(checklist.hasEarPain());
                            checkBoxPallor.setChecked(checklist.hasPallor());
                            checkBoxMalnourished.setChecked(checklist.isMalnourished());
                            checkBoxFeeding.setChecked(checklist.isAssessFeeding());
                            checkBoxBreastfeeding.setChecked(checklist.isAssessBreastfeeding());
                            checkBoxDiarrheaCough.setChecked(checklist.hasLongDiarrheaOrCough());
                            checkBoxImmunization.setChecked(checklist.needsImmunization());
                            checkBoxOtherProblems.setChecked(checklist.hasOtherProblems());
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ViewMedicalRecord.this, "Failed to load record: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
