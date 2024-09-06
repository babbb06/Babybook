package com.example.babybook;

import android.os.Bundle;
import android.util.Log;

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
import java.util.List;

public class DeclinedRequestActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AppointmentRequestAdapter adapter;
    private List<AppointmentRequest> appointmentRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_declined_request);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        appointmentRequests = new ArrayList<>();
        adapter = new AppointmentRequestAdapter(appointmentRequests, null, false); // Pass false to hide buttons
        recyclerView.setAdapter(adapter);

        // Load declined appointments
        loadAppointments("Declined");
    }


    private void loadAppointments(String status) {
        String doctorId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("appointments")
                .whereEqualTo("status", status)
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
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.w("DeclinedRequestActivity", "Error getting documents.", task.getException());
                    }
                });
    }
}
