package commen.compute.run;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

public class ComputeMetric {
   /*
    * 计算帕累托前沿的约束覆盖率
    * @param dic1 NSGA的20次试验的结果文件夹路径
    * @param dic2_1 GAVN的100案例的20次试验结果文件夹路径
    * @param dic2_2 GAVN的200案例的20次试验结果文件夹路径
    * @param output 输出结果路径
    */
	
	@Test
	public void computeMetric() {
		//1先读文件   20次试验 计算平均值  每次试验数据去重
		//2计算指标 平均超体积HV  PFS帕累托解的数目   直接读即可  平均反转MID 根据去重后计算即可  平均SNS 去重后直接计算    C(S1,S2) S2中的解被S1支配占的比率
		//3输出 每个算法 每个案例输出一个文件 包含上面的指标  C指标单独输出一个文件
		String d="data1.0/";
		String name1="G_0508201805_run1_200";
		String name2="RL_0508215701_run1_200";
		String dic1=d+name1;//NSGA 100-200
		String dic2_1=d+name2;//GAVN 100
		String file=name1.substring(name1.lastIndexOf("_"),name1.length());
		String vs1=name1.substring(0,name1.indexOf("_"));
		String vs2=name2.substring(0,name2.indexOf("_"));
		String vs=vs1+"_VS_"+vs2;
		//String dic2_2=d+"NSGAV_H0308171112_run1_200";//GAVN 200
		String output1=d+"coverage/"+vs+file;
		String output2=d+"finalPareto/"+vs+file;
		String output3=d+"paretoRatio/"+vs+file;
		
		List<Map<String,List<List<Double>>>> count1=new ArrayList<>();//20次试验 每次试验包含36个案例 每个案例包含多个帕累托解
		List<Map<String,List<List<Double>>>> count2=new ArrayList<>();//100 200
		/*List<Map<String,List<List<Double>>>> count2_1=new ArrayList<>();//100
		List<Map<String,List<List<Double>>>> count2_2=new ArrayList<>();//200
		*/
		count1=readDataFile(dic1);//100 200 
		count2=readDataFile(dic2_1);
		/*count2_2=readDataFile(dic2_2);
		count2=combine(count2_1,count2_2);//合并
        */		
		//进行计算
		Coverage c=new Coverage();
		FinalPareto fp=new FinalPareto();
		ParetoRatio pr=new ParetoRatio();
		
		c.compute(count1,count2,output1);//计算C C(GAV,NSGA) C(NSGA,GAV)
		fp.compute(count1,count2,output2);//输出20次试验汇总的pareto
		pr.compute(count1,count2,output3);//计算帕累托占比
		
		
	}
	@Test
	public void computeHV_MID() {
		String dic1="data1.0/"+"G_0508201805_run1_200";//NSGA 100-200
		String dic2_1="data1.0/"+"NSGAV_H0409144108_run1";//GAVN 100
		String dic2_2="data1.0/"+"NSGAV_H0308171112_run1_200";//GAVN 200
		String hv_path="data1.0/"+"average/HV";//子文件夹
		String mid_path="data1.0/"+"average/MID";//子文件夹
		HV_MID hv_mid=new HV_MID();
		hv_mid.compute(dic1,hv_path, "超体积");
		hv_mid.compute(dic1,mid_path, "反转MID");
		/*hv_mid.compute(dic2_1,hv_path, "超体积");
		hv_mid.compute(dic2_1,mid_path, "反转MID");*/
		/*hv_mid.compute(dic2_2,hv_path, "超体积");
		hv_mid.compute(dic2_2,mid_path, "反转MID");*/
	}
	/*
	 * 合并100 200案列结果 
	 * @param count2_1 100案例  20次试验   18个案例
	 * @param count2_2 200案例  20次试验 18个案例
	 */
	private List<Map<String, List<List<Double>>>> combine(List<Map<String, List<List<Double>>>> count2_1,
		List<Map<String, List<List<Double>>>> count2_2) {
	    for(int i=0;i<count2_1.size();i++) {
	    	Map<String, List<List<Double>>> map2=count2_2.get(i);
	    	Iterator iter=map2.entrySet().iterator();
	    	while(iter.hasNext()) {
	    		Map.Entry<String, List<List<Double>>> entry=(Entry<String, List<List<Double>>>) iter.next();
	    		count2_1.get(i).put(entry.getKey(), entry.getValue());
	    	}
	    }
	   return count2_1;
}
	/*
	 * 读取试验结果 
	 * @param dic 目录
	 * @return 读取List 包含20次试验
	 */
	public List<Map<String,List<List<Double>>>> readDataFile(String  dic){//dic-->data/NSGA_0308093039_run1_100_200
	   List<Map<String,List<List<Double>>>> twentyResults=new ArrayList<>();
	   File fileDic=new File(dic);
	   String dicList[]=fileDic.list();//子文件夹 每个文件夹代表一次试验
	   for(int i=0;i<dicList.length;i++) {
		   String childDic=dic+"/"+dicList[i];//childDic-->data/NSGA_0308093039_run1_100_200/nsga_0
		   Map<String,List<List<Double>>> oneExperResults=new HashMap<>();
		   File childFile=new File(childDic);
		   String childList[]=childFile.list();
		   for(int k=0;k<childList.length;k++) {
			   String filePath=childDic+"/"+childList[k];//具体的某个案例文件结果 filePath-->data/NSGA_0308093039_run1_100_200/nsga_0/NSGA_100_10_26_15.def.txt
			   String fileName=childList[k].substring(childList[k].indexOf('_')+1, childList[k].length());
			   List<List<Double>> datas=new ArrayList<>();
			   BufferedReader read=null;
			   try {
				read=new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
			    String line=null;
				while((line=read.readLine())!=null) {
					if(line.startsWith("项目工期")) {
						String[]ss=line.split(":");
						List<Double> data=new ArrayList<>();
						data.add(Double.parseDouble(ss[1]));
						data.add(Double.parseDouble(ss[3]));
						datas.add(data);
					}
				}
				oneExperResults.put(fileName, datas);
				//map.put(list[i].substring(list[i].indexOf('_')+1, list[i].length()-1), datas);
			   }catch(Exception e) {
				   e.printStackTrace();
			   }
		   }
		   twentyResults.add(oneExperResults);
	   }
	   return twentyResults;
		
	}
	
}
