package com.udacity.popularmovies.stagetwo.view;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.squareup.otto.Subscribe;
import com.udacity.popularmovies.stagetwo.R;
import com.udacity.popularmovies.stagetwo.adapter.GalleryItemAdapter;
import com.udacity.popularmovies.stagetwo.data.MovieContract;
import com.udacity.popularmovies.stagetwo.event.MovieEvent;
import com.udacity.popularmovies.stagetwo.network.model.Movie;
import com.udacity.popularmovies.stagetwo.network.service.DiscoverMovieServiceImpl;
import com.udacity.popularmovies.stagetwo.singleton.PopularMoviesApplication;
import com.udacity.popularmovies.stagetwo.util.Utility;


import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;


/**
 * A placeholder fragment containing a simple grid view
 * Sequence for callbacks (upon launch): -> onCreate() -> onCreateView() -> onActivityCreated() ->  onCreateLoader () -> onStart () -> onLoadFinished()-> onCreateOptionsMenu()
 * Sequence for callbacks (upon rotation): -> onPause()  -> onCreateView() -> onActivityCreated() ->  onStart() ->onLoadFinished -> onCreateOptionsMenu()
 */
public class MovieGalleryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MovieGalleryFragment.class.getSimpleName();

    @Bind(R.id.moviesGrid) GridView mMovieGrid;
    private DiscoverMovieServiceImpl mMovieService;
    private GalleryItemAdapter mFavoriteMovieAdapter;
    private static final int MOVIE_LOADER_ID = 0;

    // For the main Grid layout view, we're showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH
    };

    // these constants correspond to the projection defined above, and must change if the
    // projection changes
    private static final int COL_MOVIE_ID = 0;
    private static final int COL_MOVIE_POSTER_PATH = 1;

    public MovieGalleryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate() called");
        setRetainInstance(true);
        setHasOptionsMenu(true); // fragment should handle menu events
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView() called");
        // The CursorAdapter will take data from our cursor and populate the GridView.
        mFavoriteMovieAdapter = new GalleryItemAdapter(getActivity(), null, 0);

        View view = inflater.inflate(R.layout.fragment_moviegallery, container, false);
        ButterKnife.bind(this, view);

        //attach the adapter to the GridView
        mMovieGrid.setAdapter(mFavoriteMovieAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onActivityCreated called");
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
    }

    /**
     * This will be executed in a background thread.
     *
     * @param id
     * @param args
     * @return
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(LOG_TAG, "onCreateLoader called");
        // Defines a string to contain the selection clause
        String selectionClause = null;
        // An array to contain selection arguments
        String[] selectionArgs = null;
        // Gets a word from the UI
        String sortCriteria = Utility.getPreferredSortingCriteria(getContext());

        // Construct a selection clause that matches the word that the user entered.
        if (sortCriteria.equalsIgnoreCase(getResources().getString(R.string.pref_sort_by_popular))) {
            selectionClause = MovieContract.MovieEntry.COLUMN_IS_POPULAR + " = ?";
        } else if (sortCriteria.equalsIgnoreCase(getResources().getString(R.string.pref_sort_by_rating))) {
            selectionClause = MovieContract.MovieEntry.COLUMN_IS_RATED + " = ?";
        } else {
            selectionClause = MovieContract.MovieEntry.COLUMN_IS_FAVORITE + " = ?";
        }

        // Use the user's input string as the (only) selection argument.
        selectionArgs = new String[]{"" + 1};

        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                MOVIE_COLUMNS,   //projection
                selectionClause,  //selection
                selectionArgs,  //selection args
                null); //sort order
    }

    /**
     * This callback makes the fragment visible to the user when the containing activity is started.
     * We want to make a network request before user can  begin interacting with the user (onResume callback)
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "onStart called");
        mMovieService = new DiscoverMovieServiceImpl(getContext());
        PopularMoviesApplication.getEventBus().register(this);

        //if  user has selected either "popular" or "highest rated", sort criteria, we need to make a web call
        if (!Utility.getPreferredSortingCriteria(getContext()).equalsIgnoreCase(getResources().getString(R.string.pref_sort_by_favorite))) {
            fetchMovies();
        }
    }

    /**
     * Used to fire an event to the Bus that will fetch movie list from Open Movie DB REST back-end.
     * The sort order is retrieved from Shared Preferences
     */
    private void fetchMovies() {
        PopularMoviesApplication.getEventBus().post(Utility.produceDiscoverMovieEvent(Utility.getPreferredSortingCriteria(getContext())));
    }

    /**
     * Called when loader is complete and data is ready. Used for making UI updates.
     *
     * @param loader
     * @param cursor
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d(LOG_TAG, "onLoadFinished called cursor count is " + cursor.getCount() + " mFavoriteMovieAdapter is: " + mFavoriteMovieAdapter);
        if (mFavoriteMovieAdapter != null) {
            mFavoriteMovieAdapter.swapCursor(cursor);
        }
    }

    /**
     * Called when loader is destroyed. Release resources
     *
     * @param loader
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(LOG_TAG, "onLoaderReset called");
        if (mFavoriteMovieAdapter != null) {
            mFavoriteMovieAdapter.swapCursor(null);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        Log.d(LOG_TAG, "onCreateOptionsMenu() called");
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.galleryfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(LOG_TAG, "onOptionsItemSelected() called");
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            fetchMovies();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause called");
        PopularMoviesApplication.getEventBus().unregister(this);
    }


    /**
     * Used to navigate to Details screen through explicit intent.
     *
     * @param position grid item position clicked by the user.
     */
    @OnItemClick(R.id.moviesGrid)
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        // CursorAdapter returns a cursor at the correct position for getItem(), or null
        // if it cannot seek to that position.
        Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
        if (cursor != null) {
            String locationSetting = Utility.getPreferredSortingCriteria(getActivity());
            Intent intent = new Intent(getActivity(), DetailsActivity.class)
                    .setData(MovieContract.MovieEntry.buildMovieUri(
                                    cursor.getInt(COL_MOVIE_ID)
                            )
                    ).putExtra(DetailsActivity.EXTRA_MOVIE, cursor.getInt(COL_MOVIE_ID));
            startActivity(intent);
        }
    }

    /**
     * This method is triggered when we have updated the local DB with back-end results.
     * Restart the loader.  restartLoader will trigger onCreateLoader to be called again.
     *
     */
    @Subscribe
    public void onMovieEvent(MovieEvent movieEvent) {
        getLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
    }


    void onSortCriteriaChanged() {
        if (!Utility.getPreferredSortingCriteria(getContext()).equalsIgnoreCase(getResources().getString(R.string.pref_sort_by_favorite))) {
            fetchMovies();
        }

    }


}

