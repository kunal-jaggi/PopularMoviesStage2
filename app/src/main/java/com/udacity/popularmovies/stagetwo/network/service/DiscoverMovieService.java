package com.udacity.popularmovies.stagetwo.network.service;

import com.udacity.popularmovies.stagetwo.network.model.MovieInfo;
import com.udacity.popularmovies.stagetwo.network.model.ReviewInfo;
import com.udacity.popularmovies.stagetwo.network.model.TrailerInfo;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;


/**
 * Defines the REST API for Retrofit to access the movie DB API.
 * Created by kunaljaggi on 2/17/16.
 */
public interface DiscoverMovieService {

    @GET("/3/discover/movie")
    public Call<MovieInfo> getPopularMovies(@Query("sort_by") String sortBy, @Query("api_key") String apiKey);

    @GET("/3/movie/{id}/reviews")
    public Call<ReviewInfo> getReviews(@Path("id") int movieId, @Query("api_key") String apiKey);

    @GET("/3/movie/{id}/videos")
    public Call<TrailerInfo> getTrailers(@Path("id") int movieId, @Query("api_key") String apiKey);

}
