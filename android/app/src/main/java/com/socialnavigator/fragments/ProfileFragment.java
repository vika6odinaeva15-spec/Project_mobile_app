package com.socialnavigator.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.socialnavigator.R;
import com.socialnavigator.activities.RoleAndCityActivity;

public class ProfileFragment extends Fragment {

    private static final String PREFS_NAME = "user_prefs";
    private static final String KEY_ROLE = "role";
    private static final String KEY_CITY = "city";

    private TextView tvCity;
    private TextView tvCategory;
    private Button btnEdit;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvCity = view.findViewById(R.id.tvProfileCity);
        tvCategory = view.findViewById(R.id.tvProfileCategory);
        btnEdit = view.findViewById(R.id.btnEditProfile);

        btnEdit.setOnClickListener(v -> onEditClicked());
    }

    @Override
    public void onResume() {
        super.onResume();
        // Обновляем данные при каждом возврате на фрагмент
        loadUserData();
    }

    /**
     * Загружает данные пользователя из SharedPreferences.
     */
    private void loadUserData() {
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        String city = prefs.getString(KEY_CITY, "не выбран");
        String role = prefs.getString(KEY_ROLE, "");

        tvCity.setText(city);
        tvCategory.setText(roleToHuman(role));
    }

    /**
     * Преобразует код роли в читаемый текст.
     */
    private String roleToHuman(String role) {
        if (role == null || role.isEmpty()) {
            return "не выбрана";
        }
        switch (role) {
            case "student":
                return "Студент";
            case "pensioner":
                return "Пенсионер";
            case "svo":
                return "Участник СВО";
            default:
                return role;
        }
    }

    /**
     * Обработка клика на кнопку "Изменить".
     * Очищает SharedPreferences и открывает RoleAndCityActivity заново.
     */
    private void onEditClicked() {
        // 1. Очищаем сохранённые настройки
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();

        // 2. Открываем экран выбора роли и города,
        //    очищая стек Activity (чтобы "Назад" не возвращал на профиль)
        Intent intent = new Intent(requireActivity(), RoleAndCityActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        // 3. Закрываем MainActivity
        requireActivity().finish();
    }
}