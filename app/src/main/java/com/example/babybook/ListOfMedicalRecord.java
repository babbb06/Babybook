package com.example.babybook;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.babybook.adapter.MedicalRecordAdapter;
import com.example.babybook.model.HealthChecklist;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListOfMedicalRecord extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MedicalRecordAdapter adapter;
    private List<HealthChecklist> healthChecklists; // Corrected this line
    private FirebaseFirestore db;
    private CollectionReference medicalRecordsCollection;
    private FloatingActionButton fabCreatePost;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String childId, FirstName, LastName, dateToday, Sex, Address, Birthday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_medical_record);

        // Initialize FloatingActionButton
        fabCreatePost = findViewById(R.id.fabCreatePost);

        // Retrieve data from the intent
        childId = getIntent().getStringExtra("childId");
        FirstName = getIntent().getStringExtra("FirstName");
        LastName = getIntent().getStringExtra("LastName");
        Sex = getIntent().getStringExtra("Sex");
        Address = getIntent().getStringExtra("Address");
        Birthday = getIntent().getStringExtra("Birthday");

        Toolbar toolbar = findViewById(R.id.listToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        getSupportActionBar().setTitle("List of Medical Records");// DOCTOR SIDe

        recyclerView = findViewById(R.id.listOfMedicalRecordRecyclerView);
        healthChecklists = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MedicalRecordAdapter(healthChecklists, this);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        medicalRecordsCollection = db.collection("medicalRecords");
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(this::fetchMedicalRecords);

        fetchMedicalRecords();
        Toast.makeText(ListOfMedicalRecord.this, childId, Toast.LENGTH_SHORT).show();
        // Set onClickListener for fabCreatePost
        fabCreatePost.setOnClickListener(v -> {
            Intent intent = new Intent(ListOfMedicalRecord.this, AddMedicalRecord.class);
            intent.putExtra("childId", childId);
            intent.putExtra("FirstName", FirstName);
            intent.putExtra("LastName", LastName);
            intent.putExtra("Sex", Sex);
            intent.putExtra("Address", Address);
            intent.putExtra("Birthday", Birthday);
            startActivity(intent);
        });
    }
    private void fetchMedicalRecords() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Toast.makeText(ListOfMedicalRecord.this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentUserId = user.getUid();

        if (childId == null || childId.isEmpty()) {
            Toast.makeText(ListOfMedicalRecord.this, "Child ID is not set", Toast.LENGTH_SHORT).show();
            return;
        }

        swipeRefreshLayout.setRefreshing(true);

        // Fetch all medical records for the child
        db.collection("healthRecords")
                .document(childId)  // Use the specific child's document
                .collection("medicalRecords")  // Access the 'medicalRecords' subcollection
                .get()  // Fetch all documents at once
                .addOnCompleteListener(task -> {
                    swipeRefreshLayout.setRefreshing(false);

                    if (task.isSuccessful()) {
                        QuerySnapshot queryDocumentSnapshots = task.getResult();

                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            // Clear previous records to avoid duplication
                            healthChecklists.clear();

                            // Iterate through documents in the query result
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                HealthChecklist checklist = document.toObject(HealthChecklist.class);

                                // Ensure the checklist object has the correct data
                                checklist.setDate(document.getString("dateToday"));

                                // Add the checklist to the list
                                healthChecklists.add(checklist);
                            }

                            // Notify adapter of the new data
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(ListOfMedicalRecord.this, "No records found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Exception e = task.getException();
                        Log.e("ListOfMedicalRecord", "Error loading records: ", e);
                        Toast.makeText(ListOfMedicalRecord.this, "Failed to load records: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }




}
