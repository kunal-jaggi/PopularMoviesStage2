package com.udacity.popularmovies.stagetwo.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.udacity.popularmovies.stagetwo.R;
import com.udacity.popularmovies.stagetwo.data.MovieContract;
import com.udacity.popularmovies.stagetwo.util.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This Adapter  exposes data from a Cursor to a GridView widget.
 * This class acts as a bridge between movie grid (a subclass of AdapterView)
 * and the underlying data for the movie Grid. The Adapter provides access to the data items.
 * This Adapter is also responsible for making a View for each item in the data set.
 * Created by kunaljaggi on 2/19/16.
 */

public class GalleryItemAdapter extends CursorAdapter {

    private static final String LOG_TAG = GalleryItemAdapter.class.getSimpleName();

    private Context mContext;
    @Bind(R.id.gallery_item_imageView)
    ImageView imageView;

    public GalleryItemAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.gallery_item, parent, false);
        ButterKnife.bind(this, view);
        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //Picasso.with(mContext).invalidate(Constants.MOVIE_DB_POSTER_URL + Constants.POSTER_PHONE_SIZE + convertCursorRowToUXFormat(cursor));
        Log.d(LOG_TAG, "Loading image... for movie ID: " + cursor.getInt(0) + " movie title: " + cursor.getString(1) + " poster path: " + cursor.getString(2));

        Picasso.with(mContext)
                .load(Constants.MOVIE_DB_POSTER_URL + Constants.POSTER_PHONE_SIZE + convertCursorRowToUXFormat(cursor))
                .placeholder(R.drawable.poster_placeholder) // support download placeholder
                .error(R.drawable.poster_placeholder_error) //support error placeholder, if back-end returns empty string or null
                .into(imageView);
    }

    /**
     * This method returns Poster URL (string) from the passed cursor.
     */
    private String convertCursorRowToUXFormat(Cursor cursor) {
        // get row indices for our cursor
        int idx_movie_poster_path = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH);
        Log.d(LOG_TAG, "Column Index: " + idx_movie_poster_path);
        return cursor.getString(idx_movie_poster_path);
    }
}