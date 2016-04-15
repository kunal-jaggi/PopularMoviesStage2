package com.udacity.popularmovies.stagetwo.view;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.popularmovies.stagetwo.R;
import com.udacity.popularmovies.stagetwo.adapter.FavoriteMovieAdapter;
import com.udacity.popularmovies.stagetwo.data.MovieContract;
import com.udacity.popularmovies.stagetwo.network.model.Movie;
import com.udacity.popularmovies.stagetwo.util.Constants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * This Fragment class is added by FavoriteActivity to show saved movies.
 * Created by kunaljaggi on 2/20/16.
 */
public class FavoriteFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = FavoriteFragment.class.getSimpleName();
    private static final int MOVIE_LOADER_ID = 0;
    private FavoriteMovieAdapter mFavoriteMovieAdapter;

    @Bind(R.id.listview_favorite_movies) ListView mFavoriteMovieList;

    public FavoriteFragment() {
    }

    @Override
    public void onStart(){
        super.onStart();


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortBy = prefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_order_default));

        if(sortBy!=null && ( sortBy.equalsIgnoreCase(getResources().getString(R.string.pref_sort_by_popular)) ||   sortBy.equalsIgnoreCase(getResources().getString(R.string.pref_sort_by_rating)))){
            Intent intent= new Intent(getContext(), MainActivity.class);
            getContext().startActivity(intent);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);// fragment should handle menu events.
        setRetainInstance(true);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        ButterKnife.bind(this, view);

        // The CursorAdapter will take data from our cursor and populate the ListView.
        mFavoriteMovieAdapter = new FavoriteMovieAdapter(getActivity(), null, 0);
        mFavoriteMovieList.setAdapter(mFavoriteMovieAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * This will be executed in a background thread.
     * @param id
     * @param args
     * @return
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                null,  //projection
                null,  //selection
                null,  //selection args
                null); //sort order
    }

    /**
     * Called when loader is complete and data is ready. Used for making UI updates.
     * @param loader
     * @param cursor
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mFavoriteMovieAdapter.swapCursor(cursor);
    }

    /**
     * Called when loader is destroyed. Release resources
     * @param loader
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mFavoriteMovieAdapter.swapCursor(null);
    }
}
