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

public class Coverage {
	
	public void compute(List<Map<String, List<List<Double>>>> count1,
			List<Map<String, List<List<Double>>>> count2, String output) {
		computeC(count1,count2,output);
	}
	/*
	 * 计算C指标
	 * @param count1 算法1的结果 List-20次试验  每一次是一个Map Map-key文件名 Map-value目标值List
	 * @param count2
	 * @param output 输出文件路径
	 */
	public  void computeC(List<Map<String, List<List<Double>>>> count1, List<Map<String, List<List<Double>>>> count2, String output) {
		Map<String,Double> domiFraction1Sum=new TreeMap<>();
		Map<String,Double> domiFraction2Sum=new TreeMap<>();
		for(int i=0;i<count1.size();i++) {
			Map<String,List<Integer>> dominate1=new TreeMap<>();
			Map<String,List<Integer>> dominate2=new TreeMap<>();
			dominate1=computeDominate(count1.get(i),count2.get(i));//1 被 2 支配的比例 C(B,A)表示A被B支配的比例   C(GAVN,NSGA)
			dominate2=computeDominate(count2.get(i),count1.get(i));//2 被 1 支配的比例  C(A,B)表示B被A支配的比例 C(NSGA,GAVN)
			Map<String,Double> domiFraction1=new TreeMap<>();
			Map<String,Double> domiFraction2=new TreeMap<>();
			domiFraction1=ratioCompute(dominate1);
			domiFraction2=ratioCompute(dominate2);
			addDomiFraction(domiFraction1Sum,domiFraction1);
			addDomiFraction(domiFraction2Sum,domiFraction2);
		}
		//平均
		averageDomiFraction(domiFraction1Sum,count1.size());
		averageDomiFraction(domiFraction2Sum,count1.size());
		List<String> names=Tools.readNames();
		output(output,domiFraction1Sum,domiFraction2Sum,names);
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
	 * @param dominate1 Map-key文件名 Map-value代表该目标组合是否被支配  1-被支配   0-未被支配  即统计被支配概率
	 *    计算覆盖百分比
	 * @return domiFraction1 支配比例 Map-key文件名 Map-value支配比例
	 */
	private static Map<String, Double> ratioCompute(Map<String, List<Integer>> dominate1) {
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
}
