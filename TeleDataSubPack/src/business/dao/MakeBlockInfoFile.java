package business.dao;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import bo.ViewUnitNo;
/**
 * �����м�txt
 * @author wenxc
 *
 */
public class MakeBlockInfoFile {
	/**
	 * �ָ���
	 */
	private String sign = "\t";

	/**
	 * �����ļ�����
	 * @param viewMap Map<Integer, List<ViewUnitNo>>
	 * @throws Exception 1
	 */
	public void makeViewInfoFile(Map<Integer, List<ViewUnitNo>> viewMap)
			throws Exception {

		File viewFile = new File("ViewBlockInfo.txt");

		if (viewFile.exists()) {

			viewFile.delete();

		}

		viewFile.createNewFile();

		PrintWriter fileWriter = new PrintWriter(viewFile);

		fileWriter.println("//level	unitx	unity	blockx	blocky	province_code	proname");

		for (Integer ilevel : viewMap.keySet()) {

			List<ViewUnitNo> viewSet = viewMap.get(ilevel);
			
			CollectBlockInfo.sortUnitNo(viewSet);

			ViewUnitNo preViewUnitNo = null;
			
			for (ViewUnitNo viewUnitNo : viewSet) {
				
				if(preViewUnitNo != null){
					if(preViewUnitNo.getBlockNo().getiX() == viewUnitNo.getBlockNo().getiX() 
							&& preViewUnitNo.getBlockNo().getiY() == viewUnitNo.getBlockNo().getiY()
							&& preViewUnitNo.getStrName().equals(viewUnitNo.getStrName())){
						continue;
					}
				}

				StringBuffer strline = new StringBuffer();

				strline.append(viewUnitNo.getIlevel());
				strline.append(this.sign);
				strline.append(viewUnitNo.getiX());
				strline.append(this.sign);
				strline.append(viewUnitNo.getiY());
				strline.append(this.sign);
				strline.append(viewUnitNo.getBlockNo().getiX());
				strline.append(this.sign);
				strline.append(viewUnitNo.getBlockNo().getiY());
				strline.append(this.sign);
				strline.append(viewUnitNo.getAdmincode());
				strline.append(this.sign);
				strline.append(viewUnitNo.getStrName());
				strline.append(this.sign);
				fileWriter.println(strline.toString());
				
				preViewUnitNo = viewUnitNo;
			}

		}
		
		fileWriter.close();

	}

}
