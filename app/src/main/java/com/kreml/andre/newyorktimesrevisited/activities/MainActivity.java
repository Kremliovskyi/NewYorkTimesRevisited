package com.kreml.andre.newyorktimesrevisited.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.kreml.andre.newyorktimesrevisited.utils.NYSharedPreferences;
import com.kreml.andre.newyorktimesrevisited.R;
import com.kreml.andre.newyorktimesrevisited.databinding.ActivityMainBinding;
import com.kreml.andre.newyorktimesrevisited.fragments.LandingFragment;
import com.kreml.andre.newyorktimesrevisited.fragments.LogInFragment;
import com.kreml.andre.newyorktimesrevisited.fragments.SignUpFragment;

import static com.kreml.andre.newyorktimesrevisited.utils.Constants.LOGIN_FRAGMENT;
import static com.kreml.andre.newyorktimesrevisited.utils.Constants.SIGN_UP_FRAGMENT;
import static com.kreml.andre.newyorktimesrevisited.utils.Constants.WORKING_ACTIVITY;

/**
 * Activity we always start from
 */
public class MainActivity extends AppCompatActivity {

    private FragmentManager mFragmentManager;
    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        NYSharedPreferences preferences = NYSharedPreferences.getsInstance(this);
        mFragmentManager = getFragmentManager();
        if (!preferences.getUserLoggedIn()) {
            startFragment(new LandingFragment(), false);
        } else {
            startWorkingActivity();
        }
    }

    private void startWorkingActivity() {
        Intent intent = new Intent(this, WorkingActivity.class);
        startActivity(intent);
        finish();
    }

    private void startFragment(Fragment fragmentToStart, boolean addToBackStack) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.replace(mBinding.fragmentLanding.getId(), fragmentToStart).commit();
    }

    public void callFromFragment(int fragmentConstant) {
        Fragment fragment;
        switch (fragmentConstant) {
            case WORKING_ACTIVITY:
                startWorkingActivity();
                return;
            case LOGIN_FRAGMENT:
                fragment = new LogInFragment();
                break;
            case SIGN_UP_FRAGMENT:
                fragment = new SignUpFragment();
                break;
            default:
                fragment = new LandingFragment();
        }
        startFragment(fragment, true);
    }
}
