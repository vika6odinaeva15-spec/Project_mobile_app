package com.socialnavigator.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // ⚠️ Базовый URL — со слешем на конце!
    private static final String BASE_URL = "https://social-navigator.onrender.com/";

    private static Retrofit retrofit = null;
    private static ApiService apiService = null;

    /**
     * Получить экземпляр ApiService (Singleton).
     */
    public static ApiService getApiService() {
        if (apiService == null) {
            apiService = getRetrofit().create(ApiService.class);
        }
        return apiService;
    }

    /**
     * Создать Retrofit с настройками.
     */
    private static Retrofit getRetrofit() {
        if (retrofit == null) {
            // Логирование запросов и ответов (для отладки)
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // OkHttp — с таймаутами (Render может «засыпать»)
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .connectTimeout(30, TimeUnit.SECONDS)   // подключение
                    .readTimeout(30, TimeUnit.SECONDS)      // чтение
                    .writeTimeout(30, TimeUnit.SECONDS)     // запись
                    .build();

            // Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
