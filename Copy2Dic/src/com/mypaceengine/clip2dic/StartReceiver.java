package com.mypaceengine.clip2dic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;


public class StartReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		SharedPreferences pref=Util.getPreferences(arg0);
			if(!Util.isServiceRunning(arg0, MainService.class)){
				Intent intent = new Intent(arg0, MainService.class);
				arg0.startService(intent);
			}

	}
}
