package genetic_algorithm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class NumericalTest {
	public static void main(String[] args) {
		/*
        File ff = new File("case_def");
        String[] fl = ff.list(); 
        if (fl==null){
        	System.out.print("没有在case_def目录下找到算例文件");
        	return;           	
        }
        for(int i = 0; i<fl.length; i++){
       	 String _fn =  "case_def/" + fl[i];
       	 String _fo = "data/NSFFA_"+fl[i]+".txt";
       	 NSFFA_algorithm(_fn,_fo);
       }
       System.out.println(fl.length +"个案例果蝇算法计算完成"); 
       return;
		*/
		
        if (args.length==1){
            File ff = new File("case_def");
            //获取案例库case_def文件目录
            String[] fl = ff.list(); 
            if (fl==null){
            	System.out.print("没有在case_def目录下找到算例文件");
            	return;           	
            }
    		if (args[0].trim().toLowerCase().equals("g")){
    			for(int j = 0; j < 10; j++){
    				for(int i = 0; i<fl.length; i++){
                   	 String _fn =  "case_def/" + fl[i];
                   	 String _fo = "data/numerical/learn"+j+"/NSGA_"+fl[i]+".txt";
                   	 NSGA_algorithm(_fn,_fo);
                   }
    			}
                System.out.println(fl.length +"个案例遗传算法计算完成"); 
                return;
                
    		}
    		if (args[0].trim().toLowerCase().equals("f")){
                for(int i = 0; i<fl.length; i++){
                	 String _fn =  "case_def/" + fl[i];
                	 String _fo = "data/NSFFA_"+fl[i]+".txt";
                	 NSFFA_algorithm(_fn,_fo);
                }
                System.out.println(fl.length +"个案例果蝇算法计算完成"); 
                return;
    		}
    		if (args[0].trim().toLowerCase().equals("t")){
    			for(int j = 0; j < 10; j++){
                for(int i = 0; i<fl.length; i++){
                	 String _fn =  "case_def/" + fl[i];
                	 String _fo = "data/numerical/tlbo"+j+"/TLBO_"+fl[i]+".txt";
                	 TLBO_algorithm(_fn,_fo);
                }
    			}
                System.out.println(fl.length +"个案例教学优化算法计算完成"); 
                return;
    		}

        }else{
        	System.out.print("请输入参数：'g'、遗传算法，'f'、果蝇算法，'C'、布谷鸟算法");
        	return;
        }

	}
	
	public static void TLBO_algorithm(String casefile, String datafile) {
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
		Population solutions = Tools.getbestsolution(P, project);		
		
		 File f = new File(datafile);
		 PrintStream ps = null;
		 try {
		   if (f.exists()) f.delete();
		   f.createNewFile();
		   FileOutputStream fos = new FileOutputStream(f);
		   ps = new PrintStream(fos);
		   System.setOut(ps);
		   //输出最优解集
		   Tools.printsolutions(solutions,startTime);			   
		 } catch (IOException e) {
			e.printStackTrace();
		 }  finally {
	        if(ps != null) 	ps.close();
	     }
	}
	
	/*@param caseFile:案例读取目录
	 * @param dataFile:案例计算结果写入目录*/
	public static void NSGA_algorithm(String casefile,String datafile){
	       //记录开始计算的时间，用于统计本算法的总时间
			long startTime = System.currentTimeMillis();
			// 创建案例类对象
			Case project = new Case(casefile);

			// 初始化种群
			Population P = new Population(NSGA_II.populationSize,project,true);
			
			int generationCount = 0;
	        //循环迭代 算法指定的次数
			while (generationCount < NSGA_II.maxGenerations ) {

				P = P.getOffSpring_NSGA();

				generationCount++;
			}
			//从最后得到种群中获取最优解集
			Population solutions = Tools.getbestsolution(P, project);
			
		    //输出最优解集
			//Tools.printsolutions(solutions,startTime,datafile);		
			
			
			 File f = new File(datafile);
			 PrintStream ps = null;
			 try {
			   if (f.exists()) f.delete();
			   f.createNewFile();
			   FileOutputStream fos = new FileOutputStream(f);
			   ps = new PrintStream(fos);
			   System.setOut(ps);
			   //输出最优解集
			   Tools.printsolutions(solutions,startTime);			   
			 } catch (IOException e) {
				e.printStackTrace();
			 }  finally {
		        if(ps != null) 	ps.close();
		     }
		    
		          	
	}
	//教学算法用于操作序列搜索  使用果蝇算法用于资源序列搜索
	public static void TLBO_F_algorithm(String casefile, String datafile) {
		 //记录开始计算的时间，用于统计本算法的总时间
		long startTime = System.currentTimeMillis();
		// 创建案例类对象
		Case project = new Case(casefile);

		// 初始化种群
		Population P = new Population(TLBO.populationSize,project,true);
		
		int generationCount = 0;
		//List<Integer> best=P.getPopulation().
       //循环迭代 算法指定的次数
		while (generationCount < TLBO.maxGenerations ) {

			P = P.getOffSpring_TLBO_F();
			

			generationCount++;
		}
		//从最后得到种群中获取最优解集
		Population solutions = Tools.getbestsolution(P, project);		
		
		 File f = new File(datafile);
		 PrintStream ps = null;
		 try {
		   if (f.exists()) f.delete();
		   f.createNewFile();
		   FileOutputStream fos = new FileOutputStream(f);
		   ps = new PrintStream(fos);
		   System.setOut(ps);
		   //输出最优解集
		   Tools.printsolutions(solutions,startTime);			   
		 } catch (IOException e) {
			e.printStackTrace();
		 }  finally {
	        if(ps != null) 	ps.close();
	     }
	}
	public static void NSFFA_algorithm(String casefile,String datafile){
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
			Population solutions = Tools.getbestsolution(P, project);
			
	
			 File f = new File(datafile);
			 PrintStream ps = null;
			 try {
			   if (f.exists()) f.delete();
			   f.createNewFile();
			   FileOutputStream fos = new FileOutputStream(f);
			   ps = new PrintStream(fos);
			   System.setOut(ps);
			   //输出最优解集
			   Tools.printsolutions(solutions,startTime);			   
			 } catch (IOException e) {
				e.printStackTrace();
			 }  finally {
		        if(ps != null) 	ps.close();
		     }
	}
}
