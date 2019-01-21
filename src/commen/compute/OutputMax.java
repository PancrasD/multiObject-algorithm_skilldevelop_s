package commen.compute;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

public class OutputMax {
	 public static void main(String[]args) {
		 String path="data\\NSGAV_H01101746\\nsgah_0";
		 String output="data/"+"max/";
		 Map<String,List<Double>> result1=new TreeMap<>();
		 result1=readData(path);
		//排序
		List<Map.Entry<String, List<Double>>> domi_list1=new ArrayList<>();
	  	   for(Entry<String, List<Double>> entry:result1.entrySet()) {
	  		   domi_list1.add(entry);
	  	   }
	  	SortByNumber(domi_list1);
		//输出
	  	output(output,domi_list1);
	 }
    private static void output(String output, List<Entry<String, List<Double>>> domi_list1) {
    	File out_f=new File(output);
	  	if(out_f.exists()) out_f.delete();
	  	out_f.mkdirs();
	  	String coverge=output+"/"+"max.txt";
	  	File cov_f=new File(coverge);
	  	if(cov_f.exists())cov_f.delete();
	  	try {
	  		cov_f.createNewFile();
	        BufferedWriter write=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(cov_f)));
	        for(int i=0;i<domi_list1.size();i++) {
	        	write.write(domi_list1.get(i).getKey()+" "+domi_list1.get(i).getValue().get(0)+" "+domi_list1.get(i).getValue().get(1));
	        	write.newLine();
	        }
	        write.flush();  
	        write.close();
	  	}catch(Exception e) {
	  		e.printStackTrace();
	  	}
		
	}
	/*
     * @param domi_list1 key  文件名 value  工期-成本
       * 按照文件名进行排序
     */
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
          * 读数据
     * @param path 文件夹路径
     */
	private static Map<String, List<Double>> readData(String path) {
		Map<String, List<Double>> map=new TreeMap<>();
		   File childDic_f=new File(path);
		   String list[]=childDic_f.list();
		   for(int i=0;i<list.length;i++) {
			   List<Double> data=new ArrayList<>();
			   String f=path+"/"+list[i];
			   BufferedReader read=null;
			   try {
				read=new BufferedReader(new InputStreamReader(new FileInputStream(f)));
			    String line=null;
				while((line=read.readLine())!=null) {
					if(line.startsWith("最大工期")) {
						String[]duration=line.split(":");
						data.add(Double.parseDouble(duration[1]));
						line=read.readLine();
						String[]cost=line.split(":");
						data.add(Double.parseDouble(cost[1]));
						break;
					}
				}
				map.put(list[i].substring(list[i].indexOf('_')+1, list[i].length()-1), data);
			   }catch(Exception e) {
				   e.printStackTrace();
			   }
		   }
		 return map;
	}
}
