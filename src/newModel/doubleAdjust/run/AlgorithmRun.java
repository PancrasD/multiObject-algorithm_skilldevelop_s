package newModel.doubleAdjust.run;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import newModel.doubleAdjust.Individual;
import newModel.doubleAdjust.NSFFA;
import newModel.doubleAdjust.Population;
import newModel.doubleAdjust.Tools;
import newModel.doubleAdjust.algorithm.NTGA;
import newModel.doubleAdjust.algorithm.QGA;
import newModel.doubleAdjust.algorithm.ReinForcementLearning;
import newModel.doubleAdjust.basis.Case;



public class AlgorithmRun implements Runnable{
	String[] fl;
	ConcurrentHashMap<String, List<List<Double>>> countR;
	Case para;
	String arg;
	String dic;
	String case_def;
	public AlgorithmRun(String[] fl,String dic,String case_def,ConcurrentHashMap<String, List<List<Double>>> countR,Case para,String arg) {
		this.fl=fl;
		this.dic=dic;
		this.case_def=case_def;
		this.countR=countR;
		this.para=para;
		this.arg=arg;
	}
	@Override
	public void run() {
		if(arg.equals("gv")) {
			for(int i = 0; i<fl.length; i++){
				List<List<Double>>countResult=countR.get(fl[i]);
				if(countResult==null) {
					countResult=new ArrayList<>();
					countR.put(fl[i], countResult);
				}
               	String _fn = case_def+ "/" + fl[i];
               	String _fo = dic+"//NSGAVH_"+fl[i]+".txt";
               	//NSGAV_D_R_algorithm(_fn,_fo,countResult,para);
               	QGA qga=new QGA(_fn,_fo,countResult,para);
               	qga.schedule();
               	//NSGAV_D_R_Sub_algorithm(_fn,_fo,countResult,para);
               	//NSGAV_algorithm(_fn,_fo,countResult,para);
           }
		}
		else if(arg.equals("g")) {
			for(int i = 0; i<fl.length; i++){
              	String _fn =  case_def+"/" + fl[i];
              	String _fo = dic+"/NSGA_"+fl[i]+".txt";
              	List<List<Double>>countResult=countR.get(fl[i]);
				if(countResult==null) {
					countResult=new ArrayList<>();
					countR.put(fl[i], countResult);
				}
              	 NSGA_algorithm(_fn,_fo,countResult,para);
              }
		}if(arg.equals("ntga")) {
			for(int i = 0; i<fl.length; i++){
              	String _fn =  case_def+"/" + fl[i];
              	String _fo = dic+"/NTGA_"+fl[i]+".txt";
              	List<List<Double>>countResult=countR.get(fl[i]);
				if(countResult==null) {
					countResult=new ArrayList<>();
					countR.put(fl[i], countResult);
				}
              	 NTGA ntga=new NTGA(_fn,_fo,countResult,para);
              	 ntga.schedule();
              }
		}if(arg.equals("rl")) {
			for(int i = 0; i<fl.length; i++){
              	String _fn =  case_def+"/" + fl[i];
              	String _fo = dic+"/RL_"+fl[i]+".txt";
              	List<List<Double>>countResult=countR.get(fl[i]);
				if(countResult==null) {
					countResult=new ArrayList<>();
					countR.put(fl[i], countResult);
				}
				ReinForcementLearning rl=new ReinForcementLearning(_fn,_fo,countResult,para);
              	rl.schedule();
              }
		}
		else if(arg.equals("h")) {
			for(int i = 0; i<fl.length; i++){
				List<List<Double>>countResult=countR.get(fl[i]);
				if(countResult==null) {
					countResult=new ArrayList<>();
					countR.put(fl[i], countResult);
				}
               	String _fn = case_def+ "/" + fl[i];
               	String _fo = dic+"//NSGAVH_"+fl[i]+".txt";
               	NSGAV_H_algorithm(_fn,_fo,countResult,para);
           }
		}else if(arg.equals("f")) {
			for(int i = 0; i<fl.length; i++){
           	 String _fn =  "case_def/" + fl[i];
           	 String _fo = "data1.0/NSFFA_"+fl[i]+".txt";
           	 List<List<Double>>countResult=countR.get(fl[i]);
					if(countResult==null) {
						countResult=new ArrayList<>();
						countR.put(fl[i], countResult);
					}
           	 NSFFA_algorithm(_fn,_fo,countResult);
           }
		}
		
	}

