/*
 * @author      rx1310 <rx1310@inbox.ru>
 * @copyright   Copyright (c) o1310, 2020
 * @license     MIT License
 */

package ru.rx1310.app.a2iga;

import android.graphics.drawable.Drawable;

public class AppList {
	
	public String mAppName, mAppPackageName;
	public Drawable mAppIcon;
	
	public AppList(String n, String p, Drawable i) {
		
		this.mAppName = n;
		this.mAppPackageName = p;
		this.mAppIcon = i;
		
	}
	
	public String getAppName() {
		return mAppName;
	}
	
	public String getAppPackageName() {
		return mAppPackageName;
	}
	
	public Drawable getAppIcon() {
		return mAppIcon;
	}
	
}
