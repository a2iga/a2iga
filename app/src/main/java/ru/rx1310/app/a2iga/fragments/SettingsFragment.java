// ! rx1310 <rx1310@inbox.ru> | Copyright (c) rx1310, 2021 | MIT License

package ru.rx1310.app.a2iga.fragments;

import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;

import android.provider.Settings;
import android.net.Uri;

import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import ru.rx1310.app.a2iga.R;
import ru.rx1310.app.a2iga.activities.MainActivity;
import ru.rx1310.app.a2iga.tasks.OTACheckTask;
import ru.rx1310.app.a2iga.utils.AppUtils;
import ru.rx1310.app.a2iga.utils.SharedPrefUtils;
import ru.rx1310.app.a2iga.Constants;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

	Preference dozeMode;
	Preference appVersion, appDeveloper;
	Preference otaCheck;
	Preference moduleInfo, moduleSettings;
	PowerManager oPowerManager;
	
	Intent oIntent = new Intent();
	
	String isAssistAppPkgName;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		
		isAssistAppPkgName = SharedPrefUtils.getStringData(getContext(), Constants.ASSIST_APP_PKGNAME);
		
		oPowerManager = (PowerManager) getContext().getSystemService(getContext().POWER_SERVICE);
		
		dozeMode = findPreference("general.dozeMode");
		dozeMode.setPersistent(true);
		
		appVersion = findPreference("about.appVersion");
		appVersion.setSummary(AppUtils.getVersionName(getContext(), getContext().getPackageName()) + "." + AppUtils.getVersionCode(getContext(), getContext().getPackageName()));
		
		appDeveloper = findPreference("about.appDeveloper");
		appDeveloper.setSummary(R.string.app_author);
		
		otaCheck = findPreference("ota.check");
		if (SharedPrefUtils.getStringData(getContext(), "ota.lastCheckDate") == null) otaCheck.setSummary("Check");
		else otaCheck.setSummary(getString(R.string.pref_ota_check_desc) + " " + SharedPrefUtils.getStringData(getContext(), "ota.lastCheckDate"));
		
		moduleInfo = findPreference("module.info");
		moduleInfo.setEnabled(false);
		if (isAssistAppPkgName.contains("a2iga.module.")) moduleInfo.setSummary(String.format(getContext().getString(R.string.pref_module_info_desc), AppUtils.getVersionName(getContext(), isAssistAppPkgName), isAssistAppPkgName, AppUtils.getInstallDate(getContext(), isAssistAppPkgName, false, false)));
		else moduleInfo.setSummary(String.format(getContext().getString(R.string.pref_module_info_desc_isNotModule), AppUtils.getAppName(getContext(), isAssistAppPkgName)));
		
		moduleSettings = findPreference("module.settings");
		
		if (isAssistAppPkgName.contains("a2iga.module.")) {
			moduleSettings.setEnabled(true);
			moduleSettings.setSummary(String.format(getContext().getString(R.string.pref_module_settings_desc), AppUtils.getAppName(getContext(), isAssistAppPkgName)));
		} else {
			moduleSettings.setEnabled(false);
		}
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		
		dozeMode.setEnabled(dozeModePrefEnabled());

	}
  

    @Override
    public void onPause() {
        super.onPause();
        
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
		
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        
        otaCheck.setSummary(getString(R.string.pref_ota_check_desc) + " " + SharedPrefUtils.getStringData(getContext(), "ota.lastCheckDate"));

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
				
			case "about.appSourceCode":
				AppUtils.openURL(getContext(), "https://github.com/rx1310/a2iga");
				break;
				
			case "about.appTelegram":
				AppUtils.openURL(getContext(), "https://t.me/rx1310_dev");
				break;
				
			case "ota.check":
				OTACheckTask.checkUpdates(getContext(), true);
				break;
				
			case "ota.changelog":
				AppUtils.openURL(getContext(), "https://github.com/rx1310/a2iga/docs/changelog_" + AppUtils.getVersionCode(getContext(), getContext().getPackageName()) + ".md");
				break;

			default: break;

		}

		return super.onPreferenceTreeClick(prefScreen, pref);

	}
	
	boolean dozeModePrefEnabled() {
		if (!oPowerManager.isIgnoringBatteryOptimizations(getContext().getPackageName())) return true;
		else return false;
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
