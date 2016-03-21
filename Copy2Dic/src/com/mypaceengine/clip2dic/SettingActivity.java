package com.mypaceengine.clip2dic;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.mypaceengine.clip2dic.util.UncoughtExceptionHandler;

public class SettingActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		if(!Util.isServiceRunning(this, MainService.class)){
			Intent intent = new Intent(this, MainService.class);
			this.startService(intent);
		}
		Thread.setDefaultUncaughtExceptionHandler(new UncoughtExceptionHandler(this.getApplicationContext()));
		UncoughtExceptionHandler.showBugReportDialogIfExist(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);

		LinearLayout panel=(LinearLayout)this.findViewById(R.id.dic_list_panel);
		String[][] diclist=EachController.dicList;
		for(int i=0;i<diclist.length;i++){
			int guidance=EachController.DIC_TITLE_LIST[i];
			CustomCheckBox box=new CustomCheckBox(this);
			box.setText(guidance);
			box.setFlag(diclist[i][2]);
			box.setChecked(Util.isDictionaryEnable(this, diclist[i][2]));
			box.setTextColor(Color.WHITE);
			panel.addView(box);
			box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				// チェック状態が変更された時のハンドラ
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					CustomCheckBox box=(CustomCheckBox)buttonView;
					Util.setDictionaryEnable(SettingActivity.this, box.getFlag(), box.isChecked());

				}
			});
			}
		
		CheckBox chkBox=(CheckBox)this.findViewById(R.id.confON);
		chkBox.setChecked(Util.isConfigureON(SettingActivity.this, "JISEI_REMOVE"));
		chkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			// チェック状態が変更された時のハンドラ
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				CheckBox box=(CheckBox)buttonView;
				Util.setConfigureON(SettingActivity.this, "JISEI_REMOVE", box.isChecked());

			}
		});

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
