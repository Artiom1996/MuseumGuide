package com.epam.androidlab.museum.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Artiom on 05.07.2017.
 */

public class FoursquareServiceHelper {

    public static final String FOURSQUARE_CLIENT_ID = "SO1LWIXK2P0EENHP2SIYRGPKA1YMKCU1ECMHZHRRDZ5GJAK5";
    public static final String FOURSQUARE_CLIENT_SECRET = "UO23H1JP1NUJIRQPY5O5B3BVTL2NMA40NO0WCJX2WSIGTLSU";
    public static final String FOURSQUARE_API_VERSION = "20170614";
    public static final String MUSEUM_CATEGORY_ID = "4bf58dd8d48988d181941735";

    private static final String ROOT_URL = "https://api.foursquare.com/";

    private static Retrofit getRetrofitInstance() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        return new Retrofit.Builder()
                .baseUrl(ROOT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    public static FoursquareApiService getApiService() {
        return getRetrofitInstance().create(FoursquareApiService.class);
    }
}
