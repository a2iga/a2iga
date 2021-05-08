// ! rx1310 <rx1310@inbox.ru> | Copyright (c) rx1310, 2020 | MIT License

package ru.rx1310.app.a2iga;

import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import ru.rx1310.app.a2iga.helpers.FingerprintHelper;
import ru.rx1310.app.a2iga.utils.AppUtils;
import ru.rx1310.app.a2iga.utils.SharedPrefUtils;

public class LaunchAssistant extends Activity {

	String isAssistAppPkgName;
	Intent oIntent = new Intent();
	FingerprintHelper oFingerprintHelper;

	boolean isFingerprintPermEnabled, isFingerprintPermDialogAppIconEnabled;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		isAssistAppPkgName = SharedPrefUtils.getStringData(this, Constants.ASSIST_APP_PKGNAME);
		isFingerprintPermEnabled = SharedPrefUtils.getBooleanData(this, "security.fingerprintPerm");
		isFingerprintPermDialogAppIconEnabled = SharedPrefUtils.getBooleanData(this, "security.fingerprintPerm.dialogAppIcon");
		
		// Запускаем ассистент
		if (isFingerprintPermEnabled) startAssistAppWithFingerprint(isAssistAppPkgName);
		else startAssistApp(isAssistAppPkgName);
		
		// Убиваем активность после запуска ассистента
		//this.finish();

	}
	
	void runApp(String pkgName) {
		
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

		} else if (isAssistAppPkgName.contains("a2iga.module.")) {
			
			// ? Если установлен модуль
			oIntent.setComponent(new ComponentName(isAssistAppPkgName, isAssistAppPkgName + ".LaunchModule"));
			startActivity(oIntent);
			
		} else {

			runApp(pkgName);
			
		}

	}
	
	// Функция запуска ассистента через подтверждение отпечатка пальца
	public void startAssistAppWithFingerprint(final String pkgName) {
		
		final android.support.v7.app.AlertDialog.Builder b = new android.support.v7.app.AlertDialog.Builder(LaunchAssistant.this, R.style.AppTheme_Dialog_Alert);

		b.setTitle(R.string.fingerprint_perm_dialog);
		b.setMessage(String.format(getString(R.string.fingerprint_perm_dialog_desc), AppUtils.getAppName(this, isAssistAppPkgName)));
		b.setCancelable(false);
		
		if (isFingerprintPermDialogAppIconEnabled) {
			
			try {
				Drawable drawable = getPackageManager().getApplicationIcon(isAssistAppPkgName);
				b.setIcon(drawable);
			} catch (PackageManager.NameNotFoundException e) {
				e.printStackTrace();
			}
			
		} else {
			b.setIcon(R.drawable.ic_app_logo);
		}
		
		b.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() { // обработка нажатия кнопки "No"
			public void onClick(DialogInterface d, int i) {
				finish();
			}
		});
		
		b.create();
		b.show();
		
		if (FingerprintHelper.canUseFingerprint()) {
			
			try {
				
				oFingerprintHelper = new FingerprintHelper() {
					
					@Override
					public void onAuthenticationError(int errCode, CharSequence errMessage) {
						AppUtils.showToast(LaunchAssistant.this, errMessage + "");
						AppUtils.Log(LaunchAssistant.this, "e", "onAuthenticationError || errorCode: " + errCode + " || errorMessage: " + errMessage);
					}

					@Override
					public void onAuthenticationHelp(int helpCode, CharSequence helpMessage) {
						AppUtils.showToast(LaunchAssistant.this, "Help string: " + helpMessage + " / " + helpCode);
					}

					@Override
					public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
						runApp(pkgName);
					}

					@Override
					public void onAuthenticationFailed() {
						AppUtils.showToast(LaunchAssistant.this, getString(R.string.fingerprint_perm_denied));
						//finish();
					}
					
				};
				
				oFingerprintHelper.startAuth();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
	}

}
