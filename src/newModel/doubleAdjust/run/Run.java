package newModel.doubleAdjust.run;


import newModel.doubleAdjust.basis.Parameter;

public class Run {

	public static void main(String[] args) {
		String arg="rl";
		run(arg);
      
	}
	public static void run(String args) {
		 if(args!=null) {
			 String arg=args.trim();
			 switch(arg) {
			 case "g":runNSGA(arg);break;
			 case "gv":runGAVN(arg);break;
			 case "rl":runRL(arg);break;
			 case "nsffa":runNSFFA(arg);break;
			 case "ntga":runNTGA(arg);break;
			 case "smsemoa":runSMSEMOA(arg);break;
			 }
	  }
	}
	private static void runNSGA(String arg) {
		 int RunTime = 20;
		 int populationSize= 100;//50
		 double crossoverRate= 1;
		 double tMutationRate=0.005 ;
		 double rMutationRate=0.005 ;
		 String type="single";
		 Parameter parameter=new Parameter(populationSize,crossoverRate,tMutationRate,rMutationRate,type,RunTime);
		 
		 Runtask task=new Runtask(parameter,arg);
		 Thread thread=new Thread(task);
		 thread.setName("run"+1+"");
		 thread.start();
		
	}
	private static void runGAVN(String arg) {
		 int len=5;//3
		 int RunTime = 20;
		 int populationSize=50;//
		 double crossoverRate= 0.6;
		 double resSpp= 0;//0.15
		 double rMutationRate= 0.01;// 0.01-0.03-0.05
		 String type="single";
		 Parameter parameter=new Parameter(populationSize,crossoverRate,resSpp,rMutationRate,len,type,RunTime);
		
		 Runtask task=new Runtask(parameter,arg);
		 Thread thread=new Thread(task);
		 thread.setName("run"+1+"");
		 thread.start();
		
	}
	private static void runRL(String arg) {
		int RunTime = 1;
		 int populationSize=50;
		 double crossoverRate= 1;
		 int tour= 6;
		 double rMutationRate= 0.005;
		 int s = 5;//单次果蝇生成数量
		 double alpha = 0.1;// 知识库更新的概率
		 int NE = 3;// 提供经验的果蝇数量
		 String type="single";//编码
		 String mode="two";//种群
		 Parameter parameter=new Parameter( populationSize,  crossoverRate,  tour,  rMutationRate, s
					, alpha, NE,type,mode,RunTime);
		
		 Runtask task=new Runtask(parameter,arg);
		 Thread thread=new Thread(task);
		 thread.setName("run"+1+"");
		 thread.start();
		
	}
	private static void runNSFFA(String arg) {
		
		
	}
	private static void runNTGA(String arg) {
		 int RunTime = 1;
		 int populationSize=100;//
		 double crossoverRate= 1;
		 int tour= 6;//0.15
		 double rMutationRate= 0.005;// 0.01-0.03-0.05
		 Parameter parameter=new Parameter(populationSize,crossoverRate,tour,rMutationRate,RunTime);
		
		 Runtask task=new Runtask(parameter,arg);
		 Thread thread=new Thread(task);
		 thread.setName("run"+1+"");
		 thread.start();
		
	}
	private static void runSMSEMOA(String arg) {
		 int RunTime = 1;
		 int populationSize=100;//
		 double crossoverRate= 1;
		 int tour= 2;//0.15
		 double rMutationRate= 0.005;// 0.01-0.03-0.05
		 Parameter parameter=new Parameter(populationSize,crossoverRate,tour,rMutationRate,RunTime);
		
		 Runtask task=new Runtask(parameter,arg);
		 Thread thread=new Thread(task);
		 thread.setName("run"+1+"");
		 thread.start();
		
	}

}
