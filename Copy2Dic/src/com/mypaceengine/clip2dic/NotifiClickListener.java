package com.mypaceengine.clip2dic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotifiClickListener extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		boolean enable=!Util.isEnable(context);
		Util.setEnable(context, enable);
		String flag="–³Œø";
		int icon=R.drawable.ic_launcher_off;
		if(enable){
			flag="—LŒø";
			icon=R.drawable.ic_launcher;
		}
		AppNotification.putNotice(context, icon, "Copy2Dic["+flag+"]", "Copy2Dic", flag);

	}

}
