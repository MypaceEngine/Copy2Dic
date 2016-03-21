package com.mypaceengine.clip2dic;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.mypaceengine.clip2dic.util.CacheUty;
import com.mypaceengine.clip2dic.util.UncoughtExceptionHandler;

public class HistoryActivity extends Activity{
	HistoryActivity this_instance=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this_instance=this;
		if(!Util.isServiceRunning(this, MainService.class)){
			Intent intent = new Intent(this, MainService.class);
			this.startService(intent);
		}
		Thread.setDefaultUncaughtExceptionHandler(new UncoughtExceptionHandler(this.getApplicationContext()));
		UncoughtExceptionHandler.showBugReportDialogIfExist(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history);

		ListView panel=(ListView)this.findViewById(R.id.history_list_panel);

        // リストビューのアイテムがクリックされた時に呼び出されるコールバックリスナーを登録します
        panel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                ListView listView = (ListView) parent;
                // クリックされたアイテムを取得します
                selectitem= (Map<String, String>) listView.getItemAtPosition(position);

            }
        });

		}

	List<Map<String, String>> dataList=null;
	@Override
	protected void onResume() {
		super.onResume();
		ListView panel=(ListView)this.findViewById(R.id.history_list_panel);
		panel.setAdapter(null);
		dataList=CacheUty.getList();

		
        SimpleAdapter adapter = new SimpleAdapter(
                this,
                dataList,
                android.R.layout.simple_list_item_2,
                new String[] { "title", "comment" },
                new int[] { android.R.id.text1, android.R.id.text2 }
            );
        
        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {

            public boolean setViewValue(View view, Object cursor, String columnIndex) {

                    ((TextView) view).setTextColor(Color.WHITE);
                    return false;
            }
        });
        panel.setAdapter(adapter);
        
        SimpleOnGestureListener listener=new SimpleOnGestureListener(){

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {

				if (Math.abs(e1.getY() - e2.getY()) > 100) {
		               return false;
		            }
		            // right to left swipe
		            if (e1.getX() - e2.getX() > 120 && Math.abs(velocityX) > 200) {

		                return false;
		            } else if (e2.getX() - e1.getX() > 120 && Math.abs(velocityX) > 200) {
		            	ListView list=(ListView)findViewById(R.id.history_list_panel);
		            	if((dataList.size()>selectIndex)&&(selectIndex>=0)){
		            		try{
		            		Animation anim = AnimationUtils.loadAnimation(
		            				this_instance, R.anim.list_animation
		                        );
		            		anim.setDuration(200);
		            		list.getChildAt(selectIndex).startAnimation(anim );
		            		
		            		new Handler().postDelayed(new Runnable() {

		            		    public void run() {
		            		    	ListView list=(ListView)findViewById(R.id.history_list_panel);
		            		    	SimpleAdapter adapter = (SimpleAdapter)list.getAdapter();
				            		Map<String, String> delData=dataList.remove(selectIndex);
				            		adapter.notifyDataSetChanged();
				            		CacheUty.delHistory(delData);
				            		//configureShare();
				            		invalidateOptionsMenu();
		            		    }

		            		}, anim.getDuration());
		            		}catch(Exception ex){}
		            	}
		                return true;
		            }
		            return false;
			}
        	
		    // シングルタップ(onSingleTapUpの後に呼ばれる)
		    @Override
		    public boolean onSingleTapConfirmed(MotionEvent e) {
		    	goURL();
		        return super.onSingleTapConfirmed(e);
		    }
        };
        mGestureDetector= new GestureDetector(this, listener);
        
        panel.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {

	            	ListView list=(ListView)findViewById(R.id.history_list_panel);
	                Rect rect = new Rect();
	                int childCount = list.getChildCount();
	                int[] listViewCoords = new int[2];
	                list.getLocationOnScreen(listViewCoords);
	                int x = (int) event.getRawX() - listViewCoords[0];
	                int y = (int) event.getRawY() - listViewCoords[1];
	                View child=null;
	                for (int i = 0; i < childCount; i++) {
	                    child = list.getChildAt(i);
	                    child.getHitRect(rect);
	                    if (rect.contains(x, y)) {
	                        break;
	                    }
	                }

	                if (child != null) {
	                	selectIndex = list.getPositionForView(child);
	                }
				if(mGestureDetector!=null){
					return mGestureDetector.onTouchEvent(event);
				}

		        return false;
			}
        	
        });
	}
	GestureDetector mGestureDetector=null;
	 Map<String, String> selectitem=null;
	 int selectIndex=-1;
	private void goURL(){
		if(selectitem!=null){
		Uri selectURL=Uri.parse(selectitem.get("url"));
		if(selectURL!=null){
		Intent i = new Intent(Intent.ACTION_VIEW,selectURL);
		i.setFlags(
				Intent.FLAG_ACTIVITY_NEW_TASK|
				Intent.FLAG_ACTIVITY_MULTIPLE_TASK
				);
		startActivity(i);
		}
		}
	}
	public void configureShare(){
		StringBuffer buf=new StringBuffer();
		List<Map<String, String>> dataList=CacheUty.getList();
		if(shareIntent!=null){
			for(int i=0;i<dataList.size();i++){
				buf.append(dataList.get(i).get("title")+"\r\n");
				buf.append(dataList.get(i).get("comment")+"\r\n");
				
				shareIntent.putExtra(Intent.EXTRA_TEXT, buf.toString());
			}
		}
	}
	
	Intent shareIntent=null;
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // menuファイルの読み込み
	    getMenuInflater().inflate(R.menu.action_bar_action_provider, menu);
	    MenuItem actionItem = menu.findItem(R.id.menu_item_share_action_provider_action_bar);
	 
	    ShareActionProvider actionProvider = (ShareActionProvider) actionItem.getActionProvider();
	    actionProvider.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
	 
	    shareIntent = new Intent(Intent.ACTION_SEND);
	    shareIntent.setAction(Intent.ACTION_SEND);
	    shareIntent.setType("text/plain");
	    configureShare();
	    actionProvider.setShareIntent(shareIntent);
	 
	    return true;
	}
}
