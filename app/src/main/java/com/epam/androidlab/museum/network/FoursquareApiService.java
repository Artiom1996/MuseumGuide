package com.epam.androidlab.museum.network;

import com.epam.androidlab.museum.model.MuseumDetailResponse;
import com.epam.androidlab.museum.model.MuseumListResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Artiom on 05.07.2017.
 */

public interface FoursquareApiService {

    @GET("/v2/venues/search")
    Call<MuseumListResponse> getMuseumList(
            @Query("client_id") String clientId,
            @Query("client_secret") String clientSecret,
            @Query("v") String version,
            @Query("ll") String ll,
            @Query("categoryId") String categoryId,
            @Query("query") String query,
            @Query("radius") String radius);

    @GET("/v2/venues/{venue_id}")
    Call<MuseumDetailResponse> getMuseumDetail(
            @Path("venue_id") String venueId,
            @Query("client_id") String clientId,
            @Query("client_secret") String clientSecret,
            @Query("v") String version);



}
