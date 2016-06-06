package com.udacity.popularmovies.stagetwo.sync;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by kunaljaggi on 5/26/16.
 */

/**
 * The service which allows the sync adapter framework to access the authenticator.
 */
public class MovieAuthenticatorService extends Service {
    // Instance field that stores the authenticator object
    private MovieAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new MovieAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}