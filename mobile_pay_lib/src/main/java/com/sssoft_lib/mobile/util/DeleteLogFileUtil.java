package com.sssoft_lib.mobile.util;

import android.os.Environment;

import com.sssoft_lib.mobile.object.FileInfo;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;


public class DeleteLogFileUtil {
	public static FileFilter fileFilter = new FileFilter() {
		public boolean accept(File file) {
			String tmp = file.getName().toLowerCase();
			if (tmp.endsWith(".log") && tmp.startsWith(Constant.FileName)) {
				return true;
			}
			return false;
		}
	};
	public static void delLogFile(){
		ArrayList<FileInfo> fileList = new ArrayList<FileInfo>();
		String sdPath = Environment.getExternalStorageDirectory().getPath(); 
		sdPath = sdPath + "/sssoft/log/";
	    File dirFile = new File(sdPath);    
	    if(dirFile.exists()){
	        File[] files = dirFile.listFiles(fileFilter);
	        if(files!=null){
	        	if(files.length>10){
	        		for (int i = 0; i < files.length; i++) {
		    			File file = files[i];
		    			FileInfo fileInfo = new FileInfo();
		    			fileInfo.setName(file.getName());
		    			fileInfo.setPath(file.getPath());
		    			fileInfo.setLastModified(file.lastModified());	
		    			fileList.add(fileInfo);
		    		}
		    		Collections.sort(fileList, new FileComparator()); 
		    		for (int i = 0; i < files.length-10; i++) {
		    			 FileInfo fileInfo = (FileInfo)fileList.get(i);
		    			 File filedel = new File(sdPath+fileInfo.getName());
		    			 if (filedel.isFile() && filedel.exists()) {
		    				 filedel.delete();
		    			 }
					}
	        	}
	        }
	    } 
	}
}
