package com.socialnavigator.utils;

import androidx.annotation.DrawableRes;

import com.socialnavigator.R;

/**
 * Маппинг "Тип заведения" (поле Type из API) → drawable-ресурс.
 */
public final class TypeImageMapper {

    private TypeImageMapper() {}

    @DrawableRes
    public static int getImageResByType(String type) {
        if (type == null) return R.drawable.photo_default;

        switch (type.trim()) {
            case "Океанариум":          return R.drawable.photo_oceanarium;
            case "Музей":               return R.drawable.photo_museum;
            case "Аквапарк":            return R.drawable.photo_aquapark;
            case "Зоопарк":             return R.drawable.photo_zoo;
            case "Театр":               return R.drawable.photo_theater;
            case "Кинотеатр":           return R.drawable.photo_cinema;
            case "Термальный комплекс": return R.drawable.photo_thermal;
            case "Котокафе":            return R.drawable.photo_catcafe;
            case "Бильярдный клуб":     return R.drawable.photo_billiards;
            case "Боулинг клуб":        return R.drawable.photo_bowling;
            case "Дельфинарий":         return R.drawable.photo_dolphinarium;
            case "Антикафе":            return R.drawable.photo_anticafe;
            // "Выставка" — нет своей картинки, используем default
            default:                    return R.drawable.photo_default;
        }
    }
}
