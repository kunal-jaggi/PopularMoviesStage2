package com.udacity.popularmovies.stagetwo.event;

import com.udacity.popularmovies.stagetwo.network.model.MovieReview;
import com.udacity.popularmovies.stagetwo.network.model.Trailer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kunaljaggi on 4/30/16.
 */
public class TrailerEvent {
    private List<Trailer> mTrailerList = new ArrayList<Trailer>();

    public TrailerEvent(List<Trailer> mTrailerList) {
        this.mTrailerList = mTrailerList;
    }

    public List<Trailer> getmTrailerList() {
        return mTrailerList;
    }

    public void setmTrailerList(List<Trailer> mTrailerList) {
        this.mTrailerList = mTrailerList;
    }
}
