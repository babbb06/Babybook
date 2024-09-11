package com.example.babybook;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babybook.adapter.PostAdapter;
import com.example.babybook.model.Post;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ParentDashboardActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_parent_dashboard);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

// In the onCreate method of ParentDashboardActivity
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                switch (id) {
                    case R.id.nav_home:
                        Toast.makeText(ParentDashboardActivity.this, "Home clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_tracking:
                        // Navigate to GeofenceActivity
                        break; // Added break statement here
                    case R.id.nav_health:
                        // Navigate to HealthRecordActivity
                        Intent healthIntent = new Intent(ParentDashboardActivity.this, HealthRecordActivity.class);
                        startActivity(healthIntent);
                        break;
                    case R.id.nav_chat:
                        // Navigate to SearchDoctorChatActivity
                        Intent chatIntent = new Intent(ParentDashboardActivity.this, SearchDoctorChatActivity.class);
                        startActivity(chatIntent);
                        break;
                    case R.id.nav_doctor:
                        Intent doctorIntent = new Intent(ParentDashboardActivity.this, SearchDoctorActivity.class);
                        startActivity(doctorIntent);
                        break;
                    case R.id.doctor_nav_profile:
                        Intent profileIntent =new Intent(ParentDashboardActivity.this, UpdateProfile.class) ;
                        startActivity(profileIntent);
                        break;
                    case R.id.nav_schedules:
                        Intent schedulesIntent = new Intent(ParentDashboardActivity.this, SchedulesAppointmentActivity.class);
                        startActivity(schedulesIntent);
                        break;
                    case R.id.nav_logout:
                        showLogoutConfirmation();
                        break;
                }

                drawerLayout.closeDrawers();
                return true;
            }
        });

        // Initialize RecyclerView for posts
        recyclerView = findViewById(R.id.newsfeedRecyclerView);
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(postAdapter);

        // Load posts
        loadPosts();



        // Initialize navigation drawer header with user's full name if logged in
        if (currentUser != null) {
            String userId = currentUser.getUid();
            checkUserTypeAndFetchFullName(userId);
        }
    }

    private void checkUserTypeAndFetchFullName(String userId) {
        // Check if user is in doctorUsers collection
        db.collection("doctorUsers").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String fullName = document.getString("fullName");
                    updateNavHeader(fullName);
                } else {
                    // If not in doctorUsers, check parentUsers
                    db.collection("parentUsers").document(userId).get().addOnCompleteListener(parentTask -> {
                        if (parentTask.isSuccessful()) {
                            DocumentSnapshot parentDocument = parentTask.getResult();
                            if (parentDocument.exists()) {
                                String fullName = parentDocument.getString("fullName");
                                updateNavHeader(fullName);
                            } else {
                                Toast.makeText(ParentDashboardActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ParentDashboardActivity.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Toast.makeText(ParentDashboardActivity.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPosts() {
        db.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        postList.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            Post post = document.toObject(Post.class);
                            if (post != null) {
                                post.setPostId(document.getId());
                                postList.add(post);
                            }
                        }
                        postAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(ParentDashboardActivity.this, "Error loading posts", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateNavHeader(String fullName) {
        View headerView = navigationView.getHeaderView(0);
        TextView fullNameTextView = headerView.findViewById(R.id.full_name);
        fullNameTextView.setText(fullName);
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logoutUser();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void logoutUser() {
        mAuth.signOut();
        startActivity(new Intent(ParentDashboardActivity.this, LoginActivity.class));
        finish(); // Close this activity so user can't return using back button
    }
}
