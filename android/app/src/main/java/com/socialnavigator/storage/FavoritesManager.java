package com.socialnavigator.storage;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * Менеджер избранного.
 * Хранит ID избранных скидок в SharedPreferences.
 */
public class FavoritesManager {

    private static final String PREFS_NAME = "favorites_prefs";
    private static final String KEY_FAVORITES = "favorite_ids";

    private static FavoritesManager instance;
    private final SharedPreferences prefs;

    private FavoritesManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized FavoritesManager getInstance(Context context) {
        if (instance == null) {
            instance = new FavoritesManager(context);
        }
        return instance;
    }

    /**
     * Добавить ID в избранное.
     */
    public void add(int id) {
        Set<String> ids = getIdsSet();
        ids.add(String.valueOf(id));
        saveIdsSet(ids);
    }

    /**
     * Удалить ID из избранного.
     */
    public void remove(int id) {
        Set<String> ids = getIdsSet();
        ids.remove(String.valueOf(id));
        saveIdsSet(ids);
    }

    /**
     * Проверить, в избранном ли скидка с указанным ID.
     */
    public boolean isFavorite(int id) {
        return getIdsSet().contains(String.valueOf(id));
    }

    /**
     * Получить все ID избранных скидок.
     */
    public Set<String> getAllIds() {
        return getIdsSet();
    }

    /**
     * Очистить избранное (пригодится на будущее).
     */
    public void clear() {
        prefs.edit().remove(KEY_FAVORITES).apply();
    }

    // --- Внутренние методы ---

    private Set<String> getIdsSet() {
        // Возвращаем копию, чтобы не менять объект в prefs напрямую
        return new HashSet<>(prefs.getStringSet(KEY_FAVORITES, new HashSet<>()));
    }

    private void saveIdsSet(Set<String> ids) {
        prefs.edit().putStringSet(KEY_FAVORITES, ids).apply();
    }
}
