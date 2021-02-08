// ! rx1310 <rx1310@inbox.ru> | Copyright (c) rx1310, 2020 | MIT License

package ru.rx1310.app.a2iga.utils;

import android.annotation.SuppressLint;

import android.content.Context;
import android.content.SharedPreferences;

import android.preference.PreferenceManager;

import android.util.Log;

public class SettingsUtils {

	public static String get(Context context, String key) {

		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		String data = sharedPrefs.getString(key, null);

		if (data == null) {
			Log.d("SettingsUtils", "No settings «" + key + "» is stored!");
		} else {
			Log.d("SettingsUtils", "Got settings «" + key + "» equal to «" + data + "»");
		}

		return data;

	}

	public static void put(Context context, String key, String value) {

		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor sharedPrefsEditor = sharedPrefs.edit();

		sharedPrefsEditor.putString(key, value);
		sharedPrefsEditor.commit();

	}

}
