package com.sunmap.shpdata.tools.filter;

import java.io.File;
import java.io.FilenameFilter;

import com.sun.swing.internal.plaf.basic.resources.basic;
import com.sunmap.shpdata.tools.conf.ShpConf;
import com.sunmap.shpdata.tools.util.JdbcUtil;

public class MyFileNameFilter implements FilenameFilter {

	private ShpConf conf;
	
	
	public MyFileNameFilter(ShpConf conf) {
		this.conf = conf;
	}


	@Override
	public boolean accept(File file, String name) {
		boolean flag = false;
		String[] arr = conf.getFileType().split(";");
		for (int i = 0; i < arr.length; i++) {
			if (flag = name.endsWith(arr[i])) {
				break;
			}
		}
		return flag;
	}

}
