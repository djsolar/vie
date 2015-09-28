package main;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.DataFormatException;

import com.sunmap.businessDao.MakePartData;

public class StartDo {
	
	public static void main(String[] args) throws IOException, SQLException, InterruptedException, DataFormatException {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println("程序开始，时间：" + df.format(new Date()));
		MakePartData.makeData();
		System.out.println("程序运行结束，时间：" + df.format(new Date()));
	}

}
