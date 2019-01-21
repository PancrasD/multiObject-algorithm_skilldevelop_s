package com.newAlgorithem.gavn.doubleAdjust;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class NumericalTest_Run {

	public  void runNumerical(String args,Case para) {
		    String case_def="case_def";
            File ff = new File(case_def);
            //获取案例库case_def文件目录
            String[] fl = ff.list(); 
            if (fl==null){
            	System.out.print("没有在case_def目录下找到算例文件");
            	return;           	
            }
            ConcurrentHashMap<String, List<List<Double>>> countR=new ConcurrentHashMap<String,List<List<Double>>>();
    		if (args.trim().toLowerCase().equals("gv")){
    			ExecutorService exec=Executors.newFixedThreadPool(5);
    			String head=buildFileName();
    			for(int j = 0; j < para.getNSGAV_II().RunTime; j++){
    				String dic = "data/NSGAV_H"+head+"_"+ Thread.currentThread().getName()+"/nsgah_"+j;
	   				File f=new File(dic);
	               	if(f.exists()) {
	               		 f.delete();
	               	}
	               	f.mkdirs();
	               	Algorithm algorithm=new Algorithm(fl, dic, case_def, countR, para, args);
	               	/*Thread thread =new Thread(algorithm);
	               	thread.start();;*/
	               exec.execute(algorithm);
    			}
    			exec.shutdown();
                /*System.out.println(fl.length +"个案例遗传变邻算法计算完成"); */
                return;
                
    		}
    		if (args.trim().toLowerCase().equals("f")){
    			String head=buildFileName();
    			ExecutorService exec=Executors.newFixedThreadPool(5);
    			for(int j = 0; j < para.getNSFFA().RunTime; j++){
    				 String dic = "data/NSFFA_"+head+"_"+ Thread.currentThread().getName()+"/nsffa_"+j;
	   				 File f=new File(dic);
	               	 if(f.exists()) {
	               		 f.delete();
	               	 }
	               	 f.mkdirs();
	               	 Algorithm algorithm=new Algorithm(fl, dic, case_def, countR, para, args);
	               	 exec.execute(algorithm);
    			}
    			exec.shutdown();
                /*System.out.println(fl.length +"个案例果蝇算法计算完成"); */
                return;
    		}
    		if (args.trim().toLowerCase().equals("g")){
    			String head=buildFileName();
    			ExecutorService exec=Executors.newFixedThreadPool(5);
    			for(int j = 0; j < para.getNSGA_II().RunTime; j++){
    				 String dic = "data/NSGA_"+head+"_"+ Thread.currentThread().getName()+"/nsga_"+j;
	   				 File f=new File(dic);
	               	 if(f.exists()) {
	               		 f.delete();
	               	 }
	               	 f.mkdirs();
	               	 Algorithm algorithm=new Algorithm(fl, dic, case_def, countR, para, args);
	               	 exec.execute(algorithm);
    			}
    			exec.shutdown();
    			/*System.out.println(fl.length +"个遗传算法计算完成"); */
    			return;
          }else{
        	System.out.print("请输入参数：'g'、遗传算法，'f'、果蝇算法，'C'、布谷鸟算法");
        	return;
            }
        }
	 public  String buildFileName(){
		 //new一个时间对象date
		 Date date = new Date();
	     //格式化
		 SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmmss");
	     //格式化时间，并且作为文件名
		 return sdf.format(date);
	 }
}
