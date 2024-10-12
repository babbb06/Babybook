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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babybook.adapter.DoctorAdapter;
import com.example.babybook.model.Doctor;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchDoctorActivity extends AppCompatActivity {

    private EditText searchEditText;
    private Button searchButton;
    private ImageView filterButton;  // ImageView for the filter button
    private RecyclerView recyclerView;
    private DoctorAdapter adapter;
    private List<Doctor> doctors;
    private final String[] specializations = {
            "Pediatrician", "Hospitalist", "Child Abuse Pediatrician", "Neonatalists",
            "Emergency Pediatric Medicine", "Pediatric Critical Care Medicine",
            "Pediatric Cardiologist", "Pediatric Endocrinology", "Pediatric Gastroenterology",
            "Pediatric Neurology", "Pediatric Hematology/Oncology", "Pediatric Pulmonology",
            "Pediatric Nephrology", "Pediatric Infectious Disease", "Pediatric Rheumatology"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_doctor);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Search Doctor");
        }

        searchEditText = findViewById(R.id.search_edit_text);
        searchButton = findViewById(R.id.search_button);
        filterButton = findViewById(R.id.filterbutton);  // Link the filter button
        recyclerView = findViewById(R.id.recycler_view);

        doctors = new ArrayList<>();
        adapter = new DoctorAdapter(doctors, doctor -> {
            // Handle book appointment button click
            Intent intent = new Intent(SearchDoctorActivity.this, BookCheckupAppointmentActivity.class);
            intent.putExtra("doctorId", doctor.getId());
            intent.putExtra("schedStartTime", doctor.getSchedStartTime());
            intent.putExtra("schedEndTime", doctor.getSchedEndTime());
            intent.putStringArrayListExtra("schedDays", (ArrayList<String>) doctor.getSchedDays());
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Load all doctors when the activity starts
        loadAllDoctors();

        searchButton.setOnClickListener(v -> {
            String query = searchEditText.getText().toString();
            if (!TextUtils.isEmpty(query)) {
                searchDoctors(query);
            } else {
                Toast.makeText(SearchDoctorActivity.this, "Please enter a specialization to search.", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up the filter button
        filterButton.setOnClickListener(v -> showFilterMenu(v));  // Show filter menu when clicked
    }

    // Show filter menu for selecting a specialization
    private void showFilterMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(SearchDoctorActivity.this, view);
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
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(SearchDoctorActivity.this, "Error getting doctors.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Method to load all doctors when activity starts
    private void loadAllDoctors() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("doctorUsers")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        doctors.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Doctor doctor = document.toObject(Doctor.class);
                            if (doctor != null) {
                                doctor.setId(document.getId());
                                doctors.add(doctor);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(SearchDoctorActivity.this, "Error loading doctors.", Toast.LENGTH_SHORT).show();
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
