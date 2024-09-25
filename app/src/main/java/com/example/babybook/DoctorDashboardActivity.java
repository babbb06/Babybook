package com.example.babybook;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.babybook.adapter.PostAdapter;
import com.example.babybook.model.Post;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class DoctorDashboardActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_dashboard);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(DoctorDashboardActivity.this, LoginActivity.class));
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.doctor_toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.doctor_drawer_layout);
        navigationView = findViewById(R.id.doctor_nav_view);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(false);

// Set a custom drawable for the hamburger icon with your desired color
        toggle.setHomeAsUpIndicator(R.drawable.hamburger); // Use your own drawable
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START); // Open the drawer when icon is clicked
            }
        });

        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.doctor_nav_home:
                        break;
                    case R.id.doctor_nav_health:
                        startActivity(new Intent(DoctorDashboardActivity.this, HealthRecordActivity.class));
                        break;
                    case R.id.doctor_nav_chat:
                        startActivity(new Intent(DoctorDashboardActivity.this, SearchParentChatActivity.class));
                        break;
                    case R.id.doctor_nav_appointment:
                        startActivity(new Intent(DoctorDashboardActivity.this, ManageAppointmentsActivity.class));
                        break;
                        //new added layout
                    case R.id.doctor_nav_profile:
                        startActivity(new Intent(DoctorDashboardActivity.this, UpdateProfiledDoctor.class));
                        break;

                    case R.id.doctor_nav_logout:
                        showLogoutConfirmation();
                        break;
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });

        if (currentUser != null) {
            String userId = currentUser.getUid();
            checkUserTypeAndFetchFullName(userId);
        }

        recyclerView = findViewById(R.id.newsfeedRecyclerView);
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(postAdapter);

        FloatingActionButton fabCreatePost = findViewById(R.id.fabCreatePost);
        fabCreatePost.setOnClickListener(v -> showCreatePostDialog());

        loadPosts();
    }

    private void checkUserTypeAndFetchFullName(String userId) {
        db.collection("doctorUsers").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String fullName = document.getString("fullName");
                    String userProfileImageUrl = document.getString("profileImageUrl");
                    String specialization = document.getString("specialization");

                    // Pass both the fullName and image URL to updateNavHeader
                    updateNavHeader("Greetings, " + fullName + "!", userProfileImageUrl, specialization);
                } else {
                    Toast.makeText(DoctorDashboardActivity.this, "Doctor not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(DoctorDashboardActivity.this, "Error fetching doctor data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateNavHeader(String fullName, String userProfileImageUrl, String specialization) {
        View headerView = navigationView.getHeaderView(0);
        TextView fullNameTextView = headerView.findViewById(R.id.doctor_full_name);
        TextView doctorRole = headerView.findViewById(R.id.doctor_role);
        ImageView doctorImg = headerView.findViewById(R.id.doctor_header_imageView);

        fullNameTextView.setText(fullName);
        doctorRole.setText(specialization);

        // Load the image using Glide
        Glide.with(this)
                .load(userProfileImageUrl)
                .into(doctorImg);
    }


    private void showCreatePostDialog() {
        // Inflate the custom dialog layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_create_post, null);

        // Get the EditText from the custom layout
        final EditText input = dialogView.findViewById(R.id.input_post);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create New Post");

        // Set the custom view in the dialog
        builder.setView(dialogView);

        builder.setPositiveButton("Post", (dialog, which) -> {
            String content = input.getText().toString();
            if (!content.isEmpty()) {
                createPost(content);
            } else {
                Toast.makeText(DoctorDashboardActivity.this, "Post content cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {
            dialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        });

        dialog.show();
    }


    private void createPost(String content) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            db.collection("doctorUsers").document(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String doctorName = document.getString("fullName");
                        String doctorProfile = document.getString("profileImageUrl");
                        String specialization = document.getString("specialization");

                        // Ensure name and profile are not null
                        if (doctorName != null && doctorProfile != null) {
                            Post post = new Post(null, userId, doctorName, content, System.currentTimeMillis(), doctorProfile, specialization);

                            // Add the post to Firestore
                            db.collection("posts").add(post).addOnSuccessListener(documentReference -> {
                                // Get the auto-generated postId and update the post document
                                String postId = documentReference.getId();
                                documentReference.update("postId", postId)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(DoctorDashboardActivity.this, "Post created", Toast.LENGTH_SHORT).show();
                                            loadPosts(); // Reload posts after successful creation
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(DoctorDashboardActivity.this, "Error updating postId", Toast.LENGTH_SHORT).show();
                                        });
                            }).addOnFailureListener(e -> {
                                Toast.makeText(DoctorDashboardActivity.this, "Error creating post", Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            Toast.makeText(DoctorDashboardActivity.this, "Doctor profile data missing", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }


    private void loadPosts() {
        db.collection("posts")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        postList.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            Post post = document.toObject(Post.class);
                            post.setPostId(document.getId());
                            postList.add(post);
                        }
                        postAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(DoctorDashboardActivity.this, "Error loading posts", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> logoutUser())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void logoutUser() {
        mAuth.signOut();
        startActivity(new Intent(DoctorDashboardActivity.this, LoginActivity.class));
        finish();
    }
}
