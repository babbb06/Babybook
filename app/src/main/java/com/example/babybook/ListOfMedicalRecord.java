package com.example.babybook;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babybook.adapter.MedicalRecordAdapter;
import com.example.babybook.model.HealthChecklist;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListOfMedicalRecord extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MedicalRecordAdapter adapter;
    private List<HealthChecklist> healthChecklists;
    private FloatingActionButton fabCreatePost;
    private String childId, FirstName, LastName,dateToday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_medical_record);

        // Initialize FloatingActionButton
        fabCreatePost = findViewById(R.id.fabCreatePost);
        // Retrieve child ID from the intent
        childId = getIntent().getStringExtra("CHILD_ID");
        FirstName = getIntent().getStringExtra("FirstName");
        LastName = getIntent().getStringExtra("LastName");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        getSupportActionBar().setTitle("Medical Record");

        recyclerView = findViewById(R.id.listOfMedicalRecordRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the healthChecklists list
        healthChecklists = new ArrayList<>();
        fetchHealthRecords();

        // Set onClickListener for fabCreatePost
        fabCreatePost.setOnClickListener(v -> {
            // Create an Intent to navigate to AddMedicalRecord Activity
            Intent intent = new Intent(ListOfMedicalRecord.this, AddMedicalRecord.class);
            intent.putExtra("childId", childId);
            intent.putExtra("FirstName", FirstName);
            intent.putExtra("LastName", LastName);

            startActivity(intent);
        });
    }

    // In your activity where you display the list of medical records

    private void fetchHealthRecords() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Set up a real-time listener
        db.collection("medicalRecords")
                .whereEqualTo("childId", childId) // Filter by childId
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(ListOfMedicalRecord.this, "Failed to load records: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (queryDocumentSnapshots != null) {
                        healthChecklists.clear(); // Clear the list to avoid duplication
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            HealthChecklist checklist = document.toObject(HealthChecklist.class);
                            String dateToday = document.getString("dateToday"); // Get the dateToday field
                            checklist.setDateToday(dateToday); // Set the dateToday in the checklist object
                            healthChecklists.add(checklist);
                        }

                        // Set up the adapter
                        adapter = new MedicalRecordAdapter(healthChecklists, childId, FirstName, LastName, dateToday, ListOfMedicalRecord.this);
                        recyclerView.setAdapter(adapter);

                        // Check if there is already one medical record
                        if (healthChecklists.size() == 1) {
                            // Set click listener to show a message instead of starting a new activity
                            fabCreatePost.setOnClickListener(v -> {
                                Toast.makeText(ListOfMedicalRecord.this, "One time medical records every appointment", Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            // Set up the regular click listener for fabCreatePost to add a new record
                            fabCreatePost.setOnClickListener(v -> {
                                Intent intent = new Intent(ListOfMedicalRecord.this, AddMedicalRecord.class);
                                intent.putExtra("childId", childId);
                                intent.putExtra("FirstName", FirstName);
                                intent.putExtra("LastName", LastName);
                                startActivity(intent);
                            });
                        }
                    }
                });
    }







}
