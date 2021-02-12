// ! rx1310 <rx1310@inbox.ru> | Copyright (c) DevReader, 2020 | MIT License

package ru.rx1310.app.a2iga.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.util.Log;
import android.widget.Toast;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import android.graphics.drawable.Drawable;

public class AppUtils {

	// Логи
	public static void Log(Context context, String logType, String logMessage) {

		if (logType == "e") {
			Log.e("[E] DevReader (rx1310)", context.getClass().getName() + "\n" + logMessage);
		} else if (logType == "d") {
			Log.d("[D] DevReader (rx1310)", context.getClass().getName() + "\n" + logMessage);
		} else if (logType == "i") {
			Log.i("[I] DevReader (rx1310)", context.getClass().getName() + "\n" + logMessage);
		} else if (logType == "w") {
			Log.w("[W] DevReader (rx1310)", context.getClass().getName() + "\n" + logMessage);
		} else {
			return ;
		}

	}

	// ? Получение имени версии
	public static String getVersionName(Context context, String packageName) {

		try {

			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(packageName, 0);

			String vn = pi.versionName;

			return vn;

		} catch (Exception exc) {
			exc.printStackTrace();
			Log(context, "e", "getVersionName: " + exc);
			return "e: getVersionName()";
		}

	}

	// ? Получение кода версии
    public static int getVersionCode(Context context, String packageName) {

		try {

			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(packageName, 0);

			int vc = pi.versionCode;

			return vc;

		} catch (Exception exc) {
			exc.printStackTrace();
			Log(context, "e", "getVersionCode: " + exc);
			return 0;
		}

	}

	// ? Получение имени приложения
	public static String getAppName(Context context, String packageName) {

		try {

			PackageManager pm = context.getPackageManager();
			ApplicationInfo ai = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);

			String appName = (String) pm.getApplicationLabel(ai);

			return appName;

		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
			Log(context, "e", "getAppName: " + e);
			return "e: getAppName();";
		}

	}
	
	// ? Проверка на наличие программы
	public static boolean isAppInstalled(Context context, String packageName) {

		PackageManager packageMng = context.getPackageManager();

		try {
			packageMng.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
			return true;
		} catch(PackageManager.NameNotFoundException e) {
			return false;
		}

	}

	// ? Получение системного цвета акцента
	@ColorInt
	public static int getSystemAccentColor(Context context) {

		int[] attr = { android.R.attr.colorAccent };

		TypedArray arr = context.obtainStyledAttributes(android.R.style.Theme_DeviceDefault, attr);

		int clr = arr.getColor(0, Color.BLACK);
		arr.recycle();

		return clr;

	}

	// ? Отображение Toast
	public static void showToast(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
		Log(context, "d", "showToast: " + message);
	}

	// ? Проверка на наличие интернета
	public static boolean isNetworkAvailable(Context context) {

		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

		return networkInfo != null && networkInfo.isConnected();

	}

	// ? Получение текущ. даты
	public static String getDate(long ms, String customDateFormat) {

		SimpleDateFormat dateFormat = new SimpleDateFormat(customDateFormat);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(ms);

		return dateFormat.format(calendar.getTime());

	}

	// ? Дата установки
	public static String getInstallDate(Context context, String packageName, boolean lastUpdateTime, boolean onlyInt) {

        PackageManager packageMng =  context.getPackageManager();
        long installTimeInMs;

        Date installDate = null;
        String installDateString = null;

        try {

            PackageInfo packageInfo = packageMng.getPackageInfo(packageName, 0);

            if (lastUpdateTime) {
				installTimeInMs = packageInfo.lastUpdateTime;
			} else {
				installTimeInMs = packageInfo.firstInstallTime;
			}

			if (onlyInt) {
				installDateString  = getDate(installTimeInMs, "ddMMyyyyHHmmss");
			} else {
				installDateString  = getDate(installTimeInMs, "dd/MM/yyyy (HH:mm:ss)");
			}

        } catch (PackageManager.NameNotFoundException e) {
			Log(context, "e", "getInstallDate: " + e + "\nlastUpdateTime: " + lastUpdateTime);
            installDate = new Date(0);
            installDateString = installDate.toString();
        }

        return installDateString;

    }

	// ? Переход по ссылкам
	public static void openURL(Context context, String link) {

		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(link));
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);

	}

}
