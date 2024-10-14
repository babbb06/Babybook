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
        adapter = new ParentAdapter(parents, parent -> {
            Intent intent = new Intent(SearchParentChatActivity.this, ChatActivity.class);
            intent.putExtra("receiverId", parent.getParentID());
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        searchButton.setOnClickListener(v -> {
            String query = searchEditText.getText().toString();
            if (!TextUtils.isEmpty(query)) {
                searchParents(query);
            } else {
                Toast.makeText(SearchParentChatActivity.this, "Please enter a parent name to search.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchParents(String query) {
        String lowerCaseQuery = query.trim().toLowerCase();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("parentUsers")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        parents.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Parent parent = document.toObject(Parent.class);
                            if (parent != null && parent.getFullName() != null &&
                                    parent.getFullName().toLowerCase().contains(lowerCaseQuery)) {
                                parent.setParentID(document.getId());
                                parents.add(parent);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(SearchParentChatActivity.this, "Error getting parents.", Toast.LENGTH_SHORT).show();
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
