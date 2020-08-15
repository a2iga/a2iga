/*
 * @author      rx1310 <rx1310@inbox.ru>
 * @copyright   Copyright (c) o1310, 2020
 * @license     MIT License
 */

package o1310.rx1310.app.a2iga;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends Activity implements View.OnClickListener {
	
	EditText inputAssistantPackageName;
	Button applyChanges, setAssistantApp, runAssistantApp;
	TextView appVersion;
	SharedPreferences sharedPrefs;
	SharedPreferences.Editor sharedPrefsEditor;
	
	final String PREF_ASSISTANT_PACKAGE_NAME = "assistantPackageName";
	
	@Override
    protected void onCreate(Bundle sIS) {
        super.onCreate(sIS);
		
        setContentView(R.layout.activity_settings);
		setTitle(R.string.app_settings);
		
		sharedPrefs = getSharedPreferences("a2iga_settings", MODE_PRIVATE);
		
		String assistantPackageName = sharedPrefs.getString(PREF_ASSISTANT_PACKAGE_NAME, "");
		
		inputAssistantPackageName = findViewById(R.id.inputAssistantPackageName);
		inputAssistantPackageName.setText(assistantPackageName);
		
		applyChanges = findViewById(R.id.applyChanges);
		applyChanges.setOnClickListener(this);
		
		setAssistantApp = findViewById(R.id.setAssistantApp);
		setAssistantApp.setOnClickListener(this);
		
		runAssistantApp = findViewById(R.id.runAssistantApp);
		runAssistantApp.setOnClickListener(this);
		
		appVersion = findViewById(R.id.appVersion);
		appVersion.setOnClickListener(this);
		appVersion.setText(getString(R.string.app_version) + " " + thisAppVersion(this));
		
	}
	
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
			
			case R.id.applyChanges:
				saveAssistantPackageName();
				break;
				
			case R.id.runAssistantApp:
				startActivity(new Intent(Intent.ACTION_ASSIST).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
				break;
				
			case R.id.setAssistantApp:
				startActivity(new Intent(android.provider.Settings.ACTION_VOICE_INPUT_SETTINGS));
				break;
				
			case R.id.appVersion:
				startActivity(new Intent (Intent.ACTION_VIEW, Uri.parse("https://o1310.github.io")));
				break;
			
			default: break;
			
		}
		
	}
	
	private void saveAssistantPackageName() {
		
		sharedPrefsEditor = sharedPrefs.edit();
		sharedPrefsEditor.putString(PREF_ASSISTANT_PACKAGE_NAME, inputAssistantPackageName.getText().toString());
		sharedPrefsEditor.commit();
		
		Toast.makeText(this, R.string.message_changes_saved, Toast.LENGTH_LONG).show();
		
	}
	
	// from RebootManager (https://github.com/o1310/RebootManager)
	public static String thisAppVersion(Context c) {

		String s, a;
		int v;

		PackageManager m = c.getPackageManager();

		try {
			
			PackageInfo i = m.getPackageInfo(c.getPackageName(), 0);
			
			s = i.versionName; // Получаем название версии
			v = i.versionCode; // Получаем код версии
			a = s + "." + v;   // Объединяем название и код версии для "вида"
			
		} catch(PackageManager.NameNotFoundException e) {
			
			// в случае ошибки вернем "error(String.appVersion)"
			e.printStackTrace();
			a = "error(String.appVersion)";
			
		}

		return a; // вернем версию в формате НАЗВАНИЕ.КОД (напр.: 1.200915)

	}
	
    /*@Override
    protected void onDestroy() {
        super.onDestroy();
        saveAssistantPackageName();
		Toast.makeText(this, "A2IGA: Changes saved!", Toast.LENGTH_LONG).show();
    }*/
	
}
