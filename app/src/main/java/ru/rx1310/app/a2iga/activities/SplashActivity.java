// ! rx1310 <rx1310@inbox.ru> | Copyright (c) rx1310, 2021 | MIT License

package ru.rx1310.app.a2iga.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import android.support.v7.app.AppCompatActivity;

import ru.rx1310.app.a2iga.R;

public class SplashActivity extends AppCompatActivity {

	boolean isFirstStart;
	
	SharedPreferences mSharedPrefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		// ? Prefs
		isFirstStart = mSharedPrefs.getBoolean("isFirstStart", true);
		
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {

				// ? Запуск активности
				if (!isFirstStart) {
					// ? Если запуск первый, то будет запущена отдельная активность
					SplashActivity.this.startActivity(new Intent(SplashActivity.this, MainActivity.class));
				} else {
					// ? ... если нет, то основная
					SplashActivity.this.startActivity(new Intent(SplashActivity.this, IntroActivity.class));
				}
				
				// ? "Убийство" сплеша (иначе при нажатии пользователем
				// кнопки "Back" будет открыт снова сплеш.
				SplashActivity.this.finish();
				
				// ? Отображение анимации при переходе к MainActivity
				overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);

			}

		}, 1000); // ? 1000 = 1s (задержка перехода)

	}

}
