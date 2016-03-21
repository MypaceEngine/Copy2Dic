package com.mypaceengine.clip2dic.util;

import java.io.Serializable;

public class CacheData implements Serializable{

	private static final long serialVersionUID = 1109268597764371274L;
	
	String hatu=null;
	String url=null;
	String description=null;
	String dic=null;
	public String getUrl() {
		String result="";
		if(url!=null){
			result=url;
		}
		return result;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDescription() {
		String result="";
		if(description!=null){
			result=description;
		}
		return result;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public String getDic() {
		return dic;
	}
	public void setDic(String dic) {
		this.dic = dic;
	}
	public int length(){
		return this.getDescription().length()+this.getUrl().length();
	}
	public String getHatu() {
		return hatu;
	}
	public void setHatu(String hatu) {
		this.hatu = hatu;
	}
	

}
