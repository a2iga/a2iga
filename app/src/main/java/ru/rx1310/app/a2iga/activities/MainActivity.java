// ! rx1310 <rx1310@inbox.ru> | Copyright (c) rx1310, 2021 | MIT License

package ru.rx1310.app.a2iga.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AlertDialog;

import java.util.Random;

import ru.rx1310.app.a2iga.Constants;
import ru.rx1310.app.a2iga.R;
import ru.rx1310.app.a2iga.activities.MainActivity;
import ru.rx1310.app.a2iga.fragments.SettingsFragment;
import ru.rx1310.app.a2iga.utils.AppUtils;
import ru.rx1310.app.a2iga.utils.SharedPrefUtils;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener, View.OnClickListener {
    
	Toolbar oToolbar;
	EditText pkgNameDialogInput;
	CardView oUnsupportedApi22Card, oNotDefaultAssistCard;
	ImageView oAssistantAppIcon;
	String isAssistAppPkgName;
	TextView oAssistantAppName, oRandomPromt;
	FrameLayout oSettingsLayout;
	LinearLayout oCurrentAssistAppLayout;
	private FragmentRefreshListener fragmentRefreshListener;
	
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
		
		oCurrentAssistAppLayout = findViewById(R.id.toolbarCurrentAssistAppLayout);
		oCurrentAssistAppLayout.setOnClickListener(this);
		oCurrentAssistAppLayout.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				
				pkgNameDialogInput = new EditText(MainActivity.this);
				pkgNameDialogInput.setHint(getString(R.string.current_assistant_pkgname_dialog_hint) + " " + isAssistAppPkgName);
				
				android.support.v7.app.AlertDialog.Builder b = new android.support.v7.app.AlertDialog.Builder(MainActivity.this, R.style.AppTheme_Dialog_Alert);
				
				b.setTitle(R.string.current_assistant_pkgname_dialog_title);
				b.setMessage(R.string.current_assistant_pkgname_dialog_message);
				b.setView(pkgNameDialogInput, 50, 0, 50, 0);
				b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (pkgNameDialogInput.getText().toString().isEmpty()) dialog.dismiss();
						else {
							SharedPrefUtils.saveData(MainActivity.this, Constants.ASSIST_APP_PKGNAME, pkgNameDialogInput.getText().toString());
							AppUtils.showToast(MainActivity.this, getString(R.string.app_selected_as_assistant));
						}
					}
				});
				b.setNegativeButton(android.R.string.cancel, null);
				b.create();
				b.show();
				
				return true;
				
			}
		});
		
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

			case R.id.toolbarCurrentAssistAppLayout:
				startActivity(new Intent(MainActivity.this, AppsListActivity.class));
				break;
				
			default: break;

		}

	}
	
	@Override
	public boolean onSupportNavigateUp() {
		onBackPressed();
		return true;
	}
	
	public FragmentRefreshListener getFragmentRefreshListener() {
        return fragmentRefreshListener;
    }

    public void setFragmentRefreshListener(FragmentRefreshListener fragmentRefreshListener) {
        this.fragmentRefreshListener = fragmentRefreshListener;
    }

    public interface FragmentRefreshListener{
        void onRefresh();
    }
    
}
