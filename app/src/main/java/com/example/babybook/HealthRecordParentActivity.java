package com.example.babybook;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.babybook.adapter.HealthRecordAdapter;
import com.example.babybook.adapter.HealthRecordParentAdapter;
import com.example.babybook.model.HealthRecord;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HealthRecordParentActivity extends AppCompatActivity {

    private RecyclerView recyclerViewHealthRecords;
    private HealthRecordParentAdapter adapter;
    private List<HealthRecord> healthRecords;
    private FirebaseFirestore db;
    private CollectionReference healthRecordsCollection;
    private FirebaseAuth mAuth; // Add FirebaseAuth instance
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvNoHealthRec;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_record);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("List of Children");//parent side

        recyclerViewHealthRecords = findViewById(R.id.recyclerViewHealthRecords);
        healthRecords = new ArrayList<>();
        adapter = new HealthRecordParentAdapter(this, healthRecords); // Pass context and list
        recyclerViewHealthRecords.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewHealthRecords.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        healthRecordsCollection = db.collection("healthRecords");
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this::loadHealthRecords);
        tvNoHealthRec = findViewById(R.id.tvNoHealthRecParent);


        loadHealthRecords();

     /*  findViewById(R.id.fabAddRecord).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddRecordDialog();
            }
        });*/
    }

    private void loadHealthRecords() {
        tvNoHealthRec.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(true); // Start refreshing animation
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String currentUserId = user.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference healthRecordsCollection = db.collection("healthRecords");

            healthRecordsCollection.whereEqualTo("addedBy", currentUserId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                healthRecords.clear();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    HealthRecord healthRecord = document.toObject(HealthRecord.class);
                                    healthRecords.add(healthRecord);
                                }
                                adapter.notifyDataSetChanged();

                                // Check if there are any health records
                                if (healthRecords.isEmpty()) {
                                    tvNoHealthRec.setVisibility(View.VISIBLE); // Show the message
                                } else {
                                    tvNoHealthRec.setVisibility(View.GONE); // Hide the message
                                }
                            } else {
                                Log.e("HealthRecordActivity", "Error loading records: ", task.getException());
                                Toast.makeText(HealthRecordParentActivity.this, "Failed to load records: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                swipeRefreshLayout.setRefreshing(false);
                            }
                            swipeRefreshLayout.setRefreshing(false); // Stop refreshing animation
                        }
                    });
        } else {
            Log.e("HealthRecordActivity", "User is not authenticated.");
            Toast.makeText(this, "User is not authenticated", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false); // Stop refreshing animation
        }
    }

  /*  private void showAddRecordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_child, null);
        builder.setView(dialogView);

        EditText editTextChildName = dialogView.findViewById(R.id.editTextChildName);
        Button buttonAddChild = dialogView.findViewById(R.id.buttonAddChild);

        builder.setTitle("Add New Child");
        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        buttonAddChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String childName = editTextChildName.getText().toString().trim();

                if (childName.isEmpty()) {
                    Toast.makeText(HealthRecordParentActivity.this, "Please fill the child's name", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Get the current user's ID from Firebase Auth
                String addedBy = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "Unknown";

                HealthRecord newRecord = new HealthRecord(childName, addedBy);
                addRecordToDatabase(newRecord);
                dialog.dismiss();
            }
        });
    }
*/

    private void addRecordToDatabase(HealthRecord healthRecord) {
        healthRecordsCollection.add(healthRecord)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            // Update with document ID
                            String documentId = task.getResult().getId();
                            healthRecord.setId(documentId);

                            healthRecordsCollection.document(documentId).set(healthRecord)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(HealthRecordParentActivity.this, "Record added", Toast.LENGTH_SHORT).show();
                                                loadHealthRecords(); // Reload records to display new one
                                            } else {
                                                Toast.makeText(HealthRecordParentActivity.this, "Failed to add record", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(HealthRecordParentActivity.this, "Failed to add record", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Close this activity and return to the previous one
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
