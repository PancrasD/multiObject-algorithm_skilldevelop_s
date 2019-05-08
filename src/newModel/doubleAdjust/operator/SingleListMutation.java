package newModel.doubleAdjust.operator;

import java.util.ArrayList;
import java.util.List;

import newModel.doubleAdjust.Individual;
import newModel.doubleAdjust.basis.Case;

public class SingleListMutation implements Mutation{
    /*
     * 变异 
     * @param indiv 个体
     * @param tMutation 任务变异概率
     * @param rMutation 资源变异概率
     */
	public Individual mutation(Individual indiv,double rMutation) {
		Case project=indiv.getProject();
		List<List<Integer>> son_chromosome=new ArrayList<>();
		List<List<Integer>> chrome=indiv.getChromosome();
		List<Integer> tasks=chrome.get(0);
		List<Integer>  ress=chrome.get(1);
		int taskLength=tasks.size();
		List<Integer> _tasks=new ArrayList<>();
		List<Integer> _ress=new ArrayList<>();
		for(int i=0;i<taskLength;i++) {
			int taskid=tasks.get(i);
			_tasks.add(taskid);
			int resid=ress.get(i);
			_ress.add(resid);
		}
		for (int geneIndex = 0; geneIndex < taskLength - 1; geneIndex++) {
			if (rMutation > Math.random()) {
				// 每个任务可供选择的资源集合
				// 任务ID
				int gene=_tasks.get(geneIndex);
				List<Integer> capapleResource = project.getTasks().get(gene - 1).getresourceIDs();
				// 在可行资源中，随机选择一种
				double  randnum = Math.random();
				int r = (int) (randnum * capapleResource.size());
				_ress.set(geneIndex, capapleResource.get(r));
			}
		}
		son_chromosome.add(_tasks);
		son_chromosome.add(_ress);
		Individual son = new Individual(son_chromosome,project,true);
		return son;
	}

	
}
