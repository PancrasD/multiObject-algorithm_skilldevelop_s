package commen.compute.run;

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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.junit.Test;

import commen.compute.CountAverAndBest;
import commen.compute.Tools;


public class HV_MID {

   private  String mark;
  /*
    * 计算算法20次试验得到的试验结果的平均指标值
    * 可以是超体积和MID
    */
   public void compute(String fileName,String outpath,String mark) {
	   this.mark=mark;
	   CountAverAndBest.para="反转MID";
	   comput(fileName,outpath);
   }
   /*
     * @param name
     * @param cdic
     * @param all 链表 分别对应多次试验 value是一个map  map-key:案例名字 map-value:案例
     */
     private  void comput(String name,String cdic) {
    	
  	   //String name;//
       Map<String, Double>  map=new HashMap<>();
  	   String  dic=name;
  	   String head=Tools.buildFileName();
  	   String aver_out=cdic+name+head;
  	   File dic_f=new File(dic);
  	   String[] childDics=dic_f.list();
  	   String childdicHead=childDics[0].substring(0, childDics[0].indexOf('_')+1);
  	   //文件夹排序 按照数字编号 以标识最大超体积文件所在的文件夹位置
  	   childDics=sortDic(childDics,childdicHead);
  	   //从案例的运行实验结果读出结果值
  	   List<Map<String,List<String>>> countAll=new ArrayList<>();
  	   countAll=countAll(dic,childDics);
  	   Map<String,List<String>> countH=countAll.get(0);
  	   Map<String,List<String>> countT=countAll.get(1);
  	   //计算总的平均
  	   Map<String,List<Double>> countresultH=new TreeMap<>();
  	   for(Map.Entry<String, List<String>> entry:countH.entrySet()) {
  		   String key=entry.getKey();
  		   List<String> hyper=entry.getValue();
  		   double sum=0;
  		   int maxHIndex=0;
  		   double maxH=Double.parseDouble(hyper.get(maxHIndex));
  		   for(int m=0;m<hyper.size();m++) {
  			   sum+=Double.parseDouble(hyper.get(m));
  			   if(maxH<Double.parseDouble(hyper.get(m))) {
  				   maxH=Double.parseDouble(hyper.get(m));
  				   maxHIndex=m;
  			   }
  		   }
  		   double aver=sum/hyper.size();
  		   List<Double> result=new ArrayList<>();
  		   result.add(aver);
  		   map.put(key, aver);
  		   result.add((double)(maxHIndex));
  		   result.add(maxH);
  		   //时间
  		   List<String> time=countT.get(key);
  		   double sumT=0;
  		   for(int m=0;m<time.size();m++) {
  			   sumT+=Double.parseDouble(time.get(m));
  		   }
  		   double averT=sumT/time.size();
  		   result.add(averT);
  		   countresultH.put(key, result);
  	   }
  	   //outputResult(countH,out_dic);
  	   outprintAverage(countresultH,aver_out);
   }
   //从输出文件中统计结果
   	 private  List<Map<String, List<String>>> countAll(String dic, String[] childDics) {
   	   Map<String,List<String>> countH=new TreeMap<>();//统计超体积
   	   Map<String,List<String>> countT=new TreeMap<>();//统计时间
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
   					if(line.startsWith(mark)) {
   						String str[]=line.split(":");
   						List<String> hypervolume=countH.get(list[i]);
   						if(hypervolume==null) {
   							hypervolume=new ArrayList<>();
   							hypervolume.add(str[1]);
   							countH.put(list[i], hypervolume);
   						}
   						else {
   							hypervolume.add(str[1]);
   							countH.put(list[i], hypervolume);
   						}
   						
   					}
   					if(line.startsWith("共计用时")) {
   						String str[]=line.split("：");
   						List<String> time=countT.get(list[i]);
   						if(time==null) {
   							time=new ArrayList<>();
   							time.add(str[1].replace("秒", ""));
   							countT.put(list[i], time);
   						}
   						else {
   							time.add(str[1].replace("秒", ""));
   							countT.put(list[i], time);
   						}
   						
   					}
   				}
   			} catch (FileNotFoundException e) {
   				e.printStackTrace();
   			} catch (IOException e) {
   				e.printStackTrace();
   			}
   		   }
   	   }
   	 List<Map<String,List<String>>> countAll=new ArrayList<>();
   	 countAll.add(countH);
   	 countAll.add(countT);
   	 return countAll;
   }
     //输出平均超体积值  平均时间  最大超体积
 	private  void outprintAverage(Map<String, List<Double>> countresultH, String aver_out) {
        //average排序
   	   List<Map.Entry<String, List<Double>>> av_list=new ArrayList<>();
   	   for(Entry<String, List<Double>> entry:countresultH.entrySet()) {
   		   av_list.add(entry);
   	   }
   	   Collections.sort(av_list, new Comparator<Map.Entry<String, List<Double>>>() {
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
   	   /* String aver_out="data/average"+name+head;*/
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
   			  write.write(av_list.get(k).getKey()+ " " );
   			  List<Double> result=av_list.get(k).getValue();
   			  write.write("平均  "+result.get(0)+" "+"最大  "+result.get(1)+" "+result.get(2)+" "+"平均时间 "+result.get(3));
   			  write.newLine();
                 }
               write.close();
   		} catch (FileNotFoundException e) {
   			e.printStackTrace();
   		} catch (IOException e) {
   			e.printStackTrace();
   		}
    }
 	 //文件夹按照数字编码排序
	 private  String[] sortDic(String[] childDics,String childdicHead) {
		 List<String> cds=Arrays.asList(childDics);
		   Collections.sort(cds, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				int flag=0;
				if(Integer.valueOf(o1.replace(childdicHead, ""))<Integer.valueOf(o2.replace(childdicHead, ""))) {
					flag=-1;
				}else if(Integer.valueOf(o1.replace(childdicHead, ""))>Integer.valueOf(o2.replace(childdicHead, ""))) {
					flag=1;
				}
				return flag;
			}  
		   });
		   return (String[]) cds.toArray();
   }
}
