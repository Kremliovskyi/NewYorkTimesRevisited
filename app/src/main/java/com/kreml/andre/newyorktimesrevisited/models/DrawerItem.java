package com.kreml.andre.newyorktimesrevisited.models;

/**
 * NY article's category displayed in the drawer
 */

public class DrawerItem {

    private String mTitle;
    private boolean mSelected;

    DrawerItem(String title, boolean selected) {
        this.mTitle = title;
        this.mSelected = selected;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public boolean isSelected() {
        return mSelected;
    }

    public void setSelected(boolean selected) {
        this.mSelected = selected;
    }
}