/*
 * @author      rx1310 <rx1310@inbox.ru>
 * @copyright   Copyright (c) o1310, 2020
 * @license     MIT License
 */

package o1310.rx1310.app.a2iga.activity;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
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

import o1310.rx1310.app.a2iga.R;

public class SettingsActivity extends Activity implements View.OnClickListener {

	EditText inputAssistantPackageName;
	Button applyChanges, setAssistantApp, runAssistantApp, showPackagesList;
	TextView appVersion;
	SharedPreferences sharedPrefs;
	SharedPreferences.Editor sharedPrefsEditor;

	public static final String PREF_ASSISTANT_PACKAGE_NAME = "assistantPackageName";
	final String PREF_APP_FIRST_RUN = "appFirstRun";

	@Override
    protected void onCreate(Bundle sIS) {
        super.onCreate(sIS);

        setContentView(R.layout.activity_settings);
		setTitle(R.string.app_settings);

		sharedPrefs = getSharedPreferences("a2iga_settings", MODE_PRIVATE);

		String assistantPackageName = sharedPrefs.getString(PREF_ASSISTANT_PACKAGE_NAME, "");
		String appFirstRun = sharedPrefs.getString(PREF_APP_FIRST_RUN, "true");

		inputAssistantPackageName = findViewById(R.id.inputAssistantPackageName);
		inputAssistantPackageName.setText(assistantPackageName);

		applyChanges = findViewById(R.id.applyChanges);
		applyChanges.setOnClickListener(this);

		setAssistantApp = findViewById(R.id.setAssistantApp);
		setAssistantApp.setOnClickListener(this);

		runAssistantApp = findViewById(R.id.runAssistantApp);
		runAssistantApp.setOnClickListener(this);

		showPackagesList = findViewById(R.id.showPackagesList);
		showPackagesList.setOnClickListener(this);

		appVersion = findViewById(R.id.appVersion);
		appVersion.setOnClickListener(this);
		appVersion.setText(getString(R.string.app_version) + " " + thisAppVersion(this));

		if (appFirstRun == "true") {
			introDialog();
		}

	}

	void savePrefs(String key, String value) {

		sharedPrefsEditor = sharedPrefs.edit();
		sharedPrefsEditor.putString(key, value);
		sharedPrefsEditor.commit();

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

				// Сохраняем данные из поля ввода
			case R.id.applyChanges:
				savePrefs(PREF_ASSISTANT_PACKAGE_NAME, inputAssistantPackageName.getText().toString());
				Toast.makeText(this, R.string.message_changes_saved, Toast.LENGTH_LONG).show();
				break;

				// Запуск ассистента (для теста)
			case R.id.runAssistantApp:
				startActivity(new Intent(Intent.ACTION_ASSIST).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
				break;

				// Переход в настройки ассистентов
			case R.id.setAssistantApp:
				startActivity(new Intent(android.provider.Settings.ACTION_VOICE_INPUT_SETTINGS));
				break;

				// Переход к списку Package Name
			case R.id.showPackagesList:
				Toast.makeText(this, R.string.message_wait, Toast.LENGTH_SHORT).show();
				startActivity(new Intent (this, PackagesListActivity.class));
				break;

			case R.id.appVersion:
				startActivity(new Intent (Intent.ACTION_VIEW, Uri.parse("https://o1310.github.io")));
				break;

			default: break;

		}

	}

	// from RebootManager.java (RebootManager) | git:https://github.com/o1310/RebootManager
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

	void introDialog(){

		// создаем диалог
		AlertDialog.Builder b = new AlertDialog.Builder(this);

		b.setTitle(R.string.intro_dialog_title);
		b.setIcon(R.drawable.ic_logo);
		b.setMessage(R.string.intro_message);
		b.setCancelable(false);
		b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // обработка нажатия кнопки "Да"
				public void onClick(DialogInterface d, int i) {
					savePrefs(PREF_APP_FIRST_RUN, "false");
				}
			});
		b.setNeutralButton(R.string.intro_dialog_action_source_code, new DialogInterface.OnClickListener() { // обработка нажатия кнопки "Да"
				public void onClick(DialogInterface d, int i) {
					startActivity(new Intent (Intent.ACTION_VIEW, Uri.parse("https://github.com/o1310/a2iga")));
					savePrefs(PREF_APP_FIRST_RUN, "false");
				}
			});

		AlertDialog a = b.create(); // создаем диалог

		a.show(); // отображаем диалог

	}

    /*@Override
	 protected void onDestroy() {
	 super.onDestroy();
	 saveAssistantPackageName();
	 Toast.makeText(this, "A2IGA: Changes saved!", Toast.LENGTH_LONG).show();
	 }*/

}
 