	/*
	 * 使用超启发式算法进行进化
	 */
	private void NSGAV_H_algorithm(String _fn, String _fo, List<List<Double>> countResult, Case para2) {
		
		
	}
	/*@param caseFile:案例读取目录
	 * @param dataFile:案例计算结果写入目录
	 */
	public  void NSGAV_algorithm(String casefile,String datafile, List<List<Double>> countResult,Case para){
	       //记录开始计算的时间，用于统计本算法的总时间
			long startTime = System.currentTimeMillis();
			// 创建案例类对象
			Case project = new Case(casefile);
			project.setNSGAV_II(para.getNSGAV_II());
			project.setRunTime(para.RunTime);
			// 初始化种群
			Population P1 = new Population(project.getNSGAV_II().populationSize,project,true);
			/*int generationCount = 0; 
	        //循环迭代 算法指定的次  ,数
			while (generationCount < NSGAV_II.maxGenerations ) {
				P = P.getOffSpring_NSGAV();
				generationCount++;
			}*/
			long time=0;
			long t2 = 0;
			while (time < 30 ) {
				P1 = P1.getOffSpring_NSGAV();
				t2=System.currentTimeMillis();
				time=(t2-startTime)/1000;
			}
			//从最后得到种群中获取最优解集
			//Population solutions =Tools.getbestsolution(P,project, 0);//输出rank为0  减少一次非支配排序
			Population solutions = Tools.getbestsolution(P1,1, project);
		    //输出最优解集
			File f = new File(datafile);
			PrintStream ps = null;
			 try {
			   if (f.exists()) f.delete();
			   f.createNewFile();
			   FileOutputStream fos = new FileOutputStream(f);
			   ps = new PrintStream(fos);
			   //输出最优解集
			   Tools.printsolutions(solutions,startTime,countResult,ps);			   
			 } catch (IOException e) {
				e.printStackTrace();
			 }  finally {
		        if(ps != null) 	ps.close();
		     }       	
	}
	public  void NSGAV_D_R_algorithm(String casefile,String datafile, List<List<Double>> countResult,Case para){
			// 创建案例类对象
			Case project = new Case(casefile);
			project.setNSGAV_II(para.getNSGAV_II());
			project.setRunTime(para.RunTime);
			Case project1=new Case(project);
			// 初始化种群
			long startTime = System.currentTimeMillis();
			Population P1 = new Population(project.getNSGAV_II().populationSize,project,true);
			Population P2 = new Population(project1.getNSGAV_II().populationSize,project1,true);
			
			int count=0;
			while (count < 100000 ) {
				int k1=(int) (Math.random()*5);
				int k2=(int) (Math.random()*5);
				P1 = P1.getOffSpring_V(k1);
				P2 = P2.getOffSpring_V(k2);
				List<Individual> indivs1=P1.getpareto();
				//P1=P1.getGuide(indivs1);
				List<Individual> indivs2=P2.getpareto();
				//P2=P2.getGuide(indivs6);
				P1=P1.addPareto(indivs2);
				P2=P2.addPareto(indivs1);
				count+=(P1.getPopulationsize()*2+P2.getPopulationsize()*2+project.getCount()+project1.getCount());
				project.setCount(0);
				project1.setCount(0);
			}
			long endTime = System.currentTimeMillis();
			//从最后得到种群中获取最优解集
			Population mergedPopulation =Tools.merged(P1,P2);
			//mergedPopulation=mergedPopulation.slectPopulation(P1.getPopulationsize());//最多100
			Population solutions = Tools.getbestsolution(mergedPopulation,1, project);
			solutions=Tools.removeSame(solutions);//去重
		    //输出最优解集
			File f = new File(datafile);
			PrintStream ps = null;
			 try {
			   if (f.exists()) f.delete();
			   f.createNewFile();
			   FileOutputStream fos = new FileOutputStream(f);
			   ps = new PrintStream(fos);
			   //输出最优解集
			   Tools.printsolutions(solutions,endTime-startTime,countResult,ps);			   
			 } catch (IOException e) {
				e.printStackTrace();
			 }  finally {
		        if(ps != null) 	ps.close();
		     }       	
	}
	public  void NSGAV_D_R_Sub_algorithm(String casefile,String datafile, List<List<Double>> countResult,Case para){
	      
			// 创建案例类对象
			Case project = new Case(casefile);
			project.setNSGAV_II(para.getNSGAV_II());
			project.setRunTime(para.RunTime);
			Case project1=new Case(project);
			// 初始化种群
			long startTime = System.currentTimeMillis();
			Population P1 = new Population(project.getNSGAV_II().populationSize,project,true);
			Population P2 = new Population(project1.getNSGAV_II().populationSize,project1,true);
			int count=0;
			while (count < 100000 ) {
				int k1=(int) (Math.random()*5);
				int k2=(int) (Math.random()*5);
				P1 = P1.getOffSpring_V_Sub(k1);
				P2 = P2.getOffSpring_V_Sub(k2);
				List<Individual> indivs1=P1.getpareto();
				List<Individual> indivs2=P2.getpareto();
				P1=P1.addPareto(indivs2);
				P2=P2.addPareto(indivs1);
				count+=(P1.getPopulationsize()*2+P2.getPopulationsize()*2+project.getCount()+project1.getCount());
				project.setCount(0);
				project1.setCount(0);
			}
			long endTime = System.currentTimeMillis();
			//从最后得到种群中获取最优解集
			Population mergedPopulation =Tools.merged(P1,P2);
			Population solutions = Tools.getbestsolution(mergedPopulation,1, project);
			solutions=Tools.removeSame(solutions);//去重
		    //输出最优解集
			File f = new File(datafile);
			PrintStream ps = null;
			 try {
			   if (f.exists()) f.delete();
			   f.createNewFile();
			   FileOutputStream fos = new FileOutputStream(f);
			   ps = new PrintStream(fos);
			   //输出最优解集
			   Tools.printsolutions(solutions,endTime-startTime,countResult,ps);			   
			 } catch (IOException e) {
				e.printStackTrace();
			 }  finally {
		        if(ps != null) 	ps.close();
		     }       	
	}
	/*@param caseFile:案例读取目录
	 *@param dataFile:案例计算结果写入目录
	 *  加强学习搜索
	 */
	public  void NSGAV_D_S_algorithm(String casefile,String datafile, List<List<Double>> countResult,Case para){
	       //记录开始计算的时间，用于统计本算法的总时间
			long startTime = System.currentTimeMillis();
			// 创建案例类对象
			Case project = new Case(casefile);
			project.setNSGAV_II(para.getNSGAV_II());
			project.setRunTime(para.RunTime);
			// 初始化种群
			Population P1 = new Population(project.getNSGAV_II().populationSize,project,true);
			Population P2 = new Population(project.getNSGAV_II().populationSize,project,true);
			long time=0;
			long t2 = 0;
			List<Individual> indivs1_s=P1.getpareto();
			List<Individual> indivs2_s=P2.getpareto();
			double hyper1_s=computeHyper(indivs1_s,project);
			double hyper2_s=computeHyper(indivs2_s,project);
			List<List<Double>> incre_hyper=new ArrayList<>();
			List<Integer> searchList=new ArrayList<>();
			for(int i=0;i<5;i++) {
				List<Double> list=new ArrayList<>();
				incre_hyper.add(list);
				searchList.add(i);
			}
			int[] mark=new int[5];
			double[] prob=new double[5];
			for(int i=0;i<5;i++) {
				prob[i]=1.0/5;
			}
			while (time < 30 ) {
				int k1=selectSearch(prob,searchList);
				int k2=selectSearch(prob,searchList);
				boolean isAllTurn=updateMark(k1,k2,mark);
				P1 = P1.getOffSpring_V(k1);
				P2 = P2.getOffSpring_V(k2);
				List<Individual> indivs1=P1.getpareto();
				List<Individual> indivs2=P2.getpareto();
				double hyper1=computeHyper(indivs1,project);
				double hyper2=computeHyper(indivs2,project);
				updateHyper(hyper1,hyper2,hyper1_s,hyper2_s,incre_hyper,k1,k2);
				if(isAllTurn) {
				   updateProb(incre_hyper,prob);
				   for(int i=0;i<mark.length;i++) {
					   mark[i]=0;
				   }
				}
				hyper1_s=hyper1;
				hyper2_s=hyper2;
				Population paretoPop=combinePop(project,indivs1,indivs2);
				Population paretoPopPop=paretoPop.getOffSpring_Pareto();
				P1=P1.addPareto(paretoPopPop);
				P2=P2.addPareto(paretoPopPop);
				t2=System.currentTimeMillis();
				time=(t2-startTime)/1000;
			}
			//从最后得到种群中获取最优解集
			// 将两个种群合并
			Population mergedPopulation =P1.merged(P1,P2);
			mergedPopulation=mergedPopulation.slectPopulation(P1.getPopulationsize());//暂时去掉重复防止
			Population solutions = Tools.getbestsolution(P1,1, project);
		    //输出最优解集
			File f = new File(datafile);
			PrintStream ps = null;
			 try {
			   if (f.exists()) f.delete();
			   f.createNewFile();
			   FileOutputStream fos = new FileOutputStream(f);
			   ps = new PrintStream(fos);
			   //输出最优解集
			   Tools.printsolutions(solutions,startTime,countResult,ps);			   
			 } catch (IOException e) {
				e.printStackTrace();
			 }  finally {
		        if(ps != null) 	ps.close();
		     }       	
	}
	/*
	 * 
	 */
	private void updateHyper(double hyper1, double hyper2, double hyper1_s, double hyper2_s,
			List<List<Double>> incre_hyper, int k1, int k2) {
		double incre_hyper_1=(hyper1-hyper1_s)/hyper1;
		double incre_hyper_2=(hyper2-hyper2_s)/hyper2;
		List<Double> list1=incre_hyper.get(k1);
		List<Double> list2=incre_hyper.get(k2);
		list1.add(incre_hyper_1);
		list2.add(incre_hyper_2);
	}
	/*
	 * @prob 概率数组
	 * 使用轮盘赌 选择搜索策略
	 */
	private int selectSearch(double[] prob, List<Integer> searchList) {
		double rouletteWheelPosition = Math.random();
		double spinWheel = 0;
		int s_index = 0;
		Iterator<Integer> sl = searchList.iterator();
		while(sl.hasNext()){
			s_index = sl.next();
			spinWheel += prob[s_index];  
			if (spinWheel >= rouletteWheelPosition) {
				break;
			}		
		}
		return s_index;
	}
	/*
	 * @param incre_hyper 超体积链表
	 * @param prob 概率数组
	 * 更新邻解结构的选择概率
	 */
	private void updateProb(List<List<Double>> incre_hyper, double[] prob) {
		double temp[]=new double[incre_hyper.size()];
		for(int i=0;i<incre_hyper.size();i++) {
			double sum=0;
			List<Double> list=incre_hyper.get(i);
			for(int k=0;k<list.size();k++) {
				sum+=list.get(k);
			}
			temp[i]=sum/list.size();
		}
		//后面可能会修正
		double a=0.1;
		double sum=0;
		//计算
		for(int i=0;i<prob.length;i++) {
			prob[i]=prob[i]*(1-a)+a*temp[i];
			sum+=prob[i];
			incre_hyper.get(i).clear();//清空超体积提什
		}
		for(int i=0;i<prob.length;i++) {
			prob[i]=prob[i]/sum;
		}
	}
	/*
	 * @ k1 k2 邻解结构的序号
	 * @mark 计算使用情况的数组
	 * 更新邻解结构的使用情况
	 */
    private boolean updateMark(int k1, int k2, int[] mark) {
		if(mark[k1]==0) {
			mark[k1]=1;
		}
		if(mark[k2]==0) {
			mark[k2]=1;
		}
		for(int i=0;i<mark.length;i++) {
			if(mark[i]==0) {
				return false;
			}
		}
		return true;
	}
	/*
     * @param indivList 帕累托前沿个体
     * 计算种群超体积
     * @return hyperVolume  超体积
     */
	private double computeHyper(List<Individual> indivList, Case project) {
		List<double[]> betterObjs = new ArrayList<>();
		// 遍历输出每个个体,并格式化输出染色体结构以及目标函数
		List<Integer>indexList=new ArrayList<>();
		for (int i = 0; i < indivList.size(); i++) {
			double[] obj = indivList.get(i).getObj();
			betterObjs.add(obj);
			indexList.add(i);
		}
		int m=0;
		Collections.sort(indexList,new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				if(betterObjs.get(o1)[m]>betterObjs.get(o2)[m]) {
					return 1;
				}else if(betterObjs.get(o1)[m]<betterObjs.get(o2)[m] ){
			           return -1;
		           }
				return 0;
			}
			
		});
		 //计算反转超体积  反转的工期为所有工期之和  反转的成本为工期*最大的薪水
		double MaxDuration=project.getBorderDuration();
		double MaxCost=project.getBorderCost();
		//反转   归一   计算超体积
		List<double[]> inversObj=new ArrayList<>();
		double hyperVolume=0;
		for(int i=0;i<indexList.size();i++) {
			int index=indexList.get(i);
			double[] obj=betterObjs.get(index);
			double dura=(MaxDuration-obj[0])/MaxDuration;
			double cost=(MaxCost-obj[1])/MaxCost;
			inversObj.add(new double[]{dura,cost});
			double[] inver=inversObj.get(i);
			if(i==0) {
				hyperVolume+=inver[0]*inver[1];
			}else {
				double temp=(inver[1]-inversObj.get(i-1)[1])*inver[0];
				hyperVolume+=temp;
			}
		}
		return hyperVolume;
	}
	public  void NSGA_algorithm(String casefile,String datafile,List<List<Double>> countResult,Case para){
	       //记录开始计算的时间，用于统计本算法的总时间
			// 创建案例类对象
			Case project = new Case(casefile);
			project.setNSGA_II(para.getNSGA_II());//赋予参数
			project.setRunTime(para.RunTime);
			// 初始化种群
			long startTime = System.currentTimeMillis();
			Population P = new Population(project.getNSGA_II().populationSize,project,true);
			/*int generationCount = 0;
	        //循环迭代 算法指定的次数
			while (generationCount < NSGA_II.maxGenerations ) {
				P = P.getOffSpring_NSGA();
				generationCount++;
			}*/
			long time=0;
			long t2 = 0;
			//int generationCount = 0;
			int count=0;
			while (count<100000 ) {//time <30
				P = P.getOffSpring_NSGA();
				//t2=System.currentTimeMillis();
				count+=2*project.getNSGA_II().populationSize;
				//time=(t2-startTime)/1000;
			}
			long endTime = System.currentTimeMillis();
			//从最后得到种群中获取最优解集
			Population solutions = Tools.getbestsolution(P, 1,project);
			Population solutionsD=Tools.removeSame(solutions);
		    //输出最优解集
			//Tools.printsolutions(solutions,startTime,datafile);	
			 File f = new File(datafile); 
			 PrintStream ps = null;
			 try {
			   if (f.exists()) f.delete();
			   f.createNewFile();
			   FileOutputStream fos = new FileOutputStream(f);
			   ps = new PrintStream(fos);
			   //输出最优解集
			   Tools.printsolutions(solutionsD,endTime-startTime,countResult,ps);			   
			 } catch (IOException e) {
				e.printStackTrace();
			 }  finally {
		        if(ps != null) 	ps.close();
		     }        	
	}
	public  void NSFFA_algorithm(String casefile,String datafile, List<List<Double>> countResult){
	       //记录开始计算的时间，用于统计本算法的总时间
			long startTime = System.currentTimeMillis();
			// 创建案例类对象
			Case project = new Case(casefile);
			// 初始化种群
			Population P = new Population(NSFFA.NS,project,true);
			int generationCount = 0;
	        //循环迭代 算法指定的次数
			while (generationCount < NSFFA.maxGenerations ) {
				P = P.getOffSpring_NSFFA();
				generationCount++;
			}
			//从最后得到种群中获取最优解集
			Population solutions = Tools.getbestsolution(P,1, project);
			File f = new File(datafile);
			PrintStream ps = null;
			try {
			   if (f.exists()) f.delete();
			   f.createNewFile();
			   FileOutputStream fos = new FileOutputStream(f);
			   ps = new PrintStream(fos);
			   /*System.setOut(ps);*/
			   //输出最优解集
			   Tools.printsolutions(solutions,startTime,countResult,ps);			   
			 } catch (IOException e) {
				e.printStackTrace();
			 }  finally {
		        if(ps != null) 	ps.close();
		     }
	}
	/*
	 * 将pareto组成种群
	 */
	private Population combinePop(Case project,List<Individual>... indivs) {
		
		int newSize=0;
		for(int i=0;i<indivs.length;i++) {
			newSize+=indivs[i].size();
		}
		Individual[] combines=new Individual[newSize];
		int total=0;
		for(int i=0;i<indivs.length;i++) {
			List<Individual> indivL=indivs[i];
			for(int j=0;j<indivL.size();j++) {
				combines[total++]=indivL.get(j);
			}
		}
		Population mergedPopulation=new Population(newSize,project);
		mergedPopulation.setPopulation(combines);
		return mergedPopulation;
	}
}
