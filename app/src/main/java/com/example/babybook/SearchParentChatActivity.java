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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchParentChatActivity extends AppCompatActivity {

    private EditText searchEditText;
    private Button searchButton;
    private RecyclerView recyclerView;
    private ParentAdapter adapter;
    private List<Parent> parents;
    private List<Parent> allParents; // To store all parents

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
        allParents = new ArrayList<>(); // Initialize the list for all parents
        adapter = new ParentAdapter(parents, parent -> {
            Intent intent = new Intent(SearchParentChatActivity.this, ChatActivity.class);
            intent.putExtra("receiverId", parent.getParentID());
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Load parents based on chat activity initially
        loadParentsBasedOnChats();

        // Set up search button click listener
        searchButton.setOnClickListener(v -> {
            String query = searchEditText.getText().toString();
            if (!TextUtils.isEmpty(query)) {
                filterParents(query);
            } else {
                // If the search field is empty, show all parents
                parents.clear();
                parents.addAll(allParents);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void loadAllParents() {
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
                        // Initially display all parents
                        parents.clear();
                        parents.addAll(allParents);
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(SearchParentChatActivity.this, "Error getting parents.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadParentsBasedOnChats() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Use a Set to avoid duplicates
        Set<String> parentIds = new HashSet<>();

        // Get chats where the user is either the sender or the receiver
        db.collection("chats")
                .whereEqualTo("senderId", currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String parentId = document.getString("receiverId");
                            if (parentId != null) {
                                parentIds.add(parentId);
                            }
                        }

                        // Get chats where the user is the receiver
                        db.collection("chats")
                                .whereEqualTo("receiverId", currentUserId)
                                .get()
                                .addOnCompleteListener(receiverTask -> {
                                    if (receiverTask.isSuccessful()) {
                                        for (DocumentSnapshot document : receiverTask.getResult()) {
                                            String parentId = document.getString("senderId");
                                            if (parentId != null) {
                                                parentIds.add(parentId);
                                            }
                                        }

                                        // Fetch parent details based on the collected parent IDs
                                        if (!parentIds.isEmpty()) {
                                            fetchParentsByIds(new ArrayList<>(parentIds));
                                        } else {
                                            // If no chats found, load all parents
                                            loadAllParents();
                                        }
                                    } else {
                                        Toast.makeText(SearchParentChatActivity.this, "Error loading receiver chats.", Toast.LENGTH_SHORT).show();
                                        // If there's an error, attempt to load all parents
                                        loadAllParents();
                                    }
                                });
                    } else {
                        Toast.makeText(SearchParentChatActivity.this, "Error loading sender chats.", Toast.LENGTH_SHORT).show();
                        // If there's an error, attempt to load all parents
                        loadAllParents();
                    }
                });
    }

    private void fetchParentsByIds(List<String> parentIds) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        parents.clear();

        db.collection("parentUsers")
                .whereIn("ParentID", parentIds)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Parent parent = document.toObject(Parent.class);
                            if (parent != null) {
                                parent.setParentID(document.getId());
                                parents.add(parent);
                            }
                        }
                        adapter.notifyDataSetChanged();

                        // Show or hide the no parent message
                        if (parents.isEmpty()) {
                            Toast.makeText(SearchParentChatActivity.this, "No parents found in chats.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SearchParentChatActivity.this, "Error getting parents.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void filterParents(String query) {
        String lowerCaseQuery = query.trim().toLowerCase();

        parents.clear();
        for (Parent parent : allParents) {
            if (parent.getFullName() != null && parent.getFullName().toLowerCase().contains(lowerCaseQuery)) {
                parents.add(parent);
            }
        }
        adapter.notifyDataSetChanged();

        if (parents.isEmpty()) {
            Toast.makeText(SearchParentChatActivity.this, "No parents found.", Toast.LENGTH_SHORT).show();
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
