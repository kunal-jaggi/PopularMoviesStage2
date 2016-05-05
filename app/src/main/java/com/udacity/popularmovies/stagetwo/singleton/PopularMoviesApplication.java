package com.udacity.popularmovies.stagetwo.singleton;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.squareup.otto.Bus;

/**
 * A singleton class that represents App's global application state.
 * Created by kunaljaggi on 2/18/16.
 */

public class PopularMoviesApplication extends Application {

    private static Bus mEventBus;

    public static Bus getEventBus() {

        if (mEventBus == null) {
            mEventBus = new com.squareup.otto.Bus();
        }

        return mEventBus;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
