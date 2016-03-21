package com.mypaceengine.clip2dic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.net.Uri;
import android.os.AsyncTask;
import android.text.Html;

import com.mypaceengine.clip2dic.util.CacheData;
import com.mypaceengine.clip2dic.util.CacheUty;
import com.mypaceengine.clip2dic.util.HTMLUty;

public class WeblioSearchTask extends AsyncTask<String, String, String>  {
	EachController ctr=null;
	public void init(EachController _ctr){
		ctr=_ctr;
	}

	String source=null;
	String url=null;
	String resultHatu=null;
	public String getSource() {
		return source;
	}
	@Override
	protected String doInBackground(String... params) {
		String dic=params[0];
		if(!Util.isDictionaryEnable(ctr.ctr.service, dic)){
			return null;
		}
		String httpType=params[1];
		String host=params[2];
		String path=params[3];
		String langType=params[4];
		source=params[5];
		if(EachController.ENGLISH.equals(langType)){
			if(HTMLUty.chkZenkaku(source)){
				return null;
			}
		}
		String result=null;
		try{
			String encodeSource=source;
		Uri.Builder builder = new Uri.Builder();
		builder.scheme(httpType);
		builder.authority(host);
		builder.path("/"+path+"/"+encodeSource);
		url=builder.build().toString();
		String str=HTMLUty.connectionGET(builder);

		String regexHatu="<span class=phoneticEjjeDesc>(.+?)</span>";
		Pattern pHatu = Pattern.compile(regexHatu);
		Matcher mHatu = pHatu.matcher(str);
		if (mHatu.find()){
			resultHatu=mHatu.group(1);
			resultHatu = HTMLUty.escapeHTMLSpecific(resultHatu);
		}
		
		String regex="<meta name=\"(d|D)escription\" content=\"(.+?)\"";
		Pattern p = Pattern.compile(regex);

		Matcher m = p.matcher(str);
		if (m.find()){
		  result=m.group(2);
		  result = HTMLUty.escapeHTMLSpecific(result);

		}
		}catch(Exception ex){

		}
		if(result!=null){
			try{
				String regex = "- ��[0-9]+���ꂠ��p�a���T�E�a�p���T�B�����E�C�f�B�I����������p�ꎫ���B";
				Pattern p = Pattern.compile(regex);

				Matcher m = p.matcher(result);
				result = m.replaceFirst("");
			if(result.indexOf(source+"�Ƃ�?")==0){
				result=result.replaceFirst(source+"�Ƃ�\\?", "");
			}
			if(result.indexOf("�u"+source+"�v�Ƃ�?")==0){
				result=result.replaceFirst("�u"+source+"�v�Ƃ�\\?", "");
			}
			if(result.indexOf(source+"���C���X�g�t���ł킩��I")==0){
				result=result.replaceFirst(source+"���C���X�g�t���ł킩��I", "");
			}
			if(result.indexOf("�u"+source+"�v�Ƃ�")==0){
				result=result.replaceFirst("�u"+source+"�v�Ƃ�", "");
			}
			if(result.indexOf(source+"�̈Ӗ���a��B")==0){
				result=result.replaceFirst(source+"�̈Ӗ���a��B", "");
			}
			if(result.indexOf("?")==0){
				result=result.replaceFirst("\\?", "");
			}
			if(" ".equals(result)){
				result=null;
			}
			if(result.length()==0){
				result=null;
			}
			}catch(Exception ex){}
		}
	  if((result!=null)&&(source.length()+6>result.length())&&result.contains(source)){
		  result=null;
	  }
	  
		if(result!=null){
			CacheData data=new CacheData();
			data.setDescription(result);
			data.setUrl(url);
			data.setDic(dic);
			data.setHatu(resultHatu);
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
			ctr.setResult(source,result,resultHatu,url,this);
		}
	}

}
