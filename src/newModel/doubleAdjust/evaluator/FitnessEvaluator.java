package newModel.doubleAdjust.evaluator;

import newModel.doubleAdjust.Population;

public interface FitnessEvaluator {
	/**
	 * Evaluates the solutions in the specified population assigning the
	 * {@code FITNESS_ATTRIBUTE} attribute.
	 * 
	 * @param population the population to be evaluated
	 */
	public void evaluate(Population population);
}
