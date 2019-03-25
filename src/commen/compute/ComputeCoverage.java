package commen.compute;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.junit.Test;

public class ComputeCoverage {
   /*
    * 计算帕累托前沿的约束覆盖率
    * @param dic1 NSGA的20次试验的结果文件夹路径
    * @param dic2_1 GAVN的100案例的20次试验结果文件夹路径
    * @param dic2_2 GAVN的200案例的20次试验结果文件夹路径
    * @param output 输出结果路径
    */
	
	@Test
	public void test1() {
		//1先读文件   20次试验 计算平均值  每次试验数据去重
		//2计算指标 平均超体积HV  PFS帕累托解的数目   直接读即可  平均反转MID 根据去重后计算即可  平均SNS 去重后直接计算    C(S1,S2) S2中的解被S1支配占的比率
		//3输出 每个算法 每个案例输出一个文件 包含上面的指标  C指标单独输出一个文件
		String dic1="data/"+"NSGA_0308093039_run1_100_200";//NSGA 100-200
		String dic2_1="data/"+"NSGAV_H0308175133_run1_100";//GAVN 100
		String dic2_2="data/"+"NSGAV_H0308171112_run1_200";//GAVN 200
		String output="data/"+"coverage"+"NSGA-NSGAV";
		List<Map<String,List<List<Double>>>> count1=new ArrayList<>();//20次试验 每次试验包含36个案例 每个案例包含多个帕累托解
		List<Map<String,List<List<Double>>>> count2=new ArrayList<>();//100 200
		List<Map<String,List<List<Double>>>> count2_1=new ArrayList<>();//100
		List<Map<String,List<List<Double>>>> count2_2=new ArrayList<>();//200
		count1=readDataFile(dic1);//100 200 
		count2_1=readDataFile(dic2_1);
		count2_2=readDataFile(dic2_2);
		count2=combine(count2_1,count2_2);//合并
		//进行计算
		computeC(count1,count2,output);
		
	}
	/*
	 * 计算C指标
	 * @param count1 算法1的结果 List-20次试验  每一次是一个Map Map-key文件名 Map-value目标值List
	 * @param count2
	 * @param output 输出文件路径
	 */
	private void computeC(List<Map<String, List<List<Double>>>> count1, List<Map<String, List<List<Double>>>> count2, String output) {
		Map<String,Double> domiFraction1Sum=new TreeMap<>();
		Map<String,Double> domiFraction2Sum=new TreeMap<>();
		for(int i=0;i<count1.size();i++) {
			Map<String,List<Integer>> dominate1=new TreeMap<>();
			Map<String,List<Integer>> dominate2=new TreeMap<>();
			dominate1=computeDominate(count1.get(i),count2.get(i));//1 被 2 支配的比例 C(B,A)表示A被B支配的比例   C(GAVN,NSGA)
			dominate2=computeDominate(count2.get(i),count1.get(i));//2 被 1 支配的比例  C(A,B)表示B被A支配的比例 C(NSGA,GAVN)
			Map<String,Double> domiFraction1=new TreeMap<>();
			Map<String,Double> domiFraction2=new TreeMap<>();
			domiFraction1=domiFraction(dominate1);
			domiFraction2=domiFraction(dominate2);
			addDomiFraction(domiFraction1Sum,domiFraction1);
			addDomiFraction(domiFraction2Sum,domiFraction2);
		}
		//平均
		averageDomiFraction(domiFraction1Sum,count1.size());
		averageDomiFraction(domiFraction2Sum,count1.size());
		List<String> names=readNames();
		output(output,domiFraction1Sum,domiFraction2Sum,names);
}
	/*
	 * 按照names顺序输出结果
	 * @param domiFraction1Sum  C(B,A)表示A被B支配的比例   C(GAVN,NSGA)
	 * @param domiFraction2Sum  C(A,B)表示B被A支配的比例 C(NSGA,GAVN)
	 * @param names 指定的文件名顺序List
	 */
	private void output(String output, Map<String, Double> domiFraction1Sum, Map<String, Double> domiFraction2Sum, List<String> names) {
		File out_f=new File(output);
	  	if(out_f.exists()) out_f.delete();
	  	out_f.mkdirs();
	  	String coverge=output+"/"+"coverage.txt";
	  	File cov_f=new File(coverge);
	  	if(cov_f.exists())cov_f.delete();
	  	 BufferedWriter write=null;
	  	try {
	  		cov_f.createNewFile();
	        write=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(cov_f)));
			for(int i=0;i<names.size();i++) {
				String name =names.get(i);
				write.write(name+" "+domiFraction1Sum.get(name)+" "+domiFraction2Sum.get(name));
				write.newLine();
			}
			write.flush();
			write.close();
	  	}catch(Exception e) {
	  		e.printStackTrace();
	  	}
	}
	/*
	 * 除以试验次数平均计算的指标值
	 * @param domiFractionSum Map-key 文件名   Map-value 多次次试验的累加支配比例值 
	 * @param time 试验次数
	 */
	private void averageDomiFraction(Map<String, Double> domiFractionSum, int time) {
		Iterator iter=domiFractionSum.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry<String, Double> entry=(Entry<String, Double>) iter.next();
			String key=entry.getKey();
			domiFractionSum.put(key, entry.getValue()/time);
		}
	}
	/*
	 * @param domiFractionSum 累加的计算试验的支配比例值
	 * @param domiFraction 一次试验的累加支配比例值
	 * 将这次的试验结果加到累加的结果上
	 */
	private void addDomiFraction(Map<String, Double> domiFractionSum, Map<String, Double> domiFraction) {
		Iterator iter=domiFraction.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry<String, Double> entry=(Entry<String, Double>) iter.next();
			String key=entry.getKey();
			double al=domiFractionSum.get(key)==null?0:domiFractionSum.get(key);
			domiFractionSum.put(key,al+entry.getValue());
		}
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
	public static void main(String[]args) {
		String cdic="30s/";
		String name1="NSGA_bestNSGAV_H0930153010090852";
		String name2="NSGA_bestNSGA_1008222210090851";
		String input1="data/"+cdic+name1;
		String input2="data/"+cdic+name2;
		String head=buildFileName();
		String output="data/"+cdic+"coverage"+head;
		Map<String,List<List<Double>>> result1=new TreeMap<>();
		Map<String,List<List<Double>>> result2=new TreeMap<>();
		result1=readData(input1);
		result2=readData(input2);
		//比较数据
		Map<String,List<Integer>> dominate1=new TreeMap<>();
		Map<String,List<Integer>> dominate2=new TreeMap<>();
		dominate1=computeDominate(result1,result2);
		dominate2=computeDominate(result2,result1);
		Map<String,Double> domiFraction1=new TreeMap<>();
		Map<String,Double> domiFraction2=new TreeMap<>();
		domiFraction1=domiFraction(dominate1);
		domiFraction2=domiFraction(dominate2);
		List<String> names=readNames();
		//排序
		List<Map.Entry<String, Double>> domi_list1=new ArrayList<>();
	  	   for(Entry<String, Double> entry:domiFraction1.entrySet()) {
	  		   domi_list1.add(entry);
	  	   }
	  	SortByNumber(domi_list1);
	  	List<Map.Entry<String, Double>> domi_list2=new ArrayList<>();
	  	   for(Entry<String, Double> entry:domiFraction2.entrySet()) {
	  		   domi_list2.add(entry);
	  	   }
	  	SortByNumber(domi_list2);
	  	//输出
	  	output(output,domi_list1,domi_list2);
	}
	/*
	 * 读取name 链表用于顺序排列输出
	 */
	private static List<String> readNames() {
		List<String> namesList=new ArrayList<>();
		String names="names";
		File file=new File(names);
		BufferedReader read=null;
		try {
			read=new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		    String line=null;
		    while((line=read.readLine())!=null) {
		    	namesList.add(line.trim());
		    }
		    read.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return namesList;
	}
	/*
	 * 输出数据
	 */
	private static void output(String output, List<Entry<String, Double>> domi_list1, List<Entry<String, Double>> domi_list2) {
		File out_f=new File(output);
	  	if(out_f.exists()) out_f.delete();
	  	out_f.mkdirs();
	  	String coverge=output+"/"+"coverage.txt";
	  	File cov_f=new File(coverge);
	  	if(cov_f.exists())cov_f.delete();
	  	try {
	  		cov_f.createNewFile();
	        BufferedWriter write=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(cov_f)));
	        for(int i=0;i<domi_list1.size();i++) {
	        	write.write(domi_list1.get(i).getKey()+" "+domi_list1.get(i).getValue()+
	        			" "+domi_list2.get(i).getKey()+" "+domi_list2.get(i).getValue());
	        	write.newLine();
	        }
	        write.flush();  
	        write.close();
	  	}catch(Exception e) {
	  		e.printStackTrace();
	  	}
	
}
	//按照文件名编号排序以便输出
	private static void SortByNumber(List<Entry<String, Double>> domi_list) {
		Collections.sort(domi_list, new Comparator<Map.Entry<String, Double>>() {
	  		@Override
	  		public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
	  			String str1[] =o1.getKey().split("_|\\.");
	  			String str2[] =o2.getKey().split("_|\\.");
	  			if(Double.parseDouble(str1[1])>Double.parseDouble(str2[1])) {
	  				return 1;
	  			}else if(Double.parseDouble(str1[1])<Double.parseDouble(str2[1])) {
	  				return -1;
	  			}
	  			else if(Double.parseDouble(str1[2])>Double.parseDouble(str2[2])) {
	  				return 1;
	  			}else if(Double.parseDouble(str1[2])<Double.parseDouble(str2[2])) {
	  				return -1;
	  			}else if(Double.parseDouble(str1[3])>Double.parseDouble(str2[3])) {
	  				return 1;
	  			}else if(Double.parseDouble(str1[3])<Double.parseDouble(str2[3])) {
	  				return -1;
	  			}else if(Double.parseDouble(str1[4])>Double.parseDouble(str2[4])) {
	  				return 1;
	  			}else if(Double.parseDouble(str1[4])<Double.parseDouble(str2[4])) {
	  				return -1;
	  			}else {
	  				return -1;
	  			}
	  			
	  		} 
	  	   });
      }
	/*
	 * @param dominate1 Map-key文件名 Map-value代表该目标组合是否被支配  1-被支配   0-未被支配
	 * 计算覆盖百分比
	 * @return domiFraction1 支配比例 Map-key文件名 Map-value支配比例
	 */
	private static Map<String, Double> domiFraction(Map<String, List<Integer>> dominate1) {
		Map<String,Double> domiFraction1=new TreeMap<>();
		for(Map.Entry<String,List<Integer>> entry:dominate1.entrySet()) {
			List<Integer> list1=entry.getValue();
			int domi=0;
			for(int i=0;i<list1.size();i++) {
				if(list1.get(i)==1) {
					domi++;
				}
			}
			domiFraction1.put(entry.getKey(),domi*1.0/list1.size());
		}
	   return domiFraction1;
}
	/*
	 * 计算约束覆盖情况
	 * @param result1 算法A的结果 Map-key文件名 Map-value 该案例的目标帕累托前沿解
	 * @param result2 算法B的结果
	 * 计算C(B,A) A被B支配的比例
	 */
	private static Map<String, List<Integer>> computeDominate(Map<String, List<List<Double>>> result1,
		Map<String, List<List<Double>>> result2) {
		Map<String,List<Integer>> dominate1=new TreeMap<>();
		for(Map.Entry<String,List<List<Double>>> entry:result1.entrySet()) {
			String name=entry.getKey();
			List<List<Double>> list1=entry.getValue();
			List<List<Double>> list2=result2.get(name);
			List<Integer> dominatelist1=new ArrayList<>();
			for(int i=0;i<list1.size();i++) {
				dominatelist1.add(0);
			}
			for(int i=0;i<list1.size();i++) {
				List<Double> obj1=list1.get(i);
				for(int k=0;k<list2.size();k++) {
					List<Double> obj2=list2.get(k);
					int flag=dominate(obj1,obj2);
					if(flag==2) {
						dominatelist1.set(i, 1);//被约束
						break;
					}
				}
			}
			dominate1.put(name, dominatelist1);
		}
		return dominate1;
}
	//约束情况  返回1 obj1约束obj2  返回2 obj2约束obj1  返回0 没有相互之间的关系
	private static int dominate(List<Double> obj1, List<Double> obj2) {
		int flag, n, k;
		flag =  n = k = 0;

		//个体目标函数值
		for (int i = 0; i < obj1.size(); i++) {
			if (obj1.get(i) < obj2.get(i) ) {
				n++;
			} else if (obj1.get(i) > obj2.get(i) ) {
				k++;
			}
		}
		if (k == 0 && n > 0) {
			flag = 1;
		}
		if (n == 0 && k > 0) {
			flag = 2;
		}
		return flag;
}
	/*
	 * 读取数据
	 * @param input1 文件路径
	 * @return map Map-key:文件名 Map-value:该文件的帕累托前沿解
	 */
	private static Map<String, List<List<Double>>> readData(String input1) {
		Map<String, List<List<Double>>> map=new TreeMap<>();
	   File childDic_f=new File(input1);
	   String list[]=childDic_f.list();
	   for(int i=0;i<list.length;i++) {
		   List<List<Double>> datas=new ArrayList<>();
		   String f=input1+"/"+list[i];
		   BufferedReader read=null;
		   try {
			read=new BufferedReader(new InputStreamReader(new FileInputStream(f)));
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
			map.put(list[i].substring(list[i].indexOf('_')+1, list[i].length()-1), datas);
		   }catch(Exception e) {
			   e.printStackTrace();
		   }
	   }
	 return map;
}
	/*
	 * 生成文件夹名字标识 避免覆盖 
	 * 按照时间
	 */
	public static String buildFileName(){
	 //new一个时间对象date
	 Date date = new Date();
     //格式化
	 SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmm");
     //格式化时间，并且作为文件名
	 return sdf.format(date);
	 }
}
