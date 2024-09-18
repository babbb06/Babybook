package com.example.babybook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DoctorRegisterActivity extends AppCompatActivity {

    private static final String TAG = "DoctorRegisterActivity";
    private EditText editTextFirstName, editTextLastName, editTextEmail, editTextPassword, editTextConfirmPassword,
            editTextSpecialization, editTextClinicAddress;
    private Spinner spinnerSpecialization;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private static final int PICK_IMAGE_REQUEST = 1;
    private boolean isImageSelected = false;
    private ImageView backBtn, selectedImage;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.lastnameEt);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextSpecialization = findViewById(R.id.editTextSpecialization);
        editTextClinicAddress = findViewById(R.id.editTextClinicAddress);
        spinnerSpecialization = findViewById(R.id.spinnerspecial);

        selectedImage = findViewById(R.id.ivSelectedImage);
        backBtn = findViewById(R.id.imageView);

        ConstraintLayout btnUploadProfile = findViewById(R.id.btnAddImage);

        //SELECT PROFILE PICTURE

        btnUploadProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Profile Photo to Continue"), PICK_IMAGE_REQUEST);
            }
        });

        ArrayAdapter<CharSequence> specializationAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.special_choices,
                android.R.layout.simple_spinner_item
        );

        specializationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerSpecialization.setAdapter(specializationAdapter);


        Button buttonRegister = findViewById(R.id.buttonRegister);
        ImageView backBtn = findViewById(R.id.imageView);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerDoctor();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the back button click here
                onBackPressed(); // or finish() to close the current activity
            }
        });

        // Setup "Sign In" TextView click listener
        TextView textViewSignIn = findViewById(R.id.textViewSignIn);
        textViewSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to LoginActivity
                Intent intent = new Intent(DoctorRegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }


    private void registerDoctor() {
        //final String fullName = editTextFullName.getText().toString().trim();
        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String fullName = firstName + " " + lastName;
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString();
        String clinicAddress = editTextClinicAddress.getText().toString().trim();
        int selectedSpecializationPosition = spinnerSpecialization.getSelectedItemPosition();
        String specialization = spinnerSpecialization.getSelectedItem().toString().trim();
        TextView selectProfile = findViewById(R.id.textView4);


        if (TextUtils.isEmpty(firstName)) {
            editTextFirstName.setError("Please enter your first name");
            editTextFirstName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(lastName)) {
            editTextLastName.setError("Please enter your last name");
            editTextLastName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Please enter your email");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email address");
            editTextEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Please enter your password");
            editTextPassword.requestFocus();
            return;
        }


        if (password.length() < 6) {
            editTextPassword.setError("Password must be at least 6 characters");
            editTextPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            editTextConfirmPassword.setError("Passwords do not match");
            editTextConfirmPassword.requestFocus();
            return;
        }

        if (selectedSpecializationPosition == 0) {
            Toast.makeText(this, "Please select specialization", Toast.LENGTH_SHORT).show();
            editTextSpecialization.requestFocus();
            spinnerSpecialization.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(clinicAddress)) {
            editTextClinicAddress.setError("Please enter clinic address");
            editTextClinicAddress.requestFocus();
            return;
        }

        if (!isImageSelected) {
            selectProfile.setError("Select a profile photo");
            Toast.makeText(this, "Select a profile photo", Toast.LENGTH_SHORT).show();
            selectProfile.requestFocus();
            return;
        }

        // Register doctor with Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign up success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");

                            // Get the user ID
                            String userId = mAuth.getCurrentUser().getUid();

                            // Call the uploadProfilePicture method
                            uploadProfilePicture(userId, fullName, email, specialization, clinicAddress);

                            Toast.makeText(DoctorRegisterActivity.this, "Doctor Registration successful", Toast.LENGTH_SHORT).show();
                            // Redirect to doctor dashboard
                            redirectToLogin();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(DoctorRegisterActivity.this, "Doctor Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveDoctorData(String fullName, String email, String specialization, String clinicAddress, String profileImageUrl) {
        // Ensure user is authenticated
        if (mAuth.getCurrentUser() == null) {
            Log.w(TAG, "saveDoctorData: no authenticated user");
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

        Map<String, Object> doctor = new HashMap<>();
        doctor.put("DoctorID", userId);  // Add the user ID to the database
        doctor.put("fullName", fullName);
        doctor.put("email", email);
        doctor.put("specialization", specialization);
        doctor.put("clinicAddress", clinicAddress);
        doctor.put("profileImageUrl", profileImageUrl); // Store the profile image URL


        // Add a new document with a generated ID
        db.collection("doctorUsers")
                .document(userId)
                .set(doctor)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                            Toast.makeText(DoctorRegisterActivity.this, "Doctor data saved to Firestore", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.w(TAG, "Error writing document", task.getException());
                            Toast.makeText(DoctorRegisterActivity.this, "Failed to save doctor data to Firestore", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /* private void redirectToDashboard() {
        Intent intent = new Intent(DoctorRegisterActivity.this, DoctorDashboardActivity.class);
        startActivity(intent);
        finish();
    } */

    private void redirectToLogin() {
        // Redirect user to LoginActivity after registration
        Intent intent = new Intent(DoctorRegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Finish the current activity to prevent going back to it via back button
    }

    //TO DISPLAY THE SELECTED IMAGE TO IMAGEVIEW
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                // Display the selected image or do any other processing as needed
                // For example, you can set it in an ImageView:
                selectedImage.setImageBitmap(bitmap);

                isImageSelected = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // uploadProfilePicture to accept fullName and email
    private void uploadProfilePicture(String userId, String fullName, String email, String specialization, String clinicAddress) {
        if (selectedImageUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("doctor_profile_pictures");
            StorageReference imageRef = storageRef.child(userId + ".jpg");

            imageRef.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Image uploaded successfully
                        imageRef.getDownloadUrl().addOnCompleteListener(downloadUrlTask -> {
                            if (downloadUrlTask.isSuccessful()) {
                                String imageUrl = downloadUrlTask.getResult().toString();

                                // After successfully uploading the image, save user data with profile image URL
                                saveDoctorData(fullName, email, specialization, clinicAddress, imageUrl);
                            } else {
                                // Handle error while getting the image URL
                            }
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Handle image upload failure
                    });
        }
    }
}


