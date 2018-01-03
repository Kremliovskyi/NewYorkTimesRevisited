package com.kreml.andre.newyorktimesrevisited.models;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.kreml.andre.newyorktimesrevisited.db.User;
import com.kreml.andre.newyorktimesrevisited.utils.NYSharedPreferences;
import com.kreml.andre.newyorktimesrevisited.utils.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.realm.Realm;


public class WorkingActivityModel extends AndroidViewModel {

    private User mUser = null;
    private ArrayList<String> mQueries = generateCategoryList();
    private List<DrawerItem> mDrawerItemList;

    public WorkingActivityModel(@NonNull Application application) {
        super(application);
    }

    public final MutableLiveData<Bitmap> mSelectedImage = new MutableLiveData<>();

    public User getUser() {
        if (mUser != null) {
            return mUser;
        }
        final String userName = NYSharedPreferences.getsInstance(getApplication()).getUsername();
        Realm realm = Realm.getDefaultInstance();
        mUser = realm.where(User.class).equalTo(User.Columns.USERNAME, userName).findFirst();

        return mUser;
    }

    public ArrayList<String> getQueries() {
        return mQueries;
    }

    private ArrayList<String> generateCategoryList() {
        if (mQueries != null) {
            return mQueries;
        }
        ArrayList<String> arrayList = Utils.readAssets();
        Iterator<String> iterator = arrayList.iterator();
        while (iterator.hasNext()) {
            String category = iterator.next();
            boolean isChecked = NYSharedPreferences.getsInstance(getApplication()).getCategoryPreference(category);
            if (!isChecked) {
                iterator.remove();
            }
        }
        return arrayList;
    }

    public List<DrawerItem> generateDrawerList(int clickedPosition) {
        if (mDrawerItemList != null) {
            return mDrawerItemList;
        }
        mDrawerItemList = new ArrayList<>(mQueries.size());
        for (int i = 0; i < mQueries.size(); i++) {
            DrawerItem drawerItem = new DrawerItem(mQueries.get(i), i == clickedPosition);
            mDrawerItemList.add(drawerItem);
        }
        return mDrawerItemList;
    }

    public void putImageToDB(final String path, String userName) {
        new Thread(() -> {
            try (Realm realm = Realm.getDefaultInstance()) {
                realm.beginTransaction();
                User user = realm.where(User.class).equalTo(User.Columns.USERNAME, userName).findFirst();
                if (user != null) {
                    user.setPathToImage(path);
                }
                realm.commitTransaction();
            }
        }).start();
    }
}
