package com.socialnavigator.storage;

import androidx.annotation.NonNull;
import java.util.HashSet;
import java.util.Set;
import java.util.Collections;

import com.socialnavigator.models.Discount;
import com.socialnavigator.network.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Репозиторий скидок.
 * Загружает данные с API и кэширует в памяти.
 */
public class DiscountRepository {

    private static DiscountRepository instance;
    private final List<Discount> discounts = new ArrayList<>();

    // Callback для уведомления UI о результате загрузки
    public interface LoadCallback {
        void onSuccess(List<Discount> result);
        void onError(String message);
    }

    private DiscountRepository() {
    }

    public static synchronized DiscountRepository getInstance() {
        if (instance == null) {
            instance = new DiscountRepository();
        }
        return instance;
    }

    /**
     * Загрузить скидки с API.
     * @param callback — уведомление о результате (успех/ошибка).
     */
    public void loadFromApi(@NonNull LoadCallback callback) {
        ApiClient.getApiService()
                .getPromotions()
                .enqueue(new Callback<List<Discount>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Discount>> call,
                                           @NonNull Response<List<Discount>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Discount> deduped = deduplicate(response.body());
                            fillCoordinatesByCity(deduped);   // ← ДОБАВИЛИ fallback
                            discounts.clear();
                            discounts.addAll(deduped);
                            callback.onSuccess(discounts);
                        } else {
                            callback.onError("Ошибка сервера: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Discount>> call,
                                          @NonNull Throwable t) {
                        callback.onError("Ошибка сети: " + t.getMessage());
                    }
                });
    }

    /**
     * Получить все загруженные скидки (из кэша).
     */
    public List<Discount> getAll() {
        return discounts;
    }

    /**
     * Получить скидку по ID.
     */
    public Discount getById(int id) {
        for (Discount discount : discounts) {
            if (discount.getId() == id) {
                return discount;
            }
        }
        return null;
    }

    /**
     * Очистить кэш (пригодится при смене города/категории).
     */
    public void clear() {
        discounts.clear();
    }
    /**
     * Получить отфильтрованный список скидок.
     */
    public List<Discount> getFiltered(String city, String category, List<String> types) {
        List<Discount> result = new ArrayList<>();

        for (Discount d : discounts) {
            // Фильтр по городу
            if (city != null && !city.isEmpty() && !city.equals(d.getCity())) {
                continue;
            }
            // Фильтр по категории
            if (category != null && !category.isEmpty() && !category.equals(d.getCategory())) {
                continue;
            }
            // Фильтр по типам (множественный)
            if (types != null && !types.isEmpty()) {
                if (d.getType() == null || !types.contains(d.getType())) {
                    continue;
                }
            }
            result.add(d);
        }
        return result;
    }
    /**
     * Удалить дубликаты из списка.
     * Уникальный ключ = Name + City + Category + Type.
     */
    private List<Discount> deduplicate(List<Discount> input) {
        List<Discount> result = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        for (Discount d : input) {
            String key = d.getName() + "|" + d.getCity() + "|" +
                    d.getCategory() + "|" + d.getType();

            if (seen.add(key)) {
                result.add(d);
            }
        }
        return result;
    }
    /**
     * Получить список уникальных типов заведений.
     * Используется для фильтра.
     */
    public List<String> getAllTypes() {
        Set<String> typesSet = new HashSet<>();
        for (Discount d : discounts) {
            if (d.getType() != null && !d.getType().isEmpty()) {
                typesSet.add(d.getType());
            }
        }
        List<String> result = new ArrayList<>(typesSet);
        Collections.sort(result);
        return result;
    }
    /**
     * Заполнить координаты скидок по городу.
     * Используется как fallback, если точные координаты неизвестны.
     */
    public void fillCoordinatesByCity(List<Discount> list) {
        for (Discount d : list) {
            if (d.getCity() == null) continue;

            switch (d.getCity()) {
                case "Москва":
                    d.setLatitude(55.7558);
                    d.setLongitude(37.6173);
                    break;
                case "Санкт-Петербург":
                    d.setLatitude(59.9343);
                    d.setLongitude(30.3351);
                    break;
                case "Новосибирск":
                    d.setLatitude(55.0084);
                    d.setLongitude(82.9357);
                    break;
                case "Екатеринбург":
                    d.setLatitude(56.8389);
                    d.setLongitude(60.6057);
                    break;
                case "Казань":
                    d.setLatitude(55.8304);
                    d.setLongitude(49.0661);
                    break;
                case "Нижний Новгород":
                    d.setLatitude(56.3269);
                    d.setLongitude(44.0059);
                    break;
                default:
                    d.setLatitude(55.7558);
                    d.setLongitude(37.6173);
                    break;
            }
        }
    }
}