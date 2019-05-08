package newModel.doubleAdjust.operator;

import java.util.List;

import newModel.doubleAdjust.Individual;

public class Select {
	/*
	 * 从population和pareto轮盘赌选择出较优的个体
	 */
	public  Individual selectTournament(Individual[] population, List<Individual> pareto, int tournamentSize) {
		Individual parent;
		Individual pretender;
	    int size = population.length + pareto.size();
	    parent = randomizeIndividual(population, pareto, size);
	    pretender = randomizeIndividual(population, pareto, size);
	    parent = choose( parent, pretender);
	    for (int i = 0; i < tournamentSize - 2; ++i) {
	      pretender = randomizeIndividual(population, pareto, size);
	      parent = choose(parent, pretender);
	    }

	    return parent;
	}
	/*
	 * 从population 和   pareto随机选择一个
	 */
	private  Individual randomizeIndividual(Individual[] population, List<Individual> pareto, int size) {
		Individual pretender;
	    int random = (int) (Math.random()*size);
	    if (random >= pareto.size()) {
	      random -= pareto.size();
	      pretender = population[random];
	    } else {
	      pretender = pareto.get(random);
	    }
	    return pretender;
	}
	/*
	 * 从parent  和     pretender 选择出较rank的个体
	 */
	private  Individual choose(Individual firstCandidate, Individual secondCandidate) {
		 if (firstCandidate.getNon_dominatedRank() < secondCandidate.getNon_dominatedRank()) {
				return firstCandidate;
			}

			else if (firstCandidate.getNon_dominatedRank() > secondCandidate.getNon_dominatedRank()) {
				return secondCandidate;
			}

			return firstCandidate;
	}
}
