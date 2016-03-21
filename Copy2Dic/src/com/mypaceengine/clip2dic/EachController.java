package com.mypaceengine.clip2dic;

import java.util.ArrayList;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.mypaceengine.clip2dic.util.CacheData;
import com.mypaceengine.clip2dic.util.CacheUty;
import com.mypaceengine.clip2dic.util.HTMLUty;

public class EachController {

	Controller ctr=null;
	ArrayList<AsyncTask> list=null;
	public void init(Controller _ctr){
		ctr=_ctr;
	}
	final static public String ALL="ALL" ;
	final static public String ENGLISH="ENGLISH" ;

	final static public String DEJIENG="DEJIENG";
	final static public String DEJIKOKUGO="DEJIKOKUGO";
	final static public String EIJIROENG="EIJIROENG";
	final static public String EIJIROKOKUGO="EIJIROKOKUGO";
	final static public String HATENA="HATENA";
	final static public String PIXIV="PIXIV";
	final static public String NICONICO="NICONICO";

	final static public int[] DIC_TITLE_LIST=
		{R.string.deji_eng,
		R.string.wikipedia,
		R.string.weblio_eng,
		R.string.weblio_kokugo,
		R.string.hatena,
		R.string.pixiv,
//		R.string.ewords,
		R.string.niconico};

	final static public String DEJIZO="DEJIZO" ;
	final static public String DESC_TYPE="DESC_TYPE" ;
	final static public String DESC_TYPE2="DESC_TYPE2" ;
	final static public String NICO_TYPE="NICO_TYPE" ;
	
	final static public String ON="ON" ;
	final static public String OFF="OFF" ;
	
