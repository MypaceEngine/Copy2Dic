package com.mypaceengine.clip2dic;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;

public class CustomCheckBox extends CheckBox{

	public CustomCheckBox(Context context) {
		super(context);
	}

	public CustomCheckBox(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CustomCheckBox(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	String flag=null;
	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}



}
