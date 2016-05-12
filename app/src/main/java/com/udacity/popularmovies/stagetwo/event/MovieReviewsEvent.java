package com.udacity.popularmovies.stagetwo.event;

/**
 * Created by kunaljaggi on 4/27/16.
 */
public class MovieReviewsEvent {

    private int mMovieId;

    public MovieReviewsEvent() {
    }

    public MovieReviewsEvent(int mMovieId) {
        this.mMovieId = mMovieId;
    }

    public int getmMovieId() {
        return mMovieId;
    }

    public void setmMovieId(int mMovieId) {
        this.mMovieId = mMovieId;
    }
}
