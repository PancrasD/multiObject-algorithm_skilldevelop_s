package tlbo_task_ff_res;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NumericalTest_tlbof {
	public static void main(String[] args) {
		
		
        if (args.length==1){
            File ff = new File("case_def");
            //获取案例库case_def文件目录
            String[] fl = ff.list(); 
            if (fl==null){
            	System.out.print("没有在case_def目录下找到算例文件");
            	return;           	
            }
    		if (args[0].trim().toLowerCase().equals("g")){
    			String head=buildFileName();
    			for(int j = 0; j < 10; j++){
	    			 String dic = "data/NSGA"+head+"/nsga"+j;
	   				 File f=new File(dic);
	               	 if(f.exists()) {
	               		 f.delete();
	               	 }
	               	 f.mkdirs();
    				for(int i = 0; i<fl.length; i++){
                   	 String _fn =  "case_def/" + fl[i];
                   	 String _fo = "data/NSGA"+head+"/nsga"+j+"/NSGA_"+fl[i]+".txt";
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
                	 String _fo = "data/TLBO/tlbo"+j+"/TLBO_"+fl[i]+".txt";
                	 TLBO_algorithm(_fn,_fo);
                }
    			}
                System.out.println(fl.length +"个案例教学优化算法计算完成"); 
                return;
    		}
    		if (args[0].trim().toLowerCase().equals("tf")){
    			String head=buildFileName();
    			for(int j = 0; j < 10; j++){
    				 String dic = "data/TLBO_F"+head+"/tlbo_f"+j;
    				 File f=new File(dic);
                	 if(f.exists()) {
                		 f.delete();
                	 }
                	 f.mkdirs();
	                for(int i = 0; i<fl.length; i++){
	                	 String _fn =  "case_def/" + fl[i];
	                	 String _fo = "data/TLBO_F"+head+"/tlbo_f"+j+"/TLBO_F_"+fl[i]+".txt";
	                	 TLBO_F_algorithm(_fn,_fo);
	                }
	    			}
	                System.out.println(fl.length +"个案例教学优化果蝇算法计算完成"); 
	                return;
    		}
    		if (args[0].trim().toLowerCase().equals("tg")){
    			for(int j = 0; j < 10; j++){
    				long time=System.currentTimeMillis();
    				 String dic = "data/both"+time+"/both_"+j;
    				 File f=new File(dic);
                	 if(f.exists()) {
                		 f.delete();
                	 }
                	 f.mkdirs();
	                for(int i = 0; i<fl.length; i++){
	                	 String _fn =  "case_def/" + fl[i];
	                	 String _fo = "data/both"+time+"/both_"+j+"/both_"+fl[i]+".txt";
	                	 both_algorithm(_fn,_fo);
	                }
	    			}
	                System.out.println(fl.length +"个遗传教学计算完成"); 
	                return;
    		}

        }else{
        	System.out.print("请输入参数：'g'、遗传算法，'f'、果蝇算法，'C'、布谷鸟算法");
        	return;
        }

	}
	
	private static void both_algorithm(String casefile, String datafile) {
		 //记录开始计算的时间，用于统计本算法的总时间
		long startTime = System.currentTimeMillis();
		// 创建案例类对象
		Case project = new Case(casefile);

		// 初始化种群
		Population P = new Population(TLBOF.populationSize,project,true);
		
		int generationCount = 0;
		//List<Integer> best=P.getPopulation().
       //循环迭代 算法指定的次数
		while (generationCount < TLBOF.maxGenerations ) {

			Population p1 = P.getOffSpring_TLBO_F();
			Population p2=P.getOffSpring_NSGA();
			P=Tools.selectPopulation(p1,p2);
			generationCount++;
		}
		//获取前两层的精英个体的操作序列 进行资源搜索 种群扩充 以增加最优种群的多样性
		//Population elite = Tools.getbestsolution(P,2,project);	
		Population expandPop=P.serchMoreSpaceByRes(TLBOF.s1);
		//从最后得到种群中获取最优解集
		Population solutions = Tools.getbestsolution(expandPop,1, project);		
		
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
		Population solutions = Tools.getbestsolution(P,1, project);		
		
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
			Population solutions = Tools.getbestsolution(P,1, project);
			
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
		Population P = new Population(TLBOF.populationSize,project,true);
		
		int generationCount = 0;
		//List<Integer> best=P.getPopulation().
       //循环迭代 算法指定的次数
		while (generationCount < TLBOF.maxGenerations ) {

			P = P.getOffSpring_TLBO_F();

			generationCount++;
		}
		//获取前两层的精英个体的操作序列 进行资源搜索 种群扩充 以增加最优种群的多样性
		//Population elite = Tools.getbestsolution(P,2,project);	
		Population expandPop=P.serchMoreSpaceByRes(TLBOF.s1);
		//从最后得到种群中获取最优解集
		Population solutions = Tools.getbestsolution(expandPop,1, project);		
		
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
			Population solutions = Tools.getbestsolution(P,1, project);
			
	
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

	 public static String buildFileName()
	 {
		 //new一个时间对象date
		 Date date = new Date();
	 
	     //格式化
		 SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
	     //格式化时间，并且作为文件名
		 return sdf.format(date);
	 }
}
