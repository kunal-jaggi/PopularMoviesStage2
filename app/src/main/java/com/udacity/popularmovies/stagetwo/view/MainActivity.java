package com.udacity.popularmovies.stagetwo.view;

import android.os.Bundle;

import com.udacity.popularmovies.stagetwo.R;
import com.udacity.popularmovies.stagetwo.util.Utility;

/**
 * This is the main launcher Activity for the app. This Activity registers an intent-filter with launcher app.
 * Created by kunaljaggi on 2/14/16.
 */

public class MainActivity extends BaseActivity {

    private final String MOVIEFRAGMENT_TAG = "MFTAG";
    private String mSortCriteria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSortCriteria = Utility.getPreferredSortingCriteria(this);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MovieGalleryFragment(), MOVIEFRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String sortCriteria = Utility.getPreferredSortingCriteria(this);
        // update the location in our second pane using the fragment manager
        if (sortCriteria != null && !sortCriteria.equals(mSortCriteria)) {
            MovieGalleryFragment ff = (MovieGalleryFragment) getSupportFragmentManager().findFragmentByTag(MOVIEFRAGMENT_TAG);
            if (null != ff) {
                ff.onSortCriteriaChanged();
            }
            mSortCriteria = sortCriteria;
        }
    }
}
