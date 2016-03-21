package com.mypaceengine.clip2dic;

/**
 * Main Activity
 */

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;

import com.mypaceengine.clip2dic.util.UncoughtExceptionHandler;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		if(!Util.isServiceRunning(this, MainService.class)){
			Intent intent = new Intent(this, MainService.class);
			this.startService(intent);
		}
		Thread.setDefaultUncaughtExceptionHandler(new UncoughtExceptionHandler(this.getApplicationContext()));
		UncoughtExceptionHandler.showBugReportDialogIfExist(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	final Handler handler=new Handler();
	@Override
	protected void onResume() {
		handler.post(new Runnable() {
						@Override
						public void run() {
		String pack=Controller.getPreAppPackage();
		String keywordUrl=Controller.getURL();
		try{
		if(keywordUrl!=null){
			Uri uri = Uri.parse(keywordUrl);
			Intent i = new Intent(Intent.ACTION_VIEW,Uri.parse(keywordUrl));
			i.setFlags(
					Intent.FLAG_ACTIVITY_NEW_TASK|
					Intent.FLAG_ACTIVITY_SINGLE_TOP
					);
			startActivity(i);
		}else if(pack!=null){
			PackageManager pm = getPackageManager();
			Intent intent = pm.getLaunchIntentForPackage(pack);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
		}
		}catch(Exception ex){}
						}
		});

		super.onResume();


	}



}
