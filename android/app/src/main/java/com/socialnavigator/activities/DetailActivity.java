package com.socialnavigator.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.socialnavigator.R;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Кнопка "Назад" в ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Получаем id скидки из Intent
        int discountId = getIntent().getIntExtra("discount_id", -1);

        TextView tvTitle = findViewById(R.id.tvDetailTitle);
        TextView tvCategory = findViewById(R.id.tvDetailCategory);
        TextView tvDescription = findViewById(R.id.tvDetailDescription);
        TextView tvExpires = findViewById(R.id.tvDetailExpires);
        Button btnToggleFavorite = findViewById(R.id.btnToggleFavorite);
        Button btnShare = findViewById(R.id.btnShare);

        // Пока заглушка — заменим на реальные данные в Этапе 5
        tvTitle.setText("Акция #" + discountId);
        tvCategory.setText("Категория: student • Москва");
        tvDescription.setText("Здесь будет подробное описание акции. Пока это заглушка.");
        tvExpires.setText("Действует до: 31.12.2025");

        btnToggleFavorite.setOnClickListener(v ->
                // TODO: реализуем в Этапе 4
                btnToggleFavorite.setText("В избранном"));

        btnShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    "Посмотри эту акцию в Social Navigator: #" + discountId);
            startActivity(Intent.createChooser(shareIntent, "Поделиться"));
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}