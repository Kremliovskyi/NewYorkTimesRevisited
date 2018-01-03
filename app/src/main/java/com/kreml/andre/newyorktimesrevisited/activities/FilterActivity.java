package com.kreml.andre.newyorktimesrevisited.activities;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.kreml.andre.newyorktimesrevisited.R;
import com.kreml.andre.newyorktimesrevisited.content.FilterView;
import com.kreml.andre.newyorktimesrevisited.utils.Constants;
import com.kreml.andre.newyorktimesrevisited.utils.NYSharedPreferences;
import com.kreml.andre.newyorktimesrevisited.utils.Utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Activity to filter out NY article categories
 */

public class FilterActivity extends AppCompatActivity {

    List<String> mCategories = new ArrayList<>();
    LinearLayout mLayout = null;
    LinearLayout mLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        setContentView(R.layout.activity_filter_view);

        mLinearLayout = findViewById(R.id.activity_filter);
        mLayout = Utils.createNextLinearLayout(this);
        mLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                populateLayouts();
                mLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        mCategories = Utils.readAssets();
        for (String categoryName : mCategories) {
            boolean isChecked = NYSharedPreferences.getsInstance(this).getCategoryPreference(categoryName);
            mLayout.addView(populateFilterViews(categoryName, isChecked));
        }
        mLinearLayout.addView(mLayout);

    }

    public FilterView populateFilterViews(final String text, boolean isChecked) {
        FilterView filterView = new FilterView(this, text);
        filterView.setBackground(ContextCompat.getDrawable(this, R.drawable.filter_button_shape));
        filterView.setButtonDrawable(ContextCompat.getDrawable(this, R.drawable.categories_selector));
        filterView.setChecked(isChecked);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(Constants.LINEAR_LAYOUT_MARGIN, Constants.LINEAR_LAYOUT_MARGIN,
                Constants.LINEAR_LAYOUT_MARGIN, Constants.LINEAR_LAYOUT_MARGIN);
        filterView.setLayoutParams(params);
        filterView.setOnClickListener(v -> {
            FilterView checkbox = (FilterView) v;
            NYSharedPreferences.getsInstance(FilterActivity.this).setCategoryPreference(text, checkbox.isChecked());
        });
        return filterView;
    }

    private void populateLayouts() {
        LinearLayout newLayout = Utils.createNextLinearLayout(this);
        int generalWidth = Constants.FILTER_VIEW_MARGIN;
        int layoutWidth = mLayout.getWidth();
        Map<FilterView, Integer> tempMap = new LinkedHashMap<>();
        for (int i = 0; i < mLayout.getChildCount(); i++) {
            FilterView child = (FilterView) mLayout.getChildAt(i);
            tempMap.put(child, child.getWidth());
        }
        mLayout.removeAllViews();
        for (Map.Entry<FilterView, Integer> entry : tempMap.entrySet()) {
            if (entry != null) {
                generalWidth += entry.getValue() + Constants.FILTER_VIEW_MARGIN;
                if (generalWidth >= layoutWidth) {
                    generalWidth = Constants.FILTER_VIEW_MARGIN * 2 + entry.getValue();
                    mLinearLayout.addView(newLayout);
                    newLayout = Utils.createNextLinearLayout(this);
                }
                newLayout.addView(entry.getKey());
            }
        }
        tempMap.clear();
    }
}
