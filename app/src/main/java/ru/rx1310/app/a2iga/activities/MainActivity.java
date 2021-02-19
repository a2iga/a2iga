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
	String prefAssistantPackageName;
	TextView mAssistantAppName, mAssistantPackageName;
	SharedPreferences mSharedPreferences;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		
		prefAssistantPackageName = SharedPrefUtils.getStringData(this, Constants.PrefsKeys.ASSIST_APP_PKGNAME);
		
		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
		
		mToolbar = findViewById(R.id.toolbar);
				   setSupportActionBar(mToolbar);
		
		mAssistantAppName = findViewById(R.id.name);
		mAssistantAppName.setText(AppUtils.getAppName(this, prefAssistantPackageName));
		
		mAssistantPackageName = findViewById(R.id.package_name);
		mAssistantPackageName.setText(prefAssistantPackageName);
		
		mAssistantAppIcon = findViewById(R.id.icon);
		try {
			Drawable drawable = getPackageManager().getApplicationIcon(prefAssistantPackageName);
			mAssistantAppIcon.setImageDrawable(drawable);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		
		AppUtils.showToast(this, AppUtils.getCurrentAssist(this) + "");
		
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
	
	public void appSettingsGet(View v) {
		startActivity(new Intent(this, SettingsActivity.class));
	}
    
}
