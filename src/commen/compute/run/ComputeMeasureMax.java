package commen.compute.run;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class ComputeMeasureMax {
	//计算各个案例的指标的Max值
	@Test
	public void test() {
		String dic1="NSGA_0308093039_run1_100_200";//NSGA 100-200
		String dic2_1="NSGAV_H0308175133_run1_100";//GAVN 100
		String dic2_2="NSGAV_H0308171112_run1_200";//GAVN 200
		Map<String,Double> HV=new HashMap<>();//存储HV-MAX
		Map<String,Double> MID=new HashMap<>();//存储MID-MAX
		
		readDataFile("data\\"+dic1,HV,MID);
		readDataFile("data\\"+dic2_1,HV,MID);
		readDataFile("data\\"+dic2_2,HV,MID);
		for(Map.Entry<String, Double> entry:HV.entrySet()) {
			String name=entry.getKey();
			double hv=entry.getValue();
			double mid=MID.get(name);
			System.out.println(name+" "+hv*1.1+" "+mid*1.1);
		}
		
	}

	private void readDataFile(String dic, Map<String, Double> hV, Map<String, Double> mID) {
	   File fileDic=new File(dic);
	   String dicList[]=fileDic.list();//子文件夹 每个文件夹代表一次试验
	   for(int i=0;i<dicList.length;i++) {
		   String childDic=dic+"/"+dicList[i];
		   File childFile=new File(childDic);
		   String childList[]=childFile.list();
		   for(int j=0;j<childList.length;j++) {
			   String filePath=childDic+"/"+childList[j];//具体的某个案例文件结果
	           String fileName=childList[j].substring(childList[j].indexOf('_')+1, childList[j].length());		   
			   BufferedReader read=null;
			   try {
				read=new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
			    String line=null;
				while((line=read.readLine())!=null) {
					if(line.startsWith("超体积")) {
						String strs[]=line.split(":");
						double hv=Double.valueOf(strs[1]);
						double max=hV.get(fileName)==null?0:hV.get(fileName);
						if(max<hv) {
							hV.put(fileName, hv);
						}
					}
					if(line.startsWith("反转MID")) {
						String strs[]=line.split(":");
						double mid=Double.valueOf(strs[1]);
						double max=mID.get(fileName)==null?0:mID.get(fileName);
						if(max<mid) {
							mID.put(fileName, mid);
						}
					}
				}
			   }catch(Exception e) {
				   e.printStackTrace();
			   }
		   }
	   }
	
	}

}
