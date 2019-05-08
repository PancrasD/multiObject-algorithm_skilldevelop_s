package newModel.doubleAdjust.operator;

import java.util.ArrayList;
import java.util.List;

import newModel.doubleAdjust.Individual;

public class SingleListCrossOver implements CrossOver{
	/*
	 * 交叉
	 * @param firstP
	 * @param secondP
	 * @param cr 交叉率
	 */
   public List<Individual> crossOver(Individual firstP,Individual secondP,double cr) {
	    
	    List<Integer> firstParent=firstP.getChromosome().get(1);
	    List<Integer> secondParent =secondP.getChromosome().get(1);
	    int point = (int) (Math.random() * firstParent.size());
	    List<Integer> firstChild = new ArrayList<>(firstParent);
	    List<Integer> secondChild = new ArrayList<>(secondParent);

	    if (Math.random() < cr) {
	      for (int i = 0; i < firstParent.size(); ++i) {
	        if (i < point) {
	          firstChild.set(i, firstParent.get(i));
	          secondChild.set(i, secondParent.get(i));
	        } else {
	          firstChild.set(i, secondParent.get(i));
	          secondChild.set(i, firstParent.get(i));
	        }
	      }
	    }
        List<Integer> taskCopy1=new ArrayList<>(firstP.getChromosome().get(0));
        List<Integer> taskCopy2=new ArrayList<>(secondP.getChromosome().get(0));
        List<List<Integer>> chrome1=new ArrayList<>();
        List<List<Integer>> chrome2=new ArrayList<>();
        chrome1.add(taskCopy1);
        chrome1.add(firstChild);
        chrome2.add(taskCopy2);
        chrome2.add(secondChild);
	    List<Individual> result = new ArrayList<>();
	    Individual child1=new Individual(chrome1,firstP.getProject(),true);
	    Individual child2=new Individual(chrome2,secondP.getProject(),true);
	    result.add(child1);
	    result.add(child2);

	    return result;
   }
}
