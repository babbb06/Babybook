package com.example.babybook;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;

import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.babybook.emoji.EmojiAndNumberInputFilter;
import com.example.babybook.emoji.EmojiInputFilter;
import com.example.babybook.emoji.EmojispaceInputFilter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoctorRegisterActivity extends AppCompatActivity {

    private static final String TAG = "DoctorRegisterActivity";
    private EditText editTextFirstName, editTextLastName, editTextBirthday, editTextEmail, editTextPassword, editTextConfirmPassword, editTextPRCLicenseNumber,
            editTextSpecialization, editTextClinicAddress, etPhoneNumber, editTextActivationCode;
    private Spinner spinnerSpecialization;
    private TextInputEditText etStartTime, etEndTime;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private static final int PICK_IMAGE_REQUEST = 1;
    private boolean isImageSelected = false;
    private ImageView backBtn, selectedImage;
    private Uri selectedImageUri;
    private Dialog progressDialog;


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
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        editTextPRCLicenseNumber = findViewById(R.id.editTextPRCLicenseNumber);
        editTextSpecialization = findViewById(R.id.editTextSpecialization);
        editTextClinicAddress = findViewById(R.id.editTextClinicAddress);
        spinnerSpecialization = findViewById(R.id.spinnerspecial);
        editTextBirthday = findViewById(R.id.editTextBirthday);
        selectedImage = findViewById(R.id.ivSelectedImage);
        backBtn = findViewById(R.id.imageView);
        editTextActivationCode = findViewById(R.id.edittext_activation_code);


        //time
        etStartTime = findViewById(R.id.etStartTime);
        etEndTime = findViewById(R.id.etEndTime);

        ConstraintLayout btnUploadProfile = findViewById(R.id.btnAddImage);

        //disabled emojies and special character on first and last name
        // Set the emoji filter on relevant EditTexts
        editTextFirstName.setFilters(new InputFilter[]{new EmojiAndNumberInputFilter()});
        editTextLastName.setFilters(new InputFilter[]{new EmojiAndNumberInputFilter()});
        editTextEmail.setFilters(new InputFilter[]{new EmojiInputFilter()}); // Email allowed special characters
        editTextPassword.setFilters(new InputFilter[]{new EmojiInputFilter()});
        editTextConfirmPassword.setFilters(new InputFilter[]{new EmojiInputFilter()});
        editTextSpecialization.setFilters(new InputFilter[]{new EmojiInputFilter()});
        editTextClinicAddress.setFilters(new InputFilter[]{new EmojispaceInputFilter()});


//TIME SHOW WHEN PRESS START AND END

        etStartTime.setOnClickListener(v -> showTimePickerDialog(true));
        etEndTime.setOnClickListener(v -> showTimePickerDialog(false));

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

    public void showDatePicker(View view) {
        // Get the current date
        Calendar calendar = Calendar.getInstance();

        // Set the date to 18 years ago
        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.YEAR, -18); // Subtract 18 years

        // Create a DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        // Update the TextInputEditText with the selected date
                        String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        editTextBirthday.setText(date);
                        editTextBirthday.setError(null);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Set the maximum date to 18 years ago
        datePickerDialog.getDatePicker().setMaxDate(minDate.getTimeInMillis());

        datePickerDialog.show();
    }


    private void registerDoctor() {
        //final String fullName = editTextFullName.getText().toString().trim();
        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String fullName = firstName + " " + lastName;
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString();
        String PRCLicenseNumber = editTextPRCLicenseNumber.getText().toString();
        String activationCode = editTextActivationCode.getText().toString();
        String birthday = editTextBirthday.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String clinicAddress = editTextClinicAddress.getText().toString().trim();
        int selectedSpecializationPosition = spinnerSpecialization.getSelectedItemPosition();
        String specialization = spinnerSpecialization.getSelectedItem().toString().trim();
        String schedStartTime = etStartTime.getText().toString().trim();
        String schedEndTime = etEndTime.getText().toString().trim();
        List<String> selectedDays = getSelectedDays();

        TextView selectProfile = findViewById(R.id.textView4);

        // Concatenate country code with the phone number
        String fullPhoneNumber = "+63" + phoneNumber;


        // Restrict numbers in First and Last Name
        if (containsNumber(firstName)) {
            editTextFirstName.setError("First Name cannot contain numbers");
            editTextFirstName.requestFocus();
            return;
        }

//FIRST NAME ERROR HANDLING
        if (TextUtils.isEmpty(firstName)) {
            editTextFirstName.setError("Please enter your First Name");
            editTextFirstName.requestFocus();
            return;
        }

//LAST NAME ERROR HANDLING
        if (TextUtils.isEmpty(lastName)) {
            editTextLastName.setError("Please enter your Last Name");
            editTextLastName.requestFocus();
            return;
        }


//EMAIL ERROR HANDLING

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Please enter your Email");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid Email Address");
            editTextEmail.requestFocus();
            return;
        }

        //EMAIL ERROR HANDLING
        if (TextUtils.isEmpty(phoneNumber)) {
            etPhoneNumber.setError("Please enter your Phone Number");
            etPhoneNumber.requestFocus();
            return;
        }
        if (phoneNumber.length() < 10) {
            etPhoneNumber.setError("Phone Number must be valid");
            etPhoneNumber.requestFocus();
            return;
        }


