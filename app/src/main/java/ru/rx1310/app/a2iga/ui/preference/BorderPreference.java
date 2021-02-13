// ! rx1310 <rx1310@inbox.ru> | Copyright (c) rx1310, 2021 | MIT License

package ru.rx1310.app.a2iga.ui.preference;

import android.content.Context;
import android.util.AttributeSet;

import ru.rx1310.app.a2iga.R;

public class BorderPreference extends CategoryPreference {

	public BorderPreference(Context c, AttributeSet attrs) {
		super(c, attrs);
		setSelectable(false);
		setLayoutResource(R.layout.ui_preference_border);
	}

}
