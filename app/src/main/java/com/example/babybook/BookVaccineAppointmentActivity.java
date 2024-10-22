package com.example.babybook;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class BookVaccineAppointmentActivity extends AppCompatActivity {

    private TextInputEditText editTextFirstName, lastnameEt, editTextBirthday, editTextbirthplace, etTime, etSex;
    private TextInputEditText etStreet, etBrgy, etCity, etProvince;
    private CalendarView appointmentCalendar;
    private Button submitButton;
    private String firstName, lastName, birthDay, birthPlace, address, selectedGender, selectedService = "Vaccination";
    private String selectedTime;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String selectedDate;
    private String doctorId, schedStartTime, schedEndTime;
    private List<String> schedDays;
    private TextView tvAvailableDays, tvAvailableTime;
    private Dialog progressDialog;
    private Spinner spinnerSex;
    private Integer selectedSpinnerPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_vaccine_appointment);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize UI components
        editTextFirstName = findViewById(R.id.editTextFirstName);
        lastnameEt = findViewById(R.id.lastnameEt);
        editTextBirthday = findViewById(R.id.editTextBirthday);
        editTextbirthplace = findViewById(R.id.editTextbirthplace);
        etSex = findViewById(R.id.editTextsex);
        spinnerSex = findViewById(R.id.sexspinner);
        etStreet = findViewById(R.id.eTStreet);
        etBrgy = findViewById(R.id.eTBarangay);
        etCity = findViewById(R.id.eTCity);
        etProvince = findViewById(R.id.eTProvince);
        appointmentCalendar = findViewById(R.id.appointment_calendar);
        submitButton = findViewById(R.id.submit_button);
        etTime = findViewById(R.id.etTime);
        tvAvailableDays = findViewById(R.id.tvAvailableDays);
        tvAvailableTime = findViewById(R.id.tvAvailableTime);

        // Set an OnClickListener to display TimePickerDialog
        etTime.setOnClickListener(view -> showTimePickerDialog());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Get data from the intent
        doctorId = getIntent().getStringExtra("doctorId");
        schedStartTime = getIntent().getStringExtra("schedStartTime");
        schedEndTime = getIntent().getStringExtra("schedEndTime");
        schedDays = getIntent().getStringArrayListExtra("schedDays");

        schedDays = getIntent().getStringArrayListExtra("schedDays");

        if (schedDays != null && !schedDays.isEmpty()) {
            StringBuilder days = new StringBuilder();
            for (String day : schedDays) {
                days.append(day).append(", ");
            }
            // Remove the trailing comma and space
            if (days.length() > 0) {
                days.setLength(days.length() - 2);
            }

            //Toast.makeText(this, "Scheduled Days: " + days.toString(), Toast.LENGTH_LONG).show();
            tvAvailableDays.setText("Available Days: " + days);
            tvAvailableTime.setText("Available Time: " + schedStartTime + " - " + schedEndTime);
        } else {
            Toast.makeText(this, "No scheduled days found.", Toast.LENGTH_SHORT).show();
        }


        //Toast.makeText(this, schedStartTime + " - " + schedEndTime, Toast.LENGTH_SHORT).show();



        // SELECTING DATE WITH VALIDATIONS
        // Disable past dates on the CalendarView
        appointmentCalendar.setMinDate(System.currentTimeMillis());

        // Get allowed days from schedDays
        Set<String> allowedDays = new HashSet<>();
        if (schedDays != null) {
            for (String day : schedDays) {
                allowedDays.add(day.toLowerCase(Locale.ROOT)); // Store in lowercase for easy comparison
            }
        }

        // Find the next available date based on allowed days
        Calendar currentCal = Calendar.getInstance();
        int currentDayOfWeek = currentCal.get(Calendar.DAY_OF_WEEK);
        int daysToAdd = 0;
        boolean foundNextAvailable = false;

        // Loop to find the next available day
        while (!foundNextAvailable) {
            String dayOfWeek = new SimpleDateFormat("EEEE", Locale.getDefault()).format(currentCal.getTime()).toLowerCase(Locale.ROOT);

            if (allowedDays.contains(dayOfWeek)) {
                foundNextAvailable = true; // We've found the next available day
            } else {
                daysToAdd++;
                currentCal.add(Calendar.DAY_OF_MONTH, 1); // Move to the next day
            }
        }



        // Set the minimum date to the next available date
        appointmentCalendar.setMinDate(currentCal.getTimeInMillis());

        // Set the listener for date selection
        appointmentCalendar.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar selectedCal = Calendar.getInstance();
            selectedCal.set(year, month, dayOfMonth);

            // Check if the selected date is a valid day
            String dayOfWeek = new SimpleDateFormat("EEEE", Locale.getDefault()).format(selectedCal.getTime()).toLowerCase(Locale.ROOT);
            if (allowedDays.contains(dayOfWeek)) {
                selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
                Toast.makeText(BookVaccineAppointmentActivity.this, "Selected date: " + selectedDate, Toast.LENGTH_SHORT).show();
            } else {
                showMessageDialog("Selected day is not available for appointments.",null);
                //Toast.makeText(BookVaccineAppointmentActivity.this, "Selected day is not available for appointments.", Toast.LENGTH_SHORT).show();
                appointmentCalendar.setDate(System.currentTimeMillis(), true, true); // Reset to today or previous valid date
            }
        });



        submitButton.setOnClickListener(v -> {
            // Check if fields are filled
            if (areFieldsValid()) {
                // Create AppointmentRequest
                AppointmentRequest appointmentRequest = createAppointmentRequest();
                showProgressDialog(this);

                // Proceed to book appointment
                db.collection("appointments")
                        .add(appointmentRequest)
                        .addOnSuccessListener(documentReference -> {
                            String appointmentId = documentReference.getId();
                            appointmentRequest.setId(appointmentId);
                            documentReference.set(appointmentRequest)
                                    .addOnSuccessListener(aVoid -> {
                                        //Toast.makeText(BookVaccineAppointmentActivity.this, "Appointment booked successfully with ID: " + appointmentId, Toast.LENGTH_SHORT).show();
                                        showMessageDialog("Appointment booked successfully", () -> {
                                            startActivity(new Intent(BookVaccineAppointmentActivity.this, SchedulesAppointmentActivity.class));
                                            finish();
                                            hideProgressDialog();
                                        });

                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("BookAppointmentActivity", "Error updating appointment ID", e);
                                        //Toast.makeText(BookVaccineAppointmentActivity.this, "Error updating appointment ID", Toast.LENGTH_SHORT).show();
                                        showMessageDialog("Error booking an appointment. Please Try again.", null);
                                        hideProgressDialog();
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Log.e("BookAppointmentActivity", "Error booking appointment", e);
                            //Toast.makeText(BookVaccineAppointmentActivity.this, "Error booking appointment", Toast.LENGTH_SHORT).show();
                            showMessageDialog("Error booking an appointment. Please Try again.", null);
                            hideProgressDialog();

                        });
            } else {
                //Toast.makeText(BookVaccineAppointmentActivity.this, "Please complete all fields", Toast.LENGTH_SHORT).show();
                showMessageDialog("Please complete all fields", null);

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

    // TO SELECT TIME WITH VALIDATION OF CLINIC'S SCHEDSTARTTIME AND SCHEDENDTIME
    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Parse schedStartTime and schedEndTime
        int startMinutes = parseTimeToMinutes(schedStartTime);
        int endMinutes = parseTimeToMinutes(schedEndTime);

        // Adjust the minute to the nearest 00 or 30
        minute = (minute < 15) ? 0 : (minute < 45) ? 30 : 0;
        if (minute == 0 && hour == 23) {
            hour = 0; // Wrap around to 0 when it's 23:00
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {
                    int selectedMinutes = selectedHour * 60 + selectedMinute; // Total minutes of the selected time
                    if (selectedMinutes < startMinutes || selectedMinutes > endMinutes) {
                        showMessageDialog("Please select a time between " + schedStartTime + " and " + schedEndTime, null);
                        //Toast.makeText(this, "Please select a time between " + schedStartTime + " and " + schedEndTime, Toast.LENGTH_SHORT).show();
                    } else {
                        String time = String.format("%02d:%02d %s",
                                selectedHour % 12 == 0 ? 12 : selectedHour % 12,
                                selectedMinute,
                                selectedHour < 12 ? "AM" : "PM");
                        etTime.setText(time); // Set the selected time to etTime
                    }
                },
                hour,
                minute,
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

    private int parseTimeToMinutes(String time) {
        String[] parts = time.split(" ");
        String[] hourMinute = parts[0].split(":");
        int hour = Integer.parseInt(hourMinute[0]);
        int minute = (hourMinute.length > 1) ? Integer.parseInt(hourMinute[1]) : 0;

        if (parts.length > 1 && parts[1].equalsIgnoreCase("PM") && hour < 12) {
            hour += 12; // Convert PM hour to 24-hour format
        } else if (parts.length > 1 && parts[1].equalsIgnoreCase("AM") && hour == 12) {
            hour = 0; // Convert 12 AM to 0 hour
        }

        return hour * 60 + minute; // Convert to total minutes
    }



    private boolean areFieldsValid() {
        firstName = editTextFirstName.getText().toString().trim();
        lastName = lastnameEt.getText().toString().trim();
        selectedService = "Vaccination"; // Set default
        selectedTime = etTime.getText().toString().trim();
        birthDay = editTextBirthday.getText().toString().trim();
        selectedSpinnerPosition = spinnerSex.getSelectedItemPosition();
        birthPlace = editTextbirthplace.getText().toString().trim();
        address = etStreet.getText().toString().trim() + ", " + etBrgy.getText().toString().trim() + ", " + etCity.getText().toString().trim() + ", " + etProvince.getText().toString().trim();

        if (selectedSpinnerPosition == 1) {
            selectedGender = "Male";
        } else if (selectedSpinnerPosition == 2) {
            selectedGender = "Female";
        }




        // Check for empty fields
        boolean isValid = true;
        if (firstName.isEmpty()) {
            editTextFirstName.setError("First name is required");
            isValid = false;
        }
        if (lastName.isEmpty()) {
            lastnameEt.setError("Last name is required");
            isValid = false;
        }
        if (selectedTime.isEmpty()) {
            etTime.setError("Time is required");
            isValid = false;
        }
        if (birthDay.isEmpty()) {
            editTextBirthday.setError("Birth date is required");
            isValid = false;
        }
        if (birthPlace.isEmpty()) {
            editTextbirthplace.setError("Birth place is required");
            isValid = false;
        }
        if (address.isEmpty()) {
            etStreet.setError("Street is required");
            etBrgy.setError("Baranggay is required");
            etCity.setError("City/Municipality is required");
            etProvince.setError("Province is required");
            isValid = false;
        }

        if (selectedSpinnerPosition == 0){
            etSex.setError("Sex is required");
            isValid = false;
        }
        if (doctorId == null) {
            // You might need to set an error here if doctorId is from a UI element
            // For example, if it's a Spinner, check if it has a selected item.
            // set an error on the doctor selection UI if applicable
            isValid = false;
        }

        return isValid;
    }


    private AppointmentRequest createAppointmentRequest() {

        return new AppointmentRequest(
                null,
                firstName,
                lastName,
                firstName + " " + lastName,
                birthDay,
                selectedGender,
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

    public void showBirthdayDatePicker(View view) {
        // Get the current date
        Calendar calendar = Calendar.getInstance();


        // Create a DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        // Update the TextInputEditText with the selected date
                        String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        editTextBirthday.setText(date);
                        editTextBirthday.setError(null);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Set the maximum date to today
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());


        datePickerDialog.show();
    }

    private void showProgressDialog(Context context) {
        progressDialog = new Dialog(context);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCancelable(false); // Disable dismissing the dialog by tapping outside

        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void showMessageDialog(String message, Runnable onOkPressed) {
        // Create a new AlertDialog Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set the message
        builder.setMessage(message);

        // Set the "OK" button
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
            // Call the provided Runnable
            if (onOkPressed != null) {
                onOkPressed.run();
            }
        });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
