package com.socialnavigator.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.socialnavigator.R;
import com.socialnavigator.fragments.FavoritesFragment;
import com.socialnavigator.fragments.HomeFragment;
import com.socialnavigator.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Проверка: если пользователь ещё не выбрал роль — редирект на выбор
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        if (!prefs.contains("role")) {
            startActivity(new Intent(this, RoleAndCityActivity.class));
            finish();
            return;
        }

        bottomNavigation = findViewById(R.id.bottom_navigation);

        // Показываем HomeFragment при первом запуске
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        // Обработка кликов по вкладкам
        bottomNavigation.setOnItemSelectedListener(this::onNavItemSelected);
    }

    private boolean onNavItemSelected(@NonNull MenuItem item) {
        Fragment fragment;

        int itemId = item.getItemId();
        if (itemId == R.id.nav_home) {
            fragment = new HomeFragment();
        } else if (itemId == R.id.nav_favorites) {
            fragment = new FavoritesFragment();
        } else if (itemId == R.id.nav_profile) {
            fragment = new ProfileFragment();
        } else {
            return false;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();

        return true;
    }
}