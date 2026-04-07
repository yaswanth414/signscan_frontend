package com.example.sign_scan_.network;

import android.content.Context;
import com.example.sign_scan_.SessionManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // Android Emulator → Django localhost
    private static final String BASE_URL = "http://10.0.2.2:8000/api/";

    private static Retrofit retrofit;
    private static Retrofit authenticatedRetrofit;

    // Private constructor to prevent object creation
    private RetrofitClient() {
    }

    public static Retrofit getClient() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }

    // New method for authenticated requests
    public static ApiService getAuthenticatedApiService(Context context) {
        if (context == null) return getApiService();

        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getAccessToken();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request.Builder requestBuilder = original.newBuilder();
                    
                    if (token != null) {
                        requestBuilder.header("Authorization", "Bearer " + token);
                    }
                    
                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                })
                .build();

        Retrofit authRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return authRetrofit.create(ApiService.class);
    }
}
