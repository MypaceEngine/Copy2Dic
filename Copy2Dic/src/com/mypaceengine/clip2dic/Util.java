package com.mypaceengine.clip2dic;

import java.util.List;
import java.util.Set;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Util {
	static String ENABLE_KEY="ENABLE_KEY";
	static String BOOTUP_KEY="BOOTUP_KEY";
	static String MANER_KEY="MANER_KEY";

	static public boolean isServiceRunning(Context c, Class<?> cls) {
		ActivityManager am = (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> runningService = am.getRunningServices(Integer.MAX_VALUE);
		for (RunningServiceInfo i : runningService) {
			if (cls.getName().equals(i.service.getClassName())) {
				return true;
			}
		}
		return false;
	}
	static SharedPreferences getPreferences(Context c){
		return	c.getSharedPreferences("Copy2Dic", Context.MODE_PRIVATE);
	}
	static Editor getEditor(Context c){
		SharedPreferences pref =getPreferences(c);
		Editor e = pref.edit();
		return e;
	}
	static void writeBoolean(Context c,String key,boolean val){
		Editor e=getEditor(c);
		e.putBoolean(key,val);
		e.commit();
	}

	static boolean isEnable(Context context){
		SharedPreferences pref=getPreferences(context);
		 return pref.getBoolean("enable", true);
	}
	static void setEnable(Context context,boolean flag){
		SharedPreferences pref=getPreferences(context);
		Editor e = pref.edit();
		e.putBoolean("enable", flag);
		e.commit();
	}
	
	static boolean isConfigureON(Context context,String key){
		SharedPreferences pref=getPreferences(context);
		 return pref.getBoolean(key, false);
	}
	static void setConfigureON(Context context,String key,boolean flag){
		SharedPreferences pref=getPreferences(context);
		Editor e = pref.edit();
		e.putBoolean(key, flag);
		e.commit();
	}
	static public boolean  isDictionaryEnable(Context context,String dic){
		SharedPreferences pref=getPreferences(context);
		boolean flag=false;
		for(int i=0;i<EachController.dicList.length;i++){
			if(EachController.dicList[i][2].equals(dic)){
				if(EachController.dicList[i][1].equals(EachController.ON)){
					flag=true;
				}
				break;
			}
		}
		 return pref.getBoolean("dic_"+dic, flag);
	}
	static void setDictionaryEnable(Context context,String dic,boolean flag){
		SharedPreferences pref=getPreferences(context);
		Editor e = pref.edit();
		e.putBoolean("dic_"+dic, flag);
		e.commit();
	}

	static public  Set<String> getHistorySet(Context context){
		SharedPreferences pref=getPreferences(context);
		 return pref.getStringSet("historySet",null);
	}
	static public void setHistorySet(Context context,Set<String> set){
		SharedPreferences pref=getPreferences(context);
		Editor e = pref.edit();
		e.putStringSet("historySet", set);
		e.commit();
	}
	
	static public String descriptionCutter(String tranStr){
		if(tranStr==null){
			return null;
		}
		if(tranStr.length()>300){
			String mae=tranStr.substring(0,300);
			String ato=tranStr.substring(300);
			int maeNokori=mae.lastIndexOf("B");
			int atoNokori=ato.indexOf("B");
			int index=maeNokori+1;
			if(300-maeNokori>atoNokori){
				index=300+atoNokori+1;
			}
			if(index>tranStr.length()-1){
				index=tranStr.length()-1;
			}
			tranStr=tranStr.substring(0,index);
		}
		return tranStr;
	}

}
