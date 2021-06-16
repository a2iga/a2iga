// ! rx1310 <rx1310@inbox.ru> | Copyright (c) rx1310, 2021 | MIT License

package ru.rx1310.app.a2iga.ui;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import ru.rx1310.app.a2iga.R;

public class CategoryPreference extends Preference {

	public CategoryPreference(Context c, AttributeSet attrs) {
		super(c, attrs);
		setSelectable(false);
		setLayoutResource(R.layout.my_preference_category);
	}

}
