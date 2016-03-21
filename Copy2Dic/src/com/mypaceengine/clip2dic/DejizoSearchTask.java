package com.mypaceengine.clip2dic;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.mypaceengine.clip2dic.util.CacheData;
import com.mypaceengine.clip2dic.util.CacheUty;
import com.mypaceengine.clip2dic.util.HTMLUty;

public class DejizoSearchTask extends AsyncTask<String, String, String>  {
	EachController ctr=null;
	public void init(EachController _ctr){
		ctr=_ctr;
	}

	String source=null;
	String langType=null;
	String url=null;

	public String getSource() {
		return source;
	}
	@Override
	protected String doInBackground(String... params) {
		String dic=params[0];
		if(!Util.isDictionaryEnable(ctr.ctr.service, dic)){
			return null;
		}
		String type=params[1];
		langType=params[2];
		source=params[3];
		if(EachController.ENGLISH.equals(langType)){
			if(HTMLUty.chkZenkaku(source)){
				return null;
			}
		}

		String result=null;
		Uri.Builder builder = new Uri.Builder();
		builder.scheme("http");
		builder.authority("public.dejizo.jp");
		builder.path("/NetDicV09.asmx/SearchDicItemLite");
		builder.appendQueryParameter("Dic", type);
		builder.appendQueryParameter("Word",source );
		builder.appendQueryParameter("Scope", "HEADWORD");
		builder.appendQueryParameter("Match", "EXACT");
		builder.appendQueryParameter("Merge", "AND");
		builder.appendQueryParameter("Prof", "XHTML" );
		builder.appendQueryParameter("PageSize", "10");
		builder.appendQueryParameter("PageIndex", "0");
		String str=HTMLUty.connectionGET(builder);
		if(str!=null){
			ArrayList<String> list=parceList(str);
			for(String id:list){
				Uri.Builder builder2 = new Uri.Builder();
				builder2.scheme("http");
				builder2.authority("public.dejizo.jp");
				builder2.path("/NetDicV09.asmx/GetDicItemLite");
				builder2.appendQueryParameter("Dic", type);
				builder2.appendQueryParameter("Item", id);
				builder2.appendQueryParameter("Loc", "");
				builder2.appendQueryParameter("Prof", "XHTML" );
				Log.d("", builder2.build().toString());
				String str2=HTMLUty.connectionGET(builder2);
				if(str2!=null){
					String buf=parceList2(str2);
					if(result==null){
						result=buf;
					}else{
						result=result+" "+buf;
					}
				}
				if((result!=null)&&(result.length()>300)){
					break;
				}
			}
		}
		if((result!=null)&&(source.length()+6>result.length())&&result.contains(source)){
			result=null;
		}
		if(result!=null){
			CacheData data=new CacheData();
			data.setDescription(result);
			if((EachController.ENGLISH.equals(langType))&&(!HTMLUty.chkZenkaku(source))){
				Uri.Builder urlbuilder = new Uri.Builder();
				urlbuilder.scheme("http");
				urlbuilder.authority("ejje.weblio.jp");
				urlbuilder.path("/content/"+source);
				url=urlbuilder.build().toString();
			}else{
				Uri.Builder urlbuilder = new Uri.Builder();
				urlbuilder.scheme("http");
				urlbuilder.authority("ja.wikipedia.org");
				urlbuilder.path("/wiki/"+source);
				url=urlbuilder.build().toString();
			}
			data.setUrl(url);
			data.setDic(dic);
			CacheUty.setValue(source, data);
		}
		return result;
	}
	public ArrayList<String> parceList(String str){
		ArrayList<String> result=null;
		try{
			InputStream bais = new ByteArrayInputStream(str.getBytes("utf-8"));
		Document document = DocumentBuilderFactory.newInstance().
                newDocumentBuilder().parse(bais);
		Element root = document.getDocumentElement();
		NodeList element = root.getElementsByTagName("TitleList");
		Element TitleList=(Element)element.item(0);
		NodeList element2=TitleList.getElementsByTagName("DicItemTitle");
        //指定の要素を抜き出す
		result=new ArrayList<String>();
        for (int i = 0; i < element2.getLength(); i++)
        {
        	Node element3=((Element)element2.item(i)).getElementsByTagName("ItemID").item(0);
        	String ss=element3.getFirstChild().getNodeValue();
        	result.add(ss);
        }
		}catch(Exception ex){
			result=null;
		}
		return result;
	}
	public String parceList2(String str){
		String result=null;
		try{
			InputStream bais = new ByteArrayInputStream(str.getBytes("utf-8"));
			Document document = DocumentBuilderFactory.newInstance().
					newDocumentBuilder().parse(bais);

			//指定の要素を抜き出す
			NodeList element = document.getElementsByTagName("Body");
			StringWriter sw = new StringWriter();
			TransformerFactory tfactory = TransformerFactory.newInstance();
			Transformer transformer = tfactory.newTransformer();
			transformer.transform(new DOMSource(element.item(0)), new StreamResult(sw));
			result = sw.toString();
			result=HTMLUty.HtmlTagRemover(result);
			result=result.replaceAll("\n", " ");
			result=result.replaceAll("\t", " ");
			do{
				result=result.replaceAll("  ", " ");
			}while(result.contains("  "));
			if(result.substring(0, 1).equals(" ")){
				result=result.substring(1);
			}

			String replace="---------------------------------------------- 出典:「フリー百科辞典ウィキペディア」(2009-01-01) Text is available under GNU Free Documentation License. ["+source+"]の改定履歴 ご利用上の注意";
			result=result.replaceAll(replace, "");
		}catch(Exception ex){
			result=null;
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
			ctr.setResult(source,result,null,url,this);
		}
	}

}
