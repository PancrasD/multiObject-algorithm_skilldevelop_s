package newModel.doubleAdjust.algorithm;

import java.util.ArrayList;
import java.util.List;

import newModel.doubleAdjust.Individual;
import newModel.doubleAdjust.Population;
import newModel.doubleAdjust.Tools;
import newModel.doubleAdjust.basis.Case;
import newModel.doubleAdjust.basis.Parameter;
import newModel.doubleAdjust.operator.CrossOver;
import newModel.doubleAdjust.operator.Mutation;
import newModel.doubleAdjust.operator.Select;
import newModel.doubleAdjust.operator.SingleListCrossOver;
import newModel.doubleAdjust.operator.SingleListMutation;

public class NSGA extends Algorithm{
	
	public NSGA(String _fn, String _fo, List<List<Double>> countResult, Case para) {
		super(_fn, _fo, countResult, para);
	}


	public NSGA() {
		// TODO Auto-generated constructor stub
	}


	public Population getOffSpring(Population p,String type,int neighborType) {
		if(type.equals("single")) {
			return getOffSpring_singleList( p);
		}
		if(type.equals("double")) {
			return  getOffSpring_doubleList(p,neighborType);
		}
		return null;
	}


	/* 
	 *  单资源链进化
	 */
	public Population getOffSpring_singleList(Population p) {
		
		// 种群进行非支配排序,设置种群中每个个体的非支配等级和拥挤度值
		Case project=p.getProject();
		
		int populationSize=p.getPopulationsize();
		
		Tools.setRankAndCrowD(p, project);
		
		Individual[] population =p.getPopulation();
		
		List<Individual> newPop=makeNewPop(p,project.getPareto());
		Individual[] newPopulation=Tools.getArray(newPop);
		
		Population mergedPopulation=null;
		mergedPopulation = merged(population,newPopulation,project);
		
		Population p3 = mergedPopulation.slectPopulation(populationSize);
		//Population p3 = mergedPopulation.slectPopulationC(this.populationsize,false);
		Tools.updatePareto(project,p3);
		
		return p3;
	}
	 private List<Individual> makeNewPop(Population p,List<Individual> nonDominated ) {
		    List<Individual> newPopulation = new ArrayList<>();
		    Individual firstParent;
		    Individual secondParent;
		    Individual firstChild;
		    Individual secondChild;
		    List<List<Integer>> children;
		    Individual[] population=p.getPopulation();
		    int populationSize=population.length;
		    CrossOver crossOver=new SingleListCrossOver();
			Mutation mutation =new SingleListMutation();
			Select select=new Select();
			int tour=2;
			double crossoverRate=1;
			double mutationRate=0.005;
		    while (newPopulation.size() < populationSize) {
		    	firstParent=select.selectTournament(population,nonDominated,tour);//6 为tournamentSize
				secondParent=select.selectTournament(population,nonDominated,tour);
				List<Individual> childs=crossOver.crossOver(firstParent,secondParent,crossoverRate);//交叉率1
				firstChild=mutation.mutation(childs.get(0),mutationRate);//变异率0.005
				secondChild=mutation.mutation(childs.get(1),mutationRate);
				
		      newPopulation.add(firstChild);
		      newPopulation.add(secondChild);

		    }

		    return newPopulation;
		  }
	private Population getOffSpring_doubleList(Population p, int neighborType) {
		// TODO Auto-generated method stub
		return null;
	}
	/*
	 *  从混合种群中选择前populationSize个个体作为新一代父代种群
	 *  @param population
	 *  @param newPopulation
	 *  @param  project
	 */
	private Population merged(Individual[] population, Individual[] newPopulation, Case project) {
		int len=population.length+newPopulation.length;
		Population combinePop=new Population(len,project);
		int i=0;
		for(;i<population.length;i++) {
			combinePop.setIndividual(i, population[i]);
		}
		for(;i<population.length+newPopulation.length;i++) {
			combinePop.setIndividual(i, newPopulation[i-population.length]);
		}
		return combinePop;
	}
	/**
	 * 获取用于交叉的交配池，其中个体数量等于输入种群的个体数量
	 * 
	 * @param population
	 *            父代种群
	 * @return 用于交叉的交配池
	 */
	public Population getMatePool(Population p) {
		int populationsize=p.getPopulationsize();
		Case project=p.getProject();
		Individual[] population=p.getPopulation();
		Population matePool = new Population(populationsize,project);
		for (int i = 0; i < matePool.size(); i++) {
			int m, n;
			while (true) {
				m = (int) (Math.random() * populationsize);
				n = (int) (Math.random() * populationsize);
				if (m == n)
					continue; 
				else
					break;
			}
			matePool.setIndividual(i, population[m].binaryTournament(population[m], population[n]));
		}
		return matePool;
	}


	@Override
	public void schedule() {
		// TODO Auto-generated method stub
		
	}
}
