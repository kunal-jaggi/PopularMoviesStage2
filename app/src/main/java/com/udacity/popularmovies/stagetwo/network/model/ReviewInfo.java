package com.udacity.popularmovies.stagetwo.network.model;

/**
 * Created by kunaljaggi on 4/27/16.
 */

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ReviewInfo {

    @SerializedName("id")
    private int mId;

    @SerializedName("page")
    private int mPage;

    @SerializedName("results")
    private List<MovieReview> mReviewList = new ArrayList<MovieReview>();

    @SerializedName("total_pages")
    private Integer mTotalPages;

    @SerializedName("total_results")
    private Integer mTotalResults;

    public ReviewInfo() {
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public int getmPage() {
        return mPage;
    }

    public void setmPage(int mPage) {
        this.mPage = mPage;
    }

    public List<MovieReview> getmReviewList() {
        return mReviewList;
    }

    public void setmReviewList(List<MovieReview> mReviewList) {
        this.mReviewList = mReviewList;
    }

    public Integer getmTotalPages() {
        return mTotalPages;
    }

    public void setmTotalPages(Integer mTotalPages) {
        this.mTotalPages = mTotalPages;
    }

    public Integer getmTotalResults() {
        return mTotalResults;
    }

    public void setmTotalResults(Integer mTotalResults) {
        this.mTotalResults = mTotalResults;
    }
}