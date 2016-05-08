package com.udacity.popularmovies.stagetwo.view;

import android.content.ContentValues;
import android.content.Context;
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
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.udacity.popularmovies.stagetwo.R;
import com.udacity.popularmovies.stagetwo.adapter.MovieReviewAdapter;
import com.udacity.popularmovies.stagetwo.adapter.MovieTrailerAdapter;
import com.udacity.popularmovies.stagetwo.data.MovieContract;
import com.udacity.popularmovies.stagetwo.event.MovieEvent;
import com.udacity.popularmovies.stagetwo.event.ReviewEvent;
import com.udacity.popularmovies.stagetwo.event.TrailerEvent;
import com.udacity.popularmovies.stagetwo.network.model.Movie;
import com.udacity.popularmovies.stagetwo.network.model.MovieReview;
import com.udacity.popularmovies.stagetwo.network.model.Trailer;
import com.udacity.popularmovies.stagetwo.network.service.DiscoverMovieServiceImpl;
import com.udacity.popularmovies.stagetwo.singleton.PopularMoviesApplication;
import com.udacity.popularmovies.stagetwo.util.Constants;
import com.udacity.popularmovies.stagetwo.util.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
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

    //@Bind(R.id.movieTitle)
    TextView mMovieTileTxt;
    //@Bind(R.id.moviePoster)
    ImageView mMoviePoster;
    //@Bind(R.id.movieReleaseYear)
    TextView mMovieReleaseYear;
    //@Bind(R.id.movieRating)
    TextView mMovieRating;
    //@Bind(R.id.movieOverview)
    TextView mMovieOverview;
    //@Bind(R.id.favoriteIcon)
    ImageView mMovieFavorite;
    //@Bind(R.id.movieReviews)
   // RecyclerView mMovieReviewList;
    //@Bind(R.id.movieTrailers)
   // RecyclerView mMovieTrailerList;

    RecyclerView rv;
    RvJoiner rvJoiner = new RvJoiner();
    MovieTrailerAdapter trailerAdapter;
    MovieReviewAdapter reviewAdapter;

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
        setRetainInstance(true);

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
    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause called");
        PopularMoviesApplication.getEventBus().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    //old implementation
    /*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        ButterKnife.bind(this, view);

        View reviewView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_item_reviews, container, false);

        mMovieReviewList = (ListView) reviewView.findViewById(R.id.movieReviews);
        //handleListViewScrolling(mMovieReviewList);
        setupAdapter(null);
        return view;
    }
    */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DETAIL_URI);
            mMovieId= arguments.getInt(MOVIE_ID);
        }

        //View trailerView = inflater.inflate(R.layout.list_item_trailers, container, false);
        //mMovieTrailerList= (RecyclerView) trailerView.findViewById(R.id.movieTrailersList);
         //ButterKnife.bind(this, trailerView);
        View viewRecycler = inflater.inflate(R.layout.fragment_details, container, false);
        rv = (RecyclerView) viewRecycler.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        trailerAdapter= new MovieTrailerAdapter(null, getContext());
        reviewAdapter= new MovieReviewAdapter(null);

        rvJoiner.add(new JoinableLayout(R.layout.movie_details));
        rvJoiner.add(new JoinableLayout(R.layout.trailers));
        rvJoiner.add(new JoinableAdapter(trailerAdapter));
        rvJoiner.add(new JoinableLayout(R.layout.reviews));
        rvJoiner.add(new JoinableAdapter(reviewAdapter));
        rv.setAdapter(rvJoiner.getAdapter());

        View view = inflater.inflate(R.layout.movie_details, container, false);
