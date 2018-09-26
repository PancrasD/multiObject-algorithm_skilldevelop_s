package commen.compute;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

public class ComputeCoverage {
   //计算帕累托前沿的约束覆盖率
	public static void main(String[]args) {
		String input1="data\\NSGA_bestNSGA_0925230209262149";
		String input2="D:\\workspace\\RCSPSP\\new_multiObjective_algorithm_x\\new_multiObjective_algorithm\\"
				+ "data\\NSGA_bestNSGANoSkill_110409261755";
		String head=buildFileName();
		String output="data\\coverage"+head;
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
	//覆盖百分比
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
	//计算约束覆盖情况
	private static Map<String, List<Integer>> computeDominate(Map<String, List<List<Double>>> result1,
		Map<String, List<List<Double>>> result2) {
		Map<String,List<Integer>> dominate1=new TreeMap<>();
		for(Map.Entry<String,List<List<Double>>> entry:result1.entrySet()) {
			String name=entry.getKey();
			List<List<Double>> list1=entry.getValue();
			List<List<Double>> list2=result2.get(name);
			if(list2==null) {
				System.out.println(name);
			}
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
	//读取数据
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
			map.put(list[i], datas);
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
