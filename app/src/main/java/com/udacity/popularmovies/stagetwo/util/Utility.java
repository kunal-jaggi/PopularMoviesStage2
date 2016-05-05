package com.udacity.popularmovies.stagetwo.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.udacity.popularmovies.stagetwo.R;
import com.udacity.popularmovies.stagetwo.event.DiscoverMovieEvent;
import com.udacity.popularmovies.stagetwo.event.MovieEvent;
import com.udacity.popularmovies.stagetwo.event.MovieReviewsEvent;
import com.udacity.popularmovies.stagetwo.event.MovieTrailersEvent;
import com.udacity.popularmovies.stagetwo.event.ReviewEvent;
import com.udacity.popularmovies.stagetwo.event.TrailerEvent;
import com.udacity.popularmovies.stagetwo.network.model.MovieReview;
import com.udacity.popularmovies.stagetwo.network.model.Trailer;
import com.udacity.popularmovies.stagetwo.network.model.TrailerInfo;

import java.util.List;

/**
 * Created by kunaljaggi on 4/15/16.
 */
public class Utility {
    public static String getPreferredSortingCriteria(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sort_key),
                context.getString(R.string.pref_sort_order_default));

    }
    public static DiscoverMovieEvent produceDiscoverMovieEvent(String queryParam) {
        return new DiscoverMovieEvent(queryParam);
    }

    public static MovieEvent produceMovieEvent() {
        return new MovieEvent();
    }

    public static MovieReviewsEvent produceMovieReviewsEvent(final int movieId) {
        return new MovieReviewsEvent(movieId);
    }

    public static ReviewEvent produceReviewEvent(final List<MovieReview> mReviewList) {
        return new ReviewEvent(mReviewList);
    }

    public static MovieTrailersEvent produceMovieTrailersEvent(final int movieId) {
        return new MovieTrailersEvent(movieId);
    }

    public static TrailerEvent produceTrailerEvent(final List<Trailer> mTrailerList) {
        return new TrailerEvent(mTrailerList);
    }

}
