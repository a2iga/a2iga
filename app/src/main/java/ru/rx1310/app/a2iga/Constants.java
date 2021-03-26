// ! rx1310 <rx1310@inbox.ru> | Copyright (c) rx1310, 2021 | MIT License

package ru.rx1310.app.a2iga;

public class Constants {
	
	public static String ASSIST_APP_PKGNAME = "ASSIST_PKGNAME";
	
	public static final String randomPromts[] = {
		"A2IGA позволяет запускать приложение с любой позиции — просто запустите ассистент!",
		"Факт: A2IGA был разработан для личного пользования и изначально не было даже интерфейса и какого-то названия.",
		"Интерфейс A2IGA перетерпел крупные изменения только лишь в восьмом стабильном публичном релизе.",
		"На некоторых устройствах есть возможность запуска приложения даже с экрана блокировки. Попробуй, может и у тебя получится)",
		"Странно, но меню приложений в A2IGA появилось давно, а вот поиск по этим приложениям только недавно.\n\n*звук извинений разработчика*",
		"A2IGA не собирает никаких данных о пользователе и не содержит рекламу. Абсолютно. Нигде. Никогда.",
		"A2IGA имеет открытый исходный код. Каждый может собрать приложение сам или помочь в разработке автору.",
		"При вызове ассистента запускается A2IGA, а потом само приложение. Однако вы не увидите окно A2IGA, запуск происходит моментально (даже если смотреть в замедленной съёмке)."
    };
	
	public static class OTA {

		public static final String URL_JSON = "https://raw.githubusercontent.com/rx1310/a2iga/master/ota.json";
		public static final String URL_APK = "apkUrl";
		public static final String URL_CHANGELOG = "changelogUrl";
		public static final String VERSION_NAME = "versionName";
		public static final String VERSION_CODE = "versionCode";
		public static final String MESSAGE = "updateMessage";

	}
	
}
