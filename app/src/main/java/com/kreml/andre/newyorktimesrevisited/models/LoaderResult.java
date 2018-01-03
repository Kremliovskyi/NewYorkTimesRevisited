package com.kreml.andre.newyorktimesrevisited.models;

import java.util.List;

/**
 * Result returned by {@link NYFragmentModel}
 */

public class LoaderResult {

    private List<NYItem> mNYItemList;
    private int mMode;

    public LoaderResult(List<NYItem> NYItemList, int mode) {
        mNYItemList = NYItemList;
        mMode = mode;
    }

    public List<NYItem> getNYItemList() {
        return mNYItemList;
    }

    public int getMode() {
        return mMode;
    }
}
