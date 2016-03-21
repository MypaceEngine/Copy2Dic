package com.mypaceengine.clip2dic;

import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.net.Uri;
import android.os.AsyncTask;

import com.mypaceengine.clip2dic.util.CacheData;
import com.mypaceengine.clip2dic.util.CacheUty;
import com.mypaceengine.clip2dic.util.HTMLUty;

public class WeblioSearchTask2 extends AsyncTask<String, String, String>  {
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
		String httpType=params[1];
		String host=params[2];
		String path=params[3];
		String langType=params[4];
		source=params[5];
		if(!Util.isDictionaryEnable(ctr.ctr.service, dic)){
			return null;
		}
		if(EachController.ENGLISH.equals(langType)){
			if(HTMLUty.chkZenkaku(source)){
				return null;
			}
		}
		String result=null;
		try{
		String encodeSource=URLEncoder.encode(source, "UTF-8");
		encodeSource=encodeSource.replaceAll("%", "")+".html";
		Uri.Builder builder = new Uri.Builder();
		builder.scheme(httpType);
		builder.authority(host);
		builder.path("/"+path+"/"+encodeSource);
		String str=HTMLUty.connectionGET(builder);

		String regex="<meta name=\"(d|D)escription\" content=\"(.+?)\"";
		Pattern p = Pattern.compile(regex);

		Matcher m = p.matcher(str);
		if (m.find()){
//		  String matchstr = m.group();
		  result=m.group(2);
		  result = HTMLUty.escapeHTMLSpecific(result);
		  if((result!=null)&&(source.length()+5>result.length())&&result.contains(source)){
			  result=null;
		  }
		}
		}catch(Exception ex){

		}
		if(result!=null){

			CacheData data=new CacheData();
			data.setDescription(result);
			data.setUrl(null);
			CacheUty.setValue(source, data);
		}
		return result;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if(ctr!=null){
			ctr.setResult(source,result,null,null,this);
		}
	}

}
