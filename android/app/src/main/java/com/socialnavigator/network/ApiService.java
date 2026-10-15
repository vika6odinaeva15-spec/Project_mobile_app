package com.socialnavigator.network;

import com.socialnavigator.models.Discount;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    /**
     * GET /api/promotions
     * Возвращает список всех скидок.
     */
    @GET("api/promotions")
    Call<List<Discount>> getPromotions();
}
