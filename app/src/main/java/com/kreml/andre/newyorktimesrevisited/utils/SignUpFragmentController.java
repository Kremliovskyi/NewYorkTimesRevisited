package com.kreml.andre.newyorktimesrevisited.utils;

import android.support.v7.app.AlertDialog;

import com.kreml.andre.newyorktimesrevisited.R;
import com.kreml.andre.newyorktimesrevisited.activities.MainActivity;
import com.kreml.andre.newyorktimesrevisited.db.User;

import java.util.List;

import javax.annotation.Nonnull;

import io.realm.Realm;
import io.realm.RealmResults;

public class SignUpFragmentController {

    public void fillExistingUserList(@Nonnull List<String> userNameList) {
        try (Realm realm = Realm.getDefaultInstance()) {
            RealmResults<User> users = realm.where(User.class).findAllAsync();
            for (User user : users) {
                userNameList.add(user.getUsername());
            }
        }
    }

    public boolean checkIfSuchUserExists(String userName, @Nonnull List<String> userNameList,
                                         @Nonnull MainActivity activity) {
        if (userNameList.contains(userName)) {
            new AlertDialog.Builder(activity)
                    .setTitle(R.string.sign_up_failed)
                    .setMessage(R.string.user_already_registered)
                    .setPositiveButton(R.string.sign_in, (dialog, which) -> {
                        activity.callFromFragment(Constants.LOGIN_FRAGMENT);
                    })
                    .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                    .show();
            return true;
        }
        return false;
    }
}
