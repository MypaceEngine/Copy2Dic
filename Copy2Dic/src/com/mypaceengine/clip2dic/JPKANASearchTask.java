package com.mypaceengine.clip2dic;

import java.io.StringReader;
import java.net.URLEncoder;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

import com.mypaceengine.clip2dic.util.CacheData;
import com.mypaceengine.clip2dic.util.CacheUty;
import com.mypaceengine.clip2dic.util.HTMLUty;

public class JPKANASearchTask extends AsyncTask<String, String, String>  {
	EachController ctr=null;
	
	//yahooappid‚ÌID(FixMe)
	static String APPID="hoge";
	
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
		source=params[1];
	
		if(!HTMLUty.chkKANJI(source)){
			return null;
		}
		StringBuffer result=null;
		try{
			
			String encodeSource=source;
		Uri.Builder builder = new Uri.Builder();
		builder.scheme("http");
		builder.authority("jlp.yahooapis.jp");
		builder.path("/FuriganaService/V1/furigana");
		builder.appendQueryParameter("appid", APPID);
		builder.appendQueryParameter("sentence", encodeSource);
		String str=HTMLUty.connectionGET(builder);
		
		
		XmlPullParser xmlPullParser = Xml.newPullParser();
		 
		try {
		    xmlPullParser.setInput( new StringReader ( str ) );
		} catch (XmlPullParserException e) {
		     Log.d("XmlPullParserSample", "Error");
		}
		try {
		    int eventType;
		    eventType = xmlPullParser.getEventType();
		    while (eventType != XmlPullParser.END_DOCUMENT) {
		        if((eventType == XmlPullParser.START_TAG )&&(xmlPullParser.getDepth()==5)&&(xmlPullParser.getName().equals("Furigana"))){
		        	if(result==null){
		        		result=new StringBuffer();
		        	}
		        	result.append(xmlPullParser.nextText());
		        	
		        }
		        eventType = xmlPullParser.next();
		    }
		} catch (Exception e) {
		     Log.d("XmlPullParserSample", "Error");
		}
		
		}catch(Exception ex){

		}
		String resultS=null;
		if(result!=null){
			resultS=result.toString();
		}
		return resultS;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if(result==null){
			return;
		}
		if(ctr!=null){
			ctr.setJPHatsu(result);
			CacheUty.getValue(source);
			CacheData data=CacheUty.getValue(source);
			if(data!=null){
				data.setHatu(result);
				CacheUty.setValue(source, data);
			}
		}
	}

}
