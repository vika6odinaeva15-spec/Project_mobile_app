package com.socialnavigator.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.yandex.runtime.image.ImageProvider;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.socialnavigator.BuildConfig;
import com.socialnavigator.R;
import com.socialnavigator.models.Discount;
import com.socialnavigator.storage.DiscountRepository;
import com.socialnavigator.storage.FavoritesManager;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.mapview.MapView;

public class DetailActivity extends AppCompatActivity {

    private Discount discount;
    private FavoritesManager favoritesManager;
    private Button btnToggleFavorite;

    // Yandex MapKit
    private MapView mapView;
    private static boolean mapKitInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Инициализация MapKit (один раз за жизнь приложения)
        if (!mapKitInitialized) {
            MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY);
            MapKitFactory.initialize(this);
            mapKitInitialized = true;
        }

        setContentView(R.layout.activity_detail);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarDetail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Акция");
        }

        favoritesManager = FavoritesManager.getInstance(this);

        int discountId = getIntent().getIntExtra("discount_id", -1);
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
        TextView tvAddress = findViewById(R.id.tvDetailAddress);
        TextView tvExpires = findViewById(R.id.tvDetailExpires);
        TextView tvWebsite = findViewById(R.id.tvDetailWebsite);
        LinearLayout layoutWebsite = findViewById(R.id.layoutWebsite);
        btnToggleFavorite = findViewById(R.id.btnToggleFavorite);
        Button btnOpenWebsite = findViewById(R.id.btnOpenWebsite);
        Button btnShare = findViewById(R.id.btnShare);
        mapView = findViewById(R.id.mapview);

        // Заполняем данными
        tvTitle.setText(discount.getName());
        tvCategory.setText(discount.getType());
        tvDescription.setText(discount.getPromotional());
        tvAddress.setText(discount.getAddress());
        tvExpires.setText(discount.getTimeOfApplication());

        // === Yandex MapKit ===
        setupMap();

        // Секция сайта
        String website = discount.getLinkToWebsite();
        if (website != null && !website.isEmpty()) {
            tvWebsite.setText(website);
            layoutWebsite.setVisibility(View.VISIBLE);
            btnOpenWebsite.setVisibility(View.VISIBLE);

            btnOpenWebsite.setOnClickListener(v -> {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(website)));
                } catch (Exception e) {
                    Toast.makeText(this, "Не удалось открыть сайт", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            layoutWebsite.setVisibility(View.GONE);
            btnOpenWebsite.setVisibility(View.GONE);
        }

        // Кнопка "В избранное"
        updateFavoriteButton();
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

    private void setupMap() {
        Map map = mapView.getMapWindow().getMap();

        Point point = new Point(discount.getLatitude(), discount.getLongitude());

        // Создаём маркер
        PlacemarkMapObject placemark = map.getMapObjects().addPlacemark(point);

        // Прозрачность 90% (как в примере)
        placemark.setOpacity(0.9f);

        // Кастомная иконка (mark.png)
        placemark.setIcon(ImageProvider.fromResource(this, R.drawable.mark));

        // Обработчик тапа
        placemark.addTapListener(markerTapListener);

        // Камера к точке
        map.move(
                new CameraPosition(point, 15.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0.5f)
        );
    }
    /**
     * Тап по маркеру → внешнее приложение карт.
     */
    private final MapObjectTapListener markerTapListener = new MapObjectTapListener() {
        @Override
        public boolean onMapObjectTap(MapObject mapObject, Point point) {
            String uri = "geo:" + discount.getLatitude() + "," + discount.getLongitude() +
                    "?q=" + Uri.encode(discount.getAddress());
            try {
                startActivity(Intent.createChooser(
                        new Intent(Intent.ACTION_VIEW, Uri.parse(uri)),
                        "Открыть в приложении карт"));
            } catch (Exception e) {
                Toast.makeText(DetailActivity.this,
                        "Приложение карт не найдено", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    };

    private void updateFavoriteButton() {
        if (favoritesManager.isFavorite(discount.getId())) {
            btnToggleFavorite.setText("Убрать из избранного");
        } else {
            btnToggleFavorite.setText("Добавить в избранное");
        }
    }

    // === Жизненный цикл MapKit ===

    @Override
    protected void onStart() {
        super.onStart();
        if (mapView != null) mapView.onStart();
        MapKitFactory.getInstance().onStart();
    }

    @Override
    protected void onStop() {
        if (mapView != null) mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}