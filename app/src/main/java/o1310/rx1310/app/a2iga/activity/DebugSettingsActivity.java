/*
 * @author      rx1310 <rx1310@inbox.ru>
 * @copyright   Copyright (c) o1310, 2020
 * @license     MIT License
 */

package o1310.rx1310.app.a2iga.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.preference.PreferenceCategory;

public class DebugSettingsActivity extends PreferenceActivity {
	
	SwitchPreference showSystemApps;
	PreferenceCategory ctg_packagesList;
	
	protected void onCreate(Bundle sIS) {
		super.onCreate(sIS);
		
		setTitle("Debug settings");

		PreferenceScreen p = getPreferenceManager().createPreferenceScreen(this);
		
		setPreferenceScreen(p);
		
		ctg_packagesList = new PreferenceCategory(this);
		ctg_packagesList.setTitle("Apps list");
		
		showSystemApps = new SwitchPreference(this);
		showSystemApps.setKey("dbg_showSystemApps");
		showSystemApps.setTitle("Show system apps");

		p.addPreference(ctg_packagesList);
		p.addPreference(showSystemApps);

	}

	public boolean onPreferenceTreeClick(PreferenceScreen s, Preference p) {

		switch (p.getKey()) {

			case "dbg_showSystemApps": 
				//
				break;

		}

		return super.onPreferenceTreeClick(s, p);

	}

}
