package com.socialnavigator.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.socialnavigator.R;
import com.socialnavigator.models.Discount;
import com.socialnavigator.storage.DiscountRepository;
import com.socialnavigator.storage.FavoritesManager;

public class DetailActivity extends AppCompatActivity {

    private Discount discount;
    private FavoritesManager favoritesManager;
    private Button btnToggleFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Получаем менеджер избранного
        favoritesManager = FavoritesManager.getInstance(this);

        // Получаем ID скидки из Intent
        int discountId = getIntent().getIntExtra("discount_id", -1);

        // Ищем скидку в репозитории
        discount = DiscountRepository.getInstance().getById(discountId);

        if (discount == null) {
            Toast.makeText(this, "Скидка не найдена", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Находим View
        TextView tvTitle = findViewById(R.id.tvDetailTitle);
        TextView tvCategory = findViewById(R.id.tvDetailCategory);
        TextView tvDescription = findViewById(R.id.tvDetailDescription);
        TextView tvExpires = findViewById(R.id.tvDetailExpires);
        btnToggleFavorite = findViewById(R.id.btnToggleFavorite);
        Button btnShare = findViewById(R.id.btnShare);

        // Заполняем данными
        tvTitle.setText(discount.getName());
        tvCategory.setText(discount.getType() + " • " + discount.getAddress());
        tvDescription.setText(discount.getPromotional());
        tvExpires.setText("🕐 " + discount.getTimeOfApplication());

        // Обновляем состояние кнопки избранного
        updateFavoriteButton();

        // Клик по кнопке "В избранное / Убрать"
        btnToggleFavorite.setOnClickListener(v -> {
            int id = discount.getId();
            if (favoritesManager.isFavorite(id)) {
                favoritesManager.remove(id);
                Toast.makeText(this, "Удалено из избранного", Toast.LENGTH_SHORT).show();
            } else {
                favoritesManager.add(id);
                Toast.makeText(this, "Добавлено в избранное", Toast.LENGTH_SHORT).show();
            }
            updateFavoriteButton();
        });

        // Кнопка "Поделиться"
        btnShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    discount.getName() + "\n" + discount.getPromotional() +
                            "\n" + discount.getAddress());
            startActivity(Intent.createChooser(shareIntent, "Поделиться"));
        });
    }

    /**
     * Обновляет текст кнопки в зависимости от статуса избранного.
     */
    private void updateFavoriteButton() {
        if (favoritesManager.isFavorite(discount.getId())) {
            btnToggleFavorite.setText("Убрать из избранного");
        } else {
            btnToggleFavorite.setText("Добавить в избранное");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}