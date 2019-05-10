package newModel.doubleAdjust.algorithm;

import java.util.List;

import newModel.doubleAdjust.Individual;
import newModel.doubleAdjust.Population;
import newModel.doubleAdjust.Tools;
import newModel.doubleAdjust.basis.Case;
import newModel.doubleAdjust.basis.Initialization;
import newModel.doubleAdjust.basis.Parameter;
import newModel.doubleAdjust.basis.RandomInitialization;
import newModel.doubleAdjust.evaluator.HypervolumeContributionFitnessEvaluator;
import newModel.doubleAdjust.operator.CrossOver;
import newModel.doubleAdjust.operator.Mutation;
import newModel.doubleAdjust.operator.NondominatedSorting;
import newModel.doubleAdjust.operator.Select;
import newModel.doubleAdjust.operator.SingleListCrossOver;
import newModel.doubleAdjust.operator.SingleListMutation;

public class SMSEMOA extends Algorithm{
    
	public SMSEMOA(String _fn, String _fo, List<List<Double>> countResult, Parameter para) {
		super(_fn, _fo, countResult, para);
	}

	public SMSEMOA() {
		// TODO Auto-generated constructor stubs
	}
	@Override
	public void schedule() {
		
		// 创建案例类对象
		Case project = new Case(casefile);
		project.setParameter(para);
		project.setRunTime(para.getRunTime());
		// 初始化种群
		long startTime = System.currentTimeMillis();
		
		Initialization init=new RandomInitialization();
		Population P1 = init.initialize(project);
		
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
        * 种群进化
       * @param p 种群
     */
	private Population evolve(Population P1) {
		int count=0;
		while (count < 100000 ) {
			P1=getOffSpring(P1);
			count+=P1.getPopulationsize()*2;
		}
		return P1;
	}
    
	private Population getOffSpring(Population p) {
		String type=p.getProject().getParameter().getType();
		if(type.equals("single")) {
			return getOffSpring_singleList( p);
		}
		if(type.equals("double")) {
			return  getOffSpring_doubleList(p);
		}
		return null;
	}

	private Population getOffSpring_singleList(Population p) {
		// 种群进行非支配排序,设置种群中每个个体的非支配等级和拥挤度值
		Case project=p.getProject();
		Tools.setRankAndCrowD(p, project);
		p=makeNewPop(p,p.getProject().getPareto());
		return p;
	}
    /*
     * generate-reduce
     */
	private Population makeNewPop(Population p, List<Individual> nonDominated) {
		
		Individual firstParent;
	    Individual secondParent;
		int populationSize=p.getPopulationsize();
		Individual[] population=null;
		CrossOver crossOver=new SingleListCrossOver();
		Mutation mutation =new SingleListMutation();
		Select select=new Select();
		int tour=2;
	    int count=0;
	    double crossoverRate=1;
	    double mutationRate=0.005;
	    Individual firstChild;
	    Case project=p.getProject();
	    while (count< populationSize) {
	    	population=p.getPopulation();
	    	firstParent=select.selectTournament(population,nonDominated,tour);//2 为tournamentSize
			secondParent=select.selectTournament(population,nonDominated,tour);
			List<Individual> childs=crossOver.crossOver(firstParent,secondParent,crossoverRate);//交叉率1
			firstChild=mutation.mutation(childs.get(0),mutationRate);//变异率0.005
			Population combinePop=combine(p,firstChild);
			p=reduce(combinePop);
	    }
	    Tools.updatePareto(project,p);
		return p;
	}
	/*
	 * 移除r
	 */
	private Population reduce(Population combinePop) {
		Case project=combinePop.getProject();
		Individual[] population=combinePop.getPopulation();
		List<Integer> lastFront=getLastFront(combinePop);
		Population lastFrontPop=new Population(lastFront.size(),project);
		for(int i=0;i<lastFront.size();i++) {
			lastFrontPop.setIndividual(i, combinePop.getPopulation()[lastFront.get(i)]);
		}
		HypervolumeContributionFitnessEvaluator fitnessEvaluator=
				new HypervolumeContributionFitnessEvaluator();
		fitnessEvaluator.evaluate(lastFrontPop);
		int minIndix=-1;
		double minHyperVolume=Double.MAX_VALUE;
		for(int i=0;i<lastFront.size();i++) {
			if(minHyperVolume<population[lastFront.get(i)].getHyperVolume()) {
				minHyperVolume=population[lastFront.get(i)].getHyperVolume();
				minIndix=lastFront.get(i);
			}
		}
		Individual[] reducePop=new Individual[population.length-1];
		
		int count=0;
		for(int i=0;i<population.length;i++) {
			if(i!=minIndix) {
				reducePop[count++]=population[i];
			}
		}
		Population reducePopulation=new Population(reducePop.length,project);
		reducePopulation.setPopulation(reducePop);
		return reducePopulation;
	}

	/*
	 * 获得最后一层帕累托前沿
	 * @param  lastFront 最后一层帕累托解
	 */
    private List<Integer> getLastFront(Population population) {
    	NondominatedSorting non_sort=new NondominatedSorting(false);
    	List<List<Integer>> sort_list=non_sort.evaluate(population);
    	List<Integer> lastFront=sort_list.get(sort_list.size()-1);
		return lastFront;
	}

	/*
      * 将新生成的个体加入群体
     */
	private Population combine(Population population, Individual individual) {
		int newSize=population.getPopulationsize()+1;
		Population newPopulation =new Population(newSize,population.getProject());
		Individual[] indivs=population.getPopulation();
		Individual[] newIndivs=new Individual[newSize];
		int i=0;
		for(;i<population.getPopulationsize();i++) {
			newIndivs[i]=indivs[i];
		}
		newIndivs[i]=individual;
		newPopulation.setPopulation(newIndivs);
		return newPopulation;
	}

	private Population getOffSpring_doubleList(Population population) {
		// TODO Auto-generated method stub
		return null;
	}

}
