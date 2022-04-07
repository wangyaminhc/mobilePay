package com.sssoft_lib.mobile.util;

import java.util.Comparator;

import com.sssoft_lib.mobile.object.FileInfo;

public class FileComparator implements Comparator<FileInfo> {
	public int compare(FileInfo file1, FileInfo file2) {
		if(file1.getLastModified() < file2.getLastModified())
		{
			return -1;
		}else
		{
			return 1;
		}
	}
}
