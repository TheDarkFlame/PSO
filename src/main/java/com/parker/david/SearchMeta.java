package com.parker.david;

/**
 * a class to hold information about the search
 */
public class SearchMeta {
	/**
	 * the weight parameter for the low level swarm
	 */
	double w;
	/**
	 * the coefficient 1 (Particle best) parameter for the low level swarm
	 */
	double c1;
	/**
	 * the coefficient 2 (Global best) parameter for the low level swarm
	 */
	double c2;
	/**
	 * the max number of iterations to run the low level at
	 */
	double lowLevelMaxIterationsWithoutImprovement;

	/**
	 * the best low level swarm found thus far
	 */
	Swarm bestLowLevelSwarm;

	/**
	 * an override of the tostring method to output the params in a nice way
	 */
	@Override
	public String toString() {
		return "\nw = " + w +
				"\nc1 = " + c1 +
				"\nc2 = " + c2 +
				"\nbestIterationsWithoutImprovement = " + lowLevelMaxIterationsWithoutImprovement +
				"\nbest fitness = " + bestLowLevelSwarm.getBestFitness();
	}
}
