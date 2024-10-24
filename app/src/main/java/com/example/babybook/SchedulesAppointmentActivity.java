package com.example.babybook;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.babybook.adapter.AppointmentAdapter;
import com.example.babybook.model.AppointmentRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SchedulesAppointmentActivity extends AppCompatActivity {

    private static final int REQUEST_NOTIFICATION_PERMISSION = 1;
    private static final String TAG = "SchedulesAppointmentActivity";

    private ListView appointmentsListView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private List<AppointmentRequest> appointmentList;
    private ArrayAdapter<AppointmentRequest> adapter;
    private TextView tvNoRequestAppointments;
    private SwipeRefreshLayout swipeRefreshLayout;


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
            getSupportActionBar().setTitle("Appointment");
        }

        tvNoRequestAppointments = findViewById(R.id.tvNoRequestsParent);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        appointmentsListView = findViewById(R.id.appointments_list_view);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        appointmentList = new ArrayList<>();

        // Use the custom adapter
        adapter = new AppointmentAdapter(this, appointmentList);
        appointmentsListView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this::fetchAppointments);
        checkNotificationPermission();
    }

    private void fetchAppointments() {
        swipeRefreshLayout.setRefreshing(true); // Start refreshing animation
        if (mAuth.getCurrentUser() == null) {
            Log.e(TAG, "User not signed in. Notifications will not be scheduled.");
            swipeRefreshLayout.setRefreshing(false); // Stop refreshing
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
                            swipeRefreshLayout.setRefreshing(false); // Stop refreshing
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

                                            Log.d(TAG, "Fetched Appointment: " + appointmentRequest);

                                            appointmentList.add(appointmentRequest);
                                        }

                                        Collections.sort(appointmentList, new Comparator<AppointmentRequest>() {
                                            @Override
                                            public int compare(AppointmentRequest a1, AppointmentRequest a2) {
                                                return a2.getBookingTime().compareTo(a1.getBookingTime()); // Descending order
                                            }
                                        });

                                        adapter.notifyDataSetChanged();

                                        if (appointmentList.isEmpty()) {
                                            tvNoRequestAppointments.setVisibility(View.VISIBLE);
                                        }
                                        else{
                                            tvNoRequestAppointments.setVisibility(View.GONE);
                                        }
                                        Log.d(TAG, "Appointments updated successfully");

                                        for (AppointmentRequest appointment : appointmentList) {
                                            if ("Accepted".equals(appointment.getStatus())) {
                                                // Prepare date and time for reminder
                                                String appointmentDate = appointment.getDate(); // Assuming this is in "yyyy-MM-dd" format
                                                String appointmentTime = appointment.getTime(); // Assuming this is in "HH:mm" format

                                                // Combine date and time for proper parsing
                                                String dateTime = appointmentDate + " " + appointmentTime;

                                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                                                try {
                                                    Date date = sdf.parse(dateTime);
                                                    long reminderTimeInMillis = date.getTime() - 24 * 60 * 60 * 1000; // Subtract 1 day

                                                    // Call the scheduleReminder method with correct parameters
                                                    ScheduleReminderUtil.scheduleReminder(this, appointmentDate, appointmentTime, fullName);
                                                } catch (ParseException e) {
                                                    Log.e(TAG, "Error parsing date and time.", e);
                                                }
                                            }
                                        }

                                    } else {
                                        Log.e(TAG, "Error getting documents.", task.getException());
                                        Toast.makeText(SchedulesAppointmentActivity.this, "Error getting appointments.", Toast.LENGTH_SHORT).show();
                                    }
                                    swipeRefreshLayout.setRefreshing(false); // Stop refreshing
                                });
                    } else {
                        Log.e(TAG, "Parent document not found.");
                        Toast.makeText(SchedulesAppointmentActivity.this, "Error fetching parent data.", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false); // Stop refreshing
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching parent data.", e);
                    Toast.makeText(SchedulesAppointmentActivity.this, "Error fetching parent data.", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false); // Stop refreshing
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
