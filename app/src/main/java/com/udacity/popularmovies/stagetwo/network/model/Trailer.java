package com.udacity.popularmovies.stagetwo.network.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kunaljaggi on 4/30/16.
 */
public class Trailer {
    @SerializedName("id")
    private String mId;

    @SerializedName("iso_639_1")
    private String mIso6391;

    @SerializedName("iso_3166_1")
    private String mIso31661;

    @SerializedName("key")
    private String mKey;

    @SerializedName("name")
    private String mName;

    @SerializedName("site")
    private String mSite;

    @SerializedName("size")
    private Integer mSize;

    @SerializedName("type")
    private String mType;


    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmIso6391() {
        return mIso6391;
    }

    public void setmIso6391(String mIso6391) {
        this.mIso6391 = mIso6391;
    }

    public String getmIso31661() {
        return mIso31661;
    }

    public void setmIso31661(String mIso31661) {
        this.mIso31661 = mIso31661;
    }

    public String getmKey() {
        return mKey;
    }

    public void setmKey(String mKey) {
        this.mKey = mKey;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmSite() {
        return mSite;
    }

    public void setmSite(String mSite) {
        this.mSite = mSite;
    }

    public Integer getmSize() {
        return mSize;
    }

    public void setmSize(Integer mSize) {
        this.mSize = mSize;
    }

    public String getmType() {
        return mType;
    }

    public void setmType(String mType) {
        this.mType = mType;
    }
}
