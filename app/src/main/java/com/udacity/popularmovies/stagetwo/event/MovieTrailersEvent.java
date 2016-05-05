package com.udacity.popularmovies.stagetwo.event;

/**
 * Created by kunaljaggi on 4/30/16.
 */
public class MovieTrailersEvent {
    private int mMovieId;

    public MovieTrailersEvent() {
    }

    public MovieTrailersEvent(int mMovieId) {
        this.mMovieId = mMovieId;
    }

    public int getmMovieId() {
        return mMovieId;
    }

    public void setmMovieId(int mMovieId) {
        this.mMovieId = mMovieId;
    }
}
