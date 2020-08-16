/*
 * @author      rx1310 <rx1310@inbox.ru>
 * @copyright   Copyright (c) o1310, 2020
 * @license     MIT License
 */

package o1310.rx1310.app.a2iga.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import o1310.rx1310.app.a2iga.AppList;
import o1310.rx1310.app.a2iga.R;
import o1310.rx1310.app.a2iga.adapter.AppsListAdapter;

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
				
				ClipboardManager mClipboardMng = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				ClipData mClipData = ClipData.newPlainText(null, appsList.get(i).mAppPackageName);
				mClipboardMng.setPrimaryClip(mClipData);
				
				Toast.makeText(PackagesListActivity.this, getString(R.string.message_copied), Toast.LENGTH_SHORT).show();
				
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
