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
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import o1310.rx1310.app.a2iga.adapter.AppsListAdapter;
import o1310.rx1310.app.a2iga.R;
import o1310.rx1310.app.a2iga.AppList;
import android.os.PersistableBundle;

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
				Toast.makeText(PackagesListActivity.this, getString(R.string.message_copied) + appsList.get(i).mAppPackageName, Toast.LENGTH_SHORT).show();
			}
		});
		
        appsCount = findViewById(R.id.packagesList_countApps);
		appsCount.setText(getString(R.string.packages_list_apps_count) + " " + packagesList.getCount() + "\n\n" + getString(R.string.packages_list_desc));
		
	}
	
	private List<AppList> getAppsList() {
		
		List<AppList> appsList = new ArrayList<AppList>();
        List<PackageInfo> pkgInfo = getPackageManager().getInstalledPackages(0);
		
		for (int i = 0; i < pkgInfo.size(); i++) {
			
            PackageInfo p = pkgInfo.get(i);
			
            if ((showSystemPackages(p))) {
				
                String appName = p.applicationInfo.loadLabel(getPackageManager()).toString();
                Drawable appIcon = p.applicationInfo.loadIcon(getPackageManager());
                String appPackageName = p.applicationInfo.packageName;
				
                appsList.add(new AppList(appName, appPackageName, appIcon));
				
            }
			
        }
		
        return appsList;
		
	}

    private boolean showSystemPackages(PackageInfo pkgInfo) {
        return (pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_INSTALLED) != 0;
    }
	
}
