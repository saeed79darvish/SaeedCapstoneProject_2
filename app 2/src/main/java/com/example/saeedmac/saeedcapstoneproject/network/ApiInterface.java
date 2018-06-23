package com.example.saeedmac.saeedcapstoneproject.network;


import com.example.saeedmac.saeedcapstoneproject.model.MoviesResponse;
import com.example.saeedmac.saeedcapstoneproject.model.SingleMovie;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("movie/top_rated")
    Call<MoviesResponse> getTopRatedMovies(@Query("api_key") String apiKey);

    @GET("movie/popular")
    Call<MoviesResponse> getPopularMovies(@Query("api_key") String apiKey);

    @GET("movie/{id}")
    Call<SingleMovie> getMovieDetails(@Path("id") Long id, @Query("api_key") String apiKey, @Query("append_to_response") String params);

}
