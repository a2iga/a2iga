// ! rx1310 <rx1310@inbox.ru> | Copyright (c) rx1310, 2020 | MIT License

package ru.rx1310.app.a2iga;

import android.app.Activity;

import android.content.Intent;
import android.content.SharedPreferences;

import android.net.Uri;

import android.os.Bundle;

import android.widget.Toast;

import android.text.TextUtils;
import ru.rx1310.app.a2iga.utils.SharedPrefUtils;
import ru.rx1310.app.a2iga.tasks.OTACheckTask;
import ru.rx1310.app.a2iga.utils.AppUtils;

public class LaunchAssistant extends Activity {

	String isAssistAppPkgName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		isAssistAppPkgName = SharedPrefUtils.getStringData(this, Constants.ASSIST_APP_PKGNAME);

		// Запускаем ассистент
		startAssistApp(isAssistAppPkgName);
		
		// Убиваем активность после запуска ассистента
		this.finish();

	}

	// Функция запуска ассистента
	public void startAssistApp(String pkgName) {

		if (TextUtils.isEmpty(pkgName)) {

			/* Если packageName пустой, то отобразим
			 * toast-сообщение, которое уведомляет об этом */
			AppUtils.showToast(this, getString(R.string.pkg_name_notspecified));
			
			// Также запустим окно настроек
			//startActivity(new Intent(this, SettingsActivity.class));

		} else {

			// Если в packageName есть данные, то запускаем приложение
			Intent i = getPackageManager().getLaunchIntentForPackage(pkgName);

			if (i == null) {

				/* Если package name указан, но приложение
				 * не установлено — ищем в Play Store это приложение */
				i = new Intent(Intent.ACTION_VIEW);
				AppUtils.showToast(this, getString(R.string.app_not_found));
				i.setData(Uri.parse("market://details?id=" + pkgName));

			}

			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			startActivity(i);
			
		}

	}

}
