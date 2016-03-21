package com.mypaceengine.clip2dic;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class RewakeupService extends IntentService {

	  final static String TAG = "RewakeupService";

	  public RewakeupService() {
	    super(TAG);
	  }

	  @Override
	  protected void onHandleIntent(Intent intent) {
		  Log.d(TAG, "CheckService");
//		SharedPreferences pref=Util.getPreferences(this);
		if(!Util.isServiceRunning(this, MainService.class)){
			Intent intent2 = new Intent(this, MainService.class);
			this.startService(intent2);
			 Log.d(TAG, "RestartService");
		}
	  }
	}
