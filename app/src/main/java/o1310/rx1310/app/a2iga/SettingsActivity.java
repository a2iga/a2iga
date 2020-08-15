/*
 * @author      rx1310 <rx1310@inbox.ru>
 * @copyright   Copyright (c) o1310, 2020
 * @license     MIT License
 */

package o1310.rx1310.app.a2iga;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends Activity implements View.OnClickListener {
	
	EditText inputAssistantPackageName;
	Button applyChanges, setAssistantApp, runAssistantApp;
	SharedPreferences sharedPrefs;
	SharedPreferences.Editor sharedPrefsEditor;
	
	final String PREF_ASSISTANT_PACKAGE_NAME = "assistantPackageName";
	
	@Override
    protected void onCreate(Bundle sIS) {
        super.onCreate(sIS);
		
        setContentView(R.layout.activity_settings);
		
		sharedPrefs = getSharedPreferences("a2iga_settings", MODE_PRIVATE);
		
		inputAssistantPackageName = findViewById(R.id.inputAssistantPackageName);
		
		applyChanges = findViewById(R.id.applyChanges);
		applyChanges.setOnClickListener(this);
		
		setAssistantApp = findViewById(R.id.setAssistantApp);
		setAssistantApp.setOnClickListener(this);
		
		runAssistantApp = findViewById(R.id.runAssistantApp);
		runAssistantApp.setOnClickListener(this);
		
		loadAssistantPackageName();
		
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
			
			default: break;
			
		}
		
	}
	
	private void saveAssistantPackageName() {
		
		sharedPrefsEditor = sharedPrefs.edit();
		sharedPrefsEditor.putString(PREF_ASSISTANT_PACKAGE_NAME, inputAssistantPackageName.getText().toString());
		sharedPrefsEditor.commit();
		
		Toast.makeText(this, R.string.message_changes_saved, Toast.LENGTH_LONG).show();
		
	}
	
	private void loadAssistantPackageName() {
		
		String assistantPackageName = sharedPrefs.getString(PREF_ASSISTANT_PACKAGE_NAME, "");
		
		inputAssistantPackageName.setText(assistantPackageName);
		
	}
	
    /*@Override
    protected void onDestroy() {
        super.onDestroy();
        saveAssistantPackageName();
		Toast.makeText(this, "A2IGA: Changes saved!", Toast.LENGTH_LONG).show();
    }*/
	
}
