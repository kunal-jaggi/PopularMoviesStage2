package com.udacity.popularmovies.stagetwo.util;

import android.content.Context;
import android.content.Intent;

import com.udacity.popularmovies.stagetwo.view.FavoriteActivity;

/**
 * Created by kunaljaggi on 4/12/16.
 */
public class GlobalRedirect {

    /**
     * Routes to Favorite Activity.
     * @param context
     */
    public static void routeToFavoriteActivity(final Context context){
        Intent intent= new Intent(context, FavoriteActivity.class);
        context.startActivity(intent);
    }
}
