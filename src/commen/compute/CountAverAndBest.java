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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.junit.Test;

public class CountAverAndBest {
	public static  String para;
   /*
    * 计算正交试验得到的试验结果的平均值 便于分析参数组合
    * @param head 0-5 6-9次组合的文件目录头
    */
   @Test
   public void runExperiment() {
	   String name[]=new String[9];
	   String head="NSGAV_H0401100250_";
	   String cdic="正交//average_GAVN200_200_40_90_9/";//子文件夹
	   CountAverAndBest.para="超体积";
	   List<Map<String,Double>> all=new ArrayList<>();
	   for(int i=0;i<5;i++) {
		   name[i]=head+(i+1);
		   comput(name[i],cdic,all);
	   }
	   head="NSGAV_H0401100752_";
	   for(int i=5;i<9;i++) {
		   name[i]=head+(i+1);
		   comput(name[i],cdic,all);
	   }
	   
	   
   }
   /*
    * 计算算法20次试验得到的试验结果的平均指标值
    * 可以是超体积和MID
    */
   @Test
   public void test() {
	   List<Map<String,Double>> all=new ArrayList<>();
	   String name="NSGAV_H0329104318_run1";
	   CountAverAndBest.para="反转MID";
	   String cdic="average/MID";//子文件夹
	   comput(name,cdic,all);
   }
   private  void computExp(String name) {
  	   String cdic="average/";//子文件夹
	   //String name;//
	   String  dic="data/"+name;
	   String head=buildFileName();
	   String out_dic="data/"+cdic+"result"+name+head;
	   String aver_out="data/"+cdic+"average"+name+head;
	   String best_out="data/"+cdic+"best"+name+head;
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
	   //总的统计输出
	   outprintAverage(countresultH,aver_out);
	   //copyBestToDic(best_out,countresultH,dic,childdicHead);
	
}
    /*
     * @param name
     * @param cdic
     * @param all 链表 分别对应多次试验 value是一个map  map-key:案例名字 map-value:案例
     */
     private static void comput(String name,String cdic, List<Map<String, Double>> all) {
    	
  	   //String name;//
       Map<String, Double>  map=new HashMap<>();
  	   String  dic="data/"+name;
  	   String head=buildFileName();
  	   String out_dic="data/"+cdic+"result"+name+head;
  	   String aver_out="data/"+cdic+"average"+name+head;
  	   String best_out="data/"+cdic+"best"+name+head;
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
  	   //总的统计输出
  	   all.add(map);
  	   outprintAverage(countresultH,aver_out);
  	   //copyBestToDic(best_out,countresultH,dic,childdicHead);
	
}
	//复制最佳的超体积结果对应的案例到同意文件夹下
     private static void copyBestToDic(String best_out, Map<String, List<Double>> countresultH, String dic,String childdicHead) {
    	 
    	 File f=new File(best_out);
         if(f.exists()) f.delete();
    	   f.mkdirs();
    	 BufferedReader read=null;
  		 BufferedWriter write=null;
    	 for(Map.Entry<String, List<Double>> entry:countresultH.entrySet()) {
    		 String filename=entry.getKey();
    		 int id=(int)(double)(entry.getValue().get(1));//这个位置确定
    		 String outPath=best_out+"/"+filename;
    		 String inputPath=dic+"/"+childdicHead+id+"/"+filename;
    		 File out =new File(outPath);
    		 if(out.exists())out.delete();
    		 try {
				 read=new BufferedReader(new InputStreamReader(new FileInputStream(new File(inputPath))));
				 write=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out)));
				 String line=null;
				 while((line=read.readLine())!=null) {
					write.write(line);
					write.newLine();
				 }
				 write.flush();
			}catch (Exception e) {
				e.printStackTrace();
			}
    	 }
    	 try {
			read.close();
			write.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
}
	//从输出文件中统计结果
	 private static List<Map<String, List<String>>> countAll(String dic, String[] childDics) {
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
					if(line.startsWith(CountAverAndBest.para)) {
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
	//输出各个案例的输出结果统计
     private static void outputResult(Map<String, List<String>> countH, String out_dic) {
    	 //写入文件
  	   /*String head=buildFileName();
  	   String out_dic="data/result"+name+head;*/
  	   File out_dic_f=new File(out_dic);
  	   out_dic_f.mkdirs();
  	   for(Map.Entry<String, List<String>> entry:countH.entrySet()) {
  		   String key=entry.getKey();
  		   String out_path=out_dic+"/"+key;
  		   File f=new File(out_path);
  		   if(f.exists()) f.delete();
  		   try {
  			f.createNewFile();
  			BufferedWriter write=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f)));
  			List<String> hypervolume=entry.getValue();
  			for(int m=0;m<hypervolume.size();m++) {
  				write.write(hypervolume.get(m));
  				write.newLine();
  			}
  			write.close();
  		} catch (IOException e) {
  			e.printStackTrace();
  		} 
  		   
  	   }
    }
    //输出平均超体积值  平均时间  最大超体积
	private static void outprintAverage(Map<String, List<Double>> countresultH, String aver_out) {
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
  			  write.write("平均  "+result.get(0)+" "+"最大  "+result.get(1)+" "+result.get(2)+" "+"平均时间"+result.get(3));
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
	 private static String[] sortDic(String[] childDics,String childdicHead) {
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
	//生成文件夹名字标识 避免重复 
	public static String buildFileName(){
	 //new一个时间对象date
	 Date date = new Date();
     //格式化
	 SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmm");
     //格式化时间，并且作为文件名
	 return sdf.format(date);
	 }
}
