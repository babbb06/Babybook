package com.example.babybook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.babybook.emoji.EmojiAndNumberInputFilter;
import com.example.babybook.emoji.EmojiInputFilter;
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

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextFirstName, editTextLastName, editTextEmail, editTextPassword, editTextConfirmPassword, editTextPhoneNumber;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private static final int PICK_IMAGE_REQUEST = 1;
    private boolean isImageSelected = false;
    private ImageView backBtn, selectedImage;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.lastnameEt);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextPhoneNumber = findViewById(R.id.etPhoneNumber); // New phone number field

        Button buttonRegister = findViewById(R.id.buttonRegister);
        backBtn = findViewById(R.id.imageView);
        selectedImage = findViewById(R.id.ivSelectedImage);

        ConstraintLayout btnUploadProfile = findViewById(R.id.btnAddImage);

        // Set the emoji filter on relevant EditTexts
        editTextFirstName.setFilters(new InputFilter[]{new EmojiAndNumberInputFilter()});
        editTextLastName.setFilters(new InputFilter[]{new EmojiAndNumberInputFilter()});
        editTextEmail.setFilters(new InputFilter[]{new EmojiInputFilter()}); // Email allowed special characters
        editTextPassword.setFilters(new InputFilter[]{new EmojiInputFilter()});
        editTextConfirmPassword.setFilters(new InputFilter[]{new EmojiInputFilter()});
        editTextPhoneNumber.setFilters(new InputFilter[]{new EmojiInputFilter()});
        editTextPhoneNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10), new EmojiInputFilter() });

        // SELECT PROFILE PICTURE
        btnUploadProfile.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Profile Photo to Continue"), PICK_IMAGE_REQUEST);
        });

        buttonRegister.setOnClickListener(v -> registerUser());

        TextView textViewSignIn = findViewById(R.id.textViewSignIn);
        textViewSignIn.setOnClickListener(v -> {
            // Navigate to LoginActivity
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        backBtn.setOnClickListener(v -> onBackPressed());
    }

    private void registerUser() {
        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String fullName = firstName + " " + lastName;
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString();

        String phoneNumber = editTextPhoneNumber.getText().toString().trim(); // Get phone number


        String fullphoneNumber = "+63" + phoneNumber;
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

        if (TextUtils.isEmpty(phoneNumber)) {
            editTextPhoneNumber.setError("Please enter your phone number");
            editTextPhoneNumber.requestFocus();
            return;
        }


        if (phoneNumber.length() < 10) {
            editTextPhoneNumber.setError("Phone Number must be valid");
            editTextPhoneNumber.requestFocus();
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

        if (!isImageSelected) {
            Toast.makeText(this, "Select a profile photo", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String userId = mAuth.getCurrentUser().getUid();
                        uploadProfilePicture(userId, firstName, lastName, fullName, email, fullphoneNumber); // Pass phone number
                    } else {
                        Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserData(String firstName, String lastName, String fullName, String email, String fullphoneNumber, String profileImageUrl) {
        String userId = mAuth.getCurrentUser().getUid();

        Map<String, Object> user = new HashMap<>();
        user.put("ParentID", userId);
        user.put("firstName", firstName); // Store the first name
        user.put("lastName", lastName);   // Store the last name
        user.put("fullName", fullName);
        user.put("email", email);
        user.put("phoneNumber", fullphoneNumber); // Store the phone number
        user.put("profileImageUrl", profileImageUrl); // Store the profile image URL

        db.collection("parentUsers")
                .document(userId)
                .set(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "User data saved to Firestore", Toast.LENGTH_SHORT).show();
                        redirectToLogin();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Failed to save user data to Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void redirectToLogin() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    // TO DISPLAY THE SELECTED IMAGE TO IMAGEVIEW
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                selectedImage.setImageBitmap(bitmap);
                isImageSelected = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadProfilePicture(String userId, String firstName, String lastName, String fullName, String email, String fullphoneNumber) {
        if (selectedImageUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("parent_profile_pictures");
            StorageReference imageRef = storageRef.child(userId + ".jpg");

            imageRef.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnCompleteListener(downloadUrlTask -> {
                        if (downloadUrlTask.isSuccessful()) {
                            // Save user data with profile image URL
                            saveUserData(firstName, lastName, fullName, email, fullphoneNumber, downloadUrlTask.getResult().toString());
                        } else {
                            Toast.makeText(RegisterActivity.this, "Failed to get image URL", Toast.LENGTH_SHORT).show();
                        }
                    }))
                    .addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, "Image upload failed.", Toast.LENGTH_SHORT).show());
        }
    }
}
