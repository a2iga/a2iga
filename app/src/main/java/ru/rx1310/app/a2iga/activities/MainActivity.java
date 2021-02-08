// ! rx1310 <rx1310@inbox.ru> | Copyright (c) rx1310, 2021 | MIT License

package ru.rx1310.app.a2iga.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import ru.rx1310.app.a2iga.R;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.os.Build;

public class MainActivity extends AppCompatActivity {
    
	CardView msgUncompatApi22;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		
		msgUncompatApi22 = findViewById(R.id.uncompatibilityApi22Message);
		if (Build.VERSION.SDK_INT >= 22) {
			msgUncompatApi22.setVisibility(View.GONE);
		} else {
			msgUncompatApi22.setVisibility(View.VISIBLE);
		}
		
    }
	
	public void appsListGet(View v) {
		startActivity(new Intent(this, AppsListActivity.class));
	}
    
}
