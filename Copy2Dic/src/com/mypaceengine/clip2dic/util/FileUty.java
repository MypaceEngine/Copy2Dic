package com.mypaceengine.clip2dic.util;
/**
 * File Utility
 */
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import android.os.Environment;

public class FileUty {
	private FileUty(){

	}

	public static String createSettingPath(){
		return getExternalStorageDirectory() +File.separator+ ApplicationName+SettingFolderPath;
	}


	public static String getExternalStorageDirectory(){
		String SdPath=System.getenv("EXTERNAL_ALT_STORAGE"); 
		if(SdPath==null){
			String allPath=System.getenv("EXTERNAL_STORAGE_ALL");
			if(allPath!=null){
				String[] each=allPath.split(":");
				if((each!=null)&&(each.length>0)){
					SdPath=each[0];
				}
			}
		}
		if(SdPath==null)
			SdPath=Environment.getExternalStoragePublicDirectory("").getAbsolutePath();
		if(SdPath==null)
			SdPath=Environment.getDataDirectory().getAbsolutePath();
		if(SdPath==null)
			SdPath=	Environment.getExternalStorageDirectory().getPath();


		return SdPath;
	}
	public static boolean isMountedSD(){
		boolean result=true;
		String status   = Environment.getExternalStorageState();
		if (!status.equals(Environment.MEDIA_MOUNTED)) {
			result=false;
		}
		return result;
	}

	public static final String SettingFolderPath="setting"+File.separator;
	public static final String ApplicationName="Clip2DicCache"+File.separator;
	
	public static Serializable storeData(String filePath,Serializable data){
		FileOutputStream outFile =null;
		ObjectOutputStream outObject=null;
		if(isMountedSD()){
		try{
	        File file = new File(filePath);
	        file.getParentFile().mkdirs();
			outFile = new FileOutputStream(file);
			outObject = new ObjectOutputStream(outFile);
			outObject.writeObject(data);

		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(outObject!=null){
				try{
					outObject.close();  
				} catch (IOException e) {}
			}
			if(outFile!=null){
				try{
					outFile.close();  
				} catch (IOException e) {}
			}
		}
		}
		return data;
	}
	public static Serializable loadData(String filePath){
		Serializable data=null;
		FileInputStream inFile =null;
		ObjectInputStream inObject=null;
		if(isMountedSD()){
		try{
			if((new File(filePath)).exists()){
			inFile = new FileInputStream(filePath);
			inObject = new ObjectInputStream(inFile);
			data=(Serializable)inObject.readObject();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}catch(Exception e){
		e.printStackTrace();
		}finally{
			if(inObject!=null){
				try{
					inObject.close();  
				} catch (IOException e) {}
			}
			if(inFile!=null){
				try{
					inFile.close();  
				} catch (IOException e) {}
			}
		}
		}
		return data;
	}
	public static void storeByte(String filePath,byte[] data){
		FileOutputStream outFile =null;
		BufferedOutputStream bufferedOutputStream = null;
		if(isMountedSD()){
		try{
	        File file = new File(filePath);
	        file.getParentFile().mkdirs();
	        if((file.isFile())&&(file.exists())){
	        	file.delete();
	        }
			outFile = new FileOutputStream(file);
			bufferedOutputStream=new BufferedOutputStream(outFile);
			bufferedOutputStream.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(bufferedOutputStream!=null){
				try{
					bufferedOutputStream.close();  
				} catch (IOException e) {}
			}
			if(outFile!=null){
				try{
					outFile.close();  
				} catch (IOException e) {}
			}
		}
		}
	}
	public static byte[] loadByte(String filePath){
		byte[] data=null;
		FileInputStream inFile =null;
		BufferedInputStream bufferedInputStream = null;
		ByteArrayOutputStream outStream=null;
		if(isMountedSD()){
		try{
			if((new File(filePath)).exists()){
			inFile = new FileInputStream(filePath);
			bufferedInputStream=new BufferedInputStream(inFile);
			outStream=new ByteArrayOutputStream();

			byte[] buffer = new byte[1024];
			int length;
			while((length = bufferedInputStream.read(buffer)) > -1)
			{
				outStream.write(buffer,0,length);
			}
			data=outStream.toByteArray();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}catch(Exception e){
		e.printStackTrace();
		}finally{
			if(outStream!=null){
				try{
					outStream.close();  
				} catch (IOException e) {}
			}
			if(bufferedInputStream!=null){
				try{
					bufferedInputStream.close();  
				} catch (IOException e) {}
			}
			if(inFile!=null){
				try{
					inFile.close();  
				} catch (IOException e) {}
			}
		}
		}
		return data;
	}
	public static void remove(String filepath){
		File file = new File(filepath);
		removeExec(file);
	}
	public static void removeExec(File file){
		if(isMountedSD()){
		try{

			if( file.exists()==false ){
				return ;
			}

			if(file.isFile()){
				file.delete();
			}

			if(file.isDirectory()){
				File[] files=file.listFiles();
				for(int i=0; i<files.length; i++){
					removeExec( files[i] );
				}
				file.delete();
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		}
	}
}
