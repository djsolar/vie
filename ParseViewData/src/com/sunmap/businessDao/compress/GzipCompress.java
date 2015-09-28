package com.sunmap.businessDao.compress;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import com.sunmap.util.Constant;

public class GzipCompress {
	
	public void compress(String cityname) throws IOException {
		File nocompressfile = new File(Constant.outPath + File.separator + "nocompress" + cityname);
		FileInputStream fileInputStream = new FileInputStream(nocompressfile);
		byte[] data = new byte[(int) nocompressfile.length()];
		fileInputStream.read(data);
		fileInputStream.close();
		FileOutputStream fileOutputStream = new FileOutputStream(Constant.outPath + File.separator + cityname);
		GZIPOutputStream gzipOutputStream = new GZIPOutputStream(fileOutputStream);
		gzipOutputStream.write(data);
		gzipOutputStream.finish();
		gzipOutputStream.flush();
		gzipOutputStream.close();
		fileOutputStream.flush();
		fileOutputStream.close();
	}

}
