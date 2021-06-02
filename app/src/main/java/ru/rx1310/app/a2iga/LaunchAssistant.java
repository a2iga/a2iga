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
import android.os.CancellationSignal;
import android.widget.Toast;

import ru.rx1310.app.a2iga.utils.AppUtils;
import ru.rx1310.app.a2iga.utils.SharedPrefUtils;
import android.hardware.biometrics.BiometricPrompt;
import java.util.concurrent.Executors;
import java.util.concurrent.Executor;

public class LaunchAssistant extends Activity {

	String isAssistAppPkgName;
	Intent oIntent = new Intent();
	
	Executor oExecutor;
	
	boolean isFingerprintPermEnabled;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		isAssistAppPkgName = SharedPrefUtils.getStringData(this, Constants.ASSIST_APP_PKGNAME);
		isFingerprintPermEnabled = SharedPrefUtils.getBooleanData(this, "security.fingerprintPerm");
		
		oExecutor = Executors.newSingleThreadExecutor();
		
		// Запускаем ассистент
		if (isFingerprintPermEnabled) startAssistAppWithFingerprint(isAssistAppPkgName);
		else startAssistApp(isAssistAppPkgName);
		
		// Убиваем активность после запуска ассистента
		//this.finish();

	}
	
	void runApp(String pkgName) {
		
		if (TextUtils.isEmpty(pkgName)) {

			/* Если packageName пустой, то отобразим
			 * toast-сообщение, которое уведомляет об этом */
			AppUtils.showToast(this, getString(R.string.pkg_name_notspecified));

			// Также запустим окно настроек
			//startActivity(new Intent(this, SettingsActivity.class));

		} else if (isAssistAppPkgName.contains("a2iga.module.")) {

			// ? Если установлен модуль
			try {
				
				oIntent.setComponent(new ComponentName(isAssistAppPkgName, isAssistAppPkgName + ".ModuleLaunc3h"));
				startActivity(oIntent);
				
			} catch(Exception e) {
				AppUtils.showToast(this, "Log copied to clipboard!\n\n" + e);
				AppUtils.copyToClipboard(this, "" + e);
			}

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
		
		this.finish();
		
	}

	// Функция запуска ассистента
	public void startAssistApp(String pkgName) {
		
		runApp(pkgName);

	} // startAssistApp
	
	// Функция запуска ассистента через подтверждение отпечатка пальца
	public void startAssistAppWithFingerprint(final String pkgName) {
		
		if (TextUtils.isEmpty(pkgName)) {
			
			AppUtils.showToast(this, getString(R.string.pkg_name_notspecified));
			
		} else {
			
			BiometricPrompt mBiometricPrompt = new BiometricPrompt.Builder(this)
				.setTitle(AppUtils.getAppName(this, isAssistAppPkgName))
				.setDescription(getString(R.string.fingerprint_perm_dialog_desc))
				.setNegativeButton(getString(android.R.string.cancel), oExecutor, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//dialog.dismiss();
						LaunchAssistant.this.finish();
					}
				})
				.build();

			mBiometricPrompt.authenticate(new CancellationSignal(), oExecutor, new BiometricPrompt.AuthenticationCallback() {
				
				@Override
				public void onAuthenticationError(int errorCode, final CharSequence errString) {
					super.onAuthenticationError(errorCode, errString);
					
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							
							Toast.makeText(LaunchAssistant.this, "" + errString, Toast.LENGTH_LONG).show();
							finish();
							
						}
					});
					
				}

				@Override
				public void onAuthenticationHelp(int helpCode, final CharSequence helpString) {
					super.onAuthenticationHelp(helpCode, helpString);
					
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							
							Toast.makeText(LaunchAssistant.this, "" + helpString, Toast.LENGTH_LONG).show();
							
						}
					});
						
				}

				@Override
				public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
					super.onAuthenticationSucceeded(result);
					
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							
							runApp(pkgName);

						}
					});
					
				}
				
			});
			
		}
		
	} // startAssistAppWithFingerprint

}
