// ! rx1310 <rx1310@inbox.ru> | Copyright (c) rx1310, 2021 | MIT License

package ru.rx1310.app.a2iga.tasks;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import ru.rx1310.app.a2iga.activities.AppsListActivity;

public class LoadAppsTask extends AsyncTask<Void, Void, List<ApplicationInfo>> {

	private AppsListActivity oActivity;
	private List<ApplicationInfo> oList;
	private PackageManager oPkgMng;

	public LoadAppsTask(AppsListActivity activity, List<ApplicationInfo> list, PackageManager pkgMng) {
		
		this.oActivity = activity;
		this.oList = list;
		this.oPkgMng = pkgMng;
		
	}

	@Override
	protected List<ApplicationInfo> doInBackground(Void... params) {
		
		oList = checkForLaunchIntent(oPkgMng.getInstalledApplications(PackageManager.GET_META_DATA));
		
		return oList;
		
	}

	private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
		
		ArrayList<ApplicationInfo> applist = new ArrayList<>();
		
		for (ApplicationInfo applicationInfo : list) {
			
			try {
				if (oPkgMng.getLaunchIntentForPackage(applicationInfo.packageName) != null) applist.add(applicationInfo);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			
		}

		return applist;
		
	}

	@Override
	protected void onPostExecute(List<ApplicationInfo> list) {
		
		super.onPostExecute(list);
		
		oActivity.callBackDataFromAsynctask(list);
		
	}
	
}
