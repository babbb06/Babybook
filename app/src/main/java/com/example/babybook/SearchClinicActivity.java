package com.example.babybook;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class SearchClinicActivity extends AppCompatActivity {

    // Declare buttons
    private Button bgc, hepatitis_b, dpt, booster_1, opv_ipv;
    private Button booster_2, h_influenza_b, rotavirus, measles, mmr, booster_3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_clinic); // Change this to your XML layout file name

        // Initialize buttons
        bgc = findViewById(R.id.bgc);
        hepatitis_b = findViewById(R.id.hepatitis_b);
        dpt = findViewById(R.id.dpt);
        booster_1 = findViewById(R.id.booster_1);
        opv_ipv = findViewById(R.id.opv_ipv);
        booster_2 = findViewById(R.id.booster_2);
        h_influenza_b = findViewById(R.id.h_influenza_b);
        rotavirus = findViewById(R.id.rotavirus);
        measles = findViewById(R.id.measles);
        mmr = findViewById(R.id.mmr);
        booster_3 = findViewById(R.id.booster_3);

        // Set onClickListeners for each button
        bgc.setOnClickListener(view -> handleButtonClick("BGC"));
        hepatitis_b.setOnClickListener(view -> handleButtonClick("Hepatitis B"));
        dpt.setOnClickListener(view -> handleButtonClick("DPT"));
        booster_1.setOnClickListener(view -> handleButtonClick("Booster 1"));
        opv_ipv.setOnClickListener(view -> handleButtonClick("OPV IPV"));
        booster_2.setOnClickListener(view -> handleButtonClick("Booster 2"));
        h_influenza_b.setOnClickListener(view -> handleButtonClick("H Influenza B"));
        rotavirus.setOnClickListener(view -> handleButtonClick("Rotavirus"));
        measles.setOnClickListener(view -> handleButtonClick("Measles"));
        mmr.setOnClickListener(view -> handleButtonClick("MMR"));
        booster_3.setOnClickListener(view -> handleButtonClick("Booster 3"));
    }

    // Method to handle button clicks
    private void handleButtonClick(String buttonText) {
        // Implement your button click logic here
        // For example, you can show a toast message
        // Toast.makeText(this, buttonText + " clicked", Toast.LENGTH_SHORT).show();
    }
}
