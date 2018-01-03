package com.kreml.andre.newyorktimesrevisited.fragments;

import android.app.Fragment;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kreml.andre.newyorktimesrevisited.utils.Constants;
import com.kreml.andre.newyorktimesrevisited.activities.MainActivity;
import com.kreml.andre.newyorktimesrevisited.utils.NYSharedPreferences;
import com.kreml.andre.newyorktimesrevisited.R;
import com.kreml.andre.newyorktimesrevisited.databinding.LogInFragmentBinding;
import com.kreml.andre.newyorktimesrevisited.db.User;
import com.kreml.andre.newyorktimesrevisited.utils.Utils;

import io.realm.Realm;
import io.realm.RealmResults;


public class LogInFragment extends Fragment {

    private LogInFragmentBinding mBinding;
    private MainActivity mMainActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mMainActivity = (MainActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.log_in_fragment, container, false);
        mBinding.usernameLogin.setOnClickListener(v -> mBinding.usernameLogin.setCursorVisible(true));
        mBinding.loginButton.setOnClickListener(v -> validateUser());
        return mBinding.getRoot();
    }

    private void validateUser() {
        String userName = mBinding.usernameLogin.getText().toString();
        String password = mBinding.passwordLogin.getText().toString();
        Realm realm = Realm.getDefaultInstance();
        RealmResults<User> results = realm.where(User.class).findAllAsync();
        for (User user : results) {
            if (user.getUsername().equals(userName) && user.getPassword().equals(Utils.generateHash(password))) {
                NYSharedPreferences preferences = NYSharedPreferences.getsInstance(getActivity());
                preferences.setUserLoggedIn(true);
                preferences.setUserName(user.getUsername());
                mMainActivity.callFromFragment(Constants.WORKING_ACTIVITY);
                return;
            }
        }
        mBinding.usernameLogin.getText().clear();
        mBinding.passwordLogin.getText().clear();
        showAuthenticationFailedDialog();
    }

    private void showAuthenticationFailedDialog() {
        new AlertDialog.Builder(getActivity()).setTitle(R.string.authentication_failed)
                .setMessage(R.string.credentials_not_registered)
                .setPositiveButton(R.string.sign_up, (dialog, which) -> {
                    mMainActivity.callFromFragment(Constants.SIGN_UP_FRAGMENT);
                })
                .setNegativeButton(R.string.try_again, (dialog, which) -> {
                    mBinding.usernameLogin.getText().clear();
                    mBinding.passwordLogin.getText().clear();
                    dialog.dismiss();
                }).show();
    }
}
