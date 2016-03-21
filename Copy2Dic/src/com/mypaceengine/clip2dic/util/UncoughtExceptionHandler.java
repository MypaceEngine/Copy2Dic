package com.mypaceengine.clip2dic.util;

/**
 * uncaught Exception Handler
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
public class UncoughtExceptionHandler  implements UncaughtExceptionHandler {

	private static File BUG_REPORT_FILE = null;
	static {
		String sdcard = FileUty.getExternalStorageDirectory();
		String path = sdcard + File.separator +FileUty.ApplicationName+ "bug.txt";
		BUG_REPORT_FILE = new File(path);
	}

	private static Context sContext;
	private static PackageInfo sPackInfo;
	private UncaughtExceptionHandler mDefaultUEH;
	public UncoughtExceptionHandler(Context context) {
		sContext = context;
		try {
			sPackInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		mDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
	}

	public void uncaughtException(Thread th, Throwable t) {


		try {
			saveState(t);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		if( sContext.getMainLooper().getThread().equals(th)){
			mDefaultUEH.uncaughtException(th, t);
		}
	}

	private void saveState(Throwable e) throws FileNotFoundException {
		String sdcard = FileUty.getExternalStorageDirectory();
		String folderpath = sdcard + File.separator +FileUty.ApplicationName;
		File folder=new File(folderpath);

		if(!folder.exists()){
			folder.mkdir();
		}
		File file = BUG_REPORT_FILE;
		PrintWriter pw = null;
		pw = new PrintWriter(new FileOutputStream(file),true);
		e.printStackTrace(pw);
		pw.close();
	}

	public static final boolean showBugReportDialogIfExist(Context context) {
		boolean result=false;
		try{
			File file = BUG_REPORT_FILE;
			if (file != null & file.exists()) {
				postBugReportInBackground();//ÉoÉOïÒçê
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return result;
	}

	private static void postBugReportInBackground() {
		new Thread(new Runnable(){
			public void run() {
				postBugReport();
				File file = BUG_REPORT_FILE;
				if (file != null && file.exists()) {
					file.delete();
				}
			}}).start();
	}

	private static void postBugReport() {
		//Add Bug Report Program..
	}
}