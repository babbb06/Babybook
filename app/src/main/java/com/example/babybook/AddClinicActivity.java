package com.example.babybook;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;






public class AddClinicActivity extends AppCompatActivity {

    private LinearLayout vaccineList;
    private List<String> vaccines;
    private TextInputEditText etStartTime;
    private TextInputEditText etEndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_clinic);




        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Clinic");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Show back button
        }





        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        vaccineList = findViewById(R.id.vaccineList);
        Button addButton = findViewById(R.id.addButton);
        etStartTime = findViewById(R.id.etStartTime);
        etEndTime = findViewById(R.id.etEndTime);

        // List of vaccines
        vaccines = new ArrayList<>();
        vaccines.add("BCG");
        vaccines.add("Hepatitis B");
        vaccines.add("DPT");
        vaccines.add("Booster 1");
        vaccines.add("OPV IPV");
        vaccines.add("Booster 2");
        vaccines.add("H Influenza B");
        vaccines.add("Rotavirus");
        vaccines.add("Measles");
        vaccines.add("MMR");
        vaccines.add("Booster 3");

        etStartTime.setOnClickListener(v -> showTimePickerDialog(true));
        etEndTime.setOnClickListener(v -> showTimePickerDialog(false));

        addButton.setOnClickListener(view -> showVaccineSelectionDialog());
    }

    private void showVaccineSelectionDialog() {
        CharSequence[] vaccineArray = vaccines.toArray(new CharSequence[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose a Vaccine")
                .setItems(vaccineArray, (dialog, which) -> {
                    String selectedVaccine = vaccineArray[which].toString();
                    showVaccineInputDialog(selectedVaccine);
                })
                .setNegativeButton("Cancel", null);

        builder.create().show();
    }

    private void showVaccineInputDialog(String vaccine) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Vaccine Details");

        // Inflate the custom dialog layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_vaccine_details, null);
        builder.setView(dialogView);

        // Get references to the TextView and EditText in the dialog
        TextView vaccineNameTextView = dialogView.findViewById(R.id.vaccineNameTextView);
        EditText quantityInput = dialogView.findViewById(R.id.quantityInput);
        ImageView buttonPlus = dialogView.findViewById(R.id.buttonPlus);
        ImageView buttonMinus = dialogView.findViewById(R.id.buttonMinus);

        // Set the vaccine name in the TextView
        vaccineNameTextView.setText(vaccine);

        // Set initial quantity
        quantityInput.setText("1");

        // Add functionality for the minus button
        buttonMinus.setOnClickListener(view -> {
            int currentQuantity = Integer.parseInt(quantityInput.getText().toString());
            if (currentQuantity > 1) {
                currentQuantity--;
                quantityInput.setText(String.valueOf(currentQuantity));
            }
        });

        // Add functionality for the plus button
        buttonPlus.setOnClickListener(view -> {
            int currentQuantity = Integer.parseInt(quantityInput.getText().toString());
            if (currentQuantity < 999) { // Set a max limit for safety
                currentQuantity++;
                quantityInput.setText(String.valueOf(currentQuantity));
            }
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
                    String quantity = quantityInput.getText().toString();
                    addVaccineToList(vaccine, quantity);
                })
                .setNegativeButton("Cancel", null);

        // Show the dialog
        builder.create().show();
    }

    private void addVaccineToList(String vaccine, String quantity) {
        // Inflate the vaccine list item layout
        View vaccineItem = getLayoutInflater().inflate(R.layout.vaccine_list_item, vaccineList, false);

        // Find the TextViews in the inflated layout
        TextView vaccineNameTextView = vaccineItem.findViewById(R.id.vaccineNameTextView);
        TextView quantityTextView = vaccineItem.findViewById(R.id.quantityTextView);

        // Set the text for the TextViews
        vaccineNameTextView.setText(vaccine);
        quantityTextView.setText(quantity);

        // Add the inflated view to the vaccine list
        vaccineList.addView(vaccineItem);
    }
    // Override onOptionsItemSelected to handle back button click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // ID for the back button
                onBackPressed(); // Call this to go back
                return true; // Indicate that the event was handled
            default:
                return super.onOptionsItemSelected(item); // Handle other menu items if any
        }
    }

    private void showTimePickerDialog(final boolean isStartTime) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Adjust the minute to the nearest 00 or 30
        minute = (minute < 15) ? 0 : (minute < 45) ? 30 : 0;
        if (minute == 0 && calendar.get(Calendar.HOUR_OF_DAY) == 23) {
            hour = 0; // Wrap around to 0 when it's 23:00
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {
                    String time = String.format("%02d:%02d %s",
                            selectedHour % 12 == 0 ? 12 : selectedHour % 12,
                            selectedMinute,
                            selectedHour < 12 ? "AM" : "PM");

                    if (isStartTime) {
                        etStartTime.setText(time);
                    } else {
                        etEndTime.setText(time);
                    }
                },
                hour,
                minute, // Use adjusted minute
                false // Use 12-hour format
        ) {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                // Restrict minutes to 00 or 30
                if (minute != 0 && minute != 30) {
                    view.setMinute(minute < 15 ? 0 : 30);
                }
                super.onTimeChanged(view, hourOfDay, minute);
            }
        };

        timePickerDialog.show();
    }
}
