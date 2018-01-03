package com.kreml.andre.newyorktimesrevisited.fragments;

import android.app.Fragment;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.kreml.andre.newyorktimesrevisited.R;
import com.kreml.andre.newyorktimesrevisited.activities.MainActivity;
import com.kreml.andre.newyorktimesrevisited.databinding.SignUpFragmentBinding;
import com.kreml.andre.newyorktimesrevisited.utils.Constants;
import com.kreml.andre.newyorktimesrevisited.utils.NYSharedPreferences;
import com.kreml.andre.newyorktimesrevisited.utils.SignUpFragmentController;
import com.kreml.andre.newyorktimesrevisited.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class SignUpFragment extends Fragment implements View.OnClickListener {

    private MainActivity mActivity;
    private List<String> mUserNames = new ArrayList<>();
    private SignUpFragmentBinding mBinding;
    SignUpFragmentController mController;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (MainActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mController = new SignUpFragmentController();
        mController.fillExistingUserList(mUserNames);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.sign_up_fragment, container, false);
        mBinding.usernameInput.setOnClickListener(this);
        mBinding.emailInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (!Patterns.EMAIL_ADDRESS.matcher(mBinding.emailInput.getText().toString()).matches()) {
                    mBinding.emailInput.setError(getString(R.string.invalid_email));
                }
            } else {
                mBinding.emailInput.setError(null);
            }
        });
        mBinding.signIn.setOnClickListener(this);
        return mBinding.getRoot();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.username_input:
                mBinding.usernameInput.setCursorVisible(true);
                break;
            case R.id.sign_in:
                Editable usernameInput = mBinding.usernameInput.getText();
                Editable emailInput = mBinding.emailInput.getText();
                Editable enterPasswordInput = mBinding.enterPasswordInput.getText();
                Editable confirmPasswordInput = mBinding.confirmPasswordInput.getText();
                if (!TextUtils.isEmpty(usernameInput.toString()) &&
                        !TextUtils.isEmpty(emailInput.toString()) &&
                        !TextUtils.isEmpty(enterPasswordInput.toString()) &&
                        !TextUtils.isEmpty(confirmPasswordInput.toString())) {

                    String userName = usernameInput.toString();
                    String email = emailInput.toString();
                    String password = enterPasswordInput.toString();
                    String confirmPassword = confirmPasswordInput.toString();

                    if (validatePasswords(usernameInput, emailInput, enterPasswordInput,
                            confirmPasswordInput, password, confirmPassword)) {
                        return;
                    }
                    if (mController.checkIfSuchUserExists(userName, mUserNames, mActivity)) {
                        return;
                    }

                    NYSharedPreferences.getsInstance(mActivity).setUserLoggedIn(true);
                    NYSharedPreferences.getsInstance(mActivity).setUserName(userName);
                    Utils.createUserAccount(userName, email, Utils.generateHash(password));
                    mActivity.callFromFragment(Constants.WORKING_ACTIVITY);
                } else {
                    Toast.makeText(mActivity, R.string.fill_request, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private boolean validatePasswords(Editable usernameInput, Editable emailInput,
                                      Editable enterPasswordInput, Editable confirmPasswordInput,
                                      String password, String confirmPassword) {
        if (!Utils.validatePassword(password)) {
            usernameInput.clear();
            emailInput.clear();
            enterPasswordInput.clear();
            confirmPasswordInput.clear();
            mBinding.enterPasswordInput.setError(getString(R.string.password_requirements));
            return true;
        }
        if (!confirmPassword.equals(password)) {
            Snackbar.make(mBinding.linearLayout, R.string.confirm_pass_incorrect, Snackbar.LENGTH_LONG).show();
            enterPasswordInput.clear();
            confirmPasswordInput.clear();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(mBinding.confirmPasswordInput.getWindowToken(), 0);
            }
            return true;
        }
        return false;
    }
}

