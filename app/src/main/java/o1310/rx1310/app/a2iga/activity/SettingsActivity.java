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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;

import android.graphics.Color;

import android.net.Uri;

import android.os.Bundle;

import android.support.annotation.ColorInt;

import android.text.Html;

import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.FrameLayout;

import o1310.rx1310.app.a2iga.R;
import o1310.rx1310.app.a2iga.utils.SettingsUtils;
import android.text.TextUtils;

public class SettingsActivity extends Activity implements View.OnClickListener {

	EditText inputAssistantPackageName;
	Button applyChanges, setAssistantApp, showPackagesList;
	TextView appVersion;
	FrameLayout settingsFooter;
	
	public static final String PREF_ASSISTANT_PACKAGE_NAME = "assistantPackageName";
	
	@Override
    protected void onCreate(Bundle sIS) {
        super.onCreate(sIS);

        setContentView(R.layout.activity_settings);
		setTitle(R.string.app_settings);
		
		Intent intent = getIntent();
		String intentAction = intent.getAction();
		String intentType = intent.getType();
		
		inputAssistantPackageName = findViewById(R.id.inputAssistantPackageName);
		inputAssistantPackageName.setText(SettingsUtils.get(this, PREF_ASSISTANT_PACKAGE_NAME));
		
		applyChanges = findViewById(R.id.applyChanges);
		applyChanges.setOnClickListener(this);
		applyChanges.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				startActivity(new Intent(Intent.ACTION_ASSIST).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
				return true;
			}
		});
		
		setAssistantApp = findViewById(R.id.setAssistantApp);
		setAssistantApp.setOnClickListener(this);
		
		showPackagesList = findViewById(R.id.showPackagesList);
		showPackagesList.setOnClickListener(this);

		appVersion = findViewById(R.id.appVersion);
		appVersion.setText(getString(R.string.app_version) + " " + thisAppVersion(this));
		
		settingsFooter = findViewById(R.id.settingsFooter);
		settingsFooter.setBackgroundColor(getSystemAccentColor(this));
		settingsFooter.setOnClickListener(this);
		
		if (Intent.ACTION_SEND.equals(intentAction) && intentType != null || "text/plain".equals(intentType)) {
			setGottenPackageNameDialog(intent);
		}

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		inputAssistantPackageName.setText(SettingsUtils.get(this, PREF_ASSISTANT_PACKAGE_NAME));
		
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

			// Сохраняем данные из поля ввода
			case R.id.applyChanges:
				applyNewChanges();
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

			case R.id.settingsFooter:
				aboutDialog();
				//startActivity(new Intent (Intent.ACTION_VIEW, Uri.parse("https://o1310.github.io")));
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
	
	void applyNewChanges() {
		
		if (TextUtils.isEmpty(inputAssistantPackageName.getText().toString())) {
			inputAssistantPackageName.setError(getString(R.string.message_package_name_not_specified));
		} else {
			SettingsUtils.put(this, PREF_ASSISTANT_PACKAGE_NAME, inputAssistantPackageName.getText().toString());
			Toast.makeText(this, R.string.message_changes_saved, Toast.LENGTH_LONG).show();
		}
		
	}
	
	// получаем системный цвет акцента
	@ColorInt
	public static int getSystemAccentColor(Context c) {
		
		int[] attr = { android.R.attr.colorAccent };
		
		TypedArray arr = c.obtainStyledAttributes(android.R.style.Theme_DeviceDefault, attr);
		
		int clr = arr.getColor(0, Color.BLACK);
		arr.recycle();
		
		return clr;
		
	}
	
	void aboutDialog(){

		// создаем диалог
		AlertDialog.Builder b = new AlertDialog.Builder(this);

		b.setTitle(R.string.about_dialog_title);
		b.setIcon(R.drawable.ic_logo);
		b.setMessage(R.string.about_message);
		b.setPositiveButton(android.R.string.cancel, new DialogInterface.OnClickListener() { // обработка нажатия кнопки "Да"
			public void onClick(DialogInterface d, int i) {
				d.cancel();
			}
		});
		b.setNegativeButton("Telegram", new DialogInterface.OnClickListener() { // обработка нажатия кнопки "Telegram"
			public void onClick(DialogInterface d, int i) {
				startActivity(new Intent (Intent.ACTION_VIEW, Uri.parse("https://t.me/o1310")));
			}
		});
		b.setNeutralButton(R.string.about_dialog_action_source_code, new DialogInterface.OnClickListener() { // обработка нажатия кнопки "Да"
			public void onClick(DialogInterface d, int i) {
				startActivity(new Intent (Intent.ACTION_VIEW, Uri.parse("https://github.com/o1310/a2iga")));
			}
		});

		AlertDialog a = b.create(); // создаем диалог

		a.show(); // отображаем диалог

	}
	
	/* Отображаем AlertDialog при получении данных.
	 * Код передачи данных из приложения в A2IGA:
	 * ---
	 * 1. Intent sendPackageName = new Intent();
	 * 2. sendPackageName.setAction(Intent.ACTION_SEND);
	 * 3. sendPackageName.putExtra(Intent.EXTRA_TEXT, "com.android.settings"); // "com.android.settings" - имя пакета, которое принимает A2IGA
	 * 4. sendPackageName.setType("text/plain");
	 * 5. startActivity(Intent.createChooser(sendPackageName, "Select «A2IGA»!"));
	 * --- */
	void setGottenPackageNameDialog(Intent i){

		final String gottenPackageName = i.getStringExtra(Intent.EXTRA_TEXT);
		
		AlertDialog.Builder b = new AlertDialog.Builder(this);

		b.setTitle("A2IGA: " + getString(R.string.gotten_package_name_dialog_title));
		b.setIcon(R.mipmap.ic_launcher_round);
		b.setMessage(Html.fromHtml(String.format(getString(R.string.gotten_package_name_dialog_message), gottenPackageName)));
		b.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface d, int i) {
				SettingsUtils.put(SettingsActivity.this, PREF_ASSISTANT_PACKAGE_NAME, gottenPackageName);
				inputAssistantPackageName.setText(gottenPackageName);
				Toast.makeText(SettingsActivity.this, R.string.message_changes_saved, Toast.LENGTH_LONG).show();
			}
		});
		b.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() { 
			public void onClick(DialogInterface d, int i) {
				d.dismiss();
			}
		});

		AlertDialog a = b.create();

		a.show();

	}

    /*@Override
	 protected void onDestroy() {
	 super.onDestroy();
	 saveAssistantPackageName();
	 Toast.makeText(this, "A2IGA: Changes saved!", Toast.LENGTH_LONG).show();
	 }*/

}
 
