package newModel.doubleAdjust.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import newModel.doubleAdjust.Individual;
import newModel.doubleAdjust.Population;
import newModel.doubleAdjust.Tools;
import newModel.doubleAdjust.basis.Case;
import newModel.doubleAdjust.basis.Parameter;
import newModel.doubleAdjust.operator.Mutation;
import newModel.doubleAdjust.operator.SingleListMutation;
import newModel.doubleAdjust.util.Print;

public class ReinForcementLearning extends Algorithm{
	//计算临界值
    double[] bound=new double[2];
    HashMap<Integer,Boolean> exits=new HashMap<>();
	public ReinForcementLearning(String _fn, String _fo, List<List<Double>> countResult, Parameter para) {
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
	@Override
	public void schedule() {
		String mode=para.getMode();
		switch(mode){
		case "one":scheduleOne();break;
		case "two":scheduleTwo();break;
		default:System.out.println("模式设置错误");
		}
	}
	public void scheduleTwo() {
		// 创建案例类对象
		Case project = new Case(casefile);
		project.setParameter(para);
		project.setRunTime(para.getRunTime());
		Case project1=new Case(project);//复制一个project 
		// 初始化种群
		long startTime = System.currentTimeMillis();
		int populationSize=project.getParameter().getPopulationSize();
		Population P1 = new Population(populationSize,project,true,true);
		Population P2 = new Population(populationSize,project1,true,true);
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
	public  void scheduleOne() {
		// 创建案例类对象
		Case project = new Case(casefile);
		project.setParameter(para);
		project.setRunTime(para.getRunTime());
		int populationSize=project.getParameter().getPopulationSize();
		// 初始化种群
		long startTime = System.currentTimeMillis();
		Population P1 = new Population(populationSize,project,true,true);
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
	private Population evolve(Population P1) {
		Case project1=P1.getProject();
		NTGA ntga=new NTGA();
		NSGA nsga=new NSGA();
		NSFFA nsffa=new NSFFA();
		Parameter para=project1.getParameter();
		String type=para.getType();
		int NE=para.getNE();
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
			case 0:P1=ntga.getOffSpring_NTGA(P1);count+=P1.getPopulationsize()*2;break;
		    case 1:P1=nsffa.getOffSpring(P1, type);count+=P1.getPopulationsize()*(NE+1);break;
		    case 2:P1=nsga.getOffSpring(P1, type, 1);count+=P1.getPopulationsize()*2;break;
		    
		    default:P1=ntga.getOffSpring_NTGA(P1);count+=P1.getPopulationsize()*2;
		    }
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
		/*System.out.println("初始 "+"hv_0 "+measures1_init[0]+" mid_0 "+measures1_init[1]);
		System.out.println("结束 "+"hv_g "+measures1_f[0]+" mid_g "+measures1_f[1]);
		System.out.println(state1);*/
		return P1;
	}
	private Population[] evolve(Population P1, Population P2) {
		NTGA ntga=new NTGA();
		NSGA nsga=new NSGA();
		NSFFA nsffa=new NSFFA();
		Case project1=P1.getProject();
		Case project2=P2.getProject();
		
		Parameter para=project1.getParameter();
		String type=para.getType();
		int NE=para.getNE();
		int count=0;
		State s=new State(casefile);
		double[] measures1_init=Tools.computMeasures(P1);
		double[] measures2_init=Tools.computMeasures(P2);
		s.boundSet(measures1_init,measures2_init);
		int pr=1;
		Print print=new Print();
		int stay1=0;
		int stay2=0;
		int lastState1=-1;
		int lastState2=-1;
		while (count < 100000 ) {
			//update(exits,P1,P2);
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
			    case 0:P1=ntga.getOffSpring_NTGA(P1);count+=P1.getPopulationsize()*2;break;
			    case 1:P1=nsffa.getOffSpring(P1, type);count+=P1.getPopulationsize()*(NE+1);break;
			    case 2:P1=nsga.getOffSpring(P1, type, 1);count+=P1.getPopulationsize()*2;break;
			    
			    default:P1=ntga.getOffSpring_NTGA(P1);count+=P1.getPopulationsize()*2;
			}
			switch(action2) {
				case 0:P2=ntga.getOffSpring_NTGA(P2);count+=P2.getPopulationsize()*2;break;
				case 1:P2=nsffa.getOffSpring(P2, type);count+=P2.getPopulationsize()*(NE+1);break;
			    case 2:P2=nsga.getOffSpring(P2, type, 1);count+=P2.getPopulationsize()*2;break;
			   
		        default:P2=ntga.getOffSpring_NTGA(P2);count+=P2.getPopulationsize()*2;
		    }
			
			project1.setCount(0);
			project2.setCount(0);
			if(goQ1) {
				double[] measures1_new=Tools.computMeasures(P1);
				int newState1=s.getState(measures1_new[0], measures1_new[1]);
				double reward1=s.getReward(measures1,measures1_new);
				if(reward1<0) {
					System.out.println("原状态 "+state1+" 新状态 "+newState1+" 动作 "+action1+" 奖赏 "+reward1+" Q值 "+project1.getQ_value()[state1][action1]);
				}
				s.updateQ(reward1,project1,state1,action1,newState1);
				s.updateProb(project1,state1);
				/*if(lastState1!=newState1) {
					lastState1=newState1;
					stay1=0;
				}else if((stay1++)>20) {
					P1 = newPop(P1);
					count+=P1.getPopulationsize();
					stay1=0;
				}*/
				
			}
			if(goQ2) {
				double[] measures2_new=Tools.computMeasures(P2);
				int newState2=s.getState(measures2_new[0], measures2_new[1]);
				double reward2=s.getReward(measures2,measures2_new);
				if(reward2<0) {
					System.out.println("原状态 "+state2+" 新状态 "+newState2+" 动作 "+action2+" 奖赏 "+reward2+" Q值 "+project2.getQ_value()[state2][action2]);
				}
				s.updateQ(reward2,project2,state2,action2,newState2);
				s.updateProb(project2,state2);
				/*if(lastState2!=newState2) {
					lastState2=newState2;
					stay2=0;
				}else if((stay2++)>20) {
					P2 = newPop(P2);
					count+=P2.getPopulationsize();
					stay2=0;
				}*/
			}
			List<Individual> indivs1=P1.getpareto();
			List<Individual> indivs2=P2.getpareto();
			P1=P1.addPareto(indivs2);
			P2=P2.addPareto(indivs1);
			if(count/10000>=pr) {
//				/print.print(P1,P2,pr);
				pr++;
			}
		}
		//print.print(P1,P2,pr);
		double[] measures1_f=Tools.computMeasures(P1);
		double[] measures2_f=Tools.computMeasures(P2);
		/*System.out.println("种群1 "+"hv_0 "+measures1_init[0]+" mid_0 "+measures1_init[1]);
		System.out.println("种群1 "+"hv_g "+measures1_f[0]+" mid_g "+measures1_f[1]);
		System.out.println("种群2 "+"hv_0 "+measures2_init[0]+" mid_0 "+measures2_init[1]);
		System.out.println("种群2 "+"hv_g "+measures2_f[0]+" mid_g "+measures2_f[1]);
		*/
		Population pp[]={P1,P2};
		return pp;
	}
	/*
	 * 更新exits 记录搜索过的个体
	 */
	private void update(HashMap<Integer, Boolean> exits2, Population p1, Population p2) {
		Individual[] indivs1=p1.getPopulation();
		Individual[] indivs2=p2.getPopulation();
		for(int i=0;i<indivs1.length;i++) {
			if(!exits.containsKey(indivs1[i].getHashCode())) {
				exits.put(indivs1[i].getHashCode(), true);
			}else {
				System.out.println("has");
			}
		}
		for(int i=0;i<indivs2.length;i++) {
			if(!exits.containsKey(indivs2[i].getHashCode())) {
				exits.put(indivs2[i].getHashCode(), true);
			}else {
				System.out.println("has");
			}
		}
	}
	/*
	 * 产生新的基因型的个体
	 */
	private Population newPop(Population p1) {
		Case project=p1.getProject();
		int populationSize=p1.getPopulationsize();
		List<Individual> all=new ArrayList<>();
		List<Individual> pareto=p1.getProject().getPareto();
		Individual[] current=p1.getPopulation();
		all.addAll(pareto);
		for(int i=0;i<current.length;i++) {
		   if(!all.contains(current[i])) {
			   all.add(current[i]);
		   }
		}
		Mutation mutation =new SingleListMutation();
		List<Individual> newPop=new ArrayList<>();
		/*for(int i=0;i<p1.getPopulationsize();i++) {
			Individual indiv=p1.getPopulation()[i];
			indiv=mutation.mutation(indiv, 0.005);
			int k=0;
			while(!exits.containsKey(indiv.getHashCode())&&k<20){
				indiv=mutation.mutation(indiv, 0.005);
				k++;
			}
			newPop.add(indiv);
		}*/
 		while(newPop.size()<populationSize) {
 			Individual indiv=new Individual( project, true,true);
 			if(!all.contains(indiv)) {
 				newPop.add(indiv);
 		 		all.add(indiv);
 			}
 		}
 		Population newP=new Population(populationSize,project);
 		newP.setPopulation(Tools.getArray(newPop));
		return newP;
	}

}
