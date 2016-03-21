package com.mypaceengine.clip2dic.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.support.v4.util.LruCache;

import com.jakewharton.disklrucache.DiskLruCache;
import com.jakewharton.disklrucache.DiskLruCache.Editor;
import com.jakewharton.disklrucache.DiskLruCache.Snapshot;
import com.mypaceengine.clip2dic.MainService;
import com.mypaceengine.clip2dic.Util;

public class CacheUty {

	    private static final int MEM_CACHE_SIZE = 10 * 1024 * 1024; // 1MB

	    private static LruCache<String, CacheData> sLruCache;
	    private static DiskLruCache cache;

	    private static ArrayList<String> history;

	    static {
	        sLruCache = new LruCache<String, CacheData>(MEM_CACHE_SIZE) {
	            @Override
	            protected int sizeOf(String key, CacheData str) {
	                return str.length();
	            }
	        };
	        try{
	        File cacheDir= new File(FileUty.getExternalStorageDirectory(), "Clip2DicCache");
	        cacheDir.mkdir();
	        cache = DiskLruCache.open(cacheDir, 0, 4, 100*1024*1024);
	        }catch(Exception ex){}
	        try{
	        	history=(ArrayList<String>)FileUty.loadData(FileUty.createSettingPath()+"HistoryList");
	        }catch(Exception ex){}
	        if(history==null){
	        	history=new ArrayList<String>();
	        }
	    }

	    private CacheUty() {
	    }

	    
	    
	    public static List<Map<String,String>> getList(){
	    	List<Map<String,String>> result=new ArrayList<Map<String,String>>();
	    	try{
	    	synchronized(history){
	    	
	    	for(int i=0;i<history.size();i++){
	    		String key=history.get(i);
	    		CacheData data=getValueExec(key);
	    		String hatu=data.getHatu();
	    		if(hatu!=null){
	    		key=key+"  ‰¹:"+hatu;
	    		}
	    		if(data!=null){
	    			String description=Util.descriptionCutter(data.getDescription());
	    			HashMap<String,String> map=new HashMap<String,String>();
	    			map.put("title", key);
	    			map.put("comment", description);
	    			map.put("url", data.getUrl());
	    			map.put("dic", data.getDic());
	    			result.add(map);
	    		}
	    	}
	    	}
	    	}catch(Exception ex){}
	    	return result;
	    }
	    public static void delHistory(Map<String,String> data ){
	    	String key=data.get("title");
	    	if(history!=null){
	    		history.remove(key);
	    		try{
		    		FileUty.storeData(FileUty.createSettingPath()+"HistoryList", history);
		    	}catch(Exception ex){
		    		ex.printStackTrace();
		    	}
	    	}
	    }

	    public static void setValue(String key, CacheData str) {
	    	if(key==null){
	    		return;
	    	}
	    	addHistoryKey(key);
	    	setValueExec(key,str);
	    }

	    public static void setValueExec(String key, CacheData str) {
	    	if(str==null){
	    		return;
	    	}
	    	int len=0;
			if(str!=null){
				String re=str.getDescription().replaceAll(" ","");
				 re=re.replaceAll("\r","");
				 re=re.replaceAll("\n","");
				 len=re.length();
			}
	    	if((str.getDescription()==null)||len==0){
	    		return;
	    	}
	    	String upperKey=key.toUpperCase();
	    	try{
	    	CacheData old=getValueExec(upperKey);
	    	if((old==null)||(old.getDescription().length()<str.getDescription().length())||(!Util.isDictionaryEnable(MainService.thisService,old.getDic()))){
	    		sLruCache.put(upperKey, str);
	    		try{
	    	    	Editor edit=cache.edit(hash(upperKey));
	    	    	edit.set(0, str.getDescription());
	    	    	edit.set(1, str.getUrl());
	    	    	edit.set(2, str.getDic());
	    	    	edit.set(3, str.getHatu());
	    	    	edit.commit();
	    	    }catch(Exception ex){
	    	    	ex.printStackTrace();
	    	    }
	    	}
	    	}catch(Exception ex){
	    		sLruCache.put(upperKey, str);
	    		try{
	    	    	Editor edit=cache.edit(hash(upperKey));
	    	    	edit.set(0, str.getDescription());
	    	    	edit.set(1, str.getUrl());
	    	    	edit.set(2, str.getDic());
	    	    	edit.set(3, str.getHatu());
	    	    	edit.commit();
	    	    }catch(Exception e){
	    	    	ex.printStackTrace();
	    	    }
	    	}
	    	

	    }

	    public static CacheData getValue(String key) {
	    	addHistoryKey(key);
	    	return getValueExec(key);
	    }
	    public static CacheData getValueExec(String key) {
	    	String upperKey=key.toUpperCase();
	    	CacheData result=null;
	    	try{
	    			result=sLruCache.get(upperKey);
	    	if(result==null){
	    		try{
	    			Snapshot edit=cache.get(hash(upperKey));
	    			if(edit!=null){
	    				String description=edit.getString(0);
	    				String url=edit.getString(1);
	    				String dic=edit.getString(2);
	    				String hatu=null;
	    				try{
	    					hatu=edit.getString(3); 
	    				}catch(Exception ex){}
	    				if((description!=null)||(url!=null)){
	    					result=new CacheData();
	    					result.setDescription(description);
	    					result.setUrl(url);
	    					result.setDic(dic);
	    					result.setHatu(hatu);
	    				}
	    			}
	    	}catch(Exception ex){
	    		ex.printStackTrace();
	    	}
	    	}
	    	}catch(Exception ex){}
	        return result;
	    }

	    static private String hash(String source){
	    	String result=null;
	        MessageDigest digest=null;
	        try {
	            digest = MessageDigest.getInstance("SHA-256");
	            digest.reset();
	            byte[] bt=digest.digest(source.getBytes("UTF-8"));
	            result=String.format("%0" + (bt.length*2) + "x", new BigInteger(1, bt));
	        } catch (NoSuchAlgorithmException e1) {
	            e1.printStackTrace();
	        } catch (UnsupportedEncodingException e) {
	            e.printStackTrace();
	        }
	           return result;
	    }
	    static private void addHistoryKey(String key){
	    	synchronized(history){
	    	if(key!=null){
	    		if(history==null){
	    			history=new ArrayList<String>();
	    		}
	    		String upperKey=key.toUpperCase();
		    	try{
		    		for(int i=0;i<history.size();i++){
		    			String each=history.get(i);
		    			if(each.toUpperCase().equals(upperKey)){
		    				history.remove(i);
		    			}
		    		}
		    	}catch(Exception ex){
		    		ex.printStackTrace();
		    	}
		    	try{
		    		history.add(0,key);
			    	if(history.size()>1000){
			    		history.remove(history.size()-1);
			    	}
		    	}catch(Exception ex){
		    		ex.printStackTrace();
		    	}
		    	try{
		    		FileUty.storeData(FileUty.createSettingPath()+"HistoryList", history);
		    	}catch(Exception ex){
		    		ex.printStackTrace();
		    	}
		    }
	    	}
	    }
}
