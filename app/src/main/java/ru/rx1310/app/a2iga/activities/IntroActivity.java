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
import ru.rx1310.app.a2iga.R;

public class IntroActivity extends AppCompatActivity {

	SharedPreferences mSharedPrefs;
	SharedPreferences.Editor mSharedPrefsEditor;

	boolean isFirstStart;

	Button btnContinue;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_intro);
		
		mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		mSharedPrefsEditor = mSharedPrefs.edit();

		// ? Prefs
		isFirstStart = mSharedPrefs.getBoolean("isFirstStart", true);

		// ? Кнопка "Продолжить"
		btnContinue = findViewById(R.id.intro_btnContinue);
		btnContinue.setEnabled(false);

		Handler mHandler = new Handler(); 
		mHandler.postDelayed(new Runnable() {
			public void run() {
				btnContinue.setEnabled(true);
				btnContinue.setText(R.string.intro_continue);
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
