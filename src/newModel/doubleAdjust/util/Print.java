package newModel.doubleAdjust.util;


import newModel.doubleAdjust.Individual;
import newModel.doubleAdjust.Population;
import newModel.doubleAdjust.Tools;
import newModel.doubleAdjust.basis.Case;

public class Print {

	public void print(Population p1, Population p2,int t) {
		
		Population mergedPopulation =Tools.merged(p1,p2);
		Case project=p1.getProject();
		Population solutions = Tools.getbestsolution(mergedPopulation,1, project);
		Individual[] pop=solutions.getPopulation();
		System.out.println("第"+t+"次");
		for(int i=0;i<pop.length;i++) {
			Individual indiv=pop[i];
			System.out.println(indiv.getObj()[0]+" "+indiv.getObj()[1]);
		}
	}

}
