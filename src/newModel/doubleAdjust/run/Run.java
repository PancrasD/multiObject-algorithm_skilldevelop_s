package newModel.doubleAdjust;

public class Run {

	public static void main(String[] args) {
		String arg="gv";
		run(arg);
      
	}
	public static void run(String args) {
		 if(args!=null) {
		 String arg=args.trim();
		 if(arg.equals("g")) {
			 int RunTime = 10;
			 int populationSize= 50;//50
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
			 int len=10;//3
			 int RunTime = 2;
			 int populationSize=100;
			 double crossoverRate= 0.7;
			 double resSpp= 0.05;//0.03-0.05-0.07 0.07
			 double rMutationRate= 0.005;// 0.01-0.03-0.05
			 NSGAV_II NSGAV_II=new NSGAV_II(populationSize,crossoverRate,resSpp,rMutationRate,len,RunTime);
			 Case project =new Case();
			 project.setNSGAV_II(NSGAV_II);
			 project.setRunTime(RunTime);
			 Runtask task=new Runtask(project,arg);
			 Thread thread=new Thread(task);
			 thread.setName("run"+1+"");
			 thread.start();
		   }
		 }
	}

}
