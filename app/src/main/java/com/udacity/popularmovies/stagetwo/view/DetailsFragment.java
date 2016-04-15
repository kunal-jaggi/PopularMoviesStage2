package com.udacity.popularmovies.stagetwo.view;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.popularmovies.stagetwo.R;
import com.udacity.popularmovies.stagetwo.data.MovieContract;
import com.udacity.popularmovies.stagetwo.network.model.Movie;
import com.udacity.popularmovies.stagetwo.util.Constants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * This Fragment class is added by DetailsActivity to show details screen.
 * Created by kunaljaggi on 2/20/16.
 */
public class DetailsFragment extends Fragment {

    private static final String LOG_TAG = DetailsFragment.class.getSimpleName();
    private static final String MOVIE_DETAILS_SHARE_HASHTAG = " #PopularMoviesApp";

    @Bind(R.id.movieTitle) TextView mMovieTileTxt;
    @Bind(R.id.moviePoster) ImageView mMoviePoster;
    @Bind(R.id.movieReleaseYear) TextView mMovieReleaseYear;
    @Bind(R.id.movieRating) TextView mMovieRating;
    @Bind(R.id.movieOverview) TextView mMovieOverview;

    private Movie mSelectedMovie;

    public DetailsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        ButterKnife.bind(this, view);

        //Parent activity is started by firing-off an explicit intent.
        //Inspect the intent for movie data.
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(DetailsActivity.EXTRA_MOVIE)) {
            mSelectedMovie = intent.getParcelableExtra(DetailsActivity.EXTRA_MOVIE);
            if (mSelectedMovie != null) {
                fillDetailScreen();
            }
        }

        return view;
    }

    @OnClick(R.id.saveMovieAsFav)
    public void submit(View view) {

        ContentValues insertValues = new ContentValues();
        insertValues.put(MovieContract.MovieEntry._ID, mSelectedMovie.getmId());
        insertValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, mSelectedMovie.getmOverview());
        insertValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, mSelectedMovie.getmReleaseDate());
        insertValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, mSelectedMovie.getmVoteAverage());
        insertValues.put(MovieContract.MovieEntry.COLUMN_TITLE, mSelectedMovie.getmTitle());


        Uri locationUri = getContext().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, insertValues);
        Toast.makeText(getContext(), "Save Button CLicked,  URI PATH:"+ locationUri.getPath(), Toast.LENGTH_LONG).show();

    }

    /**
     * Used to render original title, poster image, overview (plot), user rating and release date.
     *
     */
    private void fillDetailScreen() {
        mMovieTileTxt.setText(mSelectedMovie.getmTitle());
        Picasso.with(getContext())
                .load(Constants.MOVIE_DB_POSTER_URL + Constants.POSTER_PHONE_SIZE + mSelectedMovie.getmPosterPath())
                .placeholder(R.drawable.poster_placeholder) // support download placeholder
                .error(R.drawable.poster_placeholder_error) //support error placeholder, if back-end returns empty string or null
                .into(mMoviePoster);
        mMovieRating.setText("" + mSelectedMovie.getmVoteAverage() + "/10");
        mMovieOverview.setText(mSelectedMovie.getmOverview());

        // Movie DB API returns release date in yyyy--mm-dd format
        // Extract the year through regex
        Pattern datePattern = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})");
        String year = mSelectedMovie.getmReleaseDate();
        Matcher dateMatcher = datePattern.matcher(year);
        if (dateMatcher.find()) {
            year = dateMatcher.group(1);

        }
        mMovieReleaseYear.setText(year);

        Log.d(LOG_TAG, "Movie record exists: " + isMovieFavorite());

    }

    /**
     * Returns a boolean flag indicating is a specific movie is saved as a favorite within the local DB.
     * @return boolean: return false if the cursor is empty, true otherwise.
     */
    private boolean isMovieFavorite(){
        boolean favFlag= false;

        // Defines a string to contain the selection clause
        String selectionClause = null;

// An array to contain selection arguments
        String[] selectionArgs = null;

// Gets a word from the UI
        int movieID = mSelectedMovie.getmId();
Log.d(LOG_TAG, "Movie ID " + movieID);

            // Construct a selection clause that matches the word that the user entered.
            selectionClause = MovieContract.MovieEntry._ID + " = ?";

            // Use the user's input string as the (only) selection argument.
            selectionArgs = new String[]{ ""+movieID };




        Cursor movieCursor = getContext().getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI, // The content URI of the movie table
                null,                                  // projection:  leaving "columns" null just returns all the columns.
                selectionClause,                                  // selection criteria:  cols for "where" clause
                selectionArgs,                                   // selection criteria: values for "where" clause
                null                                               // sort order
        );
        if (movieCursor == null) {
            favFlag = false;
        }else if (movieCursor.getCount() < 1){
            favFlag= false;
        }else
            favFlag= true;

        return favFlag;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detailfragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        ShareActionProvider mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // Attach an intent to this ShareActionProvider.  You can update this at any time,
        // like when the user selects a new piece of data they might like to share.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareMovieIntent());
        } else {
            Log.d(LOG_TAG, "Share Action Provider is null?");
        }
    }

    /**
     * Returns an implicit intent to launch another app. Movie title is added as intent extra.
     *
     * @return intent
     */
    private Intent createShareMovieIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND); //generic action
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET); //required to return to Popular Movies app
        shareIntent.setType("text/plain");

        if(mSelectedMovie != null){
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    mSelectedMovie.getmTitle() + MOVIE_DETAILS_SHARE_HASHTAG);
        }

        return shareIntent;
    }
}
