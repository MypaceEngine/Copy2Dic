package com.mypaceengine.clip2dic;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class AppNotification {

	   public static final int NOTIFICATION_ID = 1119;

	   private static Notification createNotification(Context context,int icon,String tooltip,String mainTitle,String subtitle) {
		   Notification notification=null;
		      Intent i = new Intent(context, NotifiClickListener.class);
		      PendingIntent pi = PendingIntent.getBroadcast(context, NOTIFICATION_ID, i, 0);

		      PendingIntent pi2 = PendingIntent.getActivity(
			          context,
			          0,                                             // requestCode
			          new Intent(context, SettingActivity.class),
			          0                                              // Default flags
			      );
		      PendingIntent pi3 = PendingIntent.getActivity(
			          context,
			          0,                                             // requestCode
			          new Intent(context, HistoryActivity.class),
			          0                                              // Default flags
			      );

		      RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.row);
		      contentView.setImageViewResource(R.id.icon, icon);
		      contentView.setTextViewText(R.id.title, mainTitle);
		      contentView .setTextViewText(R.id.text,subtitle);
		      contentView.setOnClickPendingIntent(R.id.settingbtn, pi2);
		      contentView.setOnClickPendingIntent(R.id.historybtn, pi3);

		   Notification.Builder builder = new Notification.Builder(context);
		   builder.setSmallIcon(icon)
		   .setWhen(System.currentTimeMillis())

		   .setTicker(tooltip)
		   .setContent(contentView)
		   .setContentTitle(mainTitle)
		   .setContentText(subtitle)
		   .setContentIntent(pi);
		   try{
			   notification = builder.build();
		   }catch(NoSuchMethodError nsme){
			   notification = builder.getNotification();
		   }
	       notification.flags = notification.flags
	         | Notification.FLAG_NO_CLEAR
	          | Notification.FLAG_ONGOING_EVENT;
	       notification.number = 0;
	      return notification;
	   }


	   public static void putNotice(Context context,int icon,String tooltip,String mainTitle,String subtitle) {
	      NotificationManager nm = (NotificationManager)
	         context.getSystemService(Context.NOTIFICATION_SERVICE);
	      Notification notification = createNotification(context,icon,tooltip,mainTitle,subtitle);
	      if(nm!=null){
	      nm.notify(AppNotification.NOTIFICATION_ID, notification);
	      }
	   }

	   public static void removeNotice(Context context) {
	      NotificationManager nm = (NotificationManager)
	         context.getSystemService(Context.NOTIFICATION_SERVICE);
	      if(nm!=null){
	      nm.cancel(NOTIFICATION_ID);
	      }
	   }
	}