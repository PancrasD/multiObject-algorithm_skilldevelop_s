package commen.compute.run;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import commen.compute.Tools;

public class FinalPareto {
	
	public void compute(List<Map<String, List<List<Double>>>> count1,
			List<Map<String, List<List<Double>>>> count2, String output) {
		computeFinalPareto(count1,count2,output);
	}

	/*
	 * 输出20次试验合并的最终的帕累托前沿
	 * @param count1 A算法 20次试验 36个案例的帕累托结果
	 * @param count2 B算法 20次试验 36个案例的帕累托结果
	 * @param output 输出文件路径
	 */
	public void computeFinalPareto(List<Map<String, List<List<Double>>>> count1,
			List<Map<String, List<List<Double>>>> count2, String output) {
		Map<String, List<List<Double>>> combine1=getCombine(count1);
		Map<String, List<List<Double>>> combine2=getCombine(count2);
		Map<String, List<List<Double>>> pareto1=getPareto(combine1);
		Map<String, List<List<Double>>> pareto2=getPareto(combine2);
		outPutPareto(pareto1,output,1);//不同算法输出到一个文件
		outPutPareto(pareto2,output,2);//不同算法输出到一个文件
	}
	/*
	 * 将36个案例 20次试验 的帕累托结果进行合并
	 * @param  count 20次试验结果
	 * @return combine 合并结果
	 */
	private Map<String, List<List<Double>>> getCombine(List<Map<String, List<List<Double>>>> count) {
		Map<String, List<List<Double>>> combine=new HashMap<>();
		for(int i=0;i<count.size();i++) {
			Map<String, List<List<Double>>> single=count.get(i);
			Iterator iter=single.entrySet().iterator();
			while(iter.hasNext()) {
				Map.Entry<String, List<List<Double>>> entry=(Entry<String, List<List<Double>>>) iter.next();
				if(combine.containsKey(entry.getKey())) {
					combine.get(entry.getKey()).addAll(entry.getValue());
				}else {
					List<List<Double>> list=new ArrayList<>();
					list.addAll(entry.getValue());
					combine.put(entry.getKey(), list);
				}
			}
		}
		return combine;
	}
	/*
	 * 筛选出帕累托前沿解 
	 * @param combine 36个案例 20次试验 合并的帕累托总结果
	 * @return pareto 36个案例  20次试验合并的帕累托最终前沿
	 */
	private Map<String, List<List<Double>>> getPareto(Map<String, List<List<Double>>> combine) {
		Map<String, List<List<Double>>> pareto=new HashMap<>();
		Iterator iter=combine.entrySet().iterator();
		while(iter.hasNext()) {
			Map.Entry<String, List<List<Double>>> entry=(Entry<String, List<List<Double>>>) iter.next();
			List<List<Double>> list=getParetoSingle(entry.getValue());
			pareto.put(entry.getKey(), list);
		}
		return pareto;
	}
	/*
	 * 单次案例获取的pareto
	 * @param  solutions 单次案例20次试验获取的帕累托汇总解
	 * @return 合并的最终的pareto结果
	 */
	private List<List<Double>> getParetoSingle(List<List<Double>> solutions) {
		List<List<Double>> finalPareto=new ArrayList<>();
		// 每个解都可以分成两个实体部分:1、支配该解的其他解的数量np;2、被该解支配的解集合Sp
		List<List<Integer>> spList = new ArrayList<List<Integer>>();// 存储的是个体在种群中的序列号
		for (int i = 0; i < solutions.size(); i++) {
			spList.add(new ArrayList<Integer>());
		}
		int[] np = new int[solutions.size()];
		for (int i = 0; i < solutions.size(); i++) {
			for (int j = i+1; j < solutions.size(); j++) {

				int flag=Tools.dominate(solutions.get(i),solutions.get(j));
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
		// 定义一个集合，用来存储前面已经排好等级的个体在种群的序列号
		int num = 0;
		List<Integer> FRank = new ArrayList<Integer>(); // FRank是种群中，非支配的不同等级,如F1，F2...
		for (int i = 0; i < solutions.size(); i++) {
			if (np[i] == 0) {
				FRank.add(i); //将所有没有被任何其他个体支配的个体加入到层级
				np[i] = -1; //标记个体已处理
				num ++;  //已处理的个体数量计数，当已处理个体个数达到种群人数上线即可终止处理
			}
		}
		for(int k=0;k<FRank.size();k++) {
			finalPareto.add(solutions.get(FRank.get(k)));
		}
		return finalPareto;
	}
	/*
	 * 输出最终的帕累托前沿
	 * 
	 */
	private void outPutPareto(Map<String, List<List<Double>>> pareto1, String outputPath, int mark) {
		List<String> names=Tools.readNames();
		File out_f=new File(outputPath);
	  	if(out_f.exists()) out_f.delete();
	  	out_f.mkdirs();
	  	String output=outputPath+"/"+"finalPareto"+mark+".txt";
	  	File cov_f=new File(output);
	  	if(cov_f.exists())cov_f.delete();
	  	 BufferedWriter write=null;
	  	try {
	  		cov_f.createNewFile();
	        write=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(cov_f)));
	        for(int i=0;i<names.size();i++) {
				String name =names.get(i);
				write.write(name);
				write.newLine();
				List<List<Double>> pare1=pareto1.get(name);
				for(int k=0;pare1!=null&&k<pare1.size();k++) {
					write.write(pare1.get(k).get(0)+" "+pare1.get(k).get(1));
					write.newLine();
				}
				write.newLine();
			}
			write.flush();
			write.close();
	  	}catch(Exception e) {
	  		e.printStackTrace();
	  	}
		
	}
}
