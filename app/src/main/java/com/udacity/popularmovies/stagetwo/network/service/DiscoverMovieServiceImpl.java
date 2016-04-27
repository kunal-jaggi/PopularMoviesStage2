package com.udacity.popularmovies.stagetwo.network.service;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.udacity.popularmovies.stagetwo.R;
import com.udacity.popularmovies.stagetwo.data.MovieContract;
import com.udacity.popularmovies.stagetwo.event.DiscoverMovieEvent;
import com.udacity.popularmovies.stagetwo.event.MovieEvent;
import com.udacity.popularmovies.stagetwo.network.model.Movie;
import com.udacity.popularmovies.stagetwo.network.model.MovieInfo;
import com.udacity.popularmovies.stagetwo.singleton.PopularMoviesApplication;
import com.udacity.popularmovies.stagetwo.util.Constants;
import com.udacity.popularmovies.stagetwo.util.Utility;

import java.util.List;
import java.util.Vector;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * This class implements API interface to send out network requests to Movie DB REST endpoint.
 * Created by kunaljaggi on 2/18/16.
 */
public class DiscoverMovieServiceImpl {

    private Bus mEventBus;
    private List<Movie> mMovieList;
    private Context mContext;

    private static final String LOG_TAG = DiscoverMovieServiceImpl.class.getSimpleName();
    private static final String LOG_REST = "REST";

    public DiscoverMovieServiceImpl(Bus eventBus) {
        mEventBus = eventBus;
        eventBus.register(this);
    }

    public DiscoverMovieServiceImpl() {
        PopularMoviesApplication.getEventBus().register(this);
    }

    public DiscoverMovieServiceImpl(Context context) {
        PopularMoviesApplication.getEventBus().register(this);
        this.mContext= context;
    }

    /**
     * Used to make a async call to movies DB to fetch a list of popular movies.
     *
     * @param event
     */
    @Subscribe
    public void onDiscoverMovieEvent(DiscoverMovieEvent event) {

        Retrofit client = new Retrofit.Builder()
                .baseUrl(Constants.MOVIE_DB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DiscoverMovieService api = client.create(DiscoverMovieService.class);

        Call<MovieInfo> restCall = api.getPopularMovies(event.getmSortBy(), Constants.MOVIE_DB_API_KEY);

        Log.d(LOG_REST, "Making REST call to fetch movies. Sort criteria: "+event.getmSortBy());
        restCall.enqueue(new Callback<MovieInfo>() {
            @Override
            public void onResponse(Response<MovieInfo> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    // request successful (status code 200, 201)
                    MovieInfo movieInfo = response.body();
                    mMovieList = movieInfo.getmMovieList();
                    insertMoveRecords(mMovieList);
                    PopularMoviesApplication.getEventBus().post(Utility.produceMovieEvent());
                } else {
                    //request not successful (like 400,401,403 etc)
                    //Handle errors
                    Log.d(LOG_TAG, "Web call error");
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }


    private void insertMoveRecords(final List<Movie> movieList){
        // Insert the new weather information into the database
        Vector<ContentValues> cVVector = new Vector<ContentValues>(movieList.size());
        String sortCriteria= Utility.getPreferredSortingCriteria(mContext);

        for(Movie movie: movieList){

            ContentValues movieValues = new ContentValues();
            movieValues.put(MovieContract.MovieEntry._ID, movie.getmId());
            movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getmTitle());
            movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getmVoteAverage());
            movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE,movie.getmReleaseDate());
            movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getmOverview());
            movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.getmPosterPath());

            if(sortCriteria.equalsIgnoreCase(mContext.getString(R.string.pref_sort_by_popular))){
                movieValues.put(MovieContract.MovieEntry.COLUMN_IS_POPULAR, 1);   // SQLite does not have a separate Boolean storage class.
            }else if (sortCriteria.equalsIgnoreCase(mContext.getString(R.string.pref_sort_by_rating))){
                movieValues.put(MovieContract.MovieEntry.COLUMN_IS_RATED, 1);     // Instead, Boolean values are stored as integers 0 (false) and 1 (true).
            }
            cVVector.add(movieValues);

        }

        int inserted = 0;
        // add to database
        if ( cVVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            inserted = mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
        }
        Log.d(LOG_TAG, "DiscoverMovieServiceImpl Complete. " + inserted + " Inserted");
    }
}
