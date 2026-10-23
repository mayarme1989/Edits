package com.example.edits;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        // Find BottomNavigationView from XML layout
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set listener for BottomNavigationView
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    showFragment(new HomeFragment());
                    return true;
                } else if (id == R.id.nav_add) {
                    showFragment(new AddFragment());
                    return true;
                } else if (id == R.id.nav_show) {
                    showFragment(new ShowFragment());
                    return true;
                }
                return false;
            });
        }

        // Load the initial fragment when app opens (only on first start)
        if (savedInstanceState == null) {
            showFragment(new HomeFragment());
        }
    }

    // Helper method to perform the fragment transaction
    public void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}