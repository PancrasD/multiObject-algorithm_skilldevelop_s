package newModel.doubleAdjust.algorithm;

import java.util.List;

import newModel.doubleAdjust.Individual;
import newModel.doubleAdjust.Population;
import newModel.doubleAdjust.Tools;
import newModel.doubleAdjust.basis.Case;
import newModel.doubleAdjust.operator.KnowledgeSearch;
import newModel.doubleAdjust.operator.SmellSearch;

public class NSFFA extends Algorithm{
	public NSFFA(String _fn, String _fo, List<List<Double>> countResult, Case para) {
		super(_fn, _fo, countResult, para);
	}


	public NSFFA() {
		// TODO Auto-generated constructor stub
	}


	public Population getOffSpring(Population p,String type) {
		if(type.equals("single")) {
			return getOffSpring_singleList(p,"single");
		}
		if(type.equals("double")) {
			return  getOffSpring_singleList(p,"double");
		}
		return null;
	}
	

	/*
	 * 单资源链进化
	 */
	public Population getOffSpring_singleList(Population p, String type) {
		Case project=p.getProject();
		int populationSize=p.getPopulationsize();
		SmellSearch s=new SmellSearch();
		KnowledgeSearch ks=new KnowledgeSearch();
		Population OffSpring = new Population(populationSize,project);
		// 种群进行非支配排序,设置种群中每个个体的非支配等级和拥挤度值
		Tools.setRankAndCrowD(p, project);
		
		// 基于气味搜索，每个个体生成S个个体，种群大小为NS*S
		Population p1 = s.smellSearch(p,type);
		// 将两个种群合并
		Population mp1 = merged(p.getPopulation(),p1.getPopulation(),project);
		// 从混合种群中选择前populationSize个个体作为新一代父代种群
		Population p2 = mp1.slectPopulation(populationSize);
		
		 // 基于知识的搜索
		Population Q = ks.knowledgeSearch(p2, type);
		// 将两个种群合并
		Population mp2 = merged(p2.getPopulation(),Q.getPopulation(),project);
		// 从混合种群中选择前populationSize个个体作为新一代父代种群
		OffSpring = mp2.slectPopulation(populationSize);
		Tools.updatePareto(project,OffSpring);
		
		return OffSpring;
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


	@Override
	public void schedule() {
		// TODO Auto-generated method stub
		
	}

}
