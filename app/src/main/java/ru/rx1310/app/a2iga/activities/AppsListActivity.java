// ! rx1310 <rx1310@inbox.ru> | Copyright (c) rx1310, 2021 | MIT License

package ru.rx1310.app.a2iga.activities;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageView;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import android.widget.LinearLayout;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ru.rx1310.app.a2iga.R;
import ru.rx1310.app.a2iga.adapters.ApplicationAdapter;
import ru.rx1310.app.a2iga.tasks.LoadAppsTask;
import ru.rx1310.app.a2iga.utils.SharedPrefUtils;
import ru.rx1310.app.a2iga.Constants;
import ru.rx1310.app.a2iga.utils.AppUtils;

public class AppsListActivity extends AppCompatActivity {

    ListView oListView;
    Toolbar oToolbar;
    PackageManager oPkgMan;
    ArrayAdapter<ApplicationInfo> oAdapter;
    ArrayList<ApplicationInfo> oList;
    ProgressDialog oDlgProgress;
	SearchManager oSearchMng;
	SearchView oSearchView;
	TextView oAppsCount, oCurrentAssistAppName;
	Boolean oldUIEnabled;
	String isAssistAppPkgName;
	LinearLayout oCurrentAssistApp;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appslist);

		isAssistAppPkgName = SharedPrefUtils.getStringData(this, Constants.ASSIST_APP_PKGNAME);
		oldUIEnabled = SharedPrefUtils.getBooleanData(this, "appslist.oldUI");
		
        oPkgMan = getPackageManager();
        oList = new ArrayList<>();

		oListView = findViewById(R.id.listView);
        oToolbar = findViewById(R.id.toolbar);
		oAppsCount = findViewById(R.id.appsCount);
		
		oCurrentAssistApp = findViewById(R.id.currentAssistApp);

		if (isAssistAppPkgName == null || oldUIEnabled) oCurrentAssistApp.setVisibility(View.GONE);
		
		oCurrentAssistAppName = findViewById(R.id.currentAssistAppName);
		oCurrentAssistAppName.setText(AppUtils.getAppName(this, isAssistAppPkgName));
		
        setSupportActionBar(oToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		
		oSearchMng = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        oSearchView = findViewById(R.id.searchView);
        oSearchView.setOnQueryTextListener(onQueryTextListener());
        oSearchView.setSearchableInfo(oSearchMng.getSearchableInfo(getComponentName()));
		oSearchView.setIconifiedByDefault(false);
		oSearchView.setFocusable(true);
		oSearchView.requestFocusFromTouch();
		
		oDlgProgress = ProgressDialog.show(this, getString(R.string.appslist_loading_dialog), getString(R.string.appslist_loading_dialog_desc));
        
		if (oldUIEnabled) oAdapter = new ApplicationAdapter(this, R.layout.list_item_appslist_old, oList);
		else oAdapter = new ApplicationAdapter(this, R.layout.list_item_appslist, oList);
		
        oListView.setAdapter(oAdapter);
		
    }
	
    SearchView.OnQueryTextListener onQueryTextListener() {
		
		return new SearchView.OnQueryTextListener() {
			
			@Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
				oAdapter.getFilter().filter(s);
                return false;
            }
			
        };
		
    }

    @Override
    protected void onStart() {
        super.onStart();
        new LoadAppsTask(this, oList, oPkgMan).execute();
    }


    public void callBackDataFromAsynctask(final List<ApplicationInfo> list) {
		
		oList.clear();
		
		for (int i = 0; i < list.size(); i++) {
			oList.add(list.get(i));
		}
		
		oAppsCount.setText(String.format(getString(R.string.appslist_apps_count), oList.size()));
		oAdapter.notifyDataSetChanged();
		
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {}

		oDlgProgress.dismiss();
		
    }

    public void updateUILayout(String content) {
        oAppsCount.setText(content);
    }
	
	@Override
	public boolean onSupportNavigateUp() {
		onBackPressed();
		return true;
	}
	
}