//PASSWORD ERROR HANDLING
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Please enter your Password");
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


        //Bday ERROR HANDLING
        if (TextUtils.isEmpty(birthday)) {
            editTextBirthday.setError("Please enter your Birthday");
            editTextBirthday.requestFocus();
            return;
        }


//PRC LICENSE ERROR HANDLING
        if (TextUtils.isEmpty(PRCLicenseNumber)) {
            editTextPRCLicenseNumber.setError("Please enter a  valid License Number");
            editTextPRCLicenseNumber.requestFocus();
            return;
        }
        if (PRCLicenseNumber.length() < 7) {
            editTextPRCLicenseNumber.setError("PRC License Number must be at least 7 Numbers");
            editTextPRCLicenseNumber.requestFocus();
            return;
        }

        //ACTIVATION CODE ERROR HANDLING
        if (TextUtils.isEmpty(activationCode)) {
            editTextActivationCode.setError("Please enter activation code");
            editTextActivationCode.requestFocus();
            return;
        }
        if (activationCode.length() < 5) {
            editTextActivationCode.setError("Activation Code must be at least 5 Numbers");
            editTextActivationCode.requestFocus();
            return;
        }

//SPECIALIZATION ERROR HANDLING

// NEED TO IMPROVE THE SPECIALIZATION ERROR HANDLING!!!!!

        if (selectedSpecializationPosition == 0) {
            Toast.makeText(this, "Please select specialization", Toast.LENGTH_SHORT).show();
            editTextSpecialization.requestFocus();
            spinnerSpecialization.requestFocus();
            return;
        }


//ADDRESS ERROR HANDLING

        if (TextUtils.isEmpty(clinicAddress)) {
            editTextClinicAddress.setError("Please enter clinic address");
            editTextClinicAddress.requestFocus();
            return;
        }


        // TIME ERROR HANDLING OR VALIDATION


        if (etStartTime.getText().toString().trim().isEmpty()) {
            etStartTime.setError("Start time is required");
            etStartTime.requestFocus();
            return;
        }
        if (etEndTime.getText().toString().trim().isEmpty()) {
            etEndTime.setError("End time is required");
            etEndTime.requestFocus();
            return;
        }

