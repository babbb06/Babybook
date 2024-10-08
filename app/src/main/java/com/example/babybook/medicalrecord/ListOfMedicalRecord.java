package com.example.babybook.medicalrecord;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.babybook.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ListOfMedicalRecord extends AppCompatActivity {
    private FloatingActionButton fabCreatePost;
    private String childId; // Store the child ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_medical_record);

        // Initialize FloatingActionButton
        fabCreatePost = findViewById(R.id.fabCreatePost);

        // Retrieve child ID from the intent
        childId = getIntent().getStringExtra("CHILD_ID");


        // Set onClickListener for fabCreatePost
        fabCreatePost.setOnClickListener(v -> {
            // Create an Intent to navigate to AddMedicalRecord Activity
            Intent intent = new Intent(ListOfMedicalRecord.this, AddMedicalRecord.class);

            startActivity(intent);
        });
    }
}