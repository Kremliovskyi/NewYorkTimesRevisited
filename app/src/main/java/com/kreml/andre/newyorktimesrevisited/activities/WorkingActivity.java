package com.kreml.andre.newyorktimesrevisited.activities;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.kreml.andre.newyorktimesrevisited.R;
import com.kreml.andre.newyorktimesrevisited.content.NYCategoriesAdapter;
import com.kreml.andre.newyorktimesrevisited.content.NYFragmentPagerAdapter;
import com.kreml.andre.newyorktimesrevisited.content.RoundedImage;
import com.kreml.andre.newyorktimesrevisited.db.User;
import com.kreml.andre.newyorktimesrevisited.models.DrawerItem;
import com.kreml.andre.newyorktimesrevisited.models.WorkingActivityModel;
import com.kreml.andre.newyorktimesrevisited.utils.Constants;
import com.kreml.andre.newyorktimesrevisited.utils.InternetChangeReceiver;
import com.kreml.andre.newyorktimesrevisited.utils.NYSharedPreferences;
import com.kreml.andre.newyorktimesrevisited.utils.Utils;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Activity holding the list of articles and a drawer
 */

public class WorkingActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        NYCategoriesAdapter.ViewPagerCategoryListener, View.OnClickListener {

    private static final String TAG = MainActivity.class.getName();
    private File mPhotoFile;
    private ImageView mUserImage;
    private ViewPager mPager;
    private DrawerLayout mDrawer;
    private ListView mCategoriesList;
    private int mClickedPosition;
    private NYCategoriesAdapter mAdapter;
    private WorkingActivityModel mModel;
    private User mUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!InternetChangeReceiver.isNetworkAvailable()) {
            Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
        }
        setContentView(R.layout.working_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initDrawer(toolbar);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);

        mModel = ViewModelProviders.of(this).get(WorkingActivityModel.class);
        mUser = mModel.getUser();
        if (mUser == null) {
            // Actually we can not land here unless some db crash or smt. related
            Log.e(TAG, "Critical error, we can not work without user.");
            NYSharedPreferences.getsInstance(this).clearPreferences();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        initUserNameView(headerView);
        initUserAvatar(headerView);
        initFragmentPager();
        initCategories(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.SELECT_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    handleSelectedImage(data);
                }
                break;
            case Constants.CAPTURE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    setPic(mPhotoFile.getAbsolutePath());
                    mModel.putImageToDB(mPhotoFile.getAbsolutePath(), mUser.getUsername());
                }
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mAdapter.getClickedPosition() == 0) {
            outState.putInt(Constants.CLICKED_POSITION, mClickedPosition);
        } else {
            outState.putInt(Constants.CLICKED_POSITION, mAdapter.getClickedPosition());
        }
    }

    @Override
    public final boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(WorkingActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void initCategories(@Nullable Bundle savedInstanceState) {
        mCategoriesList = mDrawer.findViewById(R.id.categories_list);
        if (savedInstanceState != null) {
            mClickedPosition = savedInstanceState.getInt(Constants.CLICKED_POSITION);
        }
        List<DrawerItem> drawerItemList = mModel.generateDrawerList(mClickedPosition);
        mAdapter = new NYCategoriesAdapter(drawerItemList, this);
        mAdapter.setListener(this);
        mCategoriesList.setAdapter(mAdapter);
    }

    private void initDrawer(Toolbar toolbar) {
        mDrawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void initFragmentPager() {
        mPager = findViewById(R.id.pager);
        mPager.setOffscreenPageLimit(Constants.PAGE_LIMIT);
        PagerTabStrip tabStrip = mPager.findViewById(R.id.pagerTabStrip);
        tabStrip.setDrawFullUnderline(true);
        tabStrip.setTabIndicatorColor(ContextCompat.getColor(this, R.color.background_main));
        NYFragmentPagerAdapter fragmentPagerAdapter = new NYFragmentPagerAdapter(getSupportFragmentManager(),
                mModel.getQueries());
        mPager.setAdapter(fragmentPagerAdapter);
    }

    private void initUserNameView(View headerView) {
        TextView mUserName = headerView.findViewById(R.id.textView);
        mUserName.setText(mUser.getUsername());
    }

    private void initUserAvatar(View headerView) {
        mUserImage = headerView.findViewById(R.id.imageView);
        tryToSetImage();
        mUserImage.setOnClickListener(this);
    }

    private void handleSelectedImage(Intent data) {
        Uri selectedImage = data.getData();
        if (selectedImage != null) {
            try (InputStream inputStream = getContentResolver().openInputStream(selectedImage)) {
                if (inputStream != null) {
                    File savedImage = new File(getFilesDir(), new SimpleDateFormat("yyyyMMdd_HHmmss",
                            Locale.getDefault()).format(new Date()));
                    FileOutputStream outputStream = new FileOutputStream(savedImage);
                    outputStream.write(IOUtils.toByteArray(inputStream));
                    inputStream.close();
                    outputStream.close();
                    setPic(savedImage.getPath());
                    mModel.putImageToDB(savedImage.getPath(), mUser.getUsername());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.PERMISSION_REQUEST_CAMERA:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                }
                break;
            default:
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void tryToSetImage() {
        MutableLiveData<Bitmap> userAvatarBitmap = mModel.mSelectedImage;
        userAvatarBitmap.observe(this, bitmap ->
                mUserImage.setImageDrawable(new RoundedImage(bitmap)));
        Bitmap avatar = userAvatarBitmap.getValue();
        if (avatar == null) {
            String pathToImage = mUser.getPathToImage();
            if (!TextUtils.isEmpty(pathToImage)) {
                setPic(pathToImage);
            }
        }
    }

    @Override
    public void setFocus(int position) {
        mPager.setCurrentItem(position);
        mDrawer.closeDrawer(Gravity.START);
    }

    @Override
    public void scrollTo(int position) {
        mCategoriesList.setSelection(position);
    }

    private void setPic(String photoFilePath) {
        Glide
                .with(getApplicationContext())
                .asBitmap()
                .load(Uri.fromFile(new File(photoFilePath)))
                .into(new SimpleTarget<Bitmap>(Constants.BITMAP_SIZE, Constants.BITMAP_SIZE) {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        mModel.mSelectedImage.postValue(resource);
                    }
                });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            mPhotoFile = null;
            try {
                mPhotoFile = Utils.createImageFile(this);
            } catch (IOException ex) {
                Log.d(TAG, ex.toString());
            }
            if (mPhotoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        Constants.PHOTO_FILE_PROVIDER_AUTHORITY, mPhotoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, Constants.CAPTURE_PICTURE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imageView) {
            new AlertDialog.Builder(WorkingActivity.this)
                    .setTitle(R.string.set_image_title)
                    .setMessage(R.string.set_image_text)
                    .setPositiveButton(R.string.take_new, (dialog, which) -> {
                        if (ContextCompat.checkSelfPermission(WorkingActivity.this,
                                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            dispatchTakePictureIntent();
                        } else {
                            ActivityCompat.requestPermissions(WorkingActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    Constants.PERMISSION_REQUEST_CAMERA);
                        }
                    })
                    .setNegativeButton(R.string.gallery, (dialog, which) -> {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(intent, Constants.SELECT_PICTURE);
                    })
                    .show();
        }
    }
}
