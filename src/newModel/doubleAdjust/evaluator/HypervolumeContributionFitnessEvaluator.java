package newModel.doubleAdjust.evaluator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import newModel.doubleAdjust.Individual;
import newModel.doubleAdjust.Population;
import newModel.doubleAdjust.Tools;

public class HypervolumeContributionFitnessEvaluator implements FitnessEvaluator{

	@Override
	public void evaluate(Population population) {// TODO compute is right 
		Individual[] indivs=population.getPopulation();
		List<Individual> indivsList=Tools.getList(indivs);
		
		for(int i=0;i<indivsList.size();i++) {
			indivsList.get(i).setHyperVolume(0);
		}
		Collections.sort(indivsList,new Comparator<Individual>(){
			@Override
			public int compare(Individual o1, Individual o2) {
				if(o1.getObj()[0]>o2.getObj()[0]) {
					return 1;
				}
				if(o1.getObj()[0]<o2.getObj()[0]) {
					return -1;
				}
				return 0;
			}
			
		});
		int len=indivsList.size();
		double max_0=indivsList.get(len-1).getObj()[0];
		double min_0=indivsList.get(0).getObj()[0];
		double max_1=indivsList.get(0).getObj()[1];
		double min_1=indivsList.get(len-1).getObj()[1];
		double distanceTemp_0=max_0-min_0;
		double distanceTemp_1=max_1-min_1;
		indivsList.get(0).setHyperVolume(Double.MAX_VALUE);
		indivsList.get(indivsList.size()-1).setHyperVolume(Double.MAX_VALUE);
		for(int i=1;i<indivsList.size()-1;i++) {
			Individual last=indivsList.get(i-1);
			Individual next=indivsList.get(i+1);
			Individual now=indivsList.get(i);
			double  distance_1=next.getObj()[0]-now.getObj()[0]/distanceTemp_0;
			double  distance_2=last.getObj()[1]-now.getObj()[1]/distanceTemp_1;
			double hyperVolume=distance_1*distance_2;
			now.setHyperVolume(hyperVolume);
		}
	}

}
