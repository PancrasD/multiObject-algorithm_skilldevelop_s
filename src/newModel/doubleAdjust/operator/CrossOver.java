package newModel.doubleAdjust.operator;

import java.util.List;

import newModel.doubleAdjust.Individual;

public interface CrossOver {
	 public List<Individual> crossOver(Individual firstP,Individual secondP,double cr);
}
