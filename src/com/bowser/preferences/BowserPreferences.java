package com.bowser.preferences;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.bowser.R;
 
public class BowserPreferences extends PreferenceActivity {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }
}