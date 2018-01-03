package com.kreml.andre.newyorktimesrevisited.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;

import com.kreml.andre.newyorktimesrevisited.R;
import com.kreml.andre.newyorktimesrevisited.activities.MainActivity;
import com.kreml.andre.newyorktimesrevisited.utils.NYSharedPreferences;

/**
 * Actual preferences
 */

public class NYPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        getPreferenceManager().findPreference(getString(R.string.log_out_preference)).setOnPreferenceClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey() == getString(R.string.log_out_preference)) {
            new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.log_out_prompt)
                    .setPositiveButton(R.string.yes, (dialog, which) -> {
                        NYSharedPreferences sharedPreferences = NYSharedPreferences.getsInstance(getActivity());
                        sharedPreferences.setUserLoggedIn(false);
                        sharedPreferences.clearPreferences();
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        getActivity().startActivity(intent);
                        getActivity().finish();
                    })
                    .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss())
                    .show();
            return true;
        }
        return false;
    }
}
