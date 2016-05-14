package com.udacity.popularmovies.stagetwo.view;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.udacity.popularmovies.stagetwo.R;
import com.udacity.popularmovies.stagetwo.adapter.MovieReviewAdapter;
import com.udacity.popularmovies.stagetwo.adapter.MovieTrailerAdapter;
import com.udacity.popularmovies.stagetwo.data.MovieContract;
import com.udacity.popularmovies.stagetwo.event.ReviewEvent;
import com.udacity.popularmovies.stagetwo.event.TrailerEvent;
import com.udacity.popularmovies.stagetwo.network.model.MovieReview;
import com.udacity.popularmovies.stagetwo.network.model.Trailer;
import com.udacity.popularmovies.stagetwo.singleton.PopularMoviesApplication;
import com.udacity.popularmovies.stagetwo.util.Constants;
import com.udacity.popularmovies.stagetwo.util.Utility;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import su.j2e.rvjoiner.JoinableAdapter;
import su.j2e.rvjoiner.JoinableLayout;
import su.j2e.rvjoiner.RvJoiner;

/**
 * This Fragment class is added by DetailsActivity to show details screen.
 * Created by kunaljaggi on 2/20/16.
 */
public class DetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailsFragment.class.getSimpleName();
    private static final String MOVIE_DETAILS_SHARE_HASHTAG = " #PopularMoviesApp";
    static final String MOVIE_ID = "ID";
    static final String DETAIL_URI = "URI";
    private Uri mUri;
    private int mMovieId;

    private TextView mMovieTileTxt;
    private ImageView mMoviePoster;
    private TextView mMovieReleaseYearTxt;
    private TextView mMovieRatingTxt;
    private TextView mMovieOverviewTxt;
    private ImageView mMovieFavorite;

    private RecyclerView rv;
    private RvJoiner rvJoiner = new RvJoiner();
    private MovieTrailerAdapter trailerAdapter;
    private MovieReviewAdapter reviewAdapter;

    private int mMovieID;
    private String mMovieTitle;
    private String mMovieOverview;
    private String mMovieVotes;
    private String mMovieReleaseDate;
    private String mMoviePosterPath;
    private boolean mIsFavorite;

    private static final int DETAIL_LOADER = 0;

    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_IS_FAVORITE
    };

    // these constants correspond to the projection defined above, and must change if the
    // projection changes
    private static final int COL_MOVIE_ID = 0;
    private static final int COL_MOVIE_TITLE = 1;
    private static final int COL_MOVIE_OVERVIEW = 2;
    private static final int COL_MOVIE_VOTE_AVERAGE = 3;
    private static final int COL_MOVIE_RELEASE_DATE = 4;
    private static final int COL_MOVIE_POSTER_PATH = 5;
    private static final int COL_MOVIE_IS_FAVORITE = 6;


    public DetailsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);// fragment should handle menu events.

        // Retain this fragment across configuration changes.
        setRetainInstance(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "onStart called");
        PopularMoviesApplication.getEventBus().register(this);
    }

    @Override
    public void onStop() {
        super.onPause();
        Log.d(LOG_TAG, "onStop called");
        PopularMoviesApplication.getEventBus().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DETAIL_URI);
            mMovieId = arguments.getInt(MOVIE_ID);
        }

        View viewRecycler = inflater.inflate(R.layout.fragment_details, container, false);
        rv = (RecyclerView) viewRecycler.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        trailerAdapter = new MovieTrailerAdapter(null, getContext());
        reviewAdapter = new MovieReviewAdapter(null);

        if (arguments == null) {
            rvJoiner.add(new JoinableLayout(R.layout.placeholder));
        } else {

            rvJoiner.add(new JoinableLayout(R.layout.movie_details, new JoinableLayout.Callback() {
                @Override
                public void onInflateComplete(View view, ViewGroup parent) {
                    mMovieTileTxt = (TextView) view.findViewById(R.id.movieTitle);
                    mMoviePoster = (ImageView) view.findViewById(R.id.moviePoster);
                    mMovieReleaseYearTxt = (TextView) view.findViewById(R.id.movieReleaseYear);
                    mMovieRatingTxt = (TextView) view.findViewById(R.id.movieRating);
                    mMovieOverviewTxt = (TextView) view.findViewById(R.id.movieOverview);
                    mMovieFavorite = (ImageView) view.findViewById(R.id.favoriteIcon);

                    fillDetailsScreen();
                }
            }));

            rvJoiner.add(new JoinableLayout(R.layout.trailers));
            rvJoiner.add(new JoinableAdapter(trailerAdapter));
            rvJoiner.add(new JoinableLayout(R.layout.reviews));
            rvJoiner.add(new JoinableAdapter(reviewAdapter));
        }
        rv.setAdapter(rvJoiner.getAdapter());

        View view = inflater.inflate(R.layout.movie_details, container, false);

        setupMovieTrailerdapter(null);
        setupMovieReviewAdapter(null);

        return viewRecycler;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mUri != null) {
            String selectionClause = MovieContract.MovieEntry._ID + " = ?";
            String[] selectionArgs = new String[]{"" + mMovieId};

            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    MOVIE_COLUMNS,      //projection
                    selectionClause,    //selection
                    selectionArgs,      //selection args
                    null                //sort order
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.v(LOG_TAG, "In onLoadFinished");
        if (!cursor.moveToFirst()) {
            return;
        }

        mMovieID = cursor.getInt(COL_MOVIE_ID);
        mMovieTitle = cursor.getString(COL_MOVIE_TITLE);
        mMovieOverview = cursor.getString(COL_MOVIE_OVERVIEW);
        mMovieVotes = cursor.getString(COL_MOVIE_VOTE_AVERAGE);
        mMovieReleaseDate = cursor.getString(COL_MOVIE_RELEASE_DATE);
        mMoviePosterPath = cursor.getString(COL_MOVIE_POSTER_PATH);
        mIsFavorite = cursor.getInt(COL_MOVIE_IS_FAVORITE) > 0;

        fillDetailsScreen();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    /**
     * Used to render original title, poster image, overview (plot), user rating and release date.
     */
    private void fillDetailsScreen() {

        if (mMovieTileTxt != null) {
            mMovieTileTxt.setText(mMovieTitle);
        }

        if (mMoviePoster != null) {
            Picasso.with(getContext())
                    .load(Constants.MOVIE_DB_POSTER_URL + Constants.POSTER_PHONE_SIZE + mMoviePosterPath)
                    .placeholder(R.drawable.poster_placeholder) // support download placeholder
                    .error(R.drawable.poster_placeholder_error) //support error placeholder, if back-end returns empty string or null
                    .into(mMoviePoster);
        }

        //we only want to display ratings rounded up to 3 chars max (e.g. 6.3)
        if (mMovieVotes != null && mMovieVotes.length() >= 3) {
            mMovieVotes = mMovieVotes.substring(0, 3);
        }
        if (mMovieRatingTxt != null) {
            mMovieRatingTxt.setText("" + mMovieVotes + "/10");
        }

        if (mMovieOverviewTxt != null) {
            mMovieOverviewTxt.setText(mMovieOverview);
        }

        if (mMovieReleaseDate != null) {
            // Movie DB API returns release date in yyyy--mm-dd format
            // Extract the year through regex
            Pattern datePattern = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})");
            Matcher dateMatcher = datePattern.matcher(mMovieReleaseDate);
            if (dateMatcher.find()) {
                mMovieReleaseDate = dateMatcher.group(1);

            }
        }

        if (mMovieReleaseYearTxt != null) {
            mMovieReleaseYearTxt.setText(mMovieReleaseDate);
        }

        if (mMovieFavorite != null) {
            if (mIsFavorite) {
                showFavoriteIcon(mMovieFavorite, R.drawable.ic_favorite_black_24dp);
            } else {
                showFavoriteIcon(mMovieFavorite, R.drawable.ic_favorite_border_black_24dp);
            }


            mMovieFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create and execute the background task.
                    DBUpdateTask task = new DBUpdateTask(mIsFavorite, mMovieID);
                    task.execute();
                }
            });
        }

        fetchMovieTrailersAndReviews(mMovieID);

    }

    private void showFavoriteIcon(ImageView image, int resoureId) {
        image.setImageResource(resoureId);
        image.setVisibility(View.VISIBLE);
    }

    /**
     * Used to fire an event to the Bus that will fetch movie list from Open Movie DB REST back-end.
     * The sort order is retrieved from Shared Preferences
     */
    private void fetchMovieTrailersAndReviews(final int movieId) {
        PopularMoviesApplication.getEventBus().post(Utility.produceMovieTrailersEvent(movieId));
        PopularMoviesApplication.getEventBus().post(Utility.produceMovieReviewsEvent(movieId));
    }

    /**
     * This method is triggered when we have updated the local DB with back-end results.
     * Restart the loader.  restartLoader will trigger onCreateLoader to be called again.
     */
    @Subscribe
    public void onReviewEvent(ReviewEvent movieReview) {
        setupMovieReviewAdapter(movieReview.getmReviewList());
        Log.d(LOG_TAG, "onReviewEvent  called");

        if (movieReview.getmReviewList() != null) {
            Log.d(LOG_TAG, "# of review items: " + movieReview.getmReviewList().size());
        }

    }

    /**
     * This method is triggered when we have updated the local DB with back-end results.
     * Restart the loader.  restartLoader will trigger onCreateLoader to be called again.
     */
    @Subscribe
    public void onTrailerEvent(TrailerEvent movieTrailer) {
        setupMovieTrailerdapter(movieTrailer.getmTrailerList());
        Log.d(LOG_TAG, "onTrailerEvent called");

        if (movieTrailer.getmTrailerList() != null) {
            Log.d(LOG_TAG, "# of review trailers: " + movieTrailer.getmTrailerList().size());
        }

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

        if (mMovieTileTxt != null) {
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    mMovieTileTxt.getText() + MOVIE_DETAILS_SHARE_HASHTAG);
        }

        return shareIntent;
    }

    private void setupMovieReviewAdapter(final List<MovieReview> movieReviews) {
        //if (getActivity() == null || mMovieReviewList == null) return;
        if (getActivity() == null) return;

        if (movieReviews != null) {
            reviewAdapter.setmMovieReviewList(movieReviews);
            reviewAdapter.notifyDataSetChanged();
            Log.d(LOG_TAG, "# of reviews in setupAdapet is: " + movieReviews.size());
        }
    }

    private void setupMovieTrailerdapter(final List<Trailer> movieTrailers) {
        if (getActivity() == null) return;

        if (movieTrailers != null) {
            trailerAdapter.setmMovieTrailerList(movieTrailers);
            trailerAdapter.notifyDataSetChanged();
        }
    }


    /**
     * Used to insert a record into SQLite DB in a non-UI worker thread.
     */
    private class DBUpdateTask extends AsyncTask<Void, Integer, Void> {

        boolean mIsFavorite;
        int movieID;

        DBUpdateTask(boolean mIsFavorite, int movieID) {
            this.mIsFavorite = mIsFavorite;
            this.movieID = movieID;
        }


        @Override
        protected void onPreExecute() {
        }

        /**
         * Note that we do NOT call the callback object's methods
         * directly from the background thread, as this could result
         * in a race condition.
         */
        @Override
        protected Void doInBackground(Void... ignore) {
            ContentValues updateValues = new ContentValues();
            if (mIsFavorite) {
                updateValues.put(MovieContract.MovieEntry.COLUMN_IS_FAVORITE, 0);
            } else {
                updateValues.put(MovieContract.MovieEntry.COLUMN_IS_FAVORITE, 1);
            }

            // Defines selection criteria for the rows you want to update
            String selectionClause = MovieContract.MovieEntry._ID + " = ?";
            String[] selectionArgs = new String[]{"" + movieID};

            // Defines a variable to contain the number of updated rows
            int rowsUpdated = 0;


            rowsUpdated = getContext().getContentResolver().update(
                    MovieContract.MovieEntry.CONTENT_URI,  // the user dictionary content URI
                    updateValues,                       // the columns to update
                    selectionClause,                    // the column to select on
                    selectionArgs);                      // the value to compare to
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... percent) {
        }

        @Override
        protected void onCancelled() {
        }

        @Override
        protected void onPostExecute(Void ignore) {

        }
    }
}
