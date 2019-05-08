package newModel.doubleAdjust.run;

import newModel.doubleAdjust.NSGAV_II;
import newModel.doubleAdjust.NSGA_II;
import newModel.doubleAdjust.algorithm.NTGA;
import newModel.doubleAdjust.basis.Case;
import newModel.doubleAdjust.basis.Parameter;

public class Run {

	public static void main(String[] args) {
		String arg="rl";
		run(arg);
      
	}
	public static void run(String args) {
		 if(args!=null) {
		 String arg=args.trim();
		 if(arg.equals("g")) {
			 int RunTime = 20;
			 int populationSize= 100;//50
			 double crossoverRate= 0.6;
			 double tMutationRate=0.05 ;
			 double rMutationRate=0.01 ;
			 NSGA_II NSGA_II=new NSGA_II(populationSize,crossoverRate,tMutationRate,rMutationRate,RunTime);
			 Case project =new Case();
			 project.setNSGA_II(NSGA_II);
			 project.setRunTime(RunTime);
			 Runtask task=new Runtask(project,arg);
			 Thread thread=new Thread(task);
			 thread.setName("run"+1+"");
			 thread.start();
			 
		 }else if(arg.equals("gv")) {
			 int len=5;//3
			 int RunTime = 20;
			 int populationSize=50;//
			 double crossoverRate= 0.6;
			 double resSpp= 0;//0.15
			 double rMutationRate= 0.01;// 0.01-0.03-0.05
			 NSGAV_II NSGAV_II=new NSGAV_II(populationSize,crossoverRate,resSpp,rMutationRate,len,RunTime);
			 Case project =new Case();
			 project.setNSGAV_II(NSGAV_II);
			 project.setRunTime(RunTime);
			 Runtask task=new Runtask(project,arg);
			 Thread thread=new Thread(task);
			 thread.setName("run"+1+"");
			 thread.start();
		   }else if(arg.equals("ntga")) {
			 int RunTime = 20;
			 int populationSize=50;//
			 double crossoverRate= 1;
			 int tour= 6;//0.15
			 double rMutationRate= 0.005;// 0.01-0.03-0.05
			 Parameter parameter=new Parameter(populationSize,crossoverRate,tour,rMutationRate,RunTime);
			 Case project =new Case();
			 project.setParameter(parameter);
			 project.setRunTime(RunTime);
			 Runtask task=new Runtask(project,arg);
			 Thread thread=new Thread(task);
			 thread.setName("run"+1+"");
			 thread.start();
		   }else if(arg.equals("rl")) {
				 int RunTime = 20;
				 int populationSize=50;
				 double crossoverRate= 0.6;
				 int tour= 6;
				 double rMutationRate= 0.05;
				 int s = 3;//单次果蝇生成数量
				 double alpha = 0.15;// 知识库更新的概率
				 int NE = 7;// 提供经验的果蝇数量
				 String type="single";
				 Parameter parameter=new Parameter( populationSize,  crossoverRate,  tour,  rMutationRate, s
							, alpha, NE,type,RunTime);
				 Case project =new Case();
				 project.setParameter(parameter);
				 project.setRunTime(RunTime);
				 Runtask task=new Runtask(project,arg);
				 Thread thread=new Thread(task);
				 thread.setName("run"+1+"");
				 thread.start();
			   }
		 }
	}

}
