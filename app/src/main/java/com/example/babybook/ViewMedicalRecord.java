package com.example.babybook;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ViewMedicalRecord extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_medical_record);



        // Retrieve the data passed from the previous activity
        Intent intent = getIntent();
        String date = intent.getStringExtra("DATE");
        String weight = intent.getStringExtra("WEIGHT");
        String temperature = intent.getStringExtra("TEMPERATURE");
        String summaryDiagnosis = intent.getStringExtra("SUMMARY_DIAGNOSIS");
        String treatmentPlan = intent.getStringExtra("TREATMENT_PLAN");
        String followUpPlan = intent.getStringExtra("FOLLOW_UP_PLAN");
        boolean isSick = intent.getBooleanExtra("IS_SICK", false);
        boolean hasCough = intent.getBooleanExtra("HAS_COUGH", false);
        boolean hasDiarrhea = intent.getBooleanExtra("HAS_DIARRHEA", false);
        boolean hasFever = intent.getBooleanExtra("HAS_FEVER", false);
        boolean hasMeasles = intent.getBooleanExtra("HAS_MEASLES", false);
        boolean hasEarPain = intent.getBooleanExtra("HAS_EAR_PAIN", false);
        boolean hasPallor = intent.getBooleanExtra("HAS_PALLOR", false);
        boolean isMalnourished = intent.getBooleanExtra("IS_MALNOURISHED", false);
        boolean assessFeeding = intent.getBooleanExtra("ASSESS_FEEDING", false);
        boolean assessBreastfeeding = intent.getBooleanExtra("ASSESS_BREASTFEEDING", false);
        boolean hasDiarrheaCough = intent.getBooleanExtra("HAS_DIARRHEA_COUGH", false);
        boolean needsImmunization = intent.getBooleanExtra("NEEDS_IMMUNIZATION", false);
        boolean hasOtherProblems = intent.getBooleanExtra("HAS_OTHER_PROBLEMS", false);

        // Display the data
        ((TextView) findViewById(R.id.textViewDate)).setText("Date: " + date);
        ((TextView) findViewById(R.id.textViewWeight)).setText("Weight: " + weight);
        ((TextView) findViewById(R.id.textViewTemperature)).setText("Temperature: " + temperature);
        ((TextView) findViewById(R.id.textViewSummaryDiagnosis)).setText("Summary of Diagnosis: " + summaryDiagnosis);
        ((TextView) findViewById(R.id.textViewTreatmentPlan)).setText("Treatment Plan: " + treatmentPlan);
        ((TextView) findViewById(R.id.textViewFollowUpPlan)).setText("Follow Up Plan: " + followUpPlan);

        // Set checkbox states
        ((CheckBox) findViewById(R.id.checkBoxSick)).setChecked(isSick);
        ((CheckBox) findViewById(R.id.checkBoxCough)).setChecked(hasCough);
        ((CheckBox) findViewById(R.id.checkBoxDiarrhea)).setChecked(hasDiarrhea);
        ((CheckBox) findViewById(R.id.checkBoxFever)).setChecked(hasFever);
        ((CheckBox) findViewById(R.id.checkBoxMeasles)).setChecked(hasMeasles);
        ((CheckBox) findViewById(R.id.checkBoxEarPain)).setChecked(hasEarPain);
        ((CheckBox) findViewById(R.id.checkBoxPallor)).setChecked(hasPallor);
        ((CheckBox) findViewById(R.id.checkBoxMalnourished)).setChecked(isMalnourished);
        ((CheckBox) findViewById(R.id.checkBoxFeeding)).setChecked(assessFeeding);
        ((CheckBox) findViewById(R.id.checkBoxBreastfeeding)).setChecked(assessBreastfeeding);
        ((CheckBox) findViewById(R.id.checkBoxDiarrheaCough)).setChecked(hasDiarrheaCough);
        ((CheckBox) findViewById(R.id.checkBoxImmunization)).setChecked(needsImmunization);
        ((CheckBox) findViewById(R.id.checkBoxOtherProblems)).setChecked(hasOtherProblems);
    }
}
