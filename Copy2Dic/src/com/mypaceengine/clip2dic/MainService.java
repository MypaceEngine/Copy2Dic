package com.mypaceengine.clip2dic;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.mypaceengine.clip2dic.util.UncoughtExceptionHandler;

public class MainService extends Service{

	class MainServiceBinder extends Binder {

		MainServiceBinder getService() {
			return MainServiceBinder.this;
		}

	}
	static public Service thisService=null;
	Controller ctr=null;
	@Override
	public IBinder onBind(Intent arg0) {
		
		return new MainServiceBinder();
	}
	@Override
	public boolean onUnbind(Intent intent) {
		return false; 	}

	@Override
	public void onCreate() {
		super.onCreate();
		thisService=this;
		Thread.setDefaultUncaughtExceptionHandler(new UncoughtExceptionHandler(this.getApplicationContext()));
		UncoughtExceptionHandler.showBugReportDialogIfExist(this);
		ctr=new Controller();
		ctr.init(this);
		String flag="����";
		int icon=R.drawable.ic_launcher_off;
		if(Util.isEnable(this)){
			flag="�L��";
			icon=R.drawable.ic_launcher;
		}
		AppNotification.putNotice(this, icon, "Copy2Dic["+flag+"]", "Copy2Dic", flag);

			setSchedule(this);
	}
	@Override
	public void onStart(Intent intent, int startId) {
		ctr.start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ctr.finish();
	}
	
	
	static boolean  first=true;
	/**
	 * �X�P�W���[����ݒ肵�܂��B
	 * @param context
	 */
	public static void setSchedule(Context context) {
	    AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
	    long time = System.currentTimeMillis();
	    if((!isSetPending(context))||first) {
//	    if(!isSetPending(context)) {
	    Intent intent = new Intent(context, RewakeupService.class);
	        PendingIntent p = PendingIntent.getService(
	                context,
	                -1,
	                intent,
	                PendingIntent.FLAG_UPDATE_CURRENT);
	        long delay =60*1000; // 1���Ԋu�Œ���I�ɏ������s��
	        am.setRepeating(AlarmManager.RTC, time, delay, p);
	        first=false;
	    }
	}
	 
	/**
	 * �X�P�W���[�����ݒ肳��Ă��邩�ǂ�����Ԃ��܂��B
	 * @param context
	 */
	public static boolean isSetPending(Context context) {
	    Intent intent = new Intent(context, RewakeupService.class);
	    PendingIntent p = PendingIntent.getService(
	            context,
	            -1,
	            intent,
	            PendingIntent.FLAG_NO_CREATE);
	    if(p == null) {
	        return false;
	    }else {
	        return true;
	    }
	}
	 
	/**
	 * �X�P�W���[�����L�����Z�����܂��B
	 * @param context
	 */
	public static void cancelSchedule(Context context) {
	    AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
	    Intent intent = new Intent(context, RewakeupService.class);
	    PendingIntent p = PendingIntent.getService(
	            context,
	            -1,
	            intent,
	            PendingIntent.FLAG_CANCEL_CURRENT);
	    am.cancel(p);
	    p.cancel(); // ������s��Ȃ���isSetPending�ňӐ}�����l���Ԃ��Ă��Ȃ�
	}

}
