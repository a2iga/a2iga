// ! rx1310 <rx1310@inbox.ru> | Copyright (c) rx1310, 2021 | MIT License

package ru.rx1310.app.a2iga.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ru.rx1310.app.a2iga.Constants;
import ru.rx1310.app.a2iga.R;
import ru.rx1310.app.a2iga.utils.AppUtils;
import ru.rx1310.app.a2iga.utils.SharedPrefUtils;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    
	Toolbar mToolbar;
	ImageView mAssistantAppIcon;
	String isAssistAppPkgName;
	TextView mAssistantAppName, mAssistantPackageName;
	SharedPreferences mSharedPreferences;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		
		isAssistAppPkgName = SharedPrefUtils.getStringData(this, Constants.PrefsKeys.ASSIST_APP_PKGNAME);
		
		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
		
		mToolbar = findViewById(R.id.toolbar);
		
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_logo);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		
		mAssistantAppName = findViewById(R.id.name);
		mAssistantPackageName = findViewById(R.id.package_name);
		mAssistantAppIcon = findViewById(R.id.icon);
		
		if (isAssistAppPkgName == null) {
			
			mAssistantAppName.setText("Empty app");
			mAssistantPackageName.setText("Empty pkg");
			mAssistantAppIcon.setImageDrawable(getDrawable(R.drawable.ic_logo_alt));
			
		} else {
			
			mAssistantAppName.setText(AppUtils.getAppName(this, isAssistAppPkgName));
			mAssistantPackageName.setText(isAssistAppPkgName);
			
			try {
				Drawable drawable = getPackageManager().getApplicationIcon(isAssistAppPkgName);
				mAssistantAppIcon.setImageDrawable(drawable);
			} catch (PackageManager.NameNotFoundException e) {
				e.printStackTrace();
			}
			
		}
		
		
		
		if (AppUtils.getCurrentAssist(this).toString().contains("a2iga")) {
			AppUtils.showToast(this, AppUtils.getCurrentAssist(this) + "");
		}
		
    }
	
	@Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPrefs, String key) {
        
		if (key.equals(Constants.PrefsKeys.ASSIST_APP_PKGNAME)) {
            recreate();
        }

	}
	
	public void appsListGet(View v) {
		startActivity(new Intent(this, AppsListActivity.class));
	}
	
	@Override
	public boolean onSupportNavigateUp() {
		onBackPressed();
		return true;
	}
    
}
