package newModel.doubleAdjust.algorithm;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import newModel.doubleAdjust.Individual;
import newModel.doubleAdjust.Population;
import newModel.doubleAdjust.Tools;
import newModel.doubleAdjust.basis.Case;
import newModel.doubleAdjust.basis.Parameter;
import newModel.gavn.NSFFA;

public class QGA extends Algorithm{
	//计算临界值
	double[] bound=new double[2];
	public QGA(String _fn, String _fo, List<List<Double>> countResult, Parameter para) {
		super(_fn, _fo, countResult, para);
		initBound();
	}
	/*
	 * 计算学习的临界值
	 */
	private void initBound() {
		//计算指标
		Map<String,double[]> upbound=Tools.readBound();
		String fileName=casefile.substring(casefile.lastIndexOf("/")+1, casefile.length())+".txt";
		bound=upbound.get(fileName);
	}

	public  void schedule() {
		// 创建案例类对象
		Case project = new Case(casefile);
		project.setParameter(para);
		project.setRunTime(para.getRunTime());
		Case project1=new Case(project);//复制一个project 
		// 初始化种群
		long startTime = System.currentTimeMillis();
		Population P1 = new Population(project.getNSGAV_II().populationSize,project,true);
		Population P2 = new Population(project1.getNSGAV_II().populationSize,project1,true);
		Population[] pp=evolve(P1,P2);
		P1=pp[0];
		P2=pp[1];
		long endTime = System.currentTimeMillis();
		//从最后得到种群中获取最优解集
		Population mergedPopulation =Tools.merged(P1,P2);
		Population solutions = Tools.getbestsolution(mergedPopulation,1, project);
		solutions=Tools.removeSame(solutions);//去重
	    //输出最优解集
		Tools.outputSolution(solutions,endTime-startTime,datafile,countResult);
		      	
	}
	public  void scheduleSingle() {
		// 创建案例类对象
		Case project = new Case(casefile);
		project.setParameter(para);
		project.setRunTime(para.getRunTime());
		// 初始化种群
		long startTime = System.currentTimeMillis();
		Population P1 = new Population(project.getParameter().getPopulationSize(),project,true);
		Population pp=evolve(P1);
		long endTime = System.currentTimeMillis();
		//从最后得到种群中获取最优解集
		Population pop=new Population(pp.getProject().getPareto().size(),project);
		pop.setPopulation(Tools.getArray(pp.getProject().getPareto()));
		Population solutions = Tools.getbestsolution(pop,1, project);
		solutions=Tools.removeSame(solutions);//去重
	    //输出最优解集
		Tools.outputSolution(solutions,endTime-startTime,datafile,countResult);
		      	
	}

