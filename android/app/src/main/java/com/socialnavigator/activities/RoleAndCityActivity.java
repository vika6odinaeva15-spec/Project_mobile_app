package com.socialnavigator.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.socialnavigator.R;

public class RoleAndCityActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "user_prefs";
    private static final String KEY_ROLE = "role";
    private static final String KEY_CITY = "city";

    private RadioGroup roleGroup;
    private Spinner citySpinner;
    private Button btnContinue;

    private final String[] cities = {
            "Москва", "Санкт-Петербург",
            "Новосибирск"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Если роль уже выбрана ранее — сразу на главный экран
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (prefs.contains(KEY_ROLE)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_role_city);

        roleGroup = findViewById(R.id.roleGroup);
        citySpinner = findViewById(R.id.citySpinner);
        btnContinue = findViewById(R.id.btnContinue);

        // Заполняем Spinner городами
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                cities
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(adapter);

        btnContinue.setOnClickListener(v -> saveAndContinue());
    }

    private void saveAndContinue() {
        String role;
        int checkedId = roleGroup.getCheckedRadioButtonId();
        if (checkedId == R.id.radioStudent) {
            role = "student";
        } else if (checkedId == R.id.radioPensioner) {
            role = "pensioner";
        } else {
            role = "svo";
        }

        String city = citySpinner.getSelectedItem().toString();

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit()
                .putString(KEY_ROLE, role)
                .putString(KEY_CITY, city)
                .apply();

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
