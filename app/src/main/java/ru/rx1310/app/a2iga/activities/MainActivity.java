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
import android.content.ComponentName;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener, View.OnClickListener {
    
	Toolbar oToolbar;
	EditText pkgNameDialogInput;
	CardView oUnsupportedApi22Card, oNotDefaultAssistCard;
	ImageView oAssistantAppIcon, oModuleVerifyIcon;
	String isAssistAppPkgName;
	TextView oAssistantAppName, oRandomPromt, oAssistAppNameSummary;
	FrameLayout oSettingsLayout;
	LinearLayout oCurrentAssistAppLayout, oBetaVersionInstalledMsgLayout, oModuleSettingsLayout;
	
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
		
		oAssistAppNameSummary = findViewById(R.id.toolbarCurrentAssistAppSummary);
		oModuleVerifyIcon = findViewById(R.id.iconVerify);
		oSettingsLayout = findViewById(R.id.layoutSettings);
		
		/* ? Сообщение о статусе Beta
		 *   Отображается только если в versionName в build.gradle
		 *   обнаружена буква "b" */
		oBetaVersionInstalledMsgLayout = findViewById(R.id.toolbarBetaMessage);
		if (AppUtils.getVersionName(this, getPackageName()).contains("b")) oBetaVersionInstalledMsgLayout.setVisibility(View.VISIBLE);
		else oBetaVersionInstalledMsgLayout.setVisibility(View.GONE);
		
		oToolbar = findViewById(R.id.toolbar);
		
		setSupportActionBar(oToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true); // ? Включение кнопки Back в Toolbar
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_logo); // ? Замена иконки Back в Toolbar на свою
		getSupportActionBar().setDisplayShowHomeEnabled(true); // ? Вязанное с setDisplayHomeAsUpEnabled
		getSupportActionBar().setTitle(R.string.activity_main); // ? Заголовок Toolbar
		
		oCurrentAssistAppLayout = findViewById(R.id.toolbarCurrentAssistAppLayout);
		oCurrentAssistAppLayout.setOnClickListener(this);
		oCurrentAssistAppLayout.setOnLongClickListener(new View.OnLongClickListener() {
			
			/* ? Обработка длительного нажатия на блоке с названием и иконкой
			 *   текущ. приложения для запуска при вызове ассистента */
			@Override
			public boolean onLongClick(View v) {
				
				/* ? Если выбран модуль, то при длительном
				 *   удержании будет вызвано окно настроект модуля */
				if (isAssistAppPkgName.contains("a2iga.module.")) {
					Intent i = new Intent(Intent.ACTION_MAIN);
					i.setComponent(new ComponentName(isAssistAppPkgName, isAssistAppPkgName + ".ModuleSettingsActivity"));
					startActivity(i);
				} else AppUtils.showToast(MainActivity.this, "👨‍💻 with ❤️ by rx1310");
				
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
			
			if (isAssistAppPkgName.contains("a2iga.module.")) {
				oAssistAppNameSummary.setText(getString(R.string.current_assistant_open_appslist) + " " + getString(R.string.current_assistant_open_module_settings));
				oModuleVerifyIcon.setVisibility(View.VISIBLE);
			} else {
				oAssistAppNameSummary.setText(getString(R.string.current_assistant_open_appslist));
				oModuleVerifyIcon.setVisibility(View.INVISIBLE);
			}
			
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
	
	/* ? Обработка нажатия на иконку в Toolbar
	 *   Отображается диалог, в котором можно вручную
	 *   указать имя пакета приложения, которое будет
	 *   запускать a2iga при вызове ассистента */
	@Override
	public boolean onSupportNavigateUp() {
		
		//onBackPressed();
		
		pkgNameDialogInput = new EditText(MainActivity.this);
		pkgNameDialogInput.setHint(getString(R.string.current_assistant_pkgname_dialog_hint) + " " + isAssistAppPkgName);

		android.support.v7.app.AlertDialog.Builder b = new android.support.v7.app.AlertDialog.Builder(MainActivity.this, R.style.AppTheme_Dialog_Alert);

		b.setTitle(R.string.current_assistant_pkgname_dialog_title);
		b.setMessage(R.string.current_assistant_pkgname_dialog_message);
		b.setView(pkgNameDialogInput, 50, 0, 50, 0); // 50, 0, 50, 0 — отступы EditText слева/справа
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
    
}
