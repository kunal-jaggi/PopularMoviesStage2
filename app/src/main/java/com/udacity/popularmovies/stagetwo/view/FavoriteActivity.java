package com.udacity.popularmovies.stagetwo.view;

import android.os.Bundle;

import com.udacity.popularmovies.stagetwo.R;

/**
 * Created by kunaljaggi on 4/12/16.
 */

public class FavoriteActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.favoriteContainer, new FavoriteFragment())
                    .commit();
        }
    }

}