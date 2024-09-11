package com.example.babybook;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babybook.model.Doctor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchDoctorChatActivity extends AppCompatActivity {

    private EditText searchEditText;
    private Button searchButton;
    private RecyclerView recyclerView;
    private DoctorAdapter2 adapter;
    private List<Doctor> doctors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_doctor_chat);

        // Set up Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Message");
        }

        searchEditText = findViewById(R.id.editTextSpecialization);
        searchButton = findViewById(R.id.buttonSearch);
        recyclerView = findViewById(R.id.recyclerViewDoctors);

        doctors = new ArrayList<>();
        adapter = new DoctorAdapter2(doctors, doctor -> {
            // Handle message icon click
            Intent intent = new Intent(SearchDoctorChatActivity.this, ChatActivity.class);
            String parentId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get the current parent ID
            intent.putExtra("senderId", parentId);
            intent.putExtra("receiverId", doctor.getId());
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        searchButton.setOnClickListener(v -> {
            String query = searchEditText.getText().toString();
            if (!TextUtils.isEmpty(query)) {
                searchDoctors(query);
            } else {
                Toast.makeText(SearchDoctorChatActivity.this, "Please enter a specialization to search.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchDoctors(String query) {
        String lowerCaseQuery = query.trim().toLowerCase();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("doctorUsers")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        doctors.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Doctor doctor = document.toObject(Doctor.class);
                            if (doctor != null && doctor.getSpecialization() != null &&
                                    doctor.getSpecialization().toLowerCase().startsWith(lowerCaseQuery)) {
                                doctor.setId(document.getId());
                                doctors.add(doctor);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(SearchDoctorChatActivity.this, "Error getting doctors.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
