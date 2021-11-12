// ! rx1310 <rx1310@inbox.ru> | Copyright (c) rx1310, 2021 | MIT License

package ru.rx1310.app.a2iga.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import android.support.v7.app.AppCompatActivity;

import ru.rx1310.app.a2iga.R;

public class SplashActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				
				// ? Запуск активности
				SplashActivity.this.startActivity(new Intent(SplashActivity.this, MainActivity.class));
				
				// ? "Убийство" сплеша (иначе при нажатии пользователем
				// кнопки "Back" будет открыт снова сплеш.
				SplashActivity.this.finish();
				
				// ? Отображение анимации при переходе к MainActivity
				overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);

			}

		}, 1000); // ? 1000 = 1s (задержка перехода)

	}

}
