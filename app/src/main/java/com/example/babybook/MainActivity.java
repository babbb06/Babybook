package com.example.babybook;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.SharedPreferences;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String FIRST_TIME_KEY = "isFirstTime";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if it's the first time launching the app
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isFirstTime = preferences.getBoolean(FIRST_TIME_KEY, true);

        if (isFirstTime) {
            // First time launching, set up the view pager
            setContentView(R.layout.activity_main);

            // Save that the app has been launched for the first time
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(FIRST_TIME_KEY, false);
            editor.apply();

            // Set up ViewPager2
            ViewPager2 viewPager = findViewById(R.id.viewPager);
            SplashPagerAdapter adapter = new SplashPagerAdapter(this);
            viewPager.setAdapter(adapter);
        } else {
            // Not the first time, redirect to login screen
            redirectToLogin();
        }
    }

    // Redirect to LoginActivity
    private void redirectToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Close MainActivity
    }

    // Adapter for ViewPager2
    private static class SplashPagerAdapter extends FragmentStateAdapter {
        public SplashPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new Splash1Fragment();
                case 1:
                    return new Splash2Fragment();
                default:
                    return new MainFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 3; // Three fragments: splash1, splash2, main
        }
    }
}