	/*
	 * 进化 
	 * @param p1 种群1
	 * @param p2 种群2
	 */
	private Population[] evolve(Population P1, Population P2) {
		Case project1=P1.getProject();
		Case project2=P2.getProject();
		int count=0;
		State s=new State(casefile);
		double[] measures1_init=Tools.computMeasures(P1);
		double[] measures2_init=Tools.computMeasures(P2);
		s.boundSet(measures1_init,measures2_init);
		while (count < 100000 ) {
			double[] measures1=Tools.computMeasures(P1);
			double[] measures2=Tools.computMeasures(P2);
			boolean goQ1=s.judgeIn(measures1);
			boolean goQ2=s.judgeIn(measures2);
			int action1=(int) (Math.random()*3);
			int action2=(int) (Math.random()*3);
			int state1=-1;
			int state2=-1;
			if(goQ1) {
				state1=s.getState(measures1[0], measures1[1]);
				action1=s.getAction(state1,project1);
			}
			if(goQ2) {
				state2=s.getState(measures2[0], measures2[1]);
				action2=s.getAction(state2,project2);
			}
			switch(action1) {
			    case 0:P1=P1.getOffSpring_V(1);count+=P1.getPopulationsize()*2;break;
			    case 1:P1=P1.getOffSpring_NSFFA();count+=P1.getPopulationsize()*(NSFFA.NE+1);break;
			    case 2:P1=P1.getOffSpring_NTGA();count+=P1.getPopulationsize()*2;break;
			    default:P1=P1.getOffSpring_V(1);count+=P1.getPopulationsize()*2;
			}
			switch(action2) {
		    case 0:P2=P2.getOffSpring_V(1);count+=P2.getPopulationsize()*2;break;
		    case 1:P2=P2.getOffSpring_NSFFA();count+=P2.getPopulationsize()*(NSFFA.NE+1);break;
		    case 2:P2=P2.getOffSpring_NTGA();count+=P2.getPopulationsize()*2;break;
		    default:P2=P2.getOffSpring_V(1);count+=P2.getPopulationsize()*2;
		    }
			/*P1 = P1.getOffSpring_V(action1);
			P2 = P2.getOffSpring_V(action2);*/
			
			//P2=P2.getGuide(indivs2);
			//count+=(P1.getPopulationsize()*2+P2.getPopulationsize()*(NSFFA.NE+1));
			project1.setCount(0);
			project2.setCount(0);
			if(goQ1) {
				double[] measures1_new=Tools.computMeasures(P1);
				int newState1=s.getState(measures1_new[0], measures1_new[1]);
				double reward1=s.getReward(measures1,measures1_new);
				if(reward1<0) {
					System.out.println();
				}
				try {
					s.updateQ(reward1,project1,state1,action1,newState1);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
				s.updateProb(project1,state1);
			}
			if(goQ2) {
				double[] measures2_new=Tools.computMeasures(P2);
				int newState2=s.getState(measures2_new[0], measures2_new[1]);
				double reward2=s.getReward(measures2,measures2_new);
				if(reward2<0) {
					System.out.println();
				}
				try {
					s.updateQ(reward2,project2,state2,action2,newState2);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
				s.updateProb(project2,state2);
			}
			List<Individual> indivs1=P1.getpareto();
			List<Individual> indivs2=P2.getpareto();
			P1=P1.addPareto(indivs2);
			P2=P2.addPareto(indivs1);
		}
		double[] measures1_f=Tools.computMeasures(P1);
		double[] measures2_f=Tools.computMeasures(P2);
		System.out.println("种群1 "+"hv_0 "+measures1_init[0]+" mid_0 "+measures1_init[1]);
		System.out.println("种群1 "+"hv_g "+measures1_f[0]+" mid_g "+measures1_f[1]);
		System.out.println("种群2 "+"hv_0 "+measures2_init[0]+" mid_0 "+measures2_init[1]);
		System.out.println("种群2 "+"hv_g "+measures2_f[0]+" mid_g "+measures2_f[1]);
		
		Population pp[]={P1,P2};
		return pp;
	}

	/*
	 * 进化 
	 * @param p1 种群1
	 * @param p2 种群2
	 */
	private Population evolve(Population P1) {
		Case project1=P1.getProject();
		int count=0;
		State s=new State(casefile);
		double[] measures1_init=Tools.computMeasures(P1);
		s.setBound(measures1_init);
		int state1=-1;
		while (count < 100000 ) {
			double[] measures1=Tools.computMeasures(P1);
			boolean goQ1=s.judgeIn(measures1);
			int action1=(int) (Math.random()*3);
			
			if(goQ1) {
				state1=s.getState(measures1[0], measures1[1]);
				action1=s.getAction(state1,project1);
			}
			switch(action1) { 
			    case 0:P1=P1.getOffSpring_V(1);count+=P1.getPopulationsize()*2;break;
			    case 1:P1=P1.getOffSpring_NSFFA();count+=P1.getPopulationsize()*(NSFFA.NE+1);break;
			    case 2:P1=P1.getOffSpring_NTGA();count+=P1.getPopulationsize()*2;break;
			    default:P1=P1.getOffSpring_V(1);count+=P1.getPopulationsize()*2;
		    }
			//P1 = P1.getOffSpring_V(action1);
			//count+=(P1.getPopulationsize()*2+project1.getCount());
			project1.setCount(0);
			if(goQ1) {
				double[] measures1_new=Tools.computMeasures(P1);
				int newState1=s.getState(measures1_new[0], measures1_new[1]);
				double reward1=s.getReward(measures1,measures1_new);
				s.updateQ(reward1,project1,state1,action1,newState1);	
				s.updateProb(project1,state1);
			}
		}
		double[] measures1_f=Tools.computMeasures(P1);
		System.out.println("初始 "+"hv_0 "+measures1_init[0]+" mid_0 "+measures1_init[1]);
		System.out.println("结束 "+"hv_g "+measures1_f[0]+" mid_g "+measures1_f[1]);
		System.out.println(state1);
		return P1;
	}

}
