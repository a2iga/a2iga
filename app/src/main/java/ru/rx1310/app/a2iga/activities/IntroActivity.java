// ! rx1310 <rx1310@inbox.ru> | Copyright (c) rx1310, 2021 | MIT License

package ru.rx1310.app.a2iga.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import ru.rx1310.app.a2iga.R;

public class IntroActivity extends AppCompatActivity {

	boolean isFirstStart;
	
	Button introContinueButton;
	//LinearLayout introTip;

	SharedPreferences mSharedPrefs;
	SharedPreferences.Editor mSharedPrefsEditor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_intro);
		
		mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		mSharedPrefsEditor = mSharedPrefs.edit();

		// ? Prefs
		isFirstStart = mSharedPrefs.getBoolean("isFirstStart", true);
		
		// ? Кнопка "Продолжить"
		introContinueButton = findViewById(R.id.el_introContinueButton);
		introContinueButton.setEnabled(false);
		
		// ? Блок с подсказкой
		//introTip = findViewById(R.id.el_introTip);
		//introTip.setVisibility(View.INVISIBLE);
		
		Handler mHandler = new Handler(); 
		mHandler.postDelayed(new Runnable() {
			public void run() {
				//introTip.setVisibility(View.VISIBLE);
				introContinueButton.setEnabled(true);
				introContinueButton.setText("Continue");
			} 
		}, 1000);
		
	}
	
	public void introContinue(View mView) {
		
		// ? Запуск основной активности
		startActivity(new Intent(IntroActivity.this, MainActivity.class));
		
		// ? Завершение жизни текущей активности
		IntroActivity.this.finish();
		
		// ? Отображение анимации перехода
		overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
		
		// ? Сохранение настройки
		mSharedPrefsEditor.putBoolean("isFirstStart", false);
		mSharedPrefsEditor.commit();
        
	}

}
