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

        // Check if user is logged in
        if (user == null) {
            Toast.makeText(ListOfMedicalRecord.this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentUserId = user.getUid();

        // Check if childId is not null or empty
        if (childId == null || childId.isEmpty()) {
            Toast.makeText(ListOfMedicalRecord.this, "Child ID is not set", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show a loading indicator if using a refresh layout or similar
        swipeRefreshLayout.setRefreshing(true);

        // Listen for real-time updates
        db.collection("healthRecords")
                .document(childId) // Specify the document using childId
                .collection("medicalRecords") // Subcollection name
                .whereEqualTo("doctorId", currentUserId)
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    swipeRefreshLayout.setRefreshing(false);

                    if (error != null) {
                        Log.e("ListOfMedicalRecord", "Error loading records: ", error);
                        Toast.makeText(ListOfMedicalRecord.this, "Failed to load records: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (queryDocumentSnapshots != null) {
                        healthChecklists.clear(); // Clear the list to avoid duplication
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            HealthChecklist checklist = document.toObject(HealthChecklist.class);
                            String dateToday = document.getString("dateToday");
                            checklist.setDateToday(dateToday);
                            healthChecklists.add(checklist);
                        }

                        // Display a "No records found" message if list is empty
                        if (healthChecklists.isEmpty()) {
                            Toast.makeText(ListOfMedicalRecord.this, "No records found", Toast.LENGTH_SHORT).show();
                        }

                        adapter.notifyDataSetChanged(); // Notify adapter of data changes
                    }
                });
    }


}
