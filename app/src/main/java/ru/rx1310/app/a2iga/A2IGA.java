// ! rx1310 <rx1310@inbox.ru> | Copyright (c) rx1310, 2020 | MIT License

package ru.rx1310.app.a2iga;

import android.app.Application;

public class A2IGA extends Application {
	
	private static A2IGA instance;

	public A2IGA(){
		instance = this;
	}

	public static A2IGA get() {
		return instance;
	}

	@Override
	public void onCreate() {
		// TODO: Implement this method
		super.onCreate();
	}

}
