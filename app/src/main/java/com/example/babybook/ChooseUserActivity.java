package com.example.babybook;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class ChooseUserActivity extends AppCompatActivity {

    private boolean isParentSelected = false;
    private ImageView parentIcon, doctorIcon;
    private Drawable originalBackground;
    private Drawable borderDrawable;
    private TextView signInBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_user);

        parentIcon = findViewById(R.id.icon_parent);
        doctorIcon = findViewById(R.id.icon_doctor);
        Button myButton = findViewById(R.id.my_button);
        signInBtn = findViewById(R.id.textViewSignIn);

        // Save the original background of the ImageView
        originalBackground = parentIcon.getBackground();
        // Get the border drawable
        borderDrawable = ContextCompat.getDrawable(this, R.drawable.imageview_border);

        parentIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isParentSelected = true;
                parentIcon.setBackground(borderDrawable); // Apply the border effect
                doctorIcon.setBackground(originalBackground); // Reset doctor icon if needed
                myButton.setText("Register as a Parent >");
                myButton.setEnabled(true);
            }
        });

        doctorIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isParentSelected = false;
                doctorIcon.setBackground(borderDrawable); // Apply the border effect
                parentIcon.setBackground(originalBackground); // Reset parent icon if needed
                myButton.setText("Register as a Doctor >");
                myButton.setEnabled(true);
            }
        });

        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isParentSelected) {
                    // Launch LoginActivity for parent
                    Intent intent = new Intent(ChooseUserActivity.this, RegisterActivity.class);
                    startActivity(intent);
                } else {
                    // Launch DoctorRegisterActivity for doctor
                    Intent intent = new Intent(ChooseUserActivity.this, DoctorRegisterActivity.class);
                    startActivity(intent);
                }
            }
        });

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    // Launch DoctorRegisterActivity for doctor
                    Intent intent = new Intent(ChooseUserActivity.this, LoginActivity.class);
                    startActivity(intent);
            }
        });
    }
}
