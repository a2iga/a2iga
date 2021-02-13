// ! rx1310 <rx1310@inbox.ru> | Copyright (c) rx1310, 2021 | MIT License

package ru.rx1310.app.a2iga.activities;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import ru.rx1310.app.a2iga.R;
import ru.rx1310.app.a2iga.utils.AppUtils;

public class SettingsActivity extends PreferenceActivity {

	ListView mListView;

	SharedPreferences mSharedPrefs;
	SharedPreferences.Editor mSharedPrefsEditor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.app_settings);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		mSharedPrefsEditor = mSharedPrefs.edit();

		mListView = findViewById(android.R.id.list);
		mListView.setDivider(null);
		
	}

	public boolean onPreferenceTreeClick(PreferenceScreen prefScreen, Preference pref) {

		switch (pref.getKey()) {

			case "key":
				
				break;

			default: break;

		}

		return super.onPreferenceTreeClick(prefScreen, pref);

	}

}
