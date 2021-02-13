// ! rx1310 <rx1310@inbox.ru> | Copyright (c) rx1310, 2021 | MIT License

package ru.rx1310.app.a2iga.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import ru.rx1310.app.a2iga.R;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.graphics.drawable.Drawable;
import android.content.pm.PackageManager;
import android.widget.ImageView;
import ru.rx1310.app.a2iga.utils.SettingsUtils;
import ru.rx1310.app.a2iga.A2IGA;
import android.widget.TextView;
import ru.rx1310.app.a2iga.utils.AppUtils;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.content.pm.ResolveInfo;
import android.content.ComponentName;
import android.provider.Settings;
import android.content.Context;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    
	Toolbar mToolbar;
	ImageView mAssistantAppIcon;
	String prefAssistantPackageName;
	TextView mAssistantAppName, mAssistantPackageName;
	private SharedPreferences mSharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		
		prefAssistantPackageName = SettingsUtils.get(this, A2IGA.PREF_PKGNAME_ASSISTANT_KEY);
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
		
		AppUtils.showToast(this, getCurrentAssist(this) + "");
		
    }
	
	public ComponentName getCurrentAssist(Context context) {
		final String setting = Settings.Secure.getString(context.getContentResolver(), "assistant");

		if (setting != null) {
			return ComponentName.unflattenFromString(setting);
		}

		return null;
	}
	
	@Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //does this need to run in uiThread?
        if (key.equals(A2IGA.PREF_PKGNAME_ASSISTANT_KEY)) {
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
