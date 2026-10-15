package com.socialnavigator.activities;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;
import com.socialnavigator.utils.TypeImageMapper;

import androidx.annotation.NonNull;
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
import com.yandex.runtime.image.ImageProvider;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "MAP_DEBUG";

    private Discount discount;
    private FavoritesManager favoritesManager;
    private Button btnToggleFavorite;

    // Yandex MapKit
    private MapView mapView;
    private static boolean mapKitInitialized = false;

    // Флаг: карта готова принимать команды (onStart вызван)
    private boolean mapReady = false;
    // Отложенные координаты, если updateMap вызван до onStart
    private boolean hasPendingCoords = false;
    private double pendingLat = 0.0;
    private double pendingLng = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "1. START");

        // Инициализация MapKit (один раз за жизнь приложения)
        if (!mapKitInitialized) {
            MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY);
            MapKitFactory.initialize(this);
            mapKitInitialized = true;
        }
        Log.d(TAG, "2. MapKit init OK");

        setContentView(R.layout.activity_detail);
        Log.d(TAG, "3. setContentView OK");

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarDetail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Акция");
        }
        Log.d(TAG, "4. toolbar OK");

        favoritesManager = FavoritesManager.getInstance(this);

        int discountId = getIntent().getIntExtra("discount_id", -1);
        discount = DiscountRepository.getInstance().getById(discountId);

        if (discount == null) {
            Toast.makeText(this, "Скидка не найдена", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Log.d(TAG, "5. discount=" + discount.getName()
                + ", lat=" + discount.getLatitude()
                + ", lng=" + discount.getLongitude());

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
        Log.d(TAG, "6. findViewById OK, mapView=" + (mapView != null));

        // Заполняем данными
        tvTitle.setText(discount.getName());
        tvCategory.setText(discount.getType());
        tvDescription.setText(discount.getPromotional());
        tvAddress.setText(discount.getAddress());
        tvExpires.setText(discount.getTimeOfApplication());
        ImageView ivDetailImage = findViewById(R.id.ivDetailImage);
        ivDetailImage.setImageResource(
                TypeImageMapper.getImageResByType(discount.getType())
        );
        Log.d(TAG, "7. setText OK, перед geocodeAddress");

        // === Yandex MapKit: Geocoder + fallback ===
        geocodeAddress(discount.getAddress(),
                discount.getLatitude(),
                discount.getLongitude());
        Log.d(TAG, "8. после geocodeAddress");

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

        Log.d(TAG, "9. onCreate FINISHED");
    }

    /**
     * Получить координаты по адресу через Android Geocoder.
     * Если не удалось — использовать координаты города (fallback).
     */
    private void geocodeAddress(String address, double fallbackLat, double fallbackLng) {
        if (address == null || address.isEmpty()) {
            Log.d("GEO", "Адрес пустой → fallback");
            updateMap(fallbackLat, fallbackLng);
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            geocoder.getFromLocationName(address, 1, new Geocoder.GeocodeListener() {
                @Override
                public void onGeocode(@NonNull List<Address> addresses) {
                    if (!addresses.isEmpty()) {
                        Address addr = addresses.get(0);
                        Log.d("GEO", "Точные координаты: " + addr.getLatitude() + ", " + addr.getLongitude());
                        updateMap(addr.getLatitude(), addr.getLongitude());
                    } else {
                        Log.d("GEO", "Geocoder не нашёл → fallback");
                        updateMap(fallbackLat, fallbackLng);
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    Log.e("GEO", "Ошибка: " + errorMessage + " → fallback");
                    updateMap(fallbackLat, fallbackLng);
                }
            });
        } else {
            new Thread(() -> {
                try {
                    Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocationName(address, 1);
                    if (addresses != null && !addresses.isEmpty()) {
                        Address addr = addresses.get(0);
                        Log.d("GEO", "Точные координаты: " + addr.getLatitude() + ", " + addr.getLongitude());
                        runOnUiThread(() -> updateMap(addr.getLatitude(), addr.getLongitude()));
                    } else {
                        Log.d("GEO", "Geocoder не нашёл → fallback");
                        runOnUiThread(() -> updateMap(fallbackLat, fallbackLng));
                    }
                } catch (IOException e) {
                    Log.e("GEO", "IOException: " + e.getMessage() + " → fallback");
                    runOnUiThread(() -> updateMap(fallbackLat, fallbackLng));
                }
            }).start();
        }
    }

    /**
     * Обновить карту: камера + маркер на координатах.
     * Безопасно вызывается как до onStart (тогда откладывается), так и после.
     */
    private void updateMap(double lat, double lng) {
        if (mapView == null) {
            Log.w(TAG, "updateMap: mapView == null, пропускаем");
            return;
        }

        // Если карта ещё не готова — запоминаем координаты и выйдем
        if (!mapReady) {
            Log.d(TAG, "updateMap: mapReady=false, откладываем lat=" + lat + ", lng=" + lng);
            pendingLat = lat;
            pendingLng = lng;
            hasPendingCoords = true;
            return;
        }

        // Гарантируем работу в UI-потоке
        mapView.post(() -> {
            try {
                // Защита от (0,0)
                double safeLat = lat;
                double safeLng = lng;
                if (safeLat == 0.0 && safeLng == 0.0) {
                    Log.w(TAG, "Координаты (0,0)! Используем Москву.");
                    safeLat = 55.7558;
                    safeLng = 37.6173;
                }

                Map map = mapView.getMapWindow().getMap();
                if (map == null) {
                    Log.w(TAG, "updateMap: map == null");
                    return;
                }

                Point point = new Point(safeLat, safeLng);

                map.getMapObjects().clear();

                PlacemarkMapObject placemark = map.getMapObjects().addPlacemark(point);
                placemark.setOpacity(0.9f);
                placemark.setIcon(ImageProvider.fromResource(this, R.drawable.mark));
                placemark.addTapListener(markerTapListener);

                map.move(
                        new CameraPosition(point, 15.0f, 0.0f, 0.0f),
                        new Animation(Animation.Type.SMOOTH, 0.5f)
                );

                Log.d(TAG, "updateMap: маркер поставлен на " + safeLat + ", " + safeLng);
            } catch (Exception e) {
                Log.e(TAG, "updateMap: исключение", e);
            }
        });
    }

    /**
     * Тап по маркеру → внешнее приложение карт.
     */
    private final MapObjectTapListener markerTapListener = new MapObjectTapListener() {
        @Override
        public boolean onMapObjectTap(MapObject mapObject, Point point) {
            // Используем точку, на которую тапнули (она всегда валидна),
            // а не discount.getLatitude() — он может быть 0.
            double lat = point.getLatitude();
            double lng = point.getLongitude();
            String query = discount != null && discount.getAddress() != null
                    ? Uri.encode(discount.getAddress())
                    : "";

            String uri = "geo:" + lat + "," + lng + "?q=" + lat + "," + lng
                    + (query.isEmpty() ? "" : "(" + query + ")");

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
        mapReady = true;

        // Если координаты пришли до onStart — применяем их сейчас
        if (hasPendingCoords) {
            Log.d(TAG, "onStart: применяем отложенные координаты "
                    + pendingLat + ", " + pendingLng);
            updateMap(pendingLat, pendingLng);
            hasPendingCoords = false;
        }
    }

    @Override
    protected void onStop() {
        mapReady = false;
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