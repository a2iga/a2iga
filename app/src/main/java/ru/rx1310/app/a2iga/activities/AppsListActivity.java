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

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.rx1310.app.a2iga.R;
import ru.rx1310.app.a2iga.adapters.ApplicationAdapter;
import ru.rx1310.app.a2iga.tasks.LoadAppsTask;

public class AppsListActivity extends AppCompatActivity {

    ListView mListView;
    Toolbar mToolbar;
    PackageManager mPkgMan;
    ArrayAdapter<ApplicationInfo> mAdapter;
    ArrayList<ApplicationInfo> mList;
    ProgressDialog mDlgProgress;
	SearchManager mSearchMng;
	SearchView mSearchView;
	TextView mAppsCount;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appslist);

        mPkgMan = getPackageManager();
        mList = new ArrayList<>();

		mListView = findViewById(R.id.listView);
        mToolbar = findViewById(R.id.toolbar);
		mAppsCount = findViewById(R.id.appsCount);
		
        setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		
		mSearchMng = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        mSearchView = findViewById(R.id.searchView);
        mSearchView.setOnQueryTextListener(onQueryTextListener());
        mSearchView.setSearchableInfo(mSearchMng.getSearchableInfo(getComponentName()));
		mSearchView.setIconifiedByDefault(false);
		mSearchView.setFocusable(true);
		mSearchView.requestFocusFromTouch();
		
		mDlgProgress = ProgressDialog.show(this, getString(R.string.dlg_appslist_loading), getString(R.string.dlg_appslist_loading_desc));
        mAdapter = new ApplicationAdapter(this, R.layout.ui_appslist_item, mList);
        mListView.setAdapter(mAdapter);
		
    }
	
    SearchView.OnQueryTextListener onQueryTextListener() {
		
		return new SearchView.OnQueryTextListener() {
			
			@Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
				mAdapter.getFilter().filter(s);
                return false;
            }
			
        };
		
    }

    @Override
    protected void onStart() {
        super.onStart();
        new LoadAppsTask(this, mList, mPkgMan).execute();
    }


    public void callBackDataFromAsynctask(List<ApplicationInfo> list) {
		
		mList.clear();

        for (int i = 0; i < list.size(); i++) {
            mList.add(list.get(i));
        }
		
		mAppsCount.setText(String.format(getString(R.string.appslist_apps_count), mList.size()));
        mAdapter.notifyDataSetChanged();
        mDlgProgress.dismiss();
		
    }

    public void updateUILayout(String content) {
        mAppsCount.setText(content);
    }
	
	@Override
	public boolean onSupportNavigateUp() {
		onBackPressed();
		return true;
	}
	
}
