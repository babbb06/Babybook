package com.example.babybook;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.babybook.adapter.AppointmentRequestAdapter;
import com.example.babybook.model.AppointmentRequest;
import com.example.babybook.model.HealthRecord;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ManageAppointmentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AppointmentRequestAdapter adapter;
    private List<AppointmentRequest> appointmentRequests;
    private String doctorId;
    private TextView tvNoRequestAppointments;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_appointments);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Appointment Requests");
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        Button checkImage = findViewById(R.id.check_image);
        Button crossImage = findViewById(R.id.cross_image);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        tvNoRequestAppointments = findViewById(R.id.tvNoRequests);

        checkImage.setOnClickListener(v -> {
            Intent intent = new Intent(ManageAppointmentsActivity.this, AcceptedRequestActivity.class);
            startActivity(intent);
        });

        crossImage.setOnClickListener(v -> {
            Intent intent = new Intent(ManageAppointmentsActivity.this, DeclinedRequestActivity.class);
            startActivity(intent);
        });

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        appointmentRequests = new ArrayList<>();
        adapter = new AppointmentRequestAdapter(appointmentRequests, new AppointmentRequestAdapter.OnItemClickListener() {
            @Override

            public void onAcceptClick(AppointmentRequest request) {
                // Show confirmation dialog for accepting
                new androidx.appcompat.app.AlertDialog.Builder(ManageAppointmentsActivity.this)
                        .setTitle("Confirm Acceptance")
                        .setMessage("Are you sure you want to accept the appointment for " + request.getFirstName() + "?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            createHealthRecords(
                                    request.getId(),
                                    "Accepted",
                                    request.getFirstName(),
                                    request.getLastName(),
                                    request.getSex(),
                                    request.getBirthDay(),
                                    request.getBirthPlace(),
                                    request.getAddress(),
                                    request.getService(),
                                    request.getDate(),
                                    request.getTime(),
                                    request.getUserId(),
                                    request.getDoctorId()
                            );
                        })
                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                        .create()
                        .show();
            }


            @Override
            public void onDeclineClick(AppointmentRequest request) {
                // List of reasons for declining the appointment
                String[] declineReasons = {
                        "\nNot available at this time – The doctor is unavailable during the requested time slot.",
                        "\nFully booked schedule – The doctor has no open slots available for that day.",
                        "\nPatient's condition requires a specialist - The patient's needs are better suited for a different medical expert.",
                        "\nUrgent personal matter - The doctor has an unexpected personal obligation.",
                        "\nConflict with another appointment - Another patient's appointment conflicts with the requested time.",
                        "\nIncomplete patient information - The provided information is insufficient for booking.",
                        "\nTime not suited for the specific service - The requested time is reserved for a different type of consultation or procedure.",
                        "\nClinic closure on that day - The clinic is closed for a holiday or maintenance.",
                        "\nInsufficient preparation time – The doctor needs more time to prepare for the requested procedure or consultation.",
                        "\nPatient's requested service not offered - The requested service is not provided by the doctor."

                };


                // Show the dialog with a scrollable list of reasons
                new androidx.appcompat.app.AlertDialog.Builder(ManageAppointmentsActivity.this)
                        .setTitle("Select Reason for Decline")
                        .setItems(declineReasons, (dialog, which) -> {
                            // Get the selected reason
                            String selectedReason = declineReasons[which];

                            // Show the confirmation dialog for declining
                            new androidx.appcompat.app.AlertDialog.Builder(ManageAppointmentsActivity.this)
                                    .setTitle("Confirm Decline")
                                    .setMessage("Are you sure you want to decline this appointment for the reason: \n" + selectedReason + "?")
                                    .setPositiveButton("Yes", (dialog1, which1) -> {
                                        // Update the appointment status and include the reason
                                        updateAppointmentStatus(request.getId(), "Declined", selectedReason.trim());
                                    })
                                    .setNegativeButton("No", (dialog12, which12) -> dialog12.dismiss())
                                    .create()
                                    .show();
                        })
                        .create()
                        .show();
            }
        }, true); // Pass true to show buttons
        recyclerView.setAdapter(adapter);

        recyclerView.addItemDecoration(new DividerItemDecoration(this));

        doctorId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        swipeRefreshLayout.setOnRefreshListener(this::loadAppointments);
        loadAppointments();
    }

    private void createHealthRecords(String id, String status, String firstName, String lastName, String sex, String birthDay, String birthPlace, String address, String service, String date, String time, String userId, String doctorId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query to check if a health record already exists
        db.collection("healthRecords")
                .whereEqualTo("firstName", firstName)
                .whereEqualTo("lastName", lastName)
                .whereEqualTo("birthDay", birthDay)
                .whereEqualTo("userId", userId)//PARENT ID
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Check if any existing record matches the criteria
                        if (task.getResult().isEmpty()) {
                            // No matching record found, create a new HealthRecord
                            HealthRecord healthRecord = new HealthRecord();
                            healthRecord.setFirstName(firstName);
                            healthRecord.setLastName(lastName);
                            healthRecord.setSex(sex);
                            healthRecord.setBirthDay(birthDay);
                            healthRecord.setBirthPlace(birthPlace);
                            healthRecord.setAddress(address);
                            healthRecord.setAddedBy(userId);
                            healthRecord.setId(id); // childId
                            healthRecord.setDate(date);
                            healthRecord.setDoctorId(doctorId);
                            healthRecord.setService(service);
                            healthRecord.setTime(time);
                            healthRecord.setStatus(status);
                            healthRecord.setUserId(userId);

                            // Save the new HealthRecord object to Firestore
                            db.collection("healthRecords").document(id)
                                    .set(healthRecord)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("ManageAppointments", "Health record successfully saved for " + firstName);

                                        loadAppointments(); // Reload appointments to reflect updated status
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w("ManageAppointments", "Error saving health record.", e);
                                        Toast.makeText(ManageAppointmentsActivity.this, "Failed to save health record", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            // Merging data logic (if needed)
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                HealthRecord existingRecord = document.toObject(HealthRecord.class);
                                // Here you can merge the existing data with new data if needed
                                // For example, update service and time if they are different
                                existingRecord.setDate(date);
                                existingRecord.setDoctorId(doctorId);

                                existingRecord.setStatus("Accepted");
                                existingRecord.setService(service);// Update as needed
                                existingRecord.setTime(time); // Update as needed

                                // Save the updated record back to Firestore
                                db.collection("healthRecords").document(document.getId())
                                        .set(existingRecord)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("ManageAppointments", "Health record updated for " + firstName);
                                            loadAppointments(); // Reload appointments to reflect updated status
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.w("ManageAppointments", "Error updating health record.", e);
                                            Toast.makeText(ManageAppointmentsActivity.this, "Failed to update health record", Toast.LENGTH_SHORT).show();
                                        });
                            }
                        }
                    } else {
                        Log.w("ManageAppointments", "Error checking existing health records.", task.getException());
                    }
                });

        // Also update the appointment status
        db.collection("appointments").document(id)
                .update("status", status,
                "toShowPopup", 1)
                .addOnSuccessListener(aVoid -> {
                    Log.d("ManageAppointments", "Appointment status updated to " + status.toLowerCase());
                })
                .addOnFailureListener(e -> {
                    Log.w("ManageAppointments", "Error updating status.", e);
                    Toast.makeText(ManageAppointmentsActivity.this, "Failed to update status", Toast.LENGTH_SHORT).show();
                });
    }


    private void loadAppointments() {
        swipeRefreshLayout.setRefreshing(true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("appointments")
                .whereEqualTo("doctorId", doctorId)
                .whereEqualTo("status", "Pending") // Only get appointments with "Pending" status
                .get()
                .addOnCompleteListener(task -> {
                    swipeRefreshLayout.setRefreshing(false);
                    if (task.isSuccessful()) {
                        appointmentRequests.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            AppointmentRequest request = document.toObject(AppointmentRequest.class);
                            request.setId(document.getId());
                            appointmentRequests.add(request);
                        }
                        // Sort the list by booking time (descending)
                        Collections.sort(appointmentRequests, (a1, a2) -> a2.getBookingTime().compareTo(a1.getBookingTime()));
                        adapter.notifyDataSetChanged();

                        if (appointmentRequests.isEmpty()) {
                            tvNoRequestAppointments.setVisibility(View.VISIBLE);
                        } else {
                            tvNoRequestAppointments.setVisibility(View.GONE);
                        }


                    } else {
                        Log.w("ManageAppointments", "Error getting documents.", task.getException());
                    }
                });
    }


    private void updateAppointmentStatus(String id, String status, String reason) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("appointments").document(id)
                .update("status", status,
                        "reason", reason)

                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ManageAppointmentsActivity.this, "Appointment " + status.toLowerCase(), Toast.LENGTH_SHORT).show();

                    // Reload appointments to reflect updated status
                    loadAppointments();
                })
                .addOnFailureListener(e -> {
                    Log.w("ManageAppointments", "Error updating status.", e);
                    Toast.makeText(ManageAppointmentsActivity.this, "Failed to update status", Toast.LENGTH_SHORT).show();
                });
    }
}
