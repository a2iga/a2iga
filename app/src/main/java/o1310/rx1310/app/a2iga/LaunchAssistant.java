/*
 * @author      rx1310 <rx1310@inbox.ru>
 * @copyright   Copyright (c) o1310, 2020
 * @license     MIT License
 */

package o1310.rx1310.app.a2iga;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

public class LaunchAssistant extends Activity {

	SharedPreferences sharedPrefs;
	SharedPreferences.Editor sharedPrefsEditor;
	
	final String PREF_ASSISTANT_PACKAGE_NAME = "assistantPackageName";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sharedPrefs = getSharedPreferences("a2iga_settings", MODE_PRIVATE);
		String assistantPackageName = sharedPrefs.getString(PREF_ASSISTANT_PACKAGE_NAME, "");
		startNewActivity(assistantPackageName);
	}
	
	public void startNewActivity(String packageName) {
		Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
		if (intent == null) {
			// Bring user to the market or let them choose an app?
			intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("market://details?id=" + packageName));
		}
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
	
}
