package newModel.doubleAdjust.run;

import newModel.doubleAdjust.NSGAV_II;
import newModel.doubleAdjust.NSGA_II;
import newModel.doubleAdjust.Tools;
import newModel.doubleAdjust.basis.Case;

public class RunExperimentalDesign {

	public static void main(String[] args) {
		String arg="gv";
		runExperimentalDesign(arg);
      
	}
	public static void runExperimentalDesign(String args) {
		 if(args!=null) {
		 String arg=args.trim();
		 if(arg.equals("g")) {
			 int[] pop= {100,150,200};
			 double[] crossR = {0.6,0.7,0.8};
			 double[] tMuRate= {0.05,0.1,0.15};//0.05,0.1,0.15
			 double[] rMuRate= {0.005,0.01,0.015};
			 int RunTime = 10;
			 int populationSize[]= {pop[0],pop[0],pop[0],pop[1],pop[1],pop[1],pop[2],pop[2],pop[2]};
			 double[] crossoverRate= {crossR[0],crossR[1],crossR[2],crossR[0],crossR[1],crossR[2],crossR[0],crossR[1],crossR[2]};
			 double[] tMutationRate= {tMuRate[0],tMuRate[1],tMuRate[2],tMuRate[1],tMuRate[2],tMuRate[0],tMuRate[2],tMuRate[0],tMuRate[1]};
			 double[] rMutationRate= {rMuRate[0],rMuRate[1],rMuRate[2],rMuRate[2],rMuRate[0],rMuRate[1],rMuRate[1],rMuRate[2],rMuRate[0]};
			 Thread threads[] = new Thread[populationSize.length]; //存放线程对象
			 String head=Tools.buildFileName();
			 for(int i=0;i<populationSize.length;i++) {
				 NSGA_II NSGA_II=new NSGA_II(populationSize[i],crossoverRate[i],tMutationRate[i],rMutationRate[i],RunTime);
				 Case project =new Case();
				 project.setNSGA_II(NSGA_II);
				 project.setRunTime(RunTime);
				 RunExperimentalDesigntask task=new RunExperimentalDesigntask(project,arg,head);
				 threads[i]=new Thread(task);
				 threads[i].setName(i+1+"");
			 }
			 for(int i=0;i<5;i++) {
				 threads[i].start();
			 }
			 try {
				threads[4].join();
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
			 for(int i=5;i<populationSize.length;i++) {
				 threads[i].start();
			 } 
			 
		 }else if(arg.equals("gv")) {
			 int[] pop= {50,75,100};
			 double[] crossR = {0.6,0.7,0.8};
			 double[] resSp= {0,0,0};//0.1
			 double[] rMuRate= {0.005,0.01,0.015};//0.1
			 int len=5;
			 int RunTime = 10;
			 int populationSize[]= {pop[0],pop[0],pop[0],pop[1],pop[1],pop[1],pop[2],pop[2],pop[2]};
			 double[] crossoverRate= {crossR[0],crossR[1],crossR[2],crossR[0],crossR[1],crossR[2],crossR[0],crossR[1],crossR[2]};
			 double[] resSpp= {resSp[0],resSp[1],resSp[2],resSp[1],resSp[2],resSp[0],resSp[2],resSp[0],resSp[1]};
			 double[] rMutationRate= {rMuRate[0],rMuRate[1],rMuRate[2],rMuRate[2],rMuRate[0],rMuRate[1],rMuRate[1],rMuRate[2],rMuRate[0]};
			 Thread threads[] = new Thread[populationSize.length]; //存放线程对象
			 String head=Tools.buildFileName();
			 for(int i=0;i<populationSize.length;i++) {
				 NSGAV_II NSGAV_II=new NSGAV_II(populationSize[i],crossoverRate[i],resSpp[i],rMutationRate[i],len,RunTime);
				 Case project =new Case();
				 project.setNSGAV_II(NSGAV_II);
				 project.setRunTime(RunTime);
				 RunExperimentalDesigntask task=new RunExperimentalDesigntask(project,arg,head);
				 threads[i]=new Thread(task);
				 threads[i].setName(i+1+"");
				 
			 }
			 for(int i=0;i<5;i++) {
				 threads[i].start();
			 }
			 try {
				threads[4].join();
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
			for(int i=5;i<populationSize.length;i++) {
				 threads[i].start();
			} 
		 }
		 }
	}

}
