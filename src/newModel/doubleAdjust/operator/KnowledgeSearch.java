package newModel.doubleAdjust.operator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import newModel.doubleAdjust.Individual;
import newModel.doubleAdjust.NSFFA;
import newModel.doubleAdjust.Population;
import newModel.doubleAdjust.basis.Case;
import newModel.doubleAdjust.basis.Parameter;
import newModel.doubleAdjust.basis.Task;

public class KnowledgeSearch {
	public Population knowledgeSearch(Population p,String type) {
		if(type.equals("single")) {
			return knowledge_BasedSearch_singleList(p);
		}
		if(type.equals("double")) {
			return knowledge_BasedSearch_singleList(p);
		}
		return null;
	}


	/**
	 * 基于知识库搜索
	 * 单链表
	 * @param pop
	 * @return
	 */
	public Population  knowledge_BasedSearch_singleList(Population pop) {
		int populationSize=pop.getPopulationsize();
		Case project=pop.getProject();
		Parameter para=project.getParameter();
		int NE=para.getNE();
		double alpha=para.getAlpha();
		Population newPopulation = new Population(populationSize,project);
		// 选择NE个精英个体
		Population EPop = pop.slectPopulation(NE);
		List<Task> tasks = project.getTasks();
		for (int i = 0; i < tasks.size(); i++) {
			Task task = tasks.get(i);
			List<Integer> capapleResourceid = task.getresourceIDs();
			Map<Integer,Double> rp = tasks.get(i).getCapaleResource();	
			
			double sumTemp_P = 0;
			for (int j = 0; j < capapleResourceid.size(); j++) {
				double temp_P = (1 - NSFFA.alpha) * rp.get(capapleResourceid.get(j))
						+alpha * getISum(EPop, task.getTaskID(), capapleResourceid.get(j)) / NE;
				rp.replace(capapleResourceid.get(j), temp_P);
				sumTemp_P += temp_P;
			}
			for (int j = 0; j < capapleResourceid.size(); j++) {
				double P = rp.get(capapleResourceid.get(j)) / sumTemp_P;
				rp.replace(capapleResourceid.get(j), P);
			}

		}

		// 遍历种群的个体，利用轮盘赌法为每个个体的染色体任务序列重新分配资源,并进行交叉算子操作
		for (int i = 0; i < populationSize; i++) {
			Individual parent = pop.getPopulation()[i];
			// 个体i的染色体结构
			List<List<Integer>> chromosome = parent.getChromosome();
			// 创建子个体的染色体对象
			List<List<Integer>> newChromosome = copyChrome(chromosome);
			

			for (int j = 0; j < newChromosome.get(0).size(); j++) {
				// 任务ID
				int tID = newChromosome.get(0).get(j);
				// 任务对象
				Task t = tasks.get(tID - 1);
				// 任务t的可执行资源集
				Map<Integer,Double> capapleResource = t.getCapaleResource();
				// 利用轮盘赌法为任务t重新分配资源
				int reassignResourceID = selectResource(capapleResource);
				newChromosome.get(1).set(j, reassignResourceID);
			}

			// 从NE精英个体群中随机选择一个个体
			int randIndex = (int) (Math.random() * EPop.size());
			Individual parent_2 = EPop.getPopulation()[randIndex];
			List<List<Integer>> chromosome_2 = parent_2.getChromosome();

			// 随机选择两个位置，p,q,需要满足1<=p<q<=J-1
			int p, q;
			while (true) {
				p = (int) (Math.random() * newChromosome.get(0).size());
				q = (int) (Math.random() * newChromosome.get(0).size());
				if (p < q) {
					break;
				}
			}
			// 用来存储已排好序的子任务序列
			List<Integer> tID_List = new ArrayList<>();
			for (int j = 0; j < chromosome_2.get(0).size(); j++) {
				if (j < p || j > q) {
					tID_List.add(newChromosome.get(0).get(j));
				}
			}
			for (int j = p; j <= q; j++) {
				for (int k = 0; k < chromosome_2.get(0).size(); k++) {
					if (!tID_List.contains(chromosome_2.get(0).get(k))) {
						tID_List.add(chromosome_2.get(0).get(k));
						newChromosome.get(0).set(j, chromosome_2.get(0).get(k));
						newChromosome.get(1).set(j, chromosome_2.get(1).get(k));
						break;
					}
				}
			}

			// 创建子个体
			Individual offspring = new Individual(newChromosome,project,1);
			
			newPopulation.setIndividual(i, offspring);
		}

		return newPopulation;
	}
	/*
	 * 复制基因
	 */
	private List<List<Integer>> copyChrome(List<List<Integer>> chromosome) {
		List<List<Integer>> newChromosome=new ArrayList<>();
		for (int m = 0; m < chromosome.size(); m++) {
			List<Integer> list = new ArrayList<>();
			for (int n = 0; n < chromosome.get(m).size(); n++) {
				list.add(chromosome.get(m).get(n));
			}
			newChromosome.add(list);
		}
		return newChromosome;
	}
	/**
	 * 计算在NE个精英个体中，有多少个体满足指定任务，被指定资源分配
	 * 
	 * @param ePop
	 * @param taskID
	 * @param resourceID
	 * @return
	 */
	private int getISum(Population ePop, int taskID, int resourceID) {
		int sum = 0;
		for (int i = 0; i < ePop.size(); i++) {
			Individual indiv = ePop.getPopulation()[i];
			// 指定任务在个体染色体中的索引
			int index_t = indiv.getChromosome().get(0).indexOf(taskID);
			int rID = indiv.getChromosome().get(1).get(index_t);
			// 如果与指定资源ID相同，sum加1
			if (rID == resourceID) {
				sum++;
			}
		}
		return sum;
	}
	/**
	 * 轮盘赌法，为每个任务重新分配资源
	 * 
	 * @param capapleResource
	 * @return
	 */
	public int selectResource(Map<Integer, Double> capapleResource) {
		double rouletteWheelPosition = Math.random();
		// 选择资源
		double spinWheel = 0;
		int Resourceid = 0;
		Iterator<Integer> rt = capapleResource.keySet().iterator();
		while(rt.hasNext()){
			Resourceid = rt.next();
			spinWheel += capapleResource.get(Resourceid);
			if (spinWheel >= rouletteWheelPosition) {
				break;
			}		
		}
		return Resourceid;
	}
}