//        mMovieTrailerList= (RecyclerView) view.findViewById(R.id.movieTrailersList);
//        mMovieReviewList= (RecyclerView) view.findViewById(R.id.movieReviewsList);
        mMovieTileTxt = (TextView) view.findViewById(R.id.movieTitle);
        mMoviePoster =  (ImageView) view.findViewById(R.id.moviePoster);
        mMovieReleaseYear =  (TextView) view.findViewById(R.id.movieReleaseYear);
        mMovieRating =  (TextView) view.findViewById(R.id.movieRating);
        mMovieOverview =  (TextView) view.findViewById(R.id.movieOverview);
        mMovieFavorite =  (ImageView) view.findViewById(R.id.favoriteIcon);

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

//        Intent intent = getActivity().getIntent();
//        if (intent == null) {
//            return null;
//        }
        if(mUri != null) {

            Log.v(LOG_TAG, "In onCreateLoader intent data is: " + mUri);

            //int movieID = intent.getIntExtra(DetailsActivity.EXTRA_MOVIE, -1);
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

        fillDetailScreen(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    /**
     * Used to render original title, poster image, overview (plot), user rating and release date.
     */
    private void fillDetailScreen(Cursor data) {
        final int movieID = data.getInt(COL_MOVIE_ID);
        String movieTitle = data.getString(COL_MOVIE_TITLE);
        String movieOverview = data.getString(COL_MOVIE_OVERVIEW);
        String movieVotes = data.getString(COL_MOVIE_VOTE_AVERAGE);
        String movieReleaseDate = data.getString(COL_MOVIE_RELEASE_DATE);
        String moviePosterPath = data.getString(COL_MOVIE_POSTER_PATH);
        final boolean isFavorite = data.getInt(COL_MOVIE_IS_FAVORITE) > 0;

        mMovieTileTxt.setText(movieTitle);
        Picasso.with(getContext())
                .load(Constants.MOVIE_DB_POSTER_URL + Constants.POSTER_PHONE_SIZE + moviePosterPath)
                .placeholder(R.drawable.poster_placeholder) // support download placeholder
                .error(R.drawable.poster_placeholder_error) //support error placeholder, if back-end returns empty string or null
                .into(mMoviePoster);

        //we only want to display ratings rounded up to 3 chars max (e.g. 6.3)
        if (movieVotes != null && movieVotes.length() >= 3) {
            movieVotes = movieVotes.substring(0, 3);
        }
        mMovieRating.setText("" + movieVotes + "/10");
        mMovieOverview.setText(movieOverview);

        // Movie DB API returns release date in yyyy--mm-dd format
        // Extract the year through regex
        Pattern datePattern = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})");
        Matcher dateMatcher = datePattern.matcher(movieReleaseDate);
        if (dateMatcher.find()) {
            movieReleaseDate = dateMatcher.group(1);

        }
        mMovieReleaseYear.setText(movieReleaseDate);

        if (isFavorite) {
            showFavoriteIcon(mMovieFavorite, R.drawable.ic_favorite_black_24dp);
        } else {
            showFavoriteIcon(mMovieFavorite, R.drawable.ic_favorite_border_black_24dp);
        }


        mMovieFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create and execute the background task.
                DBUpdateTask task = new DBUpdateTask(isFavorite, movieID);
                task.execute();

//                ContentValues updateValues = new ContentValues();
//                if (isFavorite) {
//                    updateValues.put(MovieContract.MovieEntry.COLUMN_IS_FAVORITE, 0);
//                } else {
//                    updateValues.put(MovieContract.MovieEntry.COLUMN_IS_FAVORITE, 1);
//                }
//
//                // Defines selection criteria for the rows you want to update
//                String selectionClause = MovieContract.MovieEntry._ID + " = ?";
//                String[] selectionArgs = new String[]{"" + movieID};
//
//                // Defines a variable to contain the number of updated rows
//                int rowsUpdated = 0;
//
//
//                rowsUpdated = getContext().getContentResolver().update(
//                        MovieContract.MovieEntry.CONTENT_URI,  // the user dictionary content URI
//                        updateValues,                       // the columns to update
//                        selectionClause,                    // the column to select on
//                        selectionArgs);                      // the value to compare to

            }
        });

        fetchMovieTrailersAndReviews(movieID);

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
     *
     */
    @Subscribe
    public void onReviewEvent(ReviewEvent movieReview) {
        setupMovieReviewAdapter(movieReview.getmReviewList());
    Log.d(LOG_TAG, "onReviewEvent called");

        if(movieReview.getmReviewList()!=null){
            Log.d(LOG_TAG, "# of review items: "+movieReview.getmReviewList().size());
        }

    }

    /**
     * This method is triggered when we have updated the local DB with back-end results.
     * Restart the loader.  restartLoader will trigger onCreateLoader to be called again.
     *
     */
    @Subscribe
    public void onTrailerEvent(TrailerEvent movieTrailer) {
        setupMovieTrailerdapter(movieTrailer.getmTrailerList());
        Log.d(LOG_TAG, "onTrailerEvent called");

        if(movieTrailer.getmTrailerList()!=null){
            Log.d(LOG_TAG, "# of review trailers: "+movieTrailer.getmTrailerList().size());
        }

    }

