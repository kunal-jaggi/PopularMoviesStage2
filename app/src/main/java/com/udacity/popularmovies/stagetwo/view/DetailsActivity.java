package com.udacity.popularmovies.stagetwo.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.udacity.popularmovies.stagetwo.BuildConfig;
import com.udacity.popularmovies.stagetwo.R;

/**
 * This Activity is used to show movie details.
 * Created by kunaljaggi on 2/20/16.
 */
public class DetailsActivity extends BaseActivity {

    private static final String LOG_TAG = DetailsActivity.class.getSimpleName();

    public static final String EXTRA_MOVIE = "com.udacity.popularmovies.EXTRA_MOVIE";

    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private static final String PRICE_CONFIG_KEY = "price";
    private static final String IS_PROMOTION_CONFIG_KEY = "is_promotion_on";
    private static final String DISCOUNT_CONFIG_KEY = "discount";
    private long mDiscount = 0;

    private static final String TAG_TASK_FRAGMENT = "task_fragment";
    private DetailsFragment mDetailsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        long finalPrice= fetchRentalPriceFromRemoteConfig();

        FragmentManager fm = getSupportFragmentManager();
        mDetailsFragment = (DetailsFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);

        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
        if (mDetailsFragment == null) {
            mDetailsFragment = new DetailsFragment();
            Bundle arguments = new Bundle();
            int movieId = getIntent().getIntExtra(DetailsActivity.EXTRA_MOVIE, -1);
            arguments.putInt(DetailsFragment.MOVIE_ID, movieId);
            arguments.putLong(DetailsFragment.RENTAL_PRICE, finalPrice);
            arguments.putParcelable(DetailsFragment.DETAIL_URI, getIntent().getData());
            mDetailsFragment.setArguments(arguments);
            fm.beginTransaction().add(R.id.movie_details_container, mDetailsFragment, TAG_TASK_FRAGMENT).commit();
        }

    }

    /**
     * Returns current movie rental price. This method fetches discount from Firebase server and returns the final rental price.
     * @return
     */
    private long fetchRentalPriceFromRemoteConfig() {
        // Get a singleton Remote Config instance
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        //get the initial price from config
        long initialPrice = mFirebaseRemoteConfig.getLong(PRICE_CONFIG_KEY);
        Log.d(LOG_TAG, "initial Price is: " + initialPrice);

        // Create Remote Config Setting to enable developer mode.
        // Fetching configs from the server is normally limited to 5 requests per hour.
        // Enabling developer mode allows many more requests to be made per hour, so developers
        // can test different config values during development.
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);

        // Set default Remote Config values. In general you should have in app defaults for all
        // values that you may configure using Remote Config later on. The idea is that you
        // use the in app defaults and when you need to adjust those defaults, you set an updated
        // value in the App Manager console. Then the next time you application fetches from the
        // server, the updated value will be used. You can set defaults via an xml file like done
        // here or you can set defaults inline by using one of the other setDefaults methods.S
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        long cacheExpiration = 3600; // 3600 1 hour in seconds.
        // If in developer mode cacheExpiration is set to 0 so each fetch will retrieve values from
        // the server.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }
        Log.d(LOG_TAG, "before fetch() ");
        // cacheExpirationSeconds is set to cacheExpiration here, indicating that any previously
        // fetched and cached config would be considered expired because it would have been fetched
        // more than cacheExpiration seconds ago. Thus the next fetch would go to the server unless
        // throttling is in progress. The default expiration duration is 43200 (12 hours).
        mFirebaseRemoteConfig.fetch(cacheExpiration).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(LOG_TAG, "Fetch Succeeded");
                    // Once the config is successfully fetched it must be activated before newly fetched
                    // values are returned.
                    mFirebaseRemoteConfig.activateFetched();
                } else {
                    Log.d(LOG_TAG, "Fetch failed");
                }

                if (mFirebaseRemoteConfig.getBoolean(IS_PROMOTION_CONFIG_KEY)) {
                    Log.d(LOG_TAG, "Promoting is Enabled");

                    mDiscount= mFirebaseRemoteConfig.getLong(DISCOUNT_CONFIG_KEY);
                    Log.d(LOG_TAG, "Discount is : " + mDiscount);
                }
            }

        });
        return initialPrice - mDiscount;
    }

}