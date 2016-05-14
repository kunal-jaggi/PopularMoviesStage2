package com.udacity.popularmovies.stagetwo.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.udacity.popularmovies.stagetwo.R;
import com.udacity.popularmovies.stagetwo.util.Utility;

/**
 * This is the main launcher Activity for the app. This Activity registers an intent-filter with launcher app.
 * Created by kunaljaggi on 2/14/16.
 */

public class MainActivity extends BaseActivity implements MovieGalleryFragment.Callback {

    private final String MOVIEFRAGMENT_TAG = "MFTAG";
    private boolean mTwoPane;
    private String mSortCriteria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSortCriteria = Utility.getPreferredSortingCriteria(this);
        setContentView(R.layout.activity_main);


        if (findViewById(R.id.movie_details_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_details_container, new DetailsFragment(), MOVIEFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String sortCriteria = Utility.getPreferredSortingCriteria(this);
        // update the location in our second pane using the fragment manager
        if (sortCriteria != null && !sortCriteria.equals(mSortCriteria)) {

            MovieGalleryFragment ff = (MovieGalleryFragment) getSupportFragmentManager().findFragmentById(R.id.movie_grid_container);
            if (null != ff) {
                ff.onSortCriteriaChanged();
            }

//            DetailsFragment df = (DetailsFragment) getSupportFragmentManager().findFragmentByTag(MOVIEFRAGMENT_TAG);
//            if (null != df) {
//                df.onSortCriteriaChanged();
//            }
            mSortCriteria = sortCriteria;
        }
    }

    @Override
    public void onItemSelected(Uri movieUri, int movieID) {
        if (mTwoPane) {
//             In two-pane mode, show the detail view in this activity by
//             adding or replacing the detail fragment using a
//             fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(DetailsFragment.DETAIL_URI, movieUri);
            args.putInt(DetailsFragment.MOVIE_ID, movieID);

            DetailsFragment fragment = new DetailsFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_details_container, fragment, MOVIEFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailsActivity.class)
                    .setData(movieUri)
                    .putExtra(DetailsActivity.EXTRA_MOVIE, movieID);
            startActivity(intent);
        }
    }
}
