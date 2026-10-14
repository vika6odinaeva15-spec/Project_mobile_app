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

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        int discountId = getIntent().getIntExtra("discount_id", -1);

        Discount discount = DiscountRepository.getInstance().getById(discountId);

        if (discount == null) {
            Toast.makeText(this, "Скидка не найдена", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView tvTitle = findViewById(R.id.tvDetailTitle);
        TextView tvCategory = findViewById(R.id.tvDetailCategory);
        TextView tvDescription = findViewById(R.id.tvDetailDescription);
        TextView tvExpires = findViewById(R.id.tvDetailExpires);
        Button btnToggleFavorite = findViewById(R.id.btnToggleFavorite);
        Button btnShare = findViewById(R.id.btnShare);

        tvTitle.setText(discount.getName());
        tvCategory.setText(discount.getType() + " • " + discount.getAddress());
        tvDescription.setText(discount.getPromotional());
        tvExpires.setText("🕐 " + discount.getTimeOfApplication());

        btnToggleFavorite.setOnClickListener(v ->
                Toast.makeText(this, "Избранное — в Этапе 4", Toast.LENGTH_SHORT).show());

        btnShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    discount.getName() + "\n" + discount.getPromotional() +
                            "\n" + discount.getAddress());
            startActivity(Intent.createChooser(shareIntent, "Поделиться"));
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}