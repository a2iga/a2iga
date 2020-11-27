/*
 * @author      rx1310 <rx1310@inbox.ru>
 * @copyright   Copyright (c) o1310, 2020
 * @license     MIT License
 */

package ru.rx1310.app.a2iga;

import android.app.Activity;

import android.content.Intent;
import android.content.SharedPreferences;

import android.net.Uri;

import android.os.Bundle;

import android.widget.Toast;

import android.text.TextUtils;

import ru.rx1310.app.a2iga.utils.SettingsUtils;
import ru.rx1310.app.a2iga.activity.SettingsActivity;

public class LaunchAssistant extends Activity {
	
	String assistantPackageName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		assistantPackageName = SettingsUtils.get(this, SettingsActivity.PREF_ASSISTANT_PACKAGE_NAME);
		
		// Запускаем ассистент
		startAssistantApp(assistantPackageName);
		
		// Убиваем активность после запуска ассистента
		this.finish();
		
	}
	
	// Функция запуска ассистента
	public void startAssistantApp(String packageName) {
		
		if (TextUtils.isEmpty(packageName)) {
			
			/* Если packageName пустой, то отобразим
			 * toast-сообщение, которое уведомляет об этом */
			Toast.makeText(this, R.string.message_package_name_not_specified, Toast.LENGTH_LONG).show();
			
			// Также запустим окно настроек
			startActivity(new Intent(this, SettingsActivity.class));
			
		} else {
			
			// Если в packageName есть данные, то запускаем приложение
			Intent i = getPackageManager().getLaunchIntentForPackage(packageName);

			if (i == null) {
				
				/* Если package name указан, но приложение
				 * не установлено — ищем в Play Store это приложение */
				i = new Intent(Intent.ACTION_VIEW);
				Toast.makeText(this, R.string.message_app_not_found, Toast.LENGTH_LONG).show();
				i.setData(Uri.parse("market://details?id=" + packageName));
				
			}

			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			startActivity(i);
			
		}
		
	}
	
}
