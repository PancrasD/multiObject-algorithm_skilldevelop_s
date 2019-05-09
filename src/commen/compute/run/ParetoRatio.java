package commen.compute.run;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import commen.compute.Tools;

public class ParetoRatio {
	
	public void compute(List<Map<String, List<List<Double>>>> count1,
			List<Map<String, List<List<Double>>>> count2, String output) {
		computeParetoRatio(count1,count2,output);
	}

	/*
	 * @param count1 A算法 20次试验 36个案例的帕累托结果
	 * @param count2 B算法 20次试验 36个案例的帕累托结果
	 * @param output 输出文件路径
	  * 计算帕累托占比
	 */
	public  void computeParetoRatio(List<Map<String, List<List<Double>>>> count1,
		List<Map<String, List<List<Double>>>> count2, String output) {
		Map<String,Double[]> ratioSum=new TreeMap<>();
		for(int i=0;i<count1.size();i++) {
			Map<String,List<Integer>> dominate1=new TreeMap<>();
			Map<String,List<Integer>> dominate2=new TreeMap<>();
			dominate1=computeDominate(count1.get(i),count2.get(i));//1 被 2 支配的比例 C(B,A)表示A被B支配的比例   C(GAVN,NSGA)
			dominate2=computeDominate(count2.get(i),count1.get(i));//2 被 1 支配的比例  C(A,B)表示B被A支配的比例 C(NSGA,GAVN)
			Map<String,Double[]> ratio=new TreeMap<>();
			ratio=ratioCompute(dominate1,dominate2);
			addRatio(ratioSum,ratio);
		}
		//平均
		averageRatio(ratioSum,count1.size());
		List<String> names=Tools.readNames();
		outputRatio(output,ratioSum,names);
  }
	/*
	 * 平均20次试验的案例的非支配占比
	 * @param ratioSum 20次试验统计 map-key:案例名 map-value:A B 两种算法在最终帕累托前沿占比的20次试验总和
	 * 平均结果被放到ratioSum中
	 */
	private void averageRatio(Map<String, Double[]> ratioSum, int size) {
		Iterator iter=ratioSum.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry<String, Double[]> entry=(Entry<String, Double[]>) iter.next();
			String key=entry.getKey();
			Double[] value=entry.getValue();
			Double[] aver= {value[0]/size,value[1]/size};
			ratioSum.put(key,aver);
		}
		
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
					int flag=Tools.dominate(obj1,obj2);
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
	/*
	 * 计算非支配贡献率
	 * @param dominate1 单次试验 A算法的个体被支配情况 map-key:案例 map-value:0表示这个个体解没有被B支配 1表示被支配 即计算没有被支配的解占的比例
	 * @param dominate2 单次试验 A算法的个体被支配情况
	 */
	private Map<String, Double[]> ratioCompute(Map<String, List<Integer>> dominate1,
			Map<String, List<Integer>> dominate2) {
		Map<String,Double[]> ratio=new TreeMap<>();
		for(Map.Entry<String,List<Integer>> entry:dominate1.entrySet()) {
			String key=entry.getKey();
			List<Integer> list1=entry.getValue();
			int domi1=0;
			for(int i=0;i<list1.size();i++) {
				if(list1.get(i)==0) {// 0代表没有被支配
					domi1++;
				}
			}
			int domi2=0;
			List<Integer> list2=dominate2.get(key);
			for(int i=0;i<list2.size();i++) {
				if(list2.get(i)==0) {
					domi2++;
				}
			}
			Double[] ratio_= {domi1*1.0/(domi1+domi2),domi2*1.0/(domi1+domi2)};
			ratio.put(entry.getKey(),ratio_);
		}
	   return ratio;
	}
	/*
	 * 累加20次的非支配占比以求平均值
	 */
	private void addRatio(Map<String, Double[]> ratioSum, Map<String, Double[]> ratio) {
		Iterator iter=ratio.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry<String, Double[]> entry=(Entry<String, Double[]>) iter.next();
			String key=entry.getKey();
			Double[] value=entry.getValue();
			Double[] al=null;
			if(ratioSum.containsKey(key)) {
				al=ratioSum.get(key);
				al[0]+=value[0];
				al[1]+=value[1];
			}else {
				al=new Double[2];
				al[0]=value[0];
				al[1]=value[1];
				ratioSum.put(key, al);
			}
		}
		
	}
	/*
	 * 输出统计的20次试验 36个案例的 A B 算法的帕累托占比的平均值
	 * @param output 输出文件路径
	 * @param ratioSum 统计结果 map-key:案例 map-value:占比结果
	 * @param names 案例名字
	 * 输出 : 案例名 A占比 B占比
	 */
	private void outputRatio(String output, Map<String, Double[]> ratioSum, List<String> names) {
		File out_f=new File(output);
	  	if(out_f.exists()) out_f.delete();
	  	out_f.mkdirs();
	  	String coverge=output+"/"+"ratio.txt";
	  	File cov_f=new File(coverge);
	  	if(cov_f.exists())cov_f.delete();
	  	 BufferedWriter write=null;
	  	try {
	  		cov_f.createNewFile();
	        write=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(cov_f)));
			for(int i=0;i<names.size();i++) {
				String name =names.get(i);
				Double value[]=ratioSum.get(name);
				if(value!=null) {
					write.write(name+" "+value[0]+" "+value[1]);
					write.newLine();
				}
			}
			write.flush();
			write.close();
	  	}catch(Exception e) {
	  		e.printStackTrace();
	  	}
		
	}
}
