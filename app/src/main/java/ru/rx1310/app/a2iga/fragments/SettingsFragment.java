// ! rx1310 <rx1310@inbox.ru> | Copyright (c) rx1310, 2021 | MIT License

package ru.rx1310.app.a2iga.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import ru.rx1310.app.a2iga.R;
import ru.rx1310.app.a2iga.utils.AppUtils;
import android.support.annotation.NonNull;

public class SettingsFragment extends PreferenceFragment {

	Preference dozeMode;
	Preference appVersion, appDeveloper;
	PowerManager oPowerManager;
	Intent oIntent = new Intent();
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		
		oPowerManager = (PowerManager) getContext().getSystemService(getContext().POWER_SERVICE);
		
		dozeMode = findPreference("general.dozeMode");
		
		appVersion = findPreference("about.appVersion");
		appVersion.setSummary(AppUtils.getVersionName(getContext(), getContext().getPackageName()) + "." + AppUtils.getVersionCode(getContext(), getContext().getPackageName()));
		
		appDeveloper = findPreference("about.appDeveloper");
		appDeveloper.setSummary(appDeveloperVerify());
		
		
	}

	@Override
	public void onResume() {
		super.onResume();
		
		dozeMode.setEnabled(dozeModePrefEnabled());
		
		
	}
	
	public boolean onPreferenceTreeClick(PreferenceScreen prefScreen, Preference pref) {

		switch (pref.getKey()) {
			
			case "general.dozeMode":
				ignoreDozeMode();
				break;
			
			case "about.appDeveloper":
				AppUtils.openURL(getContext(), getString(R.string.app_author_url));
				break;

			case "about.appWebsite":
				AppUtils.openURL(getContext(), getString(R.string.app_author_url) + "/a2iga");
				break;

			default: break;

		}

		return super.onPreferenceTreeClick(prefScreen, pref);

	}
	
	boolean dozeModePrefEnabled() {
		if (!oPowerManager.isIgnoringBatteryOptimizations(getContext().getPackageName())) return true;
		else return false;
	}
	
	String appDeveloperVerify() {
		if (getString(R.string.app_author) == "rx1310") return "rx1310";
		else return getString(R.string.app_author) + " (unofficial maintainer)";
	}
	
	void ignoreDozeMode() {
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			
			
			String pn = getContext().getPackageName();
			
			if (!oPowerManager.isIgnoringBatteryOptimizations(pn)) {
				
				oIntent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
				oIntent.setData(Uri.parse("package:" + pn));
				
				startActivity(oIntent);
				
			}
			
		}
		
	} // ignoreDozeMode()

}