	final static public String[][] dicList={
			{DEJIZO,OFF,DEJIENG,"EJdict",ENGLISH},	//EJDictâpòaé´ìT
			{DEJIZO,ON,DEJIKOKUGO,"wpedia",ALL},		//ÉEÉBÉLÉyÉfÉBÉAì˙ñ{åÍî≈
			{DESC_TYPE,ON,EIJIROENG,"http","ejje.weblio.jp","content",ENGLISH},		//Weblioâpòaé´èë
			{DESC_TYPE,ON,EIJIROKOKUGO,"http","www.weblio.jp","content",ALL},			//Weblioé´èë
			{DESC_TYPE,OFF,HATENA,"http","d.hatena.ne.jp","keyword",ALL},			//ÇÕÇƒÇ»∑∞‹∞ƒﬁ
			{DESC_TYPE,OFF,PIXIV,"http","dic.pixiv.net","a",ALL},					//PIXIVé´ìT
			{NICO_TYPE,OFF,NICONICO,ALL}												//∆∫∆∫ëÂïSâ»
	};
	public void start(String str){
//		source=str;
		if((str!=null)&&(str.length()>0)){
			if(list!=null){
				for(AsyncTask task:list){
					task.cancel(true);
				}
			}

			ArrayList<String> strList=null;
			
		if(Util.isConfigureON(MainService.thisService, "JISEI_REMOVE")){
			while(" ".equals(str.substring(0,1))){
				str=str.substring(1);
			}
			while(" ".equals(str.substring(str.length()-1,str.length()))){
				str=str.substring(0,str.length()-1);
			}
			str=str.replace("Åf", "'");
			if((!HTMLUty.chkZenkaku(str))&&(!str.contains(" "))){
				strList=this.getPastList(str);
				if(strList==null){
					strList=this.getFukusuuList(str);
				}
				if(strList==null){
					strList=this.getIngList(str);
				}
			}
		}
			if(strList==null){
				strList=new ArrayList<String>();
				strList.add(str);
			}
			CacheData cacheData=null;
			String key=null;
			boolean flag=false;
			try{
			for(String strS:strList){
				CacheData _cacheData=CacheUty.getValue(strS);
				if((_cacheData!=null)&&(_cacheData.getDescription()!=null)&&(_cacheData.getDescription().length()>0)&&(Util.isDictionaryEnable(ctr.service, _cacheData.getDic()))){
					if(cacheData==null){
						cacheData=_cacheData;
						key=strS;
					}else{
						String desc_1=cacheData.getDescription();
						String desc_2=_cacheData.getDescription();
						if((desc_1!=null)&&(desc_2!=null)){
							if(desc_2.length()>desc_1.length()){
								cacheData=_cacheData;
								key=strS;
							}
						}
					}
				}
			}
			}catch(Exception ex){
				Log.d("Copy2Dic", "ReloadDictionary");
			}
				if(cacheData!=null){
					setResult(key,cacheData.getDescription(),cacheData.getHatu(),cacheData.getUrl(),null);
					flag=true;
				}
			if(!flag){		
				
				list=new ArrayList<AsyncTask>();
				
				for(String strS:strList){
				for(int i=0;i<dicList.length;i++){
					if(DEJIZO.equals(dicList[i][0])){
						DejizoSearchTask task=new DejizoSearchTask();
						task.init(this);
						task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, dicList[i][2], dicList[i][3],dicList[i][4],strS);
						list.add(task);
					}else if(DESC_TYPE.equals(dicList[i][0])){
						WeblioSearchTask task=new WeblioSearchTask();
						task.init(this);
						task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, dicList[i][2],dicList[i][3],dicList[i][4], dicList[i][5],dicList[i][6],strS);
						list.add(task);
					}else if(DESC_TYPE2.equals(dicList[i][0])){
						WeblioSearchTask2 task=new WeblioSearchTask2();
						task.init(this);
						task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, dicList[i][2],dicList[i][3],dicList[i][4], dicList[i][5],dicList[i][6],strS);
						list.add(task);
					}else if(NICO_TYPE.equals(dicList[i][0])){
						NicoNicoSearchTask task=new NicoNicoSearchTask();
						task.init(this);
						task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,dicList[i][2],strS);
						list.add(task);
					}
				}
				JPKANASearchTask task=new JPKANASearchTask();
				task.init(this);
				task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"",strS);
				list.add(task);
				}
				
				
			}
		}
	}
	boolean finishFlag=false;
	public void finish(){
		if(list!=null)
		for(AsyncTask task:list){
			task.cancel(true);
		}
		finishFlag=true;
	}

	String jp_hatsu=null;
	String en_hatsu=null;
	String old=null;
	String oldUrl=null;
	String old_keywork=null;
	public void setJPHatsu(String jphatsu){
		jp_hatsu=jphatsu;
		ctr.setTextFromTask(old_keywork,old,en_hatsu,jp_hatsu,oldUrl);
	}
	
	public void setResult(String keyword,String str,String resultHatu,String url,AsyncTask task){
		if(!finishFlag){
			if(str!=null){
				if((old==null)||(old.length()<str.length())){
					old_keywork=keyword;
					old=str;
					if((en_hatsu==null)&&(resultHatu!=null)){
						en_hatsu=resultHatu;
					}
					if((url==null)||(url.length()==0)){
							Uri.Builder builder = new Uri.Builder();
							builder.scheme("http");
							builder.authority("www.weblio.jp");
							builder.path("/content/"+keyword);
							url=builder.build().toString();
					}
					oldUrl=url;
					ctr.setTextFromTask(old_keywork,old,en_hatsu,jp_hatsu,oldUrl);
				}
			}
		}

	}
	static final String[] childCare=
		{"a","b","c","d","e","f","g","h","i","j","k","l","m","n",
		"o","p","q","r","s","t","u","v","w","x","z"};
	static final String tailPast="ed";
	public ArrayList<String> getPastList(String str){
		ArrayList<String> result=null;
		str=str.toLowerCase();
		if(getTail(str,2).equals(tailPast)){
			result=new ArrayList<String>();
			result.add(str);
			String buf=null;
			if(str.length()>4){
				if(getTail(str,3).equals("ied")){
					if(checkStr(str,4)){
						buf=str.substring(0, str.length()-3)+"y";
						result.add(buf);
					}
				}
			}
			if((str.length()>5)&&(buf==null)){
				String three=getStr(str,3);
				String forth=getStr(str,4);
				if(checkStr(str,3)&&three.equals(forth)){
					buf=str.substring(0, str.length()-3);
					result.add(buf);
				}
			}
			if(buf==null){
				buf=str.substring(0, str.length()-2);
				result.add(buf);
				buf=str.substring(0, str.length()-1);
				result.add(buf);
			}
		}
		return result;
	}
	static final String tailFukusuu="s";
	public ArrayList<String> getFukusuuList(String str){
		ArrayList<String> result=null;
		str=str.toLowerCase();
		if(getTail(str,1).equals(tailFukusuu)){
			result=new ArrayList<String>();
			result.add(str);
			String buf=null;
			
			if(str.length()>2){
				if(getTail(str,2).equals("'s")){
						buf=str.substring(0, str.length()-2);
						result.add(buf);
				}
			}
			if((buf==null)&&(str.length()>4)){
				if(getTail(str,3).equals("ies")){
					if(checkStr(str,4)){
						buf=str.substring(0, str.length()-3)+"y";
						result.add(buf);
					}
				}
			}
			if((buf==null)&&(str.length()>4)){
				if(getTail(str,3).equals("ses")){
						buf=str.substring(0, str.length()-3)+"s";
						result.add(buf);
				}
			}
			if((buf==null)&&(str.length()>4)){
				if(getTail(str,3).equals("xes")){
						buf=str.substring(0, str.length()-3)+"x";
						result.add(buf);
				}
			}
			if((buf==null)&&(str.length()>5)){
				if(getTail(str,4).equals("shes")){
						buf=str.substring(0, str.length()-4)+"sh";
						result.add(buf);
				}
			}
			if((buf==null)&&(str.length()>5)){
				if(getTail(str,4).equals("ches")){
						buf=str.substring(0, str.length()-4)+"ch";
						result.add(buf);
				}
			}
			if((buf==null)&&(str.length()>4)){
				if(getTail(str,3).equals("ves")){
					buf=str.substring(0, str.length()-3)+"f"; 
					result.add(buf);
					buf=str.substring(0, str.length()-3)+"fe";
					result.add(buf);
					buf=str.substring(0, str.length()-1);
					result.add(buf);
				}
			}
			if(buf==null){
				buf=str.substring(0, str.length()-1);
				result.add(buf);
			}
		}
		return result;
	}
	
	static final String tailIng="ing";
	public ArrayList<String> getIngList(String str){
		ArrayList<String> result=null;
		str=str.toLowerCase();
		if(getTail(str,3).equals(tailIng)){
			result=new ArrayList<String>();
			result.add(str);
			String buf=null;
			if(str.length()>5){
				if(getTail(str,4).equals("ying")){
						buf=str.substring(0, str.length()-4)+"ie";
						result.add(buf);
				}
			}
			if((str.length()>6)&&(buf==null)){
				String three=getStr(str,4);
				String forth=getStr(str,5);
				if(checkStr(str,4)&&three.equals(forth)){
					buf=str.substring(0, str.length()-4);
					result.add(buf);
				}
			}
			if(buf==null){
				buf=str.substring(0, str.length()-3);
				result.add(buf);
				result.add(buf+"e");
			}
		}
		return result;
	}
	
	public boolean checkStr(String str,int num){
		boolean result=false;
		String forth=getStr(str,num);
		for(int i=0;i<childCare.length;i++){
			if(forth.equals(childCare[i])){
				result=true;
				break;
			}
		}
		return result;
	}
	public String getStr(String str,int num){
		return str.substring(str.length()-num,str.length()-num+1);
	}
	public String getTail(String str,int num){
		if(str.length()<num){
			num=str.length();
		}
		String result=str.substring(str.length()-num);
		return result;
	}


}
