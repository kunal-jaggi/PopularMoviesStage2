package com.udacity.popularmovies.stagetwo.view;

import android.support.v4.app.FragmentManager;
import android.os.Bundle;

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
            fm.beginTransaction().add(R.id.detailsContainer, mDetailsFragment, TAG_TASK_FRAGMENT).commit();
        }

//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.detailsContainer, new DetailsFragment())
//                    .commit();
//        }
    }

}