package com.example.babybook;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.babybook.databinding.ActivityEditVaccineBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditVaccineActivity extends AppCompatActivity {
    private ActivityEditVaccineBinding binding;
    private final Map<String, Integer> vaccineMap = new HashMap<>();
    private List<String> vaccines;
    private FirebaseFirestore db;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String userId = currentUser.getUid();
    private Dialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initConfig();
        fetchVaccineData();

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Edit Vaccine");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // List of vaccines
        vaccines = new ArrayList<>();
        vaccines.add("BCG");
        vaccines.add("Hepatitis B");
        vaccines.add("DPT");
        vaccines.add("Booster 1");
        vaccines.add("OPV IPV");
        vaccines.add("Booster 2");
        vaccines.add("H Influenza B");
        vaccines.add("Rotavirus");
        vaccines.add("Measles");
        vaccines.add("MMR");
        vaccines.add("Booster 3");

        db = FirebaseFirestore.getInstance();

    }


    private void initConfig() {
        initBinding();
        initListeners();
    }

    private void initBinding() {
        binding = ActivityEditVaccineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    private void initListeners() {
        binding.buttonAdd.setOnClickListener(view -> showVaccineSelectionDialog());

        binding.buttonUpdate.setOnClickListener(view -> {
            updateVaccineData();
        });

        binding.textViewClinicName.setOnClickListener(view -> showEditClinicNameDialog());


    }


    private void showVaccineInputDialog(String vaccine) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Vaccine Details");

        // Inflate the custom dialog layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_vaccine_details, null);
        builder.setView(dialogView);

        // Get references to the TextView and EditText in the dialog
        TextView vaccineNameTextView = dialogView.findViewById(R.id.vaccineNameTextView);
        EditText quantityInput = dialogView.findViewById(R.id.quantityInput);
        ImageView buttonPlus = dialogView.findViewById(R.id.buttonPlus);
        ImageView buttonMinus = dialogView.findViewById(R.id.buttonMinus);

        // Set the vaccine name in the TextView
        vaccineNameTextView.setText(vaccine);

        // Set initial quantity
        quantityInput.setText("1");

        // Add functionality for the minus button
        buttonMinus.setOnClickListener(view -> {
            int currentQuantity = Integer.parseInt(quantityInput.getText().toString());
            if (currentQuantity > 1) {
                currentQuantity--;
                quantityInput.setText(String.valueOf(currentQuantity));
            }
        });

        // Add functionality for the plus button
        buttonPlus.setOnClickListener(view -> {
            int currentQuantity = Integer.parseInt(quantityInput.getText().toString());
            if (currentQuantity < 999) { // Set a max limit for safety
                currentQuantity++;
                quantityInput.setText(String.valueOf(currentQuantity));
            }
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
                    String quantity = quantityInput.getText().toString();
                    addVaccineToList(vaccine, quantity);
                })
                .setNegativeButton("Cancel", null);

        // Show the dialog
        builder.create().show();
    }

    private void showVaccineSelectionDialog() {
        CharSequence[] vaccineArray = vaccines.toArray(new CharSequence[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose a Vaccine")
                .setItems(vaccineArray, (dialog, which) -> {
                    String selectedVaccine = vaccineArray[which].toString();
                    showVaccineInputDialog(selectedVaccine);
                })
                .setNegativeButton("Cancel", null);

        builder.create().show();
    }

    private void addVaccineToList(String vaccine, String quantity) {
        // Inflate the vaccine list item layout
        View vaccineItem = getLayoutInflater().inflate(R.layout.vaccine_list_item, binding.vaccineList, false);

        // Find the TextViews in the inflated layout
        TextView vaccineNameTextView = vaccineItem.findViewById(R.id.vaccineNameTextView);
        TextView quantityTextView = vaccineItem.findViewById(R.id.quantityTextView);
        ImageView img_delete = vaccineItem.findViewById(R.id.image_view_delete);

        // Set the text for the TextViews
        vaccineNameTextView.setText(vaccine);
        quantityTextView.setText(quantity);

        // Set the delete button's onClickListener
        img_delete.setOnClickListener(view -> {
            // Remove the vaccine item from the list view
            ((ViewGroup) vaccineItem.getParent()).removeView(vaccineItem);

            // Remove the vaccine from the vaccineMap
            vaccineMap.remove(vaccine);
        });

        // Add the inflated view to the vaccine list
        binding.vaccineList.addView(vaccineItem);

        // Add to the map
        vaccineMap.put(vaccine, Integer.parseInt(quantity));
    }


    private void fetchVaccineData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query the collection where the doctorId matches the current userId
        db.collection("clinics")
                .whereEqualTo("doctorId", userId) // Filter by doctorId matching userId
                .get() // Retrieve the data
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Check if a clinic document is found for the current user
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Assuming that the "vaccines" field is a map of vaccine names and quantities
                            Map<String, Object> vaccinesData = (Map<String, Object>) document.get("vaccines");
                            String clinicName = document.getString("clinicName");

                            if (vaccinesData != null) {
                                // Iterate over the vaccines map and update the vaccineMap
                                for (Map.Entry<String, Object> entry : vaccinesData.entrySet()) {
                                    String vaccineName = entry.getKey();
                                    Integer quantity = Integer.valueOf(entry.getValue().toString());

                                    // Add the vaccine and quantity to the vaccineMap
                                    vaccineMap.put(vaccineName, quantity);

                                    // Add the vaccine to the list on the UI
                                    addVaccineToList(vaccineName, quantity.toString());
                                }
                            }

                            binding.textViewClinicName.setText(clinicName);
                        }
                    } else {
                        // If the query failed, show an error message
                        Toast.makeText(this, "Error fetching vaccine data", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void updateVaccineData() {
        showProgressDialog(this);
        // Prepare the updated vaccine data as a Map
        Map<String, Object> updatedVaccinesData = new HashMap<>();
        for (Map.Entry<String, Integer> entry : vaccineMap.entrySet()) {
            updatedVaccinesData.put(entry.getKey(), entry.getValue());
        }

        // Update the Firestore database with the new vaccine data
        db.collection("clinics")
                .whereEqualTo("doctorId", userId) // Find the clinic associated with the current doctor
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Assuming that the first document corresponds to the current clinic of the doctor
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String clinicId = document.getId();

                            // Update the "vaccines" field in the clinic document
                            db.collection("clinics").document(clinicId)
                                    .update("vaccines", updatedVaccinesData)
                                    .addOnSuccessListener(aVoid -> {
                                        showMessageDialog("Vaccine Data has been updated.", this::finish);
                                        hideProgressDialog();
                                        Toast.makeText(EditVaccineActivity.this, "Vaccine data updated successfully!", Toast.LENGTH_SHORT).show();

                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(EditVaccineActivity.this, "Error updating vaccine data.", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        // If no clinics were found or the task failed
                        hideProgressDialog();
                        Toast.makeText(EditVaccineActivity.this, "Error finding clinic document.", Toast.LENGTH_SHORT).show();
                    }
                });
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    private void showEditClinicNameDialog() {
        // Create an AlertDialog builder for editing clinic name
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Clinic Name");

        // Inflate the custom dialog layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_clinic_name, null);
        builder.setView(dialogView);

        // Get the EditText for the new clinic name
        EditText clinicNameInput = dialogView.findViewById(R.id.clinicNameInput);

        // Pre-fill the EditText with the current clinic name
        String currentClinicName = binding.textViewClinicName.getText().toString();
        clinicNameInput.setText(currentClinicName);

        builder.setPositiveButton("Submit", (dialog, which) -> {
            String newClinicName = clinicNameInput.getText().toString().trim();
            if (!newClinicName.isEmpty()) {
                // Show progress dialog while updating the clinic name
                showProgressDialog(this);

                // Update the clinic name in the Firestore
                db.collection("clinics")
                        .whereEqualTo("doctorId", userId) // Filter by doctorId matching userId
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String clinicId = document.getId();

                                    // Update the clinic name
                                    db.collection("clinics").document(clinicId)
                                            .update("clinicName", newClinicName)
                                            .addOnSuccessListener(aVoid -> {
                                                // Update the UI
                                                binding.textViewClinicName.setText(newClinicName);
                                                hideProgressDialog();
                                                Toast.makeText(this, "Clinic name updated successfully!", Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> {
                                                hideProgressDialog();
                                                Toast.makeText(this, "Error updating clinic name", Toast.LENGTH_SHORT).show();
                                            });
                                }
                            } else {
                                hideProgressDialog();
                                Toast.makeText(this, "Error fetching clinic data", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(this, "Please enter a valid clinic name", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });

        // Show the dialog
        builder.create().show();
    }
}