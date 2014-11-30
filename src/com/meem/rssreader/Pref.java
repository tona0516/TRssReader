package com.meem.rssreader;


import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.sample.rssreader.R;

public class Pref extends PreferenceActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.pref);
    }
}