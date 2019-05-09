package newModel.doubleAdjust.run;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import newModel.doubleAdjust.basis.Parameter;


public class NumericalTest_Run {

	public  void runNumerical(String args,Parameter para) {
		    String case_def="case_def";
            File ff = new File(case_def);
            //获取案例库case_def文件目录
            String[] fl = ff.list(); 
            if (fl==null){
            	System.out.print("没有在case_def目录下找到算例文件");
            	return;           	
            }
            ConcurrentHashMap<String, List<List<Double>>> countR=new ConcurrentHashMap<String,List<List<Double>>>();
            ExecutorService exec=Executors.newFixedThreadPool(2);
			String head=buildFileName();
			for(int j = 0; j < para.getRunTime(); j++){
				String up_prefix=args.trim().toUpperCase();
				String low_prefix=args.trim().toLowerCase();
				String dic = "data1.0/"+up_prefix+"_"+head+"_"+ Thread.currentThread().getName()+"/"+low_prefix+"_"+j;
   				File f=new File(dic);
               	if(f.exists()) {
               		 f.delete();
               	}
               	f.mkdirs();
               	AlgorithmRun algorithm=new AlgorithmRun(fl, dic, case_def, countR, para, args);
               	
               exec.execute(algorithm);
			}
			exec.shutdown();
            return;
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
