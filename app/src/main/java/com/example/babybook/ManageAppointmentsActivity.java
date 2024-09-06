package com.example.babybook;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babybook.adapter.AppointmentRequestAdapter;
import com.example.babybook.model.AppointmentRequest;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_appointments);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        ImageView checkImage = findViewById(R.id.check_image);
        ImageView crossImage = findViewById(R.id.cross_image);

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
                updateAppointmentStatus(request.getId(), "Accepted");
            }

            @Override
            public void onDeclineClick(AppointmentRequest request) {
                updateAppointmentStatus(request.getId(), "Declined");
            }
        }, true); // Pass true to show buttons
        recyclerView.setAdapter(adapter);

        recyclerView.addItemDecoration(new DividerItemDecoration(this));

        doctorId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        loadAppointments();
    }

    private void loadAppointments() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("appointments")
                .whereEqualTo("doctorId", doctorId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        appointmentRequests.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            AppointmentRequest request = document.toObject(AppointmentRequest.class);
                            request.setId(document.getId());
                            appointmentRequests.add(request);
                        }
                        Collections.sort(appointmentRequests, (a1, a2) -> a2.getBookingTime().compareTo(a1.getBookingTime()));
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.w("ManageAppointments", "Error getting documents.", task.getException());
                    }
                });
    }

    private void updateAppointmentStatus(String id, String status) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("appointments").document(id)
                .update("status", status)
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
