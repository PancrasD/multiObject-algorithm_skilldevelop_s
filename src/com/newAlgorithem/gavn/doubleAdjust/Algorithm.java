package com.newAlgorithem.gavn.doubleAdjust;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;



public class Algorithm implements Runnable{
	String[] fl;
	ConcurrentHashMap<String, List<List<Double>>> countR;
	Case para;
	String arg;
	String dic;
	String case_def;
	public Algorithm(String[] fl,String dic,String case_def,ConcurrentHashMap<String, List<List<Double>>> countR,Case para,String arg) {
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
               	NSGAV_D_algorithm(_fn,_fo,countResult,para);
           }
		}else if(arg.equals("g")) {
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
		}else if(arg.equals("h")) {
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
           	 String _fo = "data/NSFFA_"+fl[i]+".txt";
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
				P1 = P1.getOffSpring_NSGAV(0);
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
	/*@param caseFile:案例读取目录
	 * @param dataFile:案例计算结果写入目录
	 */
	@SuppressWarnings("unchecked")
	public  void NSGAV_D_algorithm(String casefile,String datafile, List<List<Double>> countResult,Case para){
	       //记录开始计算的时间，用于统计本算法的总时间
			long startTime = System.currentTimeMillis();
			// 创建案例类对象
			Case project = new Case(casefile);
			project.setNSGAV_II(para.getNSGAV_II());
			project.setRunTime(para.RunTime);
			// 初始化种群
			Population P1 = new Population(project.getNSGAV_II().populationSize,project,true);
			/*Population P2 = new Population(project.getNSGAV_II().populationSize,project,true);*/
			/*Population P3 = new Population(project.getNSGAV_II().populationSize,project,true);
			Population P4 = new Population(project.getNSGAV_II().populationSize,project,true);
			Population P5 = new Population(project.getNSGAV_II().populationSize,project,true);*/
			/*Population P6 = new Population(project.getNSGAV_II().populationSize,project,true);*/
			
			/*int generationCount = 0; 
	        //循环迭代 算法指定的次  ,数
			while (generationCount < NSGAV_II.maxGenerations ) {
				P = P.getOffSpring_NSGAV();
				generationCount++;
			}*/
			long time=0;
			long t2 = 0;
			while (time < 30 ) {
				P1 = P1.getOffSpring_V(1);
				/*P1 = P1.getOffSpring_V(2);
				P1 = P1.getOffSpring_V(3);
				P1 = P1.getOffSpring_V(4);
				P1 = P1.getOffSpring_V(5);*/
				/*P1 = P1.getOffSpring_V(5);*/
				/*P3 = P3.getOffSpring_V(3);
				P4 = P4.getOffSpring_V(4);
				P5 = P5.getOffSpring_V(5);*/
				/*P6=P6.getOffSpring_NSGAV_Extreme();*/
				/*List<Individual> indivs1=P1.getpareto();
				List<Individual> indivs2=P2.getpareto();*/
				/*List<Individual> indivs3=P3.getpareto();
				List<Individual> indivs4=P4.getpareto();
				List<Individual> indivs5=P5.getpareto();*/
				/*Population paretoPop=combinePop(project,indivs1,indivs2);
				Population paretoPopPop=paretoPop.getOffSpring_Pareto();*/
				/*Population p=Tools.getbestsolution(paretoPopPop,1, project);*/
				/*List<Individual> indivs6=P6.getpareto();*/
				/*P1=P1.addPareto(paretoPopPop);
				P2=P2.addPareto(paretoPopPop);*/
				/*P3=P3.addPareto(paretoPopPop);
				P4=P4.addPareto(paretoPopPop);
				P5=P5.addPareto(paretoPopPop);*/
				/*P6=P6.addPareto(indivs1,indivs2,indivs3,indivs4,indivs5);*/
				/*P1=P1.addPareto(indivs2);
				P2=P2.addPareto(indivs1);*/
				t2=System.currentTimeMillis();
				time=(t2-startTime)/1000;
			}
			//从最后得到种群中获取最优解集
			//Population solutions =Tools.getbestsolution(P,project, 0);//输出rank为0  减少一次非支配排序
			// 将两个种群合并
			/*Population mergedPopulation =P1.merged(P1,P2);
			mergedPopulation=mergedPopulation.slectPopulationC(P1.getPopulationsize(), false);*/
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

	public  void NSGA_algorithm(String casefile,String datafile,List<List<Double>> countResult,Case para){
	       //记录开始计算的时间，用于统计本算法的总时间
			long startTime = System.currentTimeMillis();
			// 创建案例类对象
			Case project = new Case(casefile);
			project.setNSGA_II(para.getNSGA_II());//赋予参数
			project.setRunTime(para.RunTime);
			// 初始化种群
			Population P = new Population(project.getNSGA_II().populationSize,project,true);
			/*int generationCount = 0;
	        //循环迭代 算法指定的次数
			while (generationCount < NSGA_II.maxGenerations ) {
				P = P.getOffSpring_NSGA();
				generationCount++;
			}*/
			long time=0;
			long t2 = 0;
			while (time <30 ) {
				P = P.getOffSpring_NSGA();
				t2=System.currentTimeMillis();
				time=(t2-startTime)/1000;
			}
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
			   Tools.printsolutions(solutionsD,startTime,countResult,ps);			   
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
