package com.example.babybook;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.babybook.model.AppointmentRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BookAppointmentActivity extends AppCompatActivity {

    private EditText childNameEditText;
    private CalendarView appointmentCalendar;
    private Button submitButton;
    private String selectedService;
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

        childNameEditText = findViewById(R.id.child_name_edit_text);
        appointmentCalendar = findViewById(R.id.appointment_calendar);
        submitButton = findViewById(R.id.submit_button);
        GridLayout timeButtonsGrid = findViewById(R.id.time_buttons_grid);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Get doctorId from the intent
        doctorId = getIntent().getStringExtra("doctorId");

        appointmentCalendar.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
            Toast.makeText(BookAppointmentActivity.this, "Selected date: " + selectedDate, Toast.LENGTH_SHORT).show();
        });

        for (int i = 0; i < timeButtonsGrid.getChildCount(); i++) {
            View child = timeButtonsGrid.getChildAt(i);
            if (child instanceof Button) {
                Button timeButton = (Button) child;
                timeButton.setOnClickListener(v -> {
                    selectedTime = timeButton.getText().toString();
                    Toast.makeText(BookAppointmentActivity.this, "Selected time: " + selectedTime, Toast.LENGTH_SHORT).show();
                });
            }
        }

        Button checkupButton = findViewById(R.id.checkup_button);
        Button vaccinationButton = findViewById(R.id.vaccination_button);

        checkupButton.setOnClickListener(v -> {
            selectedService = "Check-up";
            Toast.makeText(BookAppointmentActivity.this, "Selected service: Check-up", Toast.LENGTH_SHORT).show();
        });

        vaccinationButton.setOnClickListener(v -> {
            selectedService = "Vaccination";
            Toast.makeText(BookAppointmentActivity.this, "Selected service: Vaccination", Toast.LENGTH_SHORT).show();
        });

        submitButton.setOnClickListener(v -> {
            String childName = childNameEditText.getText().toString();

            if (childName.isEmpty() || selectedService == null || selectedTime == null || doctorId == null) {
                Toast.makeText(BookAppointmentActivity.this, "Please complete all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            AppointmentRequest appointmentRequest = new AppointmentRequest(
                    null,
                    childName,
                    selectedService,
                    selectedDate,
                    selectedTime,
                    "Pending",
                    mAuth.getCurrentUser().getUid(),
                    doctorId,
                    new Date()
            );

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
}