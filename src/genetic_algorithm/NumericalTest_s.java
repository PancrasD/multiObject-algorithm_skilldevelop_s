package genetic_algorithm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NumericalTest_s {
	public static void main(String[] args) {
        if (args.length==1){
            File ff = new File("case_def");
            //获取案例库case_def文件目录
            String[] fl = ff.list(); 
            List<List<Double>> countResult=new ArrayList<>();
            if (fl==null){
            	System.out.print("没有在case_def目录下找到算例文件");
            	return;           	
            }
    		if (args[0].trim().toLowerCase().equals("g")){
    			String head=buildFileName();
    			for(int j = 0; j < NSGA_II.RunTime; j++){
    				 String dic = "data/NSGA_"+head+"/nsga_"+j;
	   				 File f=new File(dic);
	               	 if(f.exists()) {
	               		 f.delete();
	               	 }
	               	 f.mkdirs();
    				for(int i = 0; i<fl.length; i++){
                   	 String _fn =  "case_def/" + fl[i];
                   	 String _fo = dic+"/NSGA_"+fl[i]+".txt";
                   	 NSGA_algorithm(_fn,_fo,countResult);
                   }
    			}
                System.out.println(fl.length +"个案例遗传算法计算完成"); 
                return;
                
    		}
    		if (args[0].trim().toLowerCase().equals("f")){
    			String head=buildFileName();
    			for(int j = 0; j < NSFFA.RunTime; j++){
    				 String dic = "data/NSFFA_"+head+"/nsffa_"+j;
	   				 File f=new File(dic);
	               	 if(f.exists()) {
	               		 f.delete();
	               	 }
	               	 f.mkdirs();
    				for(int i = 0; i<fl.length; i++){
                   	 String _fn =  "case_def/" + fl[i];
                   	 String _fo = dic+"/nsffa_"+fl[i]+".txt";
                   	 NSFFA_algorithm(_fn,_fo,countResult);
                   }
    			}
                System.out.println(fl.length +"个案例果蝇算法计算完成"); 
                return;
                
    		}
    		if (args[0].trim().toLowerCase().equals("t")){
    			String head=buildFileName();
    			for(int j = 0; j < 10; j++){
    				String dic = "data/TLBO_"+head+"/tlbo"+j;
	   				 File f=new File(dic);
	               	 if(f.exists()) {
	               		 f.delete();
	               	 }
	               	 f.mkdirs();
                for(int i = 0; i<fl.length; i++){
                	 String _fn =  "case_def/" + fl[i];
                	 String _fo = "data/TLBO_"+head+"/tlbo"+j+"/TLBO_L_"+fl[i]+".txt";
                	 TLBO_algorithm(_fn,_fo,countResult);
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
	
	public static void TLBO_algorithm(String casefile, String datafile,List<List<Double>> countResult) {
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
		   Tools.printsolutions(solutions,startTime,countResult);			   
		 } catch (IOException e) {
			e.printStackTrace();
		 }  finally {
	        if(ps != null) 	ps.close();
	     }
	}
	
	/*@param caseFile:案例读取目录
	 * @param dataFile:案例计算结果写入目录*/
	public static void NSGA_algorithm(String casefile,String datafile,List<List<Double>> countResult){
	       //记录开始计算的时间，用于统计本算法的总时间
			long startTime = System.currentTimeMillis();
			// 创建案例类对象
			Case project = new Case(casefile);
			// 初始化种群
			Population P = new Population(NSGA_II.populationSize,project,true);
			int generationCount = 0;
	        //循环迭代 算法指定的次数
			/*while (generationCount < NSGA_II.maxGenerations ) {

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
			   Tools.printsolutions(solutions,startTime,countResult);			   
			 } catch (IOException e) {
				e.printStackTrace();
			 }  finally {
		        if(ps != null) 	ps.close();
		     }
		    
		          	
	}
	//教学算法用于操作序列搜索  使用果蝇算法用于资源序列搜索
	public static void TLBO_F_algorithm(String casefile, String datafile,List<List<Double>> countResult) {
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
		   Tools.printsolutions(solutions,startTime,countResult);			   
		 } catch (IOException e) {
			e.printStackTrace();
		 }  finally {
	        if(ps != null) 	ps.close();
	     }
	}
	public static void NSFFA_algorithm(String casefile,String datafile,List<List<Double>> countResult){
	       //记录开始计算的时间，用于统计本算法的总时间
			long startTime = System.currentTimeMillis();
			// 创建案例类对象
			Case project = new Case(casefile);

			// 初始化种群
			Population P = new Population(NSFFA.NS,project,true);
			
			/*int generationCount = 0;
	        //循环迭代 算法指定的次数
			while (generationCount < NSFFA.maxGenerations ) {

				P = P.getOffSpring_NSFFA();

				generationCount++;
			}*/
			long time=0;
			long t2 = 0;
			while (time < 30 ) {
				P = P.getOffSpring_NSFFA();
				t2=System.currentTimeMillis();
				time=(t2-startTime)/1000;
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
			   Tools.printsolutions(solutions,startTime,countResult);		
			   Tools.printSkill(solutions);
			} catch (IOException e) {
				e.printStackTrace();
			}  finally {
		        if(ps != null) 	ps.close();
		    }
	}
	 public static String buildFileName()
	 {
		 //new一个时间对象date
		 Date date = new Date();
	     //格式化
		 SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmm");
	     //格式化时间，并且作为文件名
		 return sdf.format(date);
	 }
}
