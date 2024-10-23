package com.example.babybook;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babybook.adapter.DoctorAdapter2;
import com.example.babybook.model.Doctor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchDoctorChatActivity extends AppCompatActivity {

    private final String[] specializations = {
            "Pediatrician", "Hospitalist", "Child Abuse Pediatrician", "Neonatalists",
            "Emergency Pediatric Medicine", "Pediatric Critical Care Medicine",
            "Pediatric Cardiologist", "Pediatric Endocrinology", "Pediatric Gastroenterology",
            "Pediatric Neurology", "Pediatric Hematology/Oncology", "Pediatric Pulmonology",
            "Pediatric Nephrology", "Pediatric Infectious Disease", "Pediatric Rheumatology", "Midwife"
    };

    private EditText searchEditText;
    private TextView tvNoDoctor;
    private ImageView filterButton;
    private Button searchButton;
    private RecyclerView recyclerView;
    private DoctorAdapter2 adapter;
    private List<Doctor> doctors;
    private List<Doctor> allDoctors; // To store all doctors

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_doctor_chat);

        // Set up Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Message");
        }

        searchEditText = findViewById(R.id.editTextSpecialization);
        searchButton = findViewById(R.id.buttonSearch);
        recyclerView = findViewById(R.id.recyclerViewDoctors);
        tvNoDoctor = findViewById(R.id.tvNoDoctor);
        filterButton = findViewById(R.id.filterbutton);
        doctors = new ArrayList<>();
        allDoctors = new ArrayList<>(); // Initialize the list for all doctors

        adapter = new DoctorAdapter2(doctors, doctor -> {
            // Handle message icon click
            Intent intent = new Intent(SearchDoctorChatActivity.this, ChatActivity.class);
            String parentId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get the current parent ID
            intent.putExtra("senderId", parentId);
            intent.putExtra("receiverId", doctor.getId());
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Load doctors based on chat activity initially
        loadDoctorsBasedOnChats();

        searchButton.setOnClickListener(v -> {
            String query = searchEditText.getText().toString();
            if (!TextUtils.isEmpty(query)) {
                filterDoctors(query);
            } else {
                Toast.makeText(SearchDoctorChatActivity.this, "Please enter doctor's name to search.", Toast.LENGTH_SHORT).show();
                // If the search field is empty, show all doctors again
                doctors.clear();
                doctors.addAll(allDoctors);
                adapter.notifyDataSetChanged();
            }
        });

        // Set up the filter button
        filterButton.setOnClickListener(v -> showFilterMenu(v));
    }

    private void loadDoctorsBasedOnChats() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Use a Set to avoid duplicates
        Set<String> doctorIds = new HashSet<>();

        // Get chats where the user is either the sender or the receiver
        db.collection("chats")
                .whereEqualTo("senderId", currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String doctorId = document.getString("receiverId");
                            if (doctorId != null) {
                                doctorIds.add(doctorId);
                            }
                        }

                        // Get chats where the user is the receiver
                        db.collection("chats")
                                .whereEqualTo("receiverId", currentUserId)
                                .get()
                                .addOnCompleteListener(receiverTask -> {
                                    if (receiverTask.isSuccessful()) {
                                        for (DocumentSnapshot document : receiverTask.getResult()) {
                                            String doctorId = document.getString("senderId");
                                            if (doctorId != null) {
                                                doctorIds.add(doctorId);
                                            }
                                        }

                                        // Fetch doctor details based on the collected doctor IDs
                                        if (!doctorIds.isEmpty()) {
                                            fetchDoctorsByIds(new ArrayList<>(doctorIds));
                                        } else {
                                            // If no chats found, load the usual list of all doctors
                                            loadAllDoctors();
                                        }
                                    } else {
                                        Toast.makeText(SearchDoctorChatActivity.this, "Error loading receiver chats.", Toast.LENGTH_SHORT).show();
                                        // If there's an error, attempt to load all doctors
                                        loadAllDoctors();
                                    }
                                });
                    } else {
                        Toast.makeText(SearchDoctorChatActivity.this, "Error loading sender chats.", Toast.LENGTH_SHORT).show();
                        // If there's an error, attempt to load all doctors
                        loadAllDoctors();
                    }
                });
    }

    private void fetchDoctorsByIds(List<String> doctorIds) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        doctors.clear();

        db.collection("doctorUsers")
                .whereIn("DoctorID", doctorIds)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Doctor doctor = document.toObject(Doctor.class);
                            if (doctor != null) {
                                doctor.setId(document.getId());
                                doctors.add(doctor);
                            }
                        }
                        adapter.notifyDataSetChanged();

                        // Show or hide the no doctor message
                        if (doctors.isEmpty()) {
                            tvNoDoctor.setVisibility(View.VISIBLE);
                            tvNoDoctor.setText("No doctors found in chats.");
                        } else {
                            tvNoDoctor.setVisibility(View.GONE);
                        }
                    } else {
                        Toast.makeText(SearchDoctorChatActivity.this, "Error getting doctors.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadAllDoctors() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("doctorUsers")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        doctors.clear();
                        allDoctors.clear(); // Clear the list before adding new data
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Doctor doctor = document.toObject(Doctor.class);
                            if (doctor != null) {
                                doctor.setId(document.getId());
                                doctors.add(doctor);
                                allDoctors.add(doctor); // Add to allDoctors list
                            }
                        }
                        adapter.notifyDataSetChanged();

                        // Show or hide the no doctor message
                        if (doctors.isEmpty()) {
                            tvNoDoctor.setVisibility(View.VISIBLE);
                            tvNoDoctor.setText("No doctors found.");
                        } else {
                            tvNoDoctor.setVisibility(View.GONE);
                        }
                    } else {
                        Toast.makeText(SearchDoctorChatActivity.this, "Error getting doctors.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void filterDoctors(String query) {
        String lowerCaseQuery = query.trim().toLowerCase();

        doctors.clear();
        for (Doctor doctor : allDoctors) {
            if (doctor.getFullName() != null && doctor.getFullName().toLowerCase().startsWith(lowerCaseQuery)) {
                doctors.add(doctor);
            }
        }
        adapter.notifyDataSetChanged();

        // Show or hide the no doctor message based on filtered results
        if (doctors.isEmpty()) {
            tvNoDoctor.setVisibility(View.VISIBLE);
            tvNoDoctor.setText("No doctors found matching your query.");
        } else {
            tvNoDoctor.setVisibility(View.GONE);
        }
    }

   // Show filter menu for selecting a specialization
    private void showFilterMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(SearchDoctorChatActivity.this, view);
        for (String specialization : specializations) {
            popupMenu.getMenu().add(specialization);
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            searchEditText.setText(item.getTitle());  // Set the selected specialization in the search bar
            searchDoctors(item.getTitle().toString());  // Trigger the search
            return true;
        });
        popupMenu.show();
    }
    // Method to search doctors based on the query
    private void searchDoctors(String query) {
        String lowerCaseQuery = query.trim().toLowerCase();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("doctorUsers")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        doctors.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Doctor doctor = document.toObject(Doctor.class);
                            if (doctor != null && doctor.getSpecialization() != null &&
                                    doctor.getSpecialization().toLowerCase().contains(lowerCaseQuery)) {
                                doctor.setId(document.getId());
                                doctors.add(doctor);
                            }
                        }

                        // Check if any doctors were found
                        if (doctors.isEmpty()) {
                            tvNoDoctor.setVisibility(View.VISIBLE);
                            tvNoDoctor.setText("No doctors found with specialization: \n" + query);
                        } else {
                            tvNoDoctor.setVisibility(View.GONE);
                        }

                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(SearchDoctorChatActivity.this, "Error getting doctors.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void filterBySpecialization(String specialization) {
        doctors.clear();
        for (Doctor doctor : allDoctors) {
            if (doctor.getSpecialization() != null && doctor.getSpecialization().equalsIgnoreCase(specialization)) {
                doctors.add(doctor);
            }
        }

        adapter.notifyDataSetChanged();

        // Show or hide the no doctor message based on filtered results
        if (doctors.isEmpty()) {
            tvNoDoctor.setVisibility(View.VISIBLE);
            tvNoDoctor.setText("No doctors found for the selected specialization.");
        } else {
            tvNoDoctor.setVisibility(View.GONE);
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
