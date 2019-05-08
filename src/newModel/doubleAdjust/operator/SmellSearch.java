package newModel.doubleAdjust.operator;

import java.util.ArrayList;
import java.util.List;

import newModel.doubleAdjust.Individual;
import newModel.doubleAdjust.Population;
import newModel.doubleAdjust.basis.Case;
import newModel.doubleAdjust.basis.Parameter;
import newModel.doubleAdjust.basis.Task;

public class SmellSearch {
	
	
	public Population smellSearch(Population p,String type) {
		if(type.equals("single")) {
			return smell_BasedSearch_singleList(p);
		}
		if(type.equals("double")) {
			return smell_BasedSearch_doubleList(p);
		}
		return null;
	}
	/*
	 * 单资源链搜索
	 */
	private Population smell_BasedSearch_doubleList(Population p) {
		Case project=p.getProject();
		Parameter para=project.getParameter();
		int populationSize=p.getPopulationsize();
		int s=para.getS();
		Population newPopulation = new Population(populationSize * s,project );
		List<Individual> indivList = new ArrayList<>();
		// 遍历输入种群中的每个个体，每个个体经过操作，生成S个子个体
		Individual[] individuals = p.getPopulation();
		for (int i = 0; i < populationSize; i++) {
			Individual individual = individuals[i];
			for (int j = 0; j < s; j++) {

				List<List<Integer>> offspringChromosome = new ArrayList<>();
				for (int m = 0; m < individual.getChromosome().size(); m++) {
					List<Integer> list = new ArrayList<>();
					for (int n = 0; n < individual.getChromosome().get(m).size(); n++) {
						list.add(individual.getChromosome().get(m).get(n));
					}
					offspringChromosome.add(list);
				}
				// 随机选择任务序列中的某个位置  对1个任务的资源重新选择 
				int index_t_1 = (int) (Math.random() * offspringChromosome.get(0).size());
				
				int taskID = offspringChromosome.get(0).get(index_t_1);
				Task task = project.getTasks().get(taskID - 1);
				
				List<Integer> capapleResource = task.getresourceIDs();
				double rd = Math.random() ;
				int index_capaple = (int) (rd * capapleResource.size());
				
				int resourceid = capapleResource.get(index_capaple);
				offspringChromosome.get(1).set(index_t_1, resourceid);
				
				// 重复随机选择任务序列中的某个位置，直到两个相邻任务没有紧前任务关系， 做1次任务位置交换
				while (true) {
					int index_t_2 = (int) (Math.random() * offspringChromosome.get(0).size());
					if (index_t_2 != (offspringChromosome.get(0).size() - 1)) {
						
						int taskID1 = offspringChromosome.get(0).get(index_t_2);
						int resourceID1 = offspringChromosome.get(1).get(index_t_2);
						int taskID2 = offspringChromosome.get(0).get(index_t_2 + 1);
						int resourceID2 = offspringChromosome.get(1).get(index_t_2 + 1);

						Task task1 = project.getTasks().get(taskID1 - 1);
						Task task2 = project.getTasks().get(taskID2 - 1);

						if (!project.isPredecessor(task1, task2)) {
							// 交换两个位置上的任务编号以及资源编号
							offspringChromosome.get(0).set(index_t_2, taskID2);
							offspringChromosome.get(1).set(index_t_2, resourceID2);
							offspringChromosome.get(0).set(index_t_2 + 1, taskID1);
							offspringChromosome.get(1).set(index_t_2 + 1, resourceID1);
					
							break;
						} 
					}
				}
				// 创建子代个体对象
				Individual offspring = new Individual(offspringChromosome,project,1);
				indivList.add(offspring);
			}
		}
		for (int i = 0; i < indivList.size(); i++) {
			newPopulation.setIndividual(i, indivList.get(i));
		}
		return newPopulation;
	}
	
	/**
	 * 基于气味搜索方法 遍历输入种群中的每个个体，每个个体经过操作，生成S个子个体
	 * 
	 * @param population
	 * @return
	 */
	public Population smell_BasedSearch_singleList(Population p) {
		Case project=p.getProject();
		Parameter para=project.getParameter();
		int populationSize=p.getPopulationsize();
		int s=para.getS();
		Population newPopulation = new Population(populationSize * s,project );
		List<Individual> indivList = new ArrayList<>();
		// 遍历输入种群中的每个个体，每个个体经过操作，生成S个子个体
		Individual[] individuals = p.getPopulation();
		Mutation mutation =new SingleListMutation();
		for (int i = 0; i < populationSize; i++) {
			Individual individual = individuals[i];
			for (int j = 0; j < s; j++) {
                
				List<List<Integer>> offspringChromosome = new ArrayList<>();
				for (int m = 0; m < individual.getChromosome().size(); m++) {
					List<Integer> list = new ArrayList<>();
					for (int n = 0; n < individual.getChromosome().get(m).size(); n++) {
						list.add(individual.getChromosome().get(m).get(n));
					}
					offspringChromosome.add(list);
				}
				// 随机选择任务序列中的某个位置  对1个任务的资源重新选择 
				int index_t_1 = (int) (Math.random() * offspringChromosome.get(0).size());
				
				int taskID = offspringChromosome.get(0).get(index_t_1);
				Task task = project.getTasks().get(taskID - 1);
				
				List<Integer> capapleResource = task.getresourceIDs();
				double rd = Math.random() ;
				int index_capaple = (int) (rd * capapleResource.size());
				
				int resourceid = capapleResource.get(index_capaple);
				offspringChromosome.get(1).set(index_t_1, resourceid);
				// 创建子代个体对象
				//Individual offspring = mutation.mutation(individual, para.getrMutationRate());
				Individual offspring=new Individual(offspringChromosome,project,true);
				indivList.add(offspring);
				
			}
		}
		for (int i = 0; i < indivList.size(); i++) {
			newPopulation.setIndividual(i, indivList.get(i));
		}
		return newPopulation;
	}
}
