package com.parker.david;

import java.util.ArrayList;

public abstract class ObjectiveFunction {

	public final Type objectiveType;

	/**
	 * get the fitness of a parameters, abstract so that this may be defined at runtime
	 *
	 * @param parameters the parameters for which the fitness is to be calculated
	 */
	abstract public double getFitness(ArrayList<Double> parameters);

	/**
	 * constructor
	 * requires defining whether this is maximisation or minimisation
	 */
	ObjectiveFunction(Type objectiveType) {
		this.objectiveType = objectiveType;
	}

	/**
	 * compares two fitnesses, returns 1 if baseFitness is better, 0 if they are equal, -1 if comparisonFitness is better
	 *
	 * @param baseFitness       the base fitness from which we do the comparison
	 * @param comparisonFitness the other fitness we are using to compare
	 */
	int compareFitnesses(double baseFitness, double comparisonFitness) {
		if (baseFitness == comparisonFitness)
			return 0;
		if (objectiveType == Type.Minimisation) {
			//if minimisation, return 1 if this is lower than other, -1 if other is lower than this
			if (baseFitness < comparisonFitness)
				return 1;
			else
				return -1;
			//if maximisation, return -1 if this is lower than other, 1 if other is lower than this
		} else {
			if (baseFitness < comparisonFitness)
				return -1;
			else
				return 1;
		}
	}

	/**
	 * define if this objective function needs to be maximised or minimised
	 */
	enum Type {
		Maximisation,
		Minimisation
	}
}
