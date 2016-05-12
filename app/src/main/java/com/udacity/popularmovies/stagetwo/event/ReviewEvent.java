package com.udacity.popularmovies.stagetwo.event;

import com.udacity.popularmovies.stagetwo.network.model.MovieReview;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kunaljaggi on 4/27/16.
 */
public class ReviewEvent {

    private List<MovieReview> mReviewList = new ArrayList<MovieReview>();

    public ReviewEvent(List<MovieReview> mReviewList) {
        this.mReviewList = mReviewList;
    }

    public List<MovieReview> getmReviewList() {
        return mReviewList;
    }

    public void setmReviewList(List<MovieReview> mReviewList) {
        this.mReviewList = mReviewList;
    }
}
