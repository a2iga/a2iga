// ! rx1310 <rx1310@inbox.ru> | Copyright (c) rx1310, 2021 | MIT License

package ru.rx1310.app.a2iga.activities;


import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

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
	TextView mHeaderText;
    Toolbar mToolbar;
    PackageManager mPkgMan;
    ArrayAdapter<ApplicationInfo> mAdapter;
    ArrayList<ApplicationInfo> mList;
    ProgressDialog mDlgProgress;
	MenuInflater mInflater;
	SearchManager mSearchMng;
	SearchView mSearchView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appslist);

        mPkgMan = getPackageManager();
        mList = new ArrayList<>();

		mHeaderText = findViewById(R.id.text_header);
        mListView = findViewById(R.id.list_view);
        mToolbar = findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		
        mDlgProgress = ProgressDialog.show(this, getString(R.string.dlg_appslist_loading), getString(R.string.dlg_appslist_loading_desc));
        mAdapter = new ApplicationAdapter(this, R.layout.ui_list_item, mList);
        mListView.setAdapter(mAdapter);
		
    }

    @Override
    public boolean onCreateOptionsMenu(Menu mMenu) {
		
        mInflater = getMenuInflater();
        mInflater.inflate(R.menu.appslist, mMenu);
        
		mSearchMng = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		
        mSearchView = (SearchView) mMenu.findItem(R.id.appslist_search).getActionView();
		mSearchView.setQueryHint(getString(R.string.menu_search_hint_appslist));
		mSearchView.setMaxWidth(Integer.MAX_VALUE);
        mSearchView.setOnQueryTextListener(onQueryTextListener());
        mSearchView.setSearchableInfo(mSearchMng.getSearchableInfo(getComponentName()));

        return true;
		
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
		
		mHeaderText.setText(String.format(getString(R.string.appslist_header_apps_count), mList.size()));
        mAdapter.notifyDataSetChanged();
        mDlgProgress.dismiss();
		
    }

    public void updateUILayout(String content) {
        mHeaderText.setText(content);
    }
	
	@Override
	public boolean onSupportNavigateUp() {
		onBackPressed();
		return true;
	}
	
}