//PHOTO ERROR HANDLING

        // Check if an image has been selected
        if (!isImageSelected) {
            // Display a toast message to the user
            Toast.makeText(this, "Please select a profile photo.", Toast.LENGTH_SHORT).show();

            // Set the button background to indicate an error
            findViewById(R.id.btnAddImage).setBackgroundResource(R.drawable.rectangle_radius_white_stroke_blackerror);

            // Set the error message for the label
            TextView lblUploadImage = findViewById(R.id.lbletbtnAddImage);
            lblUploadImage.setText("Upload Profile Photo is required");


            // Return to prevent further execution
            return;
        } else {
            // Reset the button background if the image is selected
            findViewById(R.id.btnAddImage).setBackgroundResource(R.drawable.rectangle_radius_white_stroke_black);

            // Clear the error message for the label
            TextView lblUploadImage = findViewById(R.id.lbletbtnAddImage);
            lblUploadImage.setText("Upload Profile Photo");
        }

        //showProgressDialog(this);

        db.collection("activation_codes").document(activationCode)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        // Code found, proceed with registration
                        // Proceed with registration (Firebase Authentication, storing user data, etc.)
                        String codeDocId = task.getResult().getId(); // Get the document ID (which is the activation code)
                        registerWithEmailAndPassword(email, password, fullName, firstName, lastName, birthday, email, fullPhoneNumber, PRCLicenseNumber, specialization, clinicAddress, schedStartTime, schedEndTime, selectedDays, activationCode, codeDocId);
                    } else {
                        Toast.makeText(DoctorRegisterActivity.this, "Invalid activation code", Toast.LENGTH_SHORT).show();
                        editTextActivationCode.setError("Please enter valid activation code");
                        editTextActivationCode.requestFocus();
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle error with Firestore query
                    Toast.makeText(DoctorRegisterActivity.this, "Invalid activation code", Toast.LENGTH_SHORT).show();
                    editTextActivationCode.setError("Please enter valid activation code");
                    editTextActivationCode.requestFocus();
                });
    }

    private void registerWithEmailAndPassword(String email, String password, String fullName, String firstName, String lastName, String birthday, String emailAddress, String fullPhoneNumber, String PRCLicenseNumber, String specialization, String clinicAddress, String schedStartTime, String schedEndTime, List<String> schedDays, String activationCode, String codeDocId) {
        // Show progress dialog
        showProgressDialog(this);

        // Register doctor with Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // User registration successful, now upload the profile picture and doctor data
                        String userId = mAuth.getCurrentUser().getUid();
                        uploadProfilePicture(userId, fullName, firstName, lastName, birthday, emailAddress, fullPhoneNumber, PRCLicenseNumber, specialization, clinicAddress, schedStartTime, schedEndTime, schedDays);

                        // After registration and uploading the profile picture, delete the activation code from Firestore
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("activation_codes").document(codeDocId)
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "Activation code deleted successfully.");
                                })
                                .addOnFailureListener(e -> {
                                    Log.w(TAG, "Error deleting activation code", e);
                                });

                        // Redirect or show success message
                        //Toast.makeText(DoctorRegisterActivity.this, "Doctor Registration successful", Toast.LENGTH_SHORT).show();
                        // redirectToLogin();
                    } else {
                        // If registration fails, show error message
                        hideProgressDialog();
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(DoctorRegisterActivity.this, "Doctor Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Utility method to check if a string contains emoji
    private boolean containsEmoji(String text) {
        int len = text.length();
        for (int i = 0; i < len; i++) {
            int codePoint = text.codePointAt(i);
            if (!isPrintableAscii(codePoint) && Character.getType(codePoint) != Character.CONTROL) {
                return true; // Non-printable ASCII (likely emoji)
            }
        }
        return false;
    }

    // Check if a codepoint is printable ASCII
    private boolean isPrintableAscii(int codePoint) {
        return codePoint >= 32 && codePoint <= 126;
    }

    // Utility method to check if a string contains numbers
    private boolean containsNumber(String text) {
        return text.matches(".*\\d.*");
    }

    private void saveDoctorData(String fullName, String firstName, String lastName, String birthday, String email, String fullPhoneNumber, String PRCLicenseNumber, String specialization, String clinicAddress, String profileImageUrl, String schedStartTime, String schedEndTime, List<String> selectedDays) {
        // Ensure user is authenticated
        if (mAuth.getCurrentUser() == null) {
            Log.w(TAG, "saveDoctorData: no authenticated user");
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

        Map<String, Object> doctor = new HashMap<>();
        doctor.put("DoctorID", userId);  // Add the user ID to the database
        doctor.put("fullName", fullName);
        doctor.put("firstName", firstName);
        doctor.put("lastName", lastName);
        doctor.put("birthday", birthday);
        doctor.put("email", email);
        doctor.put("phoneNumber", fullPhoneNumber);
        doctor.put("PRCLicenseNumber", PRCLicenseNumber);
        doctor.put("specialization", specialization);
        doctor.put("clinicAddress", clinicAddress);
        doctor.put("profileImageUrl", profileImageUrl); // Store the profile image URL
        doctor.put("schedStartTime", schedStartTime);
        doctor.put("schedEndTime", schedEndTime);
        doctor.put("schedDays", selectedDays);


        // Add a new document with a generated ID
        db.collection("doctorUsers")
                .document(userId)
                .set(doctor)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                            //Toast.makeText(DoctorRegisterActivity.this, "Doctor data saved to Firestore", Toast.LENGTH_SHORT).show();
                            showMessageDialog("Doctor Account has been registered.", this::redirectToLogin);
                            hideProgressDialog();
                        } else {
                            Log.w(TAG, "Error writing document", task.getException());
                            showMessageDialog("Failed to save user data to Firestore. Please try again", null);
                        }
                    }

                    private void redirectToLogin() {
                        // Redirect user to LoginActivity after registration
                        Intent intent = new Intent(DoctorRegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish(); // Finish the current activity to prevent going back to it via back button
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
    private void uploadProfilePicture(String userId, String fullName, String firstName, String lastName, String birthday, String email, String fullPhoneNumber, String PRCLicenseNumber, String specialization, String clinicAddress, String schedStartTime, String schedEndTime, List<String> schedDays) {
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
                                saveDoctorData(fullName, firstName, lastName, birthday, email, fullPhoneNumber, PRCLicenseNumber, specialization, clinicAddress, imageUrl, schedStartTime, schedEndTime, schedDays);
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


    private void showTimePickerDialog(final boolean isStartTime) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Adjust the minute to the nearest 00 or 30
        minute = (minute < 15) ? 0 : (minute < 45) ? 30 : 0;
        if (minute == 0 && calendar.get(Calendar.HOUR_OF_DAY) == 23) {
            hour = 0; // Wrap around to 0 when it's 23:00
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {
                    String time = String.format("%02d:%02d %s",
                            selectedHour % 12 == 0 ? 12 : selectedHour % 12,
                            selectedMinute,
                            selectedHour < 12 ? "AM" : "PM");

                    if (isStartTime) {
                        etStartTime.setText(time);
                    } else {
                        etEndTime.setText(time);
                    }
                },
                hour,
                minute, // Use adjusted minute
                false // Use 12-hour format
        ) {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                // Restrict minutes to 00 or 30
                if (minute != 0 && minute != 30) {
                    view.setMinute(minute < 15 ? 0 : 30);
                }
                super.onTimeChanged(view, hourOfDay, minute);
            }
        };

        timePickerDialog.show();
    }

    // DAYS SCHEDULE
    private List<String> getSelectedDays() {
        List<String> selectedDays = new ArrayList<>();
        if (((CheckBox) findViewById(R.id.cbMonday)).isChecked()) selectedDays.add("Monday");
        if (((CheckBox) findViewById(R.id.cbTuesday)).isChecked()) selectedDays.add("Tuesday");
        if (((CheckBox) findViewById(R.id.cbWednesday)).isChecked()) selectedDays.add("Wednesday");
        if (((CheckBox) findViewById(R.id.cbThursday)).isChecked()) selectedDays.add("Thursday");
        if (((CheckBox) findViewById(R.id.cbFriday)).isChecked()) selectedDays.add("Friday");
        if (((CheckBox) findViewById(R.id.cbSaturday)).isChecked()) selectedDays.add("Saturday");
        if (((CheckBox) findViewById(R.id.cbSunday)).isChecked()) selectedDays.add("Sunday");
        return selectedDays;
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
}


