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

import com.example.babybook.adapter.ParentAdapter;
import com.example.babybook.model.Parent;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class SearchParentChatActivity extends AppCompatActivity {

    private EditText searchEditText;
    private Button searchButton;
    private RecyclerView recyclerView;
    private ParentAdapter adapter;
    private List<Parent> parents;
    private List<Parent> allParents; // To store all doctors

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_parent_chat);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Message");
        }

        searchEditText = findViewById(R.id.editTextSearchParent);
        searchButton = findViewById(R.id.buttonSearchParent);
        recyclerView = findViewById(R.id.recyclerViewParents);

        parents = new ArrayList<>();
        allParents = new ArrayList<>(); // Initialize the list for all doctors
        adapter = new ParentAdapter(parents, parent -> {
            Intent intent = new Intent(SearchParentChatActivity.this, ChatActivity.class);
            intent.putExtra("receiverId", parent.getParentID());
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Load all doctors when the activity is created
        loadAllDoctors();

        // Set up search button click listener
        searchButton.setOnClickListener(v -> {
            String query = searchEditText.getText().toString();
            if (!TextUtils.isEmpty(query)) {
                filterDoctors(query);
            } else {
                // If the search field is empty, show all doctors
                parents.clear();
                parents.addAll(allParents);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void loadAllDoctors() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("parentUsers")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        allParents.clear(); // Clear the list before adding data
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Parent parent = document.toObject(Parent.class);
                            if (parent != null) {
                                parent.setParentID(document.getId());
                                allParents.add(parent);
                            }
                        }
                        // Initially display all doctors
                        parents.clear();
                        parents.addAll(allParents);
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(SearchParentChatActivity.this, "Error getting parents.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void filterDoctors(String query) {
        String lowerCaseQuery = query.trim().toLowerCase();

        parents.clear();
        for (Parent parent : allParents) {
            if (parent.getFullName() != null && parent.getFullName().toLowerCase().contains(lowerCaseQuery)) {
                parents.add(parent);
            }
        }
        adapter.notifyDataSetChanged();

        if (parents.isEmpty()) {
            Toast.makeText(SearchParentChatActivity.this, "No doctors found.", Toast.LENGTH_SHORT).show();
        }
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
