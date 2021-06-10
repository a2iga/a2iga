// ! rx1310 <rx1310@inbox.ru> | Copyright (c) rx1310, 2021 | MIT License

package ru.rx1310.app.a2iga.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.ComponentName;
import android.content.DialogInterface;

import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;

import android.provider.Settings;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.app.ActivityManager;

import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import java.util.Random;

import ru.rx1310.app.a2iga.R;
import ru.rx1310.app.a2iga.activities.MainActivity;
import ru.rx1310.app.a2iga.tasks.OTACheckTask;
import ru.rx1310.app.a2iga.utils.AppUtils;
import ru.rx1310.app.a2iga.utils.SharedPrefUtils;
import ru.rx1310.app.a2iga.Constants;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

	Preference dozeMode;
	Preference appVersion, appDeveloper, appFacts;
	Preference otaCheck, otaChangelog;
	Preference moduleInfo, moduleSettings;
	Preference securityFingerprintPerm;
	
	PowerManager oPowerManager;
	
	Intent oIntent = new Intent();
	Random oRandom = new Random();
	
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
		
		appFacts = findPreference("about.appFacts");
		
		appDeveloper = findPreference("about.appDeveloper");
		appDeveloper.setSummary(R.string.app_author);
		
		otaCheck = findPreference("ota.check");
		if (SharedPrefUtils.getStringData(getContext(), "ota.lastCheckDate") == null) otaCheck.setSummary(getString(R.string.pref_ota_check_desc_null));
		else otaCheck.setSummary(getString(R.string.pref_ota_check_desc) + " " + SharedPrefUtils.getStringData(getContext(), "ota.lastCheckDate"));
		
		otaChangelog = findPreference("ota.changelog");
		
		if (AppUtils.getVersionName(getContext(), getContext().getPackageName()).contains("0.")) {
			otaChangelog.setEnabled(false);
			otaChangelog.setSummary(R.string.beta_version_block);
		} else {
			otaChangelog.setEnabled(true);
			otaChangelog.setSummary(R.string.pref_ota_changelog_desc);
		}
		
		moduleInfo = findPreference("module.info");
		moduleInfo.setEnabled(false);
		moduleInfo.setSelectable(false);
		
		moduleSettings = findPreference("module.settings");
		moduleSettings.setEnabled(false);
		
		if (isAssistAppPkgName != null) {
			
			if (isAssistAppPkgName.contains("a2iga.module.")) {
				moduleSettings.setEnabled(true);
				moduleSettings.setSummary(String.format(getContext().getString(R.string.pref_module_settings_desc), AppUtils.getAppName(getContext(), isAssistAppPkgName)));
				moduleInfo.setSummary(String.format(getContext().getString(R.string.pref_module_info_desc), AppUtils.getVersionName(getContext(), isAssistAppPkgName), isAssistAppPkgName, AppUtils.getInstallDate(getContext(), isAssistAppPkgName, false, false)));
			} else {
				moduleSettings.setEnabled(false);
				moduleInfo.setSummary(String.format(getContext().getString(R.string.pref_module_info_desc_isNotModule), AppUtils.getAppName(getContext(), isAssistAppPkgName)));
			}
			
		} 
		
		securityFingerprintPerm = findPreference("security.fingerprintPerm");
		if (!AppUtils.isFingerprintSensorDetected(getContext())) securityFingerprintPerm.setEnabled(false);
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		
		dozeMode.setEnabled(dozeModePrefEnabled());
		appFacts.setSummary(Constants.randomPromts[oRandom.nextInt(17)]);
		
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
	
	// ? Обработка нажатия на Preferences
	public boolean onPreferenceTreeClick(PreferenceScreen prefScreen, Preference pref) {

		switch (pref.getKey()) {
			
			case "general.dozeMode":
				ignoreDozeMode();
				break;
			
			case "about.appDeveloper":
				AppUtils.openURL(getContext(), getString(R.string.app_author_url));
				break;

			case "about.appWebsite":
				AppUtils.openURL(getContext(), "https://a2iga.github.io");
				break;
				
			case "about.appSourceCode":
				AppUtils.openURL(getContext(), "https://github.com/a2iga/a2iga");
				break;
				
			case "about.appTelegram":
				AppUtils.openURL(getContext(), "https://t.me/rx1310_dev");
				break;
				
			case "ota.check":
				OTACheckTask.checkUpdates(getContext(), true);
				break;
				
			case "ota.changelog":
				AppUtils.openURL(getContext(), "https://github.com/a2iga/a2iga/blob/master/docs/changelog_" + AppUtils.getVersionCode(getContext(), getContext().getPackageName()) + ".md");
				break;
				
			case "module.settings":
				openModuleSettings();
				break;
				
			case "about.appVersion":
				debugOptions();
				break;

			default: break;

		}

		return super.onPreferenceTreeClick(prefScreen, pref);

	}
	
	// ? Если A2IGA в «белом списке», то вернется true, если нет — false
	boolean dozeModePrefEnabled() {
		if (!oPowerManager.isIgnoringBatteryOptimizations(getContext().getPackageName())) return true;
		else return false;
	}
	
	/* ? Вносим A2IGA в «белый» список режима Doze
	 *   Чтобы Android не ограничивал A2IGA и
	 *   не "усыплял" его необходимо внести A2IGA
	 *   в т.н. «белый» список режима Doze.
	 *   
	 *   Подробнее: https://clck.ru/U453L
	 */
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
	
	/* ? Запуск окна настроект модуля
	 *   Также при открытии Activity настроек
	 *	 модуля будет "послано" немного данных,
	 *   которые могут быть использованы в модуле.
	 *
	 *   Это обычный Intent, а поэтому данные эти получаются
	 *   след. образом:
	 *   
	 *   String isData = getIntent().getStringExtra("paramName");
	 */
	void openModuleSettings() {
		
		try {
			
			oIntent.setComponent(new ComponentName(isAssistAppPkgName, isAssistAppPkgName + ".ModuleSettings"));

			oIntent.putExtra("a2iga_versionCode", AppUtils.getVersionCode(getContext(), getContext().getPackageName())); // int
			oIntent.putExtra("a2iga_versionName", AppUtils.getVersionName(getContext(), getContext().getPackageName())); // string

			startActivity(oIntent);
			
		} catch(Exception e) {
			
			// ? Если активности ModuleSettings в модуле нет, то просто скажем
			//   юзеру, что настроек нет
			AppUtils.showToast(getContext(), String.format(getString(R.string.pref_module_settings_not), AppUtils.getAppName(getContext(), isAssistAppPkgName)));
			
		}
		
	} // openModuleSettings()
	
	void debugOptions() {
		
		AppUtils.showToast(getContext(), "Главное — не получить стрелу в колено 🏹");
		
		AlertDialog.Builder b = new AlertDialog.Builder(getContext(), R.style.AppTheme_Dialog_Alert);
		b.setTitle(R.string.debug);
		b.setIcon(R.drawable.ic_debug);
		b.setItems(R.array.debug_options, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface d, int p) {
				if (p == 0) {
					Intent i = new Intent(Intent.ACTION_DELETE);
					i.setData(Uri.parse("package:" + getContext().getPackageName()));
					startActivity(i);
				} if (p == 1) {
					((ActivityManager) getContext().getSystemService(getContext().ACTIVITY_SERVICE)).clearApplicationUserData();
				} if (p == 2) {
					AppUtils.showToast(getContext(), isAssistAppPkgName);
				} if (p == 3) {
					Intent sendPackageName = new Intent();
					sendPackageName.setAction(Intent.ACTION_SEND);
					sendPackageName.setClassName("ru.rx1310.app.a2iga", "ru.rx1310.app.a2iga.activities.MainActivity").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					sendPackageName.putExtra(Intent.EXTRA_TEXT, getContext().getPackageName());
					sendPackageName.setType("text/plain");
					startActivity(Intent.createChooser(sendPackageName, "Select a2iga"));
				} if (p == 4) {
					AppUtils.openURL(getContext(), "https://github.com/a2iga/a2iga/blob/master/docs/changelog_" + AppUtils.getVersionCode(getContext(), getContext().getPackageName()) + ".md");
				} if (p == 5) {
					startActivity(new Intent(android.provider.Settings.ACTION_VOICE_INPUT_SETTINGS));
				}
			}
		});
		b.show();
		
	}

}
