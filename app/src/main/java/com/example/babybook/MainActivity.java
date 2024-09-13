package com.example.babybook;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.SharedPreferences;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    //Fresh Install Only

    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String FIRST_TIME_KEY = "isFirstTime";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if it's the first time launching the app
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isFirstTime = preferences.getBoolean(FIRST_TIME_KEY, true);

        if (isFirstTime) {
            // First time launching, show splash screen (MainActivity layout)
            setContentView(R.layout.activity_main); // The splash screen layout

            // Save that the app has been launched for the first time
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(FIRST_TIME_KEY, false);
            editor.apply();

            // Allow user to stay on this screen until they interact (no automatic redirection)
        } else {
            // Not the first time, redirect to login screen
            redirectToLogin();
        }


        setContentView(R.layout.activity_main);

        Button gettingStartedButton = findViewById(R.id.my_button);
        gettingStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChooseUserActivity.class);
                startActivity(intent);
            }
        });
    }

    // Redirect to LoginActivity
    private void redirectToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Close MainActivity
    }
}