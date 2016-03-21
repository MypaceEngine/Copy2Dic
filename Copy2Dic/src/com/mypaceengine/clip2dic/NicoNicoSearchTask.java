package com.mypaceengine.clip2dic;

import org.json.JSONObject;

import android.net.Uri;
import android.os.AsyncTask;

import com.mypaceengine.clip2dic.util.CacheData;
import com.mypaceengine.clip2dic.util.CacheUty;
import com.mypaceengine.clip2dic.util.HTMLUty;

public class NicoNicoSearchTask extends AsyncTask<String, String, String>  {
	EachController ctr=null;
	public void init(EachController _ctr){
		ctr=_ctr;
	}

	String source=null;


	public String getSource() {
		return source;
	}
	@Override
	protected String doInBackground(String... params) {
		String dic=params[0];
		if(!Util.isDictionaryEnable(ctr.ctr.service, dic)){
			return null;
		}
		source=params[1];
		String result=null;
		try{
			String encodeSource=source;
		Uri.Builder builder = new Uri.Builder();
		builder.scheme("http");
		builder.authority("api.nicodic.jp");
		builder.path("/page.summary/json/a/"+encodeSource);
		String str=HTMLUty.connectionGET(builder);
		JSONObject rootObject = new JSONObject(str);

		result=rootObject.getString("summary");
		result=result.replaceAll("\n", " ");
		result=result.replaceAll("\t", " ");
		do{
			result=result.replaceAll("  ", " ");
		}while(result.contains("  "));
		  if((result!=null)&&(source.length()+5>result.length())&&result.contains(source)){
			  result=null;
		  }
		}catch(Exception ex){

		}
		if(result!=null){
			CacheData data=new CacheData();
			data.setDescription(result);
			data.setUrl("http://dic.nicovideo.jp/a/"+source);
			data.setDic(dic);
			CacheUty.setValue(source, data);
		}
		return result;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if(result==null){
			return;
		}
		if(ctr!=null){
			String url="http://dic.nicovideo.jp/a/"+source;
			ctr.setResult(source,result,null,url,this);
		}
	}

}
