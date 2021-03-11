// ! rx1310 <rx1310@inbox.ru> | Copyright (c) rx1310, 2021 | MIT License

package ru.rx1310.app.a2iga.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import ru.rx1310.app.a2iga.Constants;
import ru.rx1310.app.a2iga.R;
import ru.rx1310.app.a2iga.utils.AppUtils;
import ru.rx1310.app.a2iga.utils.SharedPrefUtils;
import android.os.Build;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener, View.OnClickListener {
    
	Toolbar mToolbar;
	CardView mUnsupportedApi22Card, mNotDefaultAssistCard;
	ImageView mAssistantAppIcon;
	String isAssistAppPkgName;
	TextView mAssistantAppName, mRandomPromt;
	SharedPreferences mSharedPreferences;
	Random mRandom = new Random();
	
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
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_settings);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setTitle(R.string.activity_main);
		
		mRandomPromt = findViewById(R.id.textRandomPromt);
		mAssistantAppName = findViewById(R.id.name);
		mAssistantAppIcon = findViewById(R.id.icon);
		
		if (isAssistAppPkgName == null) {
			
			mAssistantAppName.setText(R.string.current_assistant_null);
			mAssistantAppIcon.setImageDrawable(getDrawable(R.drawable.ic_appslist));
			
		} else {
			
			mAssistantAppName.setText(AppUtils.getAppName(this, isAssistAppPkgName));
			
			try {
				Drawable drawable = getPackageManager().getApplicationIcon(isAssistAppPkgName);
				mAssistantAppIcon.setImageDrawable(drawable);
			} catch (PackageManager.NameNotFoundException e) {
				e.printStackTrace();
			}
			
		}
		
		mNotDefaultAssistCard = findViewById(R.id.cardNotDefaultAssist);
		mNotDefaultAssistCard.setOnClickListener(this);
		mNotDefaultAssistCard.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(android.provider.Settings.ACTION_VOICE_INPUT_SETTINGS));
			}
		});
		
		mUnsupportedApi22Card = findViewById(R.id.cardUnsupportedApi22);
		
		if (Build.VERSION.SDK_INT < 22) {
			mUnsupportedApi22Card.setVisibility(View.VISIBLE);
		} else {
			mUnsupportedApi22Card.setVisibility(View.GONE);
		}
		
		
		
    }

	@Override
	protected void onResume() {
		super.onResume();
		
		if (AppUtils.getCurrentAssist(this).getClassName().toString().contains("a2iga")) {
			mNotDefaultAssistCard.setVisibility(View.GONE);
		} else {
			mNotDefaultAssistCard.setVisibility(View.VISIBLE);
		} 
		
		mRandomPromt.setText(Constants.randomPromts[mRandom.nextInt(8)]);
		
	}
	
	
	@Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPrefs, String key) {
        
		if (key.equals(Constants.PrefsKeys.ASSIST_APP_PKGNAME)) {
            recreate();
        }

	}
	
	@Override
	public void onClick(View v) {

		switch (v.getId()) {

			//case R.id.applyChanges:
			//
			//break;
				
			default: break;

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