//    @OnItemClick(R.id.movieTrailersList)
//    void onItemClick(View view, int position) {
//        TextView videoKey= (TextView) view.findViewById(R.id.trailer_video_key);
//        String youTubeUrl= Constants.YOU_TUBE_BASE_URL+videoKey.getText();
//        Log.d(LOG_TAG, "Clicked position " + position + " YouTube video URL: "+youTubeUrl);
//        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(youTubeUrl)));
//
//    }

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

        if (movieReviews != null ) {
//            LinearLayoutManager layoutManager
//                    = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
//            //RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
//            mMovieReviewList.setLayoutManager(layoutManager);
//            mMovieReviewList.setItemAnimator(new DefaultItemAnimator());
//            mMovieReviewList.setAdapter(new MovieReviewAdapter(movieReviews));
            //rvJoiner.add(new JoinableAdapter(new MovieReviewAdapter(movieReviews)));
            reviewAdapter.setmMovieReviewList(movieReviews);
            reviewAdapter.notifyDataSetChanged();
            //rv.setAdapter(rvJoiner.getAdapter());
            Log.d(LOG_TAG, "# of reviews in setupAdapet is: "+movieReviews.size());
            //mMovieTrailerList.getLayoutParams().height = 270*movieReviews.size();
            //mMovieReviewList.setAdapter(new MovieReviewAdapter( movieReviews));
        }
//        else {
//            mMovieReviewList.setAdapter(null);
//        }
    }

    private void setupMovieTrailerdapter(final List<Trailer> movieTrailers ) {
        //if (getActivity() == null ||  mMovieTrailerList== null) return;
        if (getActivity() == null ) return;

        if (movieTrailers != null ) {
//            LinearLayoutManager layoutManager
//                    = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
//           // RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
//            mMovieTrailerList.setLayoutManager(layoutManager);
//            mMovieTrailerList.setItemAnimator(new DefaultItemAnimator());
//            mMovieTrailerList.setAdapter(new MovieTrailerAdapter(movieTrailers));
            //rvJoiner.add(new JoinableAdapter(new MovieTrailerAdapter(movieTrailers)));
            trailerAdapter.setmMovieTrailerList(movieTrailers);
            trailerAdapter.notifyDataSetChanged();
            //rv.setAdapter(rvJoiner.getAdapter());
            Log.d(LOG_TAG, "# of trailers in setupAdapet is: "+movieTrailers.size());
            //mMovieTrailerList.getLayoutParams().height = 270*movieTrailers.size();
        }
//        else {
//            mMovieTrailerList.setAdapter(null);
//        }
    }



    private class DBUpdateTask extends AsyncTask<Void, Integer, Void> {

        boolean mIsFavorite;
        int movieID;

        DBUpdateTask(boolean mIsFavorite, int movieID){
            this.mIsFavorite= mIsFavorite;
            this.movieID= movieID;
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
