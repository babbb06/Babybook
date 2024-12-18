package com.example.babybook;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.babybook.adapter.PostAdapter;
import com.example.babybook.model.Post;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
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
    private Dialog progressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;


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
                    /*case R.id.doctor_nav_home:
                        break;*/
                    case R.id.doctor_nav_health:
                        startActivity(new Intent(DoctorDashboardActivity.this, HealthRecordActivity.class));
                        break;
                    case R.id.doctor_nav_chat:
                        startActivity(new Intent(DoctorDashboardActivity.this, SearchParentChatActivity.class));
                        break;
                    case R.id.doctor_nav_appointment:
                        startActivity(new Intent(DoctorDashboardActivity.this, ManageAppointmentsActivity.class));
                        break;
                    case R.id.doctor_nav_clinic:
                        checkIfDoctorHasClinic(
                                // If a clinic exists, show a message
                                () -> showMessageDialog("You have already added a clinic.", null),

                                // If no clinic exists, navigate to AddClinicActivity
                                () -> startActivity(new Intent(DoctorDashboardActivity.this, AddClinicActivity.class))
                        );
                        break;

                    case R.id.doctor_nav_edit_vaccine:
                        startActivity(new Intent(DoctorDashboardActivity.this, EditVaccineActivity.class));
                        break;
                        //new added layout
                    case R.id.doctor_nav_profile:
                        startActivity(new Intent(DoctorDashboardActivity.this, UpdateProfiledDoctor.class));
                        break;

                    case R.id.doctor_nav_logout:
                        showLogoutConfirmation();
                        break;
                    case R.id.doctor_nav_about:
                        startActivity(new Intent(DoctorDashboardActivity.this, About.class));
                        break;
                    //new added layout
                    case R.id.doctor_nav_terms:
                        startActivity(new Intent(DoctorDashboardActivity.this, Terms.class));
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
        fabCreatePost.setOnClickListener(v -> showChoosePostDialog(this));

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this::loadPosts);

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
                    updateNavHeader(fullName, userProfileImageUrl, specialization);
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

    private void showChoosePostDialog(Context context) {
        // Create a dialog
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_choose_post);

        // Find the TextViews and set click listeners
        TextView textViewFullPost = dialog.findViewById(R.id.textViewFullPost);
        TextView textViewShortPost = dialog.findViewById(R.id.textViewShortPost);



        textViewFullPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreatePostDialog(true);
                dialog.dismiss(); // Dismiss the dialog
            }
        });

        textViewShortPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreatePostDialog(false);
                dialog.dismiss(); // Dismiss the dialog
            }
        });


        // Show the dialog
        dialog.show();
    }


    private void showCreatePostDialog(Boolean full) {
        // Inflate the custom dialog layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_create_post, null);

        // Get the EditTexts from the custom layout
        EditText etHeadline = dialogView.findViewById(R.id.etHeadline);
        EditText etDate = dialogView.findViewById(R.id.etDate);
        EditText etTime = dialogView.findViewById(R.id.etTime);
        EditText etLocation = dialogView.findViewById(R.id.etLocation);
        EditText input = dialogView.findViewById(R.id.input_post);

        // Get Layouts
        TextInputLayout headlineLayout = dialogView.findViewById(R.id.headlineLayout);
        TextInputLayout dateLayout = dialogView.findViewById(R.id.dateLayout);
        TextInputLayout timeLayout = dialogView.findViewById(R.id.timeLayout);
        TextInputLayout locationLayout = dialogView.findViewById(R.id.locationLayout);

        etDate.setOnClickListener(v -> showDatePickerDialog(etDate));
        etTime.setOnClickListener(v -> showTimePickerDialog(etTime));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (full) {
            builder.setTitle("Create New Medical Mission Post");
        }else{
            builder.setTitle("Create New Medical Information Post");
        }

        // Set the custom view in the dialog
        builder.setView(dialogView);

        if (full) {
            // Show all EditTexts and their layouts
            headlineLayout.setVisibility(View.VISIBLE);
            dateLayout.setVisibility(View.VISIBLE);
            timeLayout.setVisibility(View.VISIBLE);
            locationLayout.setVisibility(View.VISIBLE);
        } else {
            // Hide all EditTexts and their layouts except for the content EditText
            headlineLayout.setVisibility(View.GONE);
            dateLayout.setVisibility(View.GONE);
            timeLayout.setVisibility(View.GONE);
            locationLayout.setVisibility(View.GONE);
        }

        builder.setPositiveButton("Post", (dialog, which) -> {
            String headline = etHeadline.getText().toString();
            String date = etDate.getText().toString();
            String time = etTime.getText().toString();
            String location = etLocation.getText().toString();
            String content = input.getText().toString();

            // Allow empty fields for non-full posts
            if (full) {
                if (headline.isEmpty() || date.isEmpty() || time.isEmpty() || location.isEmpty() || content.isEmpty()) {
                    showMessageDialog("Please fill all fields and try again.", null);
                } else {
                    createPost(headline, date, time, location, content);
                }
            } else {
                // If not full, only content is mandatory
                if (content.isEmpty()) {
                    showMessageDialog("Please fill the content field and try again.", null);
                } else {
                    createPost("", "", "", "", content); // Pass empty strings for the other fields
                }
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




    private void createPost(String headline, String date, String time, String location, String content) {
        showProgressDialog(this);

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
                            Post post = new Post(null, userId, doctorName, headline, date, time, location, content, System.currentTimeMillis(), doctorProfile, specialization);

                            // Add the post to Firestore
                            db.collection("posts").add(post).addOnSuccessListener(documentReference -> {
                                // Get the auto-generated postId and update the post document
                                String postId = documentReference.getId();
                                documentReference.update("postId", postId)
                                        .addOnSuccessListener(aVoid -> {
                                            // Toast.makeText(DoctorDashboardActivity.this, "Post created", Toast.LENGTH_SHORT).show();
                                            showMessageDialog("Your post has been created.", this::loadPosts);
                                            hideProgressDialog();
                                            //loadPosts(); // Reload posts after successful creation
                                        })
                                        .addOnFailureListener(e -> {
                                            //Toast.makeText(DoctorDashboardActivity.this, "Error updating postId", Toast.LENGTH_SHORT).show();
                                            showMessageDialog("Error creating post. Please try again later.", null);
                                        });
                            }).addOnFailureListener(e -> {
                                //Toast.makeText(DoctorDashboardActivity.this, "Error creating post", Toast.LENGTH_SHORT).show();
                                showMessageDialog("Error creating post. Please try again later.", null);
                            });
                        } else {
                            //Toast.makeText(DoctorDashboardActivity.this, "Doctor profile data missing", Toast.LENGTH_SHORT).show();
                            showMessageDialog("Error creating post. Please try again later.", null);
                        }
                    }
                }
            });
        }
    }


    private void loadPosts() {
        swipeRefreshLayout.setRefreshing(true);
        db.collection("posts")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    swipeRefreshLayout.setRefreshing(false);
                    if (task.isSuccessful()) {
                        postList.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            Post post = document.toObject(Post.class);
                            post.setPostId(document.getId());
                            postList.add(post);
                        }
                        postAdapter.notifyDataSetChanged(); // Update UI after modifying postList
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

    protected void onResume() {
        super.onResume();
        loadPosts();
    }

    // TIME DIALOG TO ETTIME
    private void showTimePickerDialog(EditText etTime) { // Accept etTime as a parameter
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Adjust the minute to the nearest 00 or 30
        minute = (minute < 15) ? 0 : (minute < 45) ? 30 : 0;
        if (minute == 0 && hour == 23) {
            hour = 0; // Wrap around to 0 when it's 23:00
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {
                    String time = String.format("%02d:%02d %s",
                            selectedHour % 12 == 0 ? 12 : selectedHour % 12,
                            selectedMinute,
                            selectedHour < 12 ? "AM" : "PM");
                    etTime.setText(time); // Set the selected time to etTime
                },
                hour,
                minute, // Use adjusted minute
                false // Use 12-hour format
        );

        timePickerDialog.show();
    }

    // DATE DIALOG TO ETTIME
    private void showDatePickerDialog(EditText etDate) { // Accept etDate as a parameter
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = String.format("%02d/%02d/%d", selectedMonth + 1, selectedDay, selectedYear);
                    etDate.setText(date); // Set the selected date to etDate
                },
                year,
                month,
                day
        );

        // Set the minimum date to today
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

        datePickerDialog.show();
    }

    private void showProgressDialog(Context context) {
        progressDialog = new Dialog(context);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCancelable(false); // Disable dismissing the dialog by tapping outside

        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void showMessageDialog(String message, Runnable onOkPressed) {
        // Create a new AlertDialog Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set the message
        builder.setMessage(message);

        // Set the "OK" button
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
            // Call the provided Runnable
            if (onOkPressed != null) {
                onOkPressed.run();
            }
        });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void checkIfDoctorHasClinic(Runnable onClinicExists, Runnable onClinicNotExists) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String doctorId = currentUser.getUid();

        // Query the 'clinics' collection for a document with the matching 'doctorId'
        db.collection("clinics")
                .whereEqualTo("doctorId", doctorId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Clinic already exists for this doctor
                        if (onClinicExists != null) {
                            onClinicExists.run();
                        }
                    } else {
                        // No clinic found for this doctor
                        if (onClinicNotExists != null) {
                            onClinicNotExists.run();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle errors in querying Firestore
                    Toast.makeText(DoctorDashboardActivity.this, "Error checking clinic data", Toast.LENGTH_SHORT).show();
                });
    }




}
