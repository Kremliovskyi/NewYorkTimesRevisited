package com.kreml.andre.newyorktimesrevisited.content;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.kreml.andre.newyorktimesrevisited.fragments.NYFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * This pager holds fragments associated with NY article's categories
 */

public class NYFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private List<String> mQueries;

    public NYFragmentPagerAdapter(FragmentManager fm, ArrayList<String> queries) {
        super(fm);
        this.mQueries = queries;
    }

    @Override
    public Fragment getItem(int position) {
        return NYFragment.newInstance(mQueries.get(position));
    }

    @Override
    public int getCount() {
        return mQueries.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mQueries.get(position).trim();
    }


}
