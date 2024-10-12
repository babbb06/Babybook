package com.example.babybook;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.babybook.model.AppointmentRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BookAppointmentActivity extends AppCompatActivity {

    private TextInputEditText editTextFirstName, lastnameEt, editTextBirthday, editTextbirthplace, eTAddress, etTime;
    private CalendarView appointmentCalendar;
    private Button submitButton;
    private String firstName, lastName, birthDay, birthPlace, address, selectedService;
    private String selectedTime;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String selectedDate;
    private String doctorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize UI components
        editTextFirstName = findViewById(R.id.editTextFirstName);
        lastnameEt = findViewById(R.id.lastnameEt);
        editTextBirthday = findViewById(R.id.editTextBirthday);
        editTextbirthplace = findViewById(R.id.editTextbirthplace);
        eTAddress = findViewById(R.id.eTAddress);
        appointmentCalendar = findViewById(R.id.appointment_calendar);
        submitButton = findViewById(R.id.submit_button);
        etTime = findViewById(R.id.etTime);

        // Set an OnClickListener to display TimePickerDialog
        etTime.setOnClickListener(view -> showTimePickerDialog());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Get doctorId from the intent
        doctorId = getIntent().getStringExtra("doctorId");

        appointmentCalendar.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
            Toast.makeText(BookAppointmentActivity.this, "Selected date: " + selectedDate, Toast.LENGTH_SHORT).show();
        });

        submitButton.setOnClickListener(v -> {
            // Check if fields are filled
            if (areFieldsValid()) {
                // Create AppointmentRequest
                AppointmentRequest appointmentRequest = createAppointmentRequest();

                // Proceed to book appointment
                db.collection("appointments")
                        .add(appointmentRequest)
                        .addOnSuccessListener(documentReference -> {
                            String appointmentId = documentReference.getId();
                            appointmentRequest.setId(appointmentId);
                            documentReference.set(appointmentRequest)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(BookAppointmentActivity.this, "Appointment booked successfully with ID: " + appointmentId, Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(BookAppointmentActivity.this, SchedulesAppointmentActivity.class);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("BookAppointmentActivity", "Error updating appointment ID", e);
                                        Toast.makeText(BookAppointmentActivity.this, "Error updating appointment ID", Toast.LENGTH_SHORT).show();
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Log.e("BookAppointmentActivity", "Error booking appointment", e);
                            Toast.makeText(BookAppointmentActivity.this, "Error booking appointment", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(BookAppointmentActivity.this, "Please complete all fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showTimePickerDialog() {
        // Get the current time as default values
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Show the TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (TimePicker view, int hourOfDay, int minuteOfHour) -> {
                    // Format the time and set it to the TextView
                    String formattedTime = String.format("%02d:%02d", hourOfDay, minuteOfHour);
                    etTime.setText(formattedTime);
                    selectedTime = formattedTime; // Store selected time
                },
                hour,
                minute,
                true
        );

        timePickerDialog.show();
    }

    private boolean areFieldsValid() {
        firstName = editTextFirstName.getText().toString().trim();
        lastName = lastnameEt.getText().toString().trim();
        selectedService = "Vaccination"; // Set default or modify based on user input
        selectedTime = etTime.getText().toString().trim();

        return !firstName.isEmpty() && !lastName.isEmpty() && selectedService != null && selectedTime != null && doctorId != null;
    }

    private AppointmentRequest createAppointmentRequest() {
        birthDay = editTextBirthday.getText().toString().trim();
        birthPlace = editTextbirthplace.getText().toString().trim();
        address = eTAddress.getText().toString().trim();

        return new AppointmentRequest(
                null,
                firstName,
                lastName,
                birthDay,
                birthPlace,
                address,
                selectedService,
                selectedDate,
                selectedTime,
                "Pending",
                mAuth.getCurrentUser().getUid(),
                doctorId,
                new Date()
        );
    }
}
