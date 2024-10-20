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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListOfMedicalRecord extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MedicalRecordAdapter adapter;
    private List<HealthChecklist> healthChecklists;
    private FloatingActionButton fabCreatePost;
    private String childId,FirstName,LastName,dateToday; // Store the child ID


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
        getSupportActionBar().setTitle("Medical Record");// DOCTOR SIDE


        recyclerView = findViewById(R.id.listOfMedicalRecordRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch health records from Firestore and populate the list
        healthChecklists = new ArrayList<>();
        fetchHealthRecords();

        Toast.makeText(this, "Child ID is " + childId, Toast.LENGTH_SHORT).show();
        // Set onClickListener for fabCreatePost
        fabCreatePost.setOnClickListener(v -> {
            // Create an Intent to navigate to AddMedicalRecord Activity
            Intent intent = new Intent(ListOfMedicalRecord.this, AddMedicalRecord.class);
            intent.putExtra("childId", childId); // Pass the child ID if needed
            intent.putExtra("FirstName", FirstName);
            intent.putExtra("LastName", LastName);

            startActivity(intent);
        });


    }

    private void fetchHealthRecords() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("medicalRecords")
                .whereEqualTo("childId", childId) // Filter by childId
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    healthChecklists.clear(); // Clear the list to avoid duplication
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        HealthChecklist checklist = document.toObject(HealthChecklist.class);
                        String dateToday = document.getString("dateToday"); // Get the dateToday field
                        checklist.setDateToday(dateToday); // Set the dateToday in the checklist object
                        healthChecklists.add(checklist);
                    }
                    adapter = new MedicalRecordAdapter(healthChecklists, childId, FirstName, LastName, dateToday, ListOfMedicalRecord.this);

                    recyclerView.setAdapter(adapter);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ListOfMedicalRecord.this, "Failed to load records: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}







