// ! rx1310 <rx1310@inbox.ru> | Copyright (c) rx1310, 2021 | MIT License

package ru.rx1310.app.a2iga.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

import ru.rx1310.app.a2iga.Constants;
import ru.rx1310.app.a2iga.R;
import ru.rx1310.app.a2iga.utils.AppUtils;
import ru.rx1310.app.a2iga.utils.SharedPrefUtils;
import ru.rx1310.app.a2iga.fragments.SettingsFragment;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener, View.OnClickListener {
    
	Toolbar oToolbar;
	CardView oUnsupportedApi22Card, oNotDefaultAssistCard;
	ImageView oAssistantAppIcon;
	String isAssistAppPkgName;
	TextView oAssistantAppName, oRandomPromt;
	FrameLayout oSettingsLayout;
	
	SharedPreferences oSharedPreferences;
	
	Random oRandom = new Random();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		getFragmentManager().beginTransaction().replace(R.id.layoutSettings, new SettingsFragment()).commit();
		
		isAssistAppPkgName = SharedPrefUtils.getStringData(this, Constants.ASSIST_APP_PKGNAME);
		
		oSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		oSharedPreferences.registerOnSharedPreferenceChangeListener(this);
		
		oSettingsLayout = findViewById(R.id.layoutSettings);
		
		oToolbar = findViewById(R.id.toolbar);
		
		setSupportActionBar(oToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_logo);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setTitle(R.string.activity_main);
		
		//oRandomPromt = findViewById(R.id.textRandomPromt);
		oAssistantAppName = findViewById(R.id.name);
		oAssistantAppIcon = findViewById(R.id.icon);
		
		if (isAssistAppPkgName == null) {
			
			oAssistantAppName.setText(R.string.current_assistant_null);
			oAssistantAppIcon.setImageDrawable(getDrawable(R.drawable.ic_appslist));
			
		} else {
			
			oAssistantAppName.setText(AppUtils.getAppName(this, isAssistAppPkgName));
			
			try {
				Drawable drawable = getPackageManager().getApplicationIcon(isAssistAppPkgName);
				oAssistantAppIcon.setImageDrawable(drawable);
			} catch (PackageManager.NameNotFoundException e) {
				e.printStackTrace();
			}
			
		}
		
		oNotDefaultAssistCard = findViewById(R.id.cardNotDefaultAssist);
		oNotDefaultAssistCard.setOnClickListener(this);
		oNotDefaultAssistCard.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(android.provider.Settings.ACTION_VOICE_INPUT_SETTINGS));
			}
		});
		
		oUnsupportedApi22Card = findViewById(R.id.cardUnsupportedApi22);
		
		if (Build.VERSION.SDK_INT < 22) {
			oUnsupportedApi22Card.setVisibility(View.VISIBLE);
			oSettingsLayout.setVisibility(View.VISIBLE);
		} else {
			oUnsupportedApi22Card.setVisibility(View.GONE);
			oSettingsLayout.setVisibility(View.GONE);
		}
		
    }

	@Override
	protected void onResume() {
		super.onResume();
		
		if (AppUtils.getCurrentAssist(this).getClassName().toString().contains("a2iga")) {
			oNotDefaultAssistCard.setVisibility(View.GONE);
			oSettingsLayout.setVisibility(View.VISIBLE);
		} else {
			oNotDefaultAssistCard.setVisibility(View.VISIBLE);
			oSettingsLayout.setVisibility(View.GONE);
		} 
		
		//oRandomPromt.setText(Constants.randomPromts[oRandom.nextInt(8)]);
		
	}
	
	
	@Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPrefs, String key) {
        
		if (key.equals(Constants.ASSIST_APP_PKGNAME)) {
            recreate();
        }

	}
	
	@Override
	public void onClick(View v) {

		switch (v.getId()) {

			//case R.id.:
			//
			//break;
				
			default: break;

		}

	}
	
	public void showAppsList(View v) {
		startActivity(new Intent(this, AppsListActivity.class));
	}
	
	@Override
	public boolean onSupportNavigateUp() {
		onBackPressed();
		return true;
	}
    
}
