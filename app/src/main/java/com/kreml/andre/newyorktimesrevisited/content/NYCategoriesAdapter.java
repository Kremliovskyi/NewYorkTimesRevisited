package com.kreml.andre.newyorktimesrevisited.content;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kreml.andre.newyorktimesrevisited.R;
import com.kreml.andre.newyorktimesrevisited.models.DrawerItem;

import java.util.List;

/**
 * Adapter for legacy {@link android.widget.ListView} to display the NY article's categories in the drawer
 */

public class NYCategoriesAdapter extends BaseAdapter {

    private List<DrawerItem> mDrawerItems;
    private Context mContext;
    private ViewPagerCategoryListener mListener;
    private int mClickedPosition;

    public NYCategoriesAdapter(List<DrawerItem> drawerItems, Context context) {
        mDrawerItems = drawerItems;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mDrawerItems.size();
    }

    @Override
    public DrawerItem getItem(int position) {
        return mDrawerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.category_item, parent, false);
            holder = new ViewHolder();
            holder.mCategory = convertView.findViewById(R.id.category_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mCategory.setText(getItem(position).getTitle());
        holder.mCategory.setBackgroundColor(getItem(position).isSelected()
                ? ContextCompat.getColor(mContext, R.color.colorAccent)
                : ContextCompat.getColor(mContext, R.color.background_main));

        holder.mCategory.setOnClickListener(v -> {
            for (int i = 0; i < mDrawerItems.size(); i++) {
                mDrawerItems.get(i).setSelected(i == position);
            }
            notifyDataSetChanged();
            setClickedPosition(position);
            mListener.setFocus(position);
            mListener.scrollTo(position);
        });

        return convertView;
    }

    private class ViewHolder {
        private TextView mCategory;
    }


    public void setListener(ViewPagerCategoryListener listener) {
        this.mListener = listener;
    }

    public interface ViewPagerCategoryListener {
        void setFocus(int position);

        void scrollTo(int position);
    }

    public void setClickedPosition(int clickedPosition) {
        this.mClickedPosition = clickedPosition;
    }

    public int getClickedPosition() {
        return mClickedPosition;
    }

}
