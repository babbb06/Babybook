package com.example.babybook;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.babybook.adapter.AppointmentRequestAdapter;
import com.example.babybook.model.AppointmentRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AcceptedRequestActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AppointmentRequestAdapter adapter;
    private List<AppointmentRequest> appointmentRequests;
    private TextView tvNoAcceptedRequests;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accepted_request);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        tvNoAcceptedRequests = findViewById(R.id.tvNoAcceptedRequests);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        appointmentRequests = new ArrayList<>();
        adapter = new AppointmentRequestAdapter(appointmentRequests, null, false); // Pass false to hide buttons
        recyclerView.setAdapter(adapter);


        swipeRefreshLayout.setOnRefreshListener(() -> loadAppointments("Accepted"));

        // Load accepted appointments initially
        loadAppointments("Accepted");

    }

    private void loadAppointments(String status) {
        swipeRefreshLayout.setRefreshing(true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String doctorId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get the current doctor's ID

        db.collection("appointments")
                .whereEqualTo("status", status)
                .whereEqualTo("doctorId", doctorId) // Ensure only the current doctor's accepted requests are loaded
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
                        adapter.notifyDataSetChanged();

                        if (appointmentRequests.isEmpty()) {
                            tvNoAcceptedRequests.setVisibility(View.VISIBLE);
                        } else {
                            tvNoAcceptedRequests.setVisibility(View.GONE);
                        }
                    } else {
                        Log.w("AcceptedRequestActivity", "Error getting documents.", task.getException());
                    }
                });
    }

}
