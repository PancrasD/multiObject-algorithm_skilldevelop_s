package commen.compute;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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


public class CountRatio {
	public  static final String NSGA="NSGA";
	public  static final String GAVN="GAVN";
	public static void main(String[] args) {
		String dic="data/";
		String cdic1="NSGA_0304105124_run1";
		String cdic2="NSGAV_H0304162138_run1";
		Map<String,List<List<Double>>> result1=new TreeMap<>();
		Map<String,List<List<Double>>> result2=new TreeMap<>();
		result1=readResult(dic+cdic1,result1);
		result2=readResult(dic+cdic2,result2);
		String head=buildFileName();
		String output=dic+cdic1+"VS"+cdic2+"ratio"+head;
		Map<String,List<Double>> ratio=new HashMap<>();
		Map<String,List<Double>> number=new HashMap<>();
		computeR(ratio,number,result1,result2);
		//排序
		List<Map.Entry<String, List<Double>>> ratio_list1=new ArrayList<>();
	  	   for(Entry<String, List<Double>> entry:ratio.entrySet()) {
	  		   ratio_list1.add(entry);
	  	   }
	  	SortByNumber(ratio_list1);
		outPutResult(ratio_list1,number,output);
		
	}
	/*
	 * @param ratio_list1 两种算法所占的帕累托前沿解比例
	 * @param number_list2 两种算法的帕累托前沿解数量
	 * 输出结果 
	 */
	private static void outPutResult(List<Entry<String, List<Double>>> ratio_list1,
			Map<String, List<Double>> number, String output) {
	   File out_dic_f=new File(output);
  	   out_dic_f.mkdirs();
  	   String filePath=output+"/ratio.txt";
  	   File f=new File(filePath);
  	   if(f.exists()) f.delete();
  	   try {
		   f.createNewFile();
		   BufferedWriter write=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f)));
		   for(Map.Entry<String, List<Double>> entry:ratio_list1) {
			   String key=entry.getKey();
				write.write(key+" ");
				List<Double> ratio=entry.getValue();
				for(int m=0;m<ratio.size();m++) {
					write.write(ratio.get(m)+" ");
				}
				List<Double> numberL=number.get(key);
				for(int m=0;m<numberL.size();m++) {
					write.write(numberL.get(m)+" ");
				}
				write.newLine();
		   }
		   write.close();
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
	}
	//按照文件名编号排序以便输出
	private static void SortByNumber(List<Entry<String, List<Double>>> domi_list) {
		Collections.sort(domi_list, new Comparator<Map.Entry<String, List<Double>>>() {
	  		@Override
	  		public int compare(Entry<String, List<Double>> o1, Entry<String, List<Double>> o2) {
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
	 * @param ratio 存放占比 key:文件名  value:NSGA占比  GAVN占比
	 * @param number 存放各自的帕累托前沿解数量  key:文件名  value:NSGA  GAVN
	 * @param result1 NSGA的所有运行次数的案例的统计结果
	 * @param result2 GAVN的所有运行次数的案例的统计结果
	 * 将两种算法的帕累托解进行合并 计算帕莱托前沿解占比
	 */
	private static void computeR(Map<String, List<Double>> ratio, Map<String, List<Double>> number, Map<String, List<List<Double>>> result1, Map<String, List<List<Double>>> result2) {
		for(Iterator<Entry<String, List<List<Double>>>> iter=result1.entrySet().iterator();iter.hasNext();) {
			Map.Entry<String, List<List<Double>>> entry=iter.next();
			String fileName=entry.getKey();
			List<List<Double>> fileResult1=entry.getValue();
			List<List<Double>> fileResult2=result2.get(fileName);
			List<List<Double>> all=new ArrayList<>();//所有的结果
			Map<List<Double>,String> mark=new HashMap<>();//标记属于哪个案例
			combine(fileResult1,fileResult2,all,mark);
			List<List<Integer>> rankIndex=non_Dominated_Sort(all);
			List<Integer> firstP=rankIndex.get(0);
			double Number1=0;
			double Number2=0;
			for(int i=0;i<firstP.size();i++) {
				int index=firstP.get(i);
				List<Double> obj=all.get(index);
				if(mark.get(obj).equals(CountRatio.NSGA)) {
					Number1++;
				}else if(mark.get(obj).equals(CountRatio.GAVN)) {
					Number2++;
				}
			}
			List<Double> list1=new ArrayList<>();
			List<Double> list2=new ArrayList<>();
			list1.add(Number1*1.0/firstP.size());
			list1.add(Number2*1.0/firstP.size());
			list2.add(Number1);
			list2.add(Number2);
			ratio.put(fileName, list1);
			number.put(fileName, list2);
		}
	}
	/*
	 * @param all 排序的结果集
	 */
	private static List<List<Integer>> non_Dominated_Sort(List<List<Double>> all) {
		int level=1;
		int populationSize=all.size();
		List<List<Integer>> indivIndexRank = new ArrayList<List<Integer>>(); 
		// 每个解都可以分成两个实体部分:1、支配该解的其他解的数量np;2、被该解支配的解集合Sp
		List<List<Integer>> spList = new ArrayList<List<Integer>>();// 存储的是个体在种群中的序列号
		for (int i = 0; i < populationSize; i++) {
			spList.add(new ArrayList<Integer>());
		}
		int[] np = new int[populationSize];
		for (int i = 0; i < populationSize; i++) {
			for (int j = i+1; j < populationSize; j++) {

				int flag=Dominated(all.get(i), all.get(j));
				if (flag == 1) { // 前者支配后者
					spList.get(i).add(j); // 将个体j加入个体i的支配个体队列
					np[j]++;  // 支配个体j的个体数+1
				}
				if (flag == 2) { // 后者支配前者
					spList.get(j).add(i);
					np[i]++;
				}
			}
		}
		int num = 0;
		int Rank =0;
		while (num < populationSize) {
			List<Integer> FRank = new ArrayList<Integer>(); // FRank是种群中，非支配的不同等级,如F1，F2...
			for (int i = 0; i < populationSize; i++) {
				if (np[i] == 0) {
					FRank.add(i); //将所有没有被任何其他个体支配的个体加入到层级
					np[i] = -1; //标记个体已处理
					num ++;  //已处理的个体数量计数，当已处理个体个数达到种群人数上线即可终止处理
				}
			}
			//被分层的个体所支配的个体的被支配个体数量减1
			for (int i = 0; i < FRank.size(); i++) {
				List<Integer> sp=spList.get(FRank.get(i));
				for (int j = 0; j < sp.size(); j++) {
					np[sp.get(j)]--;
				}
			}
			indivIndexRank.add(FRank);
			Rank ++;//Rank(0-->1)>=level=1
			if ((level != 0)&&(Rank >= level)){
				break;
			}
		}
		return indivIndexRank;
	}
	private static int Dominated(List<Double> list1, List<Double> list2) {
		int flag, n, k;
		flag =  n = k = 0;

		//个体目标函数值
		double[] obj1=new double[] {list1.get(0),list1.get(1)};
		double[] obj2=new double[] {list2.get(0),list2.get(1)};
		
		for (int i = 0; i < obj1.length; i++) {
			if (obj1[i] < obj2[i]) {
				n++;
			} else if (obj1[i] > obj2[i]) {
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
	 * @param fileResult1 NSGA的案例结果
	 * @param fileResult2 GAVN的案例结果
	 * @param all 汇总结果map
	 * @param mark 标记属于哪个算法
	 * 将两个算法的结果进行合并 以进行非支配排序
	 */
	private static void combine(List<List<Double>> fileResult1, List<List<Double>> fileResult2, List<List<Double>> all,
			Map<List<Double>, String> mark) {
		for(Iterator<List<Double>> iter=fileResult1.iterator();iter.hasNext();) {
			List<Double> obj=iter.next();
			all.add(obj);
			mark.put(obj, CountRatio.NSGA);
		}
		for(Iterator<List<Double>> iter=fileResult2.iterator();iter.hasNext();) {
			List<Double> obj=iter.next();
			all.add(obj);
			mark.put(obj, CountRatio.GAVN);
		}
		
	}
	/*
	 * @param cdic 案列多次运行结果的父路径
	 * @param 结果存放的map
	 * 将案例计算的所有运行次数下的结果按照案例名统计
	 */
	private static Map<String, List<List<Double>>> readResult(String cdic, Map<String, List<List<Double>>> result) {
        File dic =new File(cdic);
        String[] childDics = dic.list(); 
        for(int i=0;i<childDics.length;i++) {
        	String childDicPath=cdic+"/"+childDics[i];
        	result=readData(childDicPath,result);
        }
		return result;
	}
	//读取子文件夹下的案例计算结果 以文件名存放  单次运行统计
	private static Map<String, List<List<Double>>> readData(String input1,Map<String, List<List<Double>>> map) {
	   File childDic_f=new File(input1);
	   String list[]=childDic_f.list();
	   for(int i=0;i<list.length;i++) {
		   String fileName=list[i].substring(list[i].indexOf('_')+1, list[i].length()-1);
		   List<List<Double>> datas=map.get(fileName);
		   if(datas==null) {
			   datas=new ArrayList<>();
			   map.put(fileName, datas);
		   }
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
		   }catch(Exception e) {
			   e.printStackTrace();
		   }
	   }
	 return map;
}
	//生成文件夹名字标识 避免覆盖 
	public static String buildFileName(){
	 //new一个时间对象date
	 Date date = new Date();
     //格式化
	 SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmm");
     //格式化时间，并且作为文件名
	 return sdf.format(date);
	 }
}
