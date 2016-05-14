package com.udacity.popularmovies.stagetwo.view;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.udacity.popularmovies.stagetwo.R;

/**
 * This Activity is used to show movie details.
 * Created by kunaljaggi on 2/20/16.
 */
public class DetailsActivity extends BaseActivity {

    public static final String EXTRA_MOVIE = "com.udacity.popularmovies.EXTRA_MOVIE";

    private static final String TAG_TASK_FRAGMENT = "task_fragment";
    private DetailsFragment mDetailsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        FragmentManager fm = getSupportFragmentManager();
        mDetailsFragment = (DetailsFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);

        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
        if (mDetailsFragment == null) {
            mDetailsFragment = new DetailsFragment();
            Bundle arguments = new Bundle();
            int movieId = getIntent().getIntExtra(DetailsActivity.EXTRA_MOVIE, -1);
            arguments.putInt(DetailsFragment.MOVIE_ID, movieId);
            arguments.putParcelable(DetailsFragment.DETAIL_URI, getIntent().getData());
            mDetailsFragment.setArguments(arguments);
            fm.beginTransaction().add(R.id.movie_details_container, mDetailsFragment, TAG_TASK_FRAGMENT).commit();
        }

    }

}