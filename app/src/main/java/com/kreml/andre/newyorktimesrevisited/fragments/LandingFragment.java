package com.kreml.andre.newyorktimesrevisited.fragments;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kreml.andre.newyorktimesrevisited.utils.Constants;
import com.kreml.andre.newyorktimesrevisited.activities.MainActivity;
import com.kreml.andre.newyorktimesrevisited.R;
import com.kreml.andre.newyorktimesrevisited.databinding.FragmentLandingBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class LandingFragment extends Fragment implements View.OnClickListener {

    private MainActivity mActivity;

    public LandingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (MainActivity) context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentLandingBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_landing,
                container, false);
        binding.signUp.setOnClickListener(this);
        binding.logIn.setOnClickListener(this);
        return binding.getRoot();
    }

    @Override
    public void onClick(View v) {
        int fragmentId = 0;
        switch (v.getId()) {
            case R.id.sign_up:
                fragmentId = Constants.SIGN_UP_FRAGMENT;
                break;
            case R.id.log_in:
                fragmentId = Constants.LOGIN_FRAGMENT;
                break;
        }
        mActivity.callFromFragment(fragmentId);
    }
}
