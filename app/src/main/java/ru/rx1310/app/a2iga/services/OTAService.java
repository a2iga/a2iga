package ru.rx1310.app.a2iga.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import ru.rx1310.app.a2iga.tasks.OTACheckTask;
import ru.rx1310.app.a2iga.utils.AppUtils;

public class OTAService extends Service {
	
    private static Timer oTimer = new Timer(); 
    private Context oContext;
	
    public IBinder onBind(Intent i) {
		return null;
    }

    public void onCreate() {
		super.onCreate();
		
		oContext = this; 
		
		startService();
		
    }

    private void startService() {
		
		long frequence = TimeUnit.HOURS.toMillis(1);
		
        oTimer.scheduleAtFixedRate(new Task(), 0, frequence);
		
    }
	
	public int getInt(String s){
		return Integer.parseInt(s.replaceAll("[\\D]", ""));
	}
	
    private class Task extends TimerTask { 
	
        public void run() {
            handler.sendEmptyMessage(0);
        }
		
    }    

    public void onDestroy() {
		super.onDestroy();
    }

    private final Handler handler = new Handler() {
		
        @Override
        public void handleMessage(Message msg) {
			
            AppUtils.Log(oContext, "d", "OTAService\n\nhandleMessage: " + msg);
			//AppUtils.showToast(oContext, checkFrequence);
			
			OTACheckTask.checkUpdates(oContext, false);
			
        }
		
    };    

}
