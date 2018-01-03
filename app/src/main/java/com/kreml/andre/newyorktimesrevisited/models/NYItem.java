package com.kreml.andre.newyorktimesrevisited.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Holder for NY article's card elements
 */

public class NYItem implements Parcelable {

    private String mWebUrl;
    private String mPhoto;
    private String mHeadLine;
    private String mSnippet;

    private NYItem(NYItemBuilder builder) {
        mWebUrl = builder.mWebUrl;
        mPhoto = builder.mPhoto;
        mHeadLine = builder.mHeadLine;
        mSnippet = builder.mSnippet;
    }

    public String getWebUrl() {
        return mWebUrl;
    }

    public String getPhoto() {
        return mPhoto;
    }

    public String getHeadLine() {
        return mHeadLine;
    }

    public String getSnippet() {
        return mSnippet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NYItem item = (NYItem) o;

        if (!mWebUrl.equals(item.mWebUrl)) return false;
        if (!mPhoto.equals(item.mPhoto)) return false;
        if (!mHeadLine.equals(item.mHeadLine)) return false;
        return mSnippet.equals(item.mSnippet);
    }

    @Override
    public int hashCode() {
        int result = mWebUrl.hashCode();
        result = 31 * result + mPhoto.hashCode();
        result = 31 * result + mHeadLine.hashCode();
        result = 31 * result + mSnippet.hashCode();
        return result;
    }

    private NYItem(Parcel in) {
        mWebUrl = in.readString();
        mPhoto = in.readString();
        mHeadLine = in.readString();
        mSnippet = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mWebUrl);
        dest.writeString(mPhoto);
        dest.writeString(mHeadLine);
        dest.writeString(mSnippet);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<NYItem> CREATOR = new Parcelable.Creator<NYItem>() {
        @Override
        public NYItem createFromParcel(Parcel in) {
            return new NYItem(in);
        }

        @Override
        public NYItem[] newArray(int size) {
            return new NYItem[size];
        }
    };


    public static class NYItemBuilder {

        private String mWebUrl;
        private String mPhoto;
        private String mHeadLine;
        private String mSnippet;

        public NYItemBuilder setWebUrl(String webUrl) {
            this.mWebUrl = webUrl;
            return this;
        }

        public NYItemBuilder setPhoto(String photo) {
            mPhoto = photo;
            return this;
        }

        public NYItemBuilder setHeadLine(String headLine) {
            mHeadLine = headLine;
            return this;
        }

        public NYItemBuilder setSnippet(String snippet) {
            mSnippet = snippet;
            return this;
        }

        public NYItem build() {
            return new NYItem(this);
        }
    }
}