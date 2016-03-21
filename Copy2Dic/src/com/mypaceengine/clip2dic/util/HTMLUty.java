package com.mypaceengine.clip2dic.util;
/**
 * HTML Treat Tool
 */
import java.io.IOException;
import java.lang.Character.UnicodeBlock;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.net.Uri;
import android.text.Html;

public class HTMLUty {
	/**
	 * HTMLタグ削除
	 *
	 * @param str 文字列
	 * @return HTMLタグ削除後の文字列
	 */
	public static String HtmlTagRemover(String str) {
		// 文字列のすべてのタグを取り除く
		return str.replaceAll("<.+?>", "");
	}
	public static String connectionGET(Uri.Builder builder){
		HttpGet request = new HttpGet(builder.build().toString());
		return connectionExec(request,builder);
	}
	public static String connectionPOST(Uri.Builder builder,Header[] headers,HttpParams params){
		HttpPost request = new HttpPost(builder.build().toString());
		if(headers!=null)
		request.setHeaders(headers);
		if(params!=null)
		request.setParams(params);
		return connectionExec(request,builder);
	}
	public static String connectionExec(HttpRequestBase request,Uri.Builder builder){
		String result=null;
		DefaultHttpClient httpClient = new DefaultHttpClient();

		try {
		    result = httpClient.execute(request, new ResponseHandler<String>() {
		        @Override
		        public String handleResponse(HttpResponse response)
		                throws ClientProtocolException, IOException {

		            switch (response.getStatusLine().getStatusCode()) {
		            case HttpStatus.SC_OK:
		                return EntityUtils.toString(response.getEntity(), "UTF-8");

		            case HttpStatus.SC_NOT_FOUND:
		                return null;

		            default:
		            	return null;
		            }

		        }
		    });
		} catch (ClientProtocolException e) {
		    result=null;
		} catch (IOException e) {
			result=null;
		} catch (Exception e) {
			result=null;
		} finally {
		    httpClient.getConnectionManager().shutdown();
		}
		return result;
	}
	static String[][] escapeList={
		{"&quot;","\""},
		{"&amp;","&"},
		{"&lt;","<"},
		{"&gt;",">"},
		{"&nbsp;"," "},
		{"&copy;","@"},
		{"&laquo;","<"},
		{"&raquo;",">"}
	};
	static public String escapeHTMLSpecific(String str){
		for(int i=0;i<escapeList.length;i++){
			str=str.replaceAll(escapeList[i][0], escapeList[i][1]);
		}
		str=Html.fromHtml(str).toString();
		return str;
	}
	/**
	 * 全角が含まれているか
	 * @param s
	 * @return
	 */
	static public boolean chkZenkaku(String s){
		boolean result=false;
		char[] chars = s.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (String.valueOf(chars[i]).getBytes().length >= 2) {
				result=true;
				break;
			}
		}
		return result;
	}
	/**
	 * 全部半角か
	 * @param s
	 * @return
	 */
	static public boolean chkHankaku(String s){
		boolean result=true;
		char[] chars = s.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (String.valueOf(chars[i]).getBytes().length >= 2) {
				result=false;
				break;
			}
		}
		return result;
	}
	/**
	 * 漢字を含むか
	 * @param s
	 * @return
	 */
	static public boolean chkKANJI(String s){
		if(chkHankaku(s)){
			return false; 
		}
		boolean result=false;
		char[] chars = s.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if(UnicodeBlock.of(chars[i]) == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS){
				result=true;
				break;
			}
		}
		return result;
	}
}
