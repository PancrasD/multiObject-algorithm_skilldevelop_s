package newModel.doubleAdjust.operator;

import newModel.doubleAdjust.Individual;

public interface Mutation {
	public Individual mutation(Individual indiv,double rMutation);
}
