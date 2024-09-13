package com.example.babybook;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.babybook.model.AppointmentRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SchedulesAppointmentActivity extends AppCompatActivity {

    private static final int REQUEST_NOTIFICATION_PERMISSION = 1;
    private static final String TAG = "SchedulesAppointmentActivity";

    private ListView appointmentsListView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private List<AppointmentRequest> appointmentList;
    private ArrayAdapter<AppointmentRequest> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedules_appointment);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enable the up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Schedule");
        }

        appointmentsListView = findViewById(R.id.appointments_list_view);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        appointmentList = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, appointmentList);
        appointmentsListView.setAdapter(adapter);

        checkNotificationPermission();
    }

    private void fetchAppointments() {
        if (mAuth.getCurrentUser() == null) {
            Log.e(TAG, "User not signed in. Notifications will not be scheduled.");
            return; // Exit early if the user is not signed in
        }

        String userId = mAuth.getCurrentUser().getUid();
        Log.d(TAG, "Fetching appointments for user ID: " + userId);

        db.collection("parentUsers").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String fullName = documentSnapshot.getString("fullName");

                        if (fullName == null) {
                            Log.e(TAG, "Parent full name is missing.");
                            return;
                        }

                        db.collection("appointments")
                                .whereEqualTo("userId", userId)
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        appointmentList.clear();
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            AppointmentRequest appointmentRequest = document.toObject(AppointmentRequest.class);
                                            appointmentRequest.setId(document.getId());

                                            Log.d(TAG, "Fetched Appointment: " + appointmentRequest.toString());

                                            appointmentList.add(appointmentRequest);
                                        }

                                        Collections.sort(appointmentList, new Comparator<AppointmentRequest>() {
                                            @Override
                                            public int compare(AppointmentRequest a1, AppointmentRequest a2) {
                                                return a2.getBookingTime().compareTo(a1.getBookingTime()); // Descending order
                                            }
                                        });

                                        adapter.notifyDataSetChanged();
                                        Log.d(TAG, "Appointments updated successfully");

                                        for (AppointmentRequest appointment : appointmentList) {
                                            if ("Accepted".equals(appointment.getStatus())) {
                                                ScheduleReminderUtil.scheduleReminder(this, appointment.getDate(), appointment.getTime(), fullName);
                                            }
                                        }

                                    } else {
                                        Log.e(TAG, "Error getting documents.", task.getException());
                                        Toast.makeText(SchedulesAppointmentActivity.this, "Error getting appointments.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Log.e(TAG, "Parent document not found.");
                        Toast.makeText(SchedulesAppointmentActivity.this, "Error fetching parent data.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching parent data.", e);
                    Toast.makeText(SchedulesAppointmentActivity.this, "Error fetching parent data.", Toast.LENGTH_SHORT).show();
                });
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATION_PERMISSION);
            } else {
                fetchAppointments();
            }
        } else {
            fetchAppointments();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchAppointments();
            } else {
                Toast.makeText(this, "Notification permission is required for reminders.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Close this activity and return to the previous one
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
