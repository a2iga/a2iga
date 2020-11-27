// ! rx1310 <rx1310@inbox.ru> | Copyright (c) rx1310, 2020 | MIT License

package ru.rx1310.app.a2iga.activity;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;

import android.graphics.Color;
import android.graphics.drawable.Drawable;

import android.os.Bundle;

import android.view.View;

import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ru.rx1310.app.a2iga.AppList;
import ru.rx1310.app.a2iga.R;
import ru.rx1310.app.a2iga.adapter.AppsListAdapter;
import ru.rx1310.app.a2iga.utils.SettingsUtils;

public class PackagesListActivity extends Activity {

    List<AppList> appsList;
	ListView packagesList;
    AppsListAdapter appsListAdapter;
	TextView appsCount;
	
	@Override
	protected void onCreate(Bundle sIS) {
		super.onCreate(sIS);
		
		setContentView(R.layout.activity_packageslist);
		
		appsList = getAppsList();

		appsListAdapter = new AppsListAdapter(PackagesListActivity.this, appsList);
		
		packagesList = findViewById(R.id.packagesList);
		packagesList.setAdapter(appsListAdapter);
        packagesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> av, View v, final int i, long l) {
				
				AlertDialog.Builder b = new AlertDialog.Builder(PackagesListActivity.this);
				
				b.setTitle(appsList.get(i).mAppName);
				b.setIcon(appsList.get(i).mAppIcon);
				b.setItems(R.array.package_list_action, new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface d, int itemPos) {
						
						if (itemPos == 0) {
							
							SettingsUtils.put(PackagesListActivity.this, SettingsActivity.PREF_ASSISTANT_PACKAGE_NAME, appsList.get(i).mAppPackageName);
							finish();
							Toast.makeText(PackagesListActivity.this, getString(R.string.message_app_selected_as_assistant), Toast.LENGTH_SHORT).show();
							
						}
						
						if (itemPos == 1) {
							
							ClipboardManager mClipboardMng = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
							ClipData mClipData = ClipData.newPlainText(null, appsList.get(i).mAppPackageName);
							mClipboardMng.setPrimaryClip(mClipData);

							Toast.makeText(PackagesListActivity.this, getString(R.string.message_package_name_copied), Toast.LENGTH_SHORT).show();
							
						}
						
					}
				});
				b.setPositiveButton(android.R.string.cancel, new DialogInterface.OnClickListener() { // обработка нажатия кнопки "Да"
					public void onClick(DialogInterface d, int i) {
						d.dismiss();
					}
				});
				
				b.show();
				
			}
		});
		
        appsCount = findViewById(R.id.packagesList_countApps);
		appsCount.setText(getString(R.string.packages_list_apps_count) + " " + packagesList.getCount() + "\n" + getString(R.string.packages_list_desc));
		appsCount.setBackgroundColor(SettingsActivity.getSystemAccentColor(this));
		
	}
		
	private List<AppList> getAppsList() {
		
		List<AppList> appsList = new ArrayList<AppList>();
        List<PackageInfo> pkgInfo = getPackageManager().getInstalledPackages(0);
		
		for (int i = 0; i < pkgInfo.size(); i++) {
			
            PackageInfo p = pkgInfo.get(i);
			
            if ((p.applicationInfo.flags & ApplicationInfo.FLAG_INSTALLED) != 0) {
				
                String appName = p.applicationInfo.loadLabel(getPackageManager()).toString();
                Drawable appIcon = p.applicationInfo.loadIcon(getPackageManager());
                String appPackageName = p.applicationInfo.packageName;
				
                appsList.add(new AppList(appName, appPackageName, appIcon));
				
            }
			
        }
		
		Collections.sort(appsList, new Comparator<AppList>() {
			public int compare(final AppList p1, final AppList p2) {
				return p1.getAppName().compareTo(p2.getAppName());
			}
		});
		
		
        return appsList;
		
	}

}
