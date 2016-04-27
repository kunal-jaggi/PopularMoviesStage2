package com.udacity.popularmovies.stagetwo.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.udacity.popularmovies.stagetwo.R;
import com.udacity.popularmovies.stagetwo.event.DiscoverMovieEvent;
import com.udacity.popularmovies.stagetwo.event.MovieEvent;

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
}
