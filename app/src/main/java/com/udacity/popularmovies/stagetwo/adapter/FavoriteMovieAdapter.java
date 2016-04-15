package com.udacity.popularmovies.stagetwo.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.popularmovies.stagetwo.R;
import com.udacity.popularmovies.stagetwo.data.MovieContract;

/**
 * Created by kunaljaggi on 4/12/16.
 */

public class FavoriteMovieAdapter extends CursorAdapter {


    public FavoriteMovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    /*
         we go straight from the cursor to the
        string.
     */
    private String convertCursorRowToUXFormat(Cursor cursor) {
        // get row indices for our cursor
        int idx_movie_title = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE);
//        int idx_min_temp = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP);
//        int idx_date = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE);
//        int idx_short_desc = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC);


        return cursor.getString(idx_movie_title);
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_favorite, parent, false);

        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.

        TextView tv = (TextView) view;
        tv.setText(convertCursorRowToUXFormat(cursor));
    }
}