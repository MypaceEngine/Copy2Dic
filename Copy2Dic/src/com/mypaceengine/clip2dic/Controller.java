package com.mypaceengine.clip2dic;

import java.util.List;

import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ClipboardManager.OnPrimaryClipChangedListener;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;
public class Controller implements OnPrimaryClipChangedListener{

	MainService service=null;
	ClipboardManager cm =null;
	public void init(MainService _service){
		service=_service;

	}
	TextView textView=null;
	View view=null;
	private void createBoard(){
		textView=null;
		WindowManager windowManager = (WindowManager) service.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		if(view!=null){
			try{
				windowManager.removeView(view);
			}catch(Exception ex){}
		}

		LayoutInflater layoutInflater = LayoutInflater.from(service);
		view=layoutInflater.inflate(R.layout.frontpanel, null);
		textView=(TextView)view.findViewById(R.id.front_text);
		textView.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				setVisible(false);
				if(url!=null){
					confNowApp();
					Intent i = new Intent(service.getApplicationContext(),MainActivity.class);
					i.setFlags(
							Intent.FLAG_ACTIVITY_NEW_TASK);
					service.startActivity(i);
					stopController();
				}
			}

		});
		view.setOnKeyListener(new OnKeyListener(){

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
					setVisible(false);
				}
				return false;
			}
			
		});
		view.setOnTouchListener(new OnTouchListener (){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				setVisible(false);
				return false;
			}

		});

		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
				WindowManager.LayoutParams.FLAG_FULLSCREEN |
				 WindowManager.LayoutParams.FLAG_SPLIT_TOUCH |  
				 WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);
		windowManager.addView(view, params);
		setVisible(false);
	}

	static String preAppP=null;
	public void confNowApp(){
		try{
			preAppP=null;
			Context context = service.getApplicationContext();
			ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			List<ActivityManager.RunningTaskInfo> taskInfo=am.getRunningTasks(5);
			if((taskInfo!=null)&&(taskInfo.size()>0)){
				taskInfo.get(0).topActivity.getPackageName();
				preAppP=taskInfo.get(0).topActivity.getPackageName();
			}
		}catch(Exception ex){}
	}

	static String getPreAppPackage(){
		String result=preAppP;
//		preAppP=null;
		return result;
	}
	
	public String getTopPackage(){
		String result=null;
		ActivityManager am = (ActivityManager) service.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> taskInfo=am.getRunningTasks(5);
		if((taskInfo!=null)&&(taskInfo.size()>0)){
			result=taskInfo.get(0).topActivity.getPackageName();
		}
		return result;
	}
	
	final Handler handler=new Handler();
	public void setVisible(boolean flag){
		if(view!=null){
			if(flag){
					handler.post(new Runnable() {
						@Override
						public void run() {
							view.setVisibility(View.VISIBLE);
						}
					});
			}else{
				handler.post(new Runnable() {
					@Override
					public void run() {
						view.setVisibility(View.GONE);
					}
				});
			}
		}
	}


	public void start(){
		createBoard();
		cm= (ClipboardManager)service.getSystemService(Context.CLIPBOARD_SERVICE);
		cm.addPrimaryClipChangedListener(this);
	}

	public static final String MATCH_URL =
			  "^([a-zA-Z0-9]+)(:\\/\\/[-_.!~*\\'()a-zA-Z0-9;\\/?:\\@&=+\\$,%#]+)$";

	EachController eachController=null;
	public void stopController(){
		if(eachController!=null){
			eachController.finish();
		}
	}
	@Override
	public void onPrimaryClipChanged() {
		if(!Util.isEnable(service)){
			return;
		}
		String str=null;
		//クリップボードからClipDataを取得
		ClipData cd = cm.getPrimaryClip();

		//クリップデータからItemを取得
		if(cd != null){
			ClipData.Item item = cd.getItemAt(0);
			try{
			str=item.getText().toString();
			}catch(Exception ex){}
//			throw new RuntimeException();
		}
		if((str==null)||(str.length()==0)){
			return;
		}
		str=str.replaceAll("\r\n", " ");
		str=str.replaceAll("\n", " ");
		str=str.replaceAll("\r", " ");
		str=str.replaceAll("  ", " ");
		if(eachController!=null){
			eachController.finish();
		}

		if (str.matches(MATCH_URL)) {
			return;
		}
		url=null;
		eachController=new EachController();
		eachController.init(this);
		eachController.start(str);
//		Toast.makeText(service.getApplicationContext(), "PrimaryClipChangedListener "+str, Toast.LENGTH_SHORT).show();
	}

	static public String getURL(){
		String result=url;
		url=null;
		return result;
	}

	static String url=null;
	public void setTextFromTask(String keyword,String str,String en_hatu,String jp_hatu,String _url){
		int len=0;
		int lan=0;
		if(keyword!=null){
			String ra=keyword.replaceAll(" ","");
			ra=ra.replaceAll("\r","");
			ra=ra.replaceAll("\n","");
			 lan=ra.length();
		}
		if((keyword==null)||(lan==0)){
			return;
		}
		
		if(str!=null){
			String re=str.replaceAll(" ","");
			 re=re.replaceAll("\r","");
			 re=re.replaceAll("\n","");
			 len=re.length();
		}
		if((str==null)||(len==0)){
			str="";
		}
		if(en_hatu==null){
			en_hatu="";
		}
		if(jp_hatu==null){
			jp_hatu="";
		}else{
			jp_hatu="  "+jp_hatu;
		}
		if(
				(en_hatu.length()==0)&&
				(jp_hatu.length()==0)&&
				(str.length()==0)
				){
			return;
		}
		if(eachController!=null){
			setText("["+keyword+"] "+en_hatu+jp_hatu+"\n"+str);
		}
		if(_url!=null){
			url=_url;
		}
	}

	String tranStr=null;
	public void setText(String str){
		tranStr=str;
		handler.post(new Runnable() {
			@Override
			public void run() {
				if(tranStr!=null){
					tranStr=Util.descriptionCutter(tranStr);
					textView.setText(tranStr);
					setVisible(true);
					textView.invalidate();
					tranStr=null;
				}
			}
		});
	}

	public void finish(){
		cm.removePrimaryClipChangedListener(this);
		if(eachController!=null){
			eachController.finish();
		}
	}

}
