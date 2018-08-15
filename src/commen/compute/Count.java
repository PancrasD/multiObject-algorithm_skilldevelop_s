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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Count {
   public static void main(String[]args) {
	   String  dic="data/TLBO_F";
	   File dic_f=new File(dic);
	   String[] childDics=dic_f.list();
	   //生成统计文件及统计结果文件  
	   Map<String,List<String>> count=new TreeMap<>();
	   Map<String,Integer> num=new TreeMap<>();
	   for(int k=0;k<childDics.length;k++) {
		   String childDic=dic+"/"+childDics[k];
		   File childDic_f=new File(childDic);
		   String list[]=childDic_f.list();
		   for(int i=0;i<list.length;i++) {
			   String f=dic+"/"+childDics[k]+"/"+list[i];
			   BufferedReader read=null;
			   try {
				read=new BufferedReader(new InputStreamReader(new FileInputStream(f)));
			    String line=null;
				while((line=read.readLine())!=null) {
					if(line.startsWith("指标MOCV")) {
						String str[]=line.split(":");
						List<String> mocv=count.get(list[i]);
						if(mocv==null) {
							mocv=new ArrayList<>();
							mocv.add(str[1]);
							count.put(list[i], mocv);
						}
						else {
							mocv.add(str[1]);
							count.put(list[i], mocv);
						}
						
					}
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   }
	   }
	   //计算总的平均
	   Map<String,Double> average=new TreeMap<>();
	   
	   for(Map.Entry<String, List<String>> entry:count.entrySet()) {
		   String key=entry.getKey();
		   List<String> mocv=entry.getValue();
		   double sum=0;
		   for(int m=0;m<mocv.size();m++) {
			   sum+=Double.parseDouble(mocv.get(m));
		   }
		   double aver=sum/mocv.size();
		   average.put(key, aver);
	   }
	   //写入文件
	   String out_dic="data/count";
	   File out_dic_f=new File(out_dic);
	   out_dic_f.mkdirs();
	   for(Map.Entry<String, List<String>> entry:count.entrySet()) {
		   String key=entry.getKey();
		   String out_path=out_dic+"/"+key;
		   File f=new File(out_path);
		   if(f.exists()) f.delete();
		   try {
			f.createNewFile();
			BufferedWriter write=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f)));
			List<String> mocv=entry.getValue();
			for(int m=0;m<mocv.size();m++) {
				write.write(mocv.get(m));
				write.newLine();
			}
			write.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		   
	   }
	   //总的统计输出
	   //average排序
	   List<Map.Entry<String, Double>> av_list=new ArrayList<>();
	   for(Map.Entry<String, Double> entry:average.entrySet()) {
		   av_list.add(entry);
	   }
	   Collections.sort(av_list, new Comparator<Map.Entry<String, Double>>() {

		@Override
		public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
			String str1[] =o1.getKey().split("_|\\.");
			String str2[] =o2.getKey().split("_|\\.");
			if(Double.parseDouble(str1[1].substring(1))>Double.parseDouble(str2[1].substring(1))) {
				return 1;
			}else if(Double.parseDouble(str1[1].substring(1))<Double.parseDouble(str2[1].substring(1))) {
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
	   String aver_out="data/average";
	   File aver_out_f=new File(aver_out);
	   if(aver_out_f.exists()) aver_out_f.delete();
	   aver_out_f.mkdirs();
	   String aver_sum=aver_out+"/"+"count.txt";
	   File aver_sum_f=new File(aver_sum);
	   if(aver_sum_f.exists())aver_sum_f.delete();
		try {
			aver_sum_f.createNewFile();
            BufferedWriter write=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(aver_sum_f)));
            for(int k=0;k<av_list.size();k++) {
			  write.write(av_list.get(k).getKey()+ " " +av_list.get(k).getValue());
			  write.newLine();
              }
            write.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   
   }
}
