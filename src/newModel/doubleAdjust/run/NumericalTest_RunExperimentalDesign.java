package newModel.doubleAdjust.run;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import newModel.doubleAdjust.Individual;
import newModel.doubleAdjust.NSFFA;
import newModel.doubleAdjust.Population;
import newModel.doubleAdjust.TLBO;
import newModel.doubleAdjust.Tools;
import newModel.doubleAdjust.basis.Case;


public class NumericalTest_RunExperimentalDesign {

	public  void runNumerical(String args,Case para,String head) {
		    String case_def="case_def";
            File ff = new File(case_def);
            //获取案例库case_def文件目录
            String[] fl = ff.list(); 
            if (fl==null){
            	System.out.print("没有在case_def目录下找到算例文件");
            	return;           	
            }
            HashMap<String, List<List<Double>>> countR=new HashMap<String,List<List<Double>>>();
    		if (args.trim().toLowerCase().equals("gv")){
    			
    			for(int j = 0; j < para.getNSGAV_II().RunTime; j++){
    				String dic = "data1.0/GAV_experiment/"+head+"_"+ Thread.currentThread().getName()+"/gav_"+j;
    				File f=new File(dic);
	               	if(f.exists()) {
	               		 f.delete();
	               	}
	               	f.mkdirs();
    				for(int i = 0; i<fl.length; i++){
						List<List<Double>>countResult=countR.get(fl[i]);
						if(countResult==null) {
							countResult=new ArrayList<>();
							countR.put(fl[i], countResult);
						}
		               	String _fn =  "case_def/" + fl[i];
		               	String _fo = dic+"//NSGAVH_"+fl[i]+".txt";
		               	//NSGAV_D_algorithm(_fn,_fo,countResult,para);
		               	NSGAV_algorithm(_fn,_fo,countResult,para);
                   }
    			}
                System.out.println(fl.length +"个案例遗传变邻算法计算完成"); 
                return;
                
    		}else if (args.trim().toLowerCase().equals("g")){
    			for(int j = 0; j < para.getNSGA_II().RunTime; j++){
   				 String dic = "data1.0/NSGA_experiment/"+head+"_"+ Thread.currentThread().getName()+"/nsga_"+j;
	   				 File f=new File(dic);
	               	 if(f.exists()) {
	               		 f.delete();
	               	 }
	               	 f.mkdirs();
   				for(int i = 0; i<fl.length; i++){
                  	 String _fn =  "case_def/" + fl[i];
                  	 String _fo = dic+"/NSGA_"+fl[i]+".txt";
                  	List<List<Double>>countResult=countR.get(fl[i]);
					if(countResult==null) {
						countResult=new ArrayList<>();
						countR.put(fl[i], countResult);
					}
                  	 NSGA_algorithm(_fn,_fo,countResult,para);
                  }
   			}
         }
    		
    		if (args.trim().toLowerCase().equals("f")){
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
                System.out.println(fl.length +"个案例果蝇算法计算完成"); 
                return;
    		}
    		if (args.trim().toLowerCase().equals("t")){
    			for(int j = 0; j < 10; j++){
                for(int i = 0; i<fl.length; i++){
                	 String _fn =  "case_def/" + fl[i];
                	 String _fo = "data/TLBO/tlbo"+j+"/TLBO_"+fl[i]+".txt";
                	 List<List<Double>>countResult=countR.get(fl[i]);
						if(countResult==null) {
							countResult=new ArrayList<>();
							countR.put(fl[i], countResult);
						}
                	 TLBO_algorithm(_fn,_fo,countResult);
                }
    			}
                System.out.println(fl.length +"个案例教学优化算法计算完成"); 
                return;
    		}else{
        	System.out.print("请输入参数：'g'、遗传算法，'f'、果蝇算法，'C'、布谷鸟算法");
        	return;
         }
        }
	public  void TLBO_algorithm(String casefile, String datafile, List<List<Double>> countResult) {
		 //记录开始计算的时间，用于统计本算法的总时间
		long startTime = System.currentTimeMillis();
		// 创建案例类对象
		Case project = new Case(casefile);
		// 初始化种群
		Population P = new Population(TLBO.populationSize,project,true);
		int generationCount = 0;
        //循环迭代 算法指定的次数
	 	while (generationCount < TLBO.maxGenerations ) {
			P = P.getOffSpring_TLBO();
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
		  /* System.setOut(ps);*/
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
	public  void NSGAV_algorithm(String casefile,String datafile, List<List<Double>> countResult,Case para){
	       //记录开始计算的时间，用于统计本算法的总时间
			long startTime = System.currentTimeMillis();
			// 创建案例类对象
			Case project = new Case(casefile);
			project.setNSGAV_II(para.getNSGAV_II());
			project.setRunTime(para.getRunTime());
			// 初始化种群
			Population P = new Population(project.getNSGAV_II().populationSize,project,true);
			
			/*int generationCount = 0; 
	        //循环迭代 算法指定的次  ,数
			while (generationCount < NSGAV_II.maxGenerations ) {
				P = P.getOffSpring_NSGAV();
				generationCount++;
			}*/
			long time=0;
			long t2 = 0;
			while (time < 30 ) {
				P = P.getOffSpring_NSGAV();
				t2=System.currentTimeMillis();
				time=(t2-startTime)/1000;
			}
			//从最后得到种群中获取最优解集
			//Population solutions =Tools.getbestsolution(P,project, 0);//输出rank为0  减少一次非支配排序
			Population solutions = Tools.getbestsolution(P,1, project);
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
			// 创建案例类对象
			Case project = new Case(casefile);
			project.setNSGAV_II(para.getNSGAV_II());
			project.setRunTime(para.getRunTime());
			// 初始化种群
			Case project1=new Case(project);
			long startTime = System.currentTimeMillis();
			Population P1 = new Population(project.getNSGAV_II().populationSize,project,true);
			Population P2 = new Population(project1.getNSGAV_II().populationSize,project1,true);/*
			Population P3 = new Population(project.getNSGAV_II().populationSize,project,true);
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
			int count=0;
			while (count < 100000 ) {
				int k1=(int) (Math.random()*5);
				int k2=(int) (Math.random()*5);
				P1 = P1.getOffSpring_V(k1);//先去掉k 作为先导V
				P2 = P2.getOffSpring_V(k2);
				/*P3 = P3.getOffSpring_NSGAV(3);
				P4 = P4.getOffSpring_NSGAV(4);
				P5 = P5.getOffSpring_NSGAV(5);*/
				/*P6=P6.getOffSpring_NSGAV_Extreme();*/
				List<Individual> indivs1=P1.getpareto();
				/*List<Individual> indivs2=P2.getpareto();
				List<Individual> indivs3=P3.getpareto();
				List<Individual> indivs4=P4.getpareto();
				List<Individual> indivs5=P5.getpareto();*/
				List<Individual> indivs2=P2.getpareto();
				/*P1=P1.addPareto(indivs2,indivs3,indivs4,indivs5,indivs6);
				P2=P2.addPareto(indivs1,indivs3,indivs4,indivs5,indivs6);
				P3=P3.addPareto(indivs1,indivs2,indivs4,indivs5,indivs6);
				P4=P4.addPareto(indivs1,indivs2,indivs3,indivs5,indivs6);
				P5=P5.addPareto(indivs1,indivs2,indivs3,indivs4,indivs6);
				P6=P6.addPareto(indivs1,indivs2,indivs3,indivs4,indivs5);*/
				P1=P1.addPareto(indivs2);
				P2=P2.addPareto(indivs1);
				count+=(P1.getPopulationsize()*2+P2.getPopulationsize()*2+project.getCount()+project1.getCount());
				project.setCount(0);
				project1.setCount(0);
				//t2=System.currentTimeMillis();
				//time=(t2-startTime)/1000;
			}
			//从最后得到种群中获取最优解集
			//Population solutions =Tools.getbestsolution(P,project, 0);//输出rank为0  减少一次非支配排序
			// 将两个种群合并
			//Population mergedPopulation =P1.merged(P1,P2);
			long endTime = System.currentTimeMillis();
			Population mergedPopulation =Tools.merged(P1,P2);
			//mergedPopulation=mergedPopulation.slectPopulation(P1.getPopulationsize());
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
	public  void NSGA_algorithm(String casefile,String datafile,List<List<Double>> countResult,Case para){
	       //记录开始计算的时间，用于统计本算法的总时间
			
			// 创建案例类对象
			Case project = new Case(casefile);
			project.setNSGA_II(para.getNSGA_II());//赋予参数
			project.setRunTime(para.getRunTime());
			// 初始化种群
			long startTime = System.currentTimeMillis();
			Population P = new Population(project.getNSGA_II().populationSize,project,true);
			/*int generationCount = 0;
	        //循环迭代 算法指定的次数
			while (generationCount < NSGA_II.maxGenerations ) {
				P = P.getOffSpring_NSGA();
				generationCount++;
			}*/
			int count=0;
			while (count<100000 ) {//time <30
				P = P.getOffSpring_NSGA();
				count+=2*project.getNSGA_II().populationSize;
//				t2=System.currentTimeMillis();
//				time=(t2-startTime)/1000;
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
	@SuppressWarnings("unchecked")
	public  void NSGAV_DD_algorithm(String casefile,String datafile, List<List<Double>> countResult,Case para){
	       //记录开始计算的时间，用于统计本算法的总时间
			long startTime = System.currentTimeMillis();
			// 创建案例类对象
			Case project = new Case(casefile);
			project.setNSGAV_II(para.getNSGAV_II());
			project.setRunTime(para.getRunTime());
			// 初始化种群
			Population P1 = new Population(project.getNSGAV_II().populationSize,project,true);
			Population P2 = new Population(project.getNSGAV_II().populationSize,project,true);
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
				int k1=(int) (Math.random()*5+1);
				int k2=(int) (Math.random()*5+1);
				P1 = P1.getOffSpring_V(k1);
				P2 = P2.getOffSpring_V(k2);
				/*P3 = P3.getOffSpring_V(3);
				P4 = P4.getOffSpring_V(4);
				P5 = P5.getOffSpring_V(5);*/
				/*P6=P6.getOffSpring_NSGAV_Extreme();*/
				List<Individual> indivs1=P1.getpareto();
				List<Individual> indivs2=P2.getpareto();
				/*List<Individual> indivs3=P3.getpareto();
				List<Individual> indivs4=P4.getpareto();
				List<Individual> indivs5=P5.getpareto();*/
				Population paretoPop=combinePop(project,indivs1,indivs2);
				Population paretoPopPop=paretoPop.getOffSpring_Pareto();
				/*Population p=Tools.getbestsolution(paretoPopPop,1, project);*/
				/*List<Individual> indivs6=P6.getpareto();*/
				P1=P1.addPareto(paretoPopPop);
				P2=P2.addPareto(paretoPopPop);
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
			Population mergedPopulation =P1.merged(P1,P2);
			mergedPopulation=mergedPopulation.slectPopulationC(P1.getPopulationsize(), false);
			Population solutions = Tools.getbestsolution(mergedPopulation,1, project);
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
