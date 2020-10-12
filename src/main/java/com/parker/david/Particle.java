package com.parker.david;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * a single particle in the swarm. keeps track of its position and velocity and its best position ever
 */
public class Particle implements Comparable<Particle> {
	/**
	 * the current position
	 */
	private ArrayList<Double> position;

	/**
	 * the current velocity
	 */
	private ArrayList<Double> velocity;

	/**
	 * a vector correlating to the position vector, detailing the max for each respective index's value
	 */
	private final ArrayList<Double> maxConstraints;

	/**
	 * a vector correlating to the position vector, detailing the min for each respective index's value
	 */
	private final ArrayList<Double> minConstraints;

	/**
	 * the objective function
	 */
	private ObjectiveFunction objectiveFunction;

	/**
	 * a clone of the best position, mostly here for record keeping's sake
	 */
	private ArrayList<Double> bestPosition;

	/**
	 * the best fitness, could be calculated from the best position each time, but saved here to improve computational efficiency
	 */
	private double bestFitness;

	/**
	 * current fitness
	 */
	private double fitness;

	/**
	 * search parameter c1, the coefficient for the best personal position
	 */
	private final double cPersonal;

	/**
	 * search parameter c2, the coefficient for the best neighbourhood position
	 */
	private final double cNeighbourhood;

	/**
	 * search parameter w, the coefficient for the velocity position
	 */
	private final double weight;

	/**
	 * the constructor, sets parameters and the the initial fitness and best position to the current position
	 */
	Particle(double cPersonal, double cNeighbourhood, double weight, ArrayList<Double> initialPosition, ArrayList<Double> initialVelocity, ArrayList<Double> minConstraints, ArrayList<Double> maxConstraints, ObjectiveFunction objectiveFunction) {
		this.maxConstraints = maxConstraints;
		this.minConstraints = minConstraints;
		this.objectiveFunction = objectiveFunction;
		this.weight = weight;
		this.cPersonal = cPersonal;
		this.cNeighbourhood = cNeighbourhood;
		velocity = initialVelocity;
		position = initialPosition;

		//calculate the initial fitness and best position
		fitness = objectiveFunction.getFitness(position);
		bestPosition = position;
		bestFitness = fitness;
	}

	/**
	 * update this particle, return true if the particle finds a new best position
	 *
	 * @param neighbourhoodBestPosition the best position in this particle's neighbourhood
	 * @return a boolean, true if a new best position found, false otherwise
	 */
	public boolean update(ArrayList<Double> neighbourhoodBestPosition) {

		//generate our random numbers between 0 and 1 for this iteration
		double rhoPersonal = Utilities.getRandom0To1();
		double rhoNeighbour = Utilities.getRandom0To1();
		for (int i = 0; i < position.size(); i++) {

			//find out a theoretical new velocity
			double theoreticalNewVelocity = velocity.get(i) * weight + //velocity component
					(bestPosition.get(i) - position.get(i)) * rhoPersonal * cPersonal + //personal best component
					(neighbourhoodBestPosition.get(i) - position.get(i)) * rhoNeighbour * cNeighbourhood; //neighbourhood best component

			//find out a theoretical position
			double theoreticalNewPosition = position.get(i) + velocity.get(i);

			//constrain the position to get the real new position
			double realNewPosition = Utilities.constrainDouble(theoreticalNewPosition, minConstraints.get(i), maxConstraints.get(i));

			//find the velocity from old position to the new constrained position (thus the real velocity, not theoretical velocity)
			double realNewVelocity = realNewPosition - position.get(i);

			//update the velocity and position
			position.set(i, realNewPosition);
			velocity.set(i, realNewVelocity);

		}

		// update fitness, return true if finding a best fitness, else return false
		fitness = objectiveFunction.getFitness(position);
		if (fitness > bestFitness) {
			bestFitness = fitness;
			bestPosition = Utilities.deepCopy(position);
			return true; //new best fitness, return true
		} else {
			return false; //no new best fitness, return false
		}
	}

	/**
	 * the header for the particles toString method
	 *
	 * @return a string comma separated header
	 */
	public String particleHeader() {
		ArrayList<String> builder = new ArrayList<>();
		for (int i = 0; i < position.size(); i++) {
			builder.add("p" + i);
		}
		for (int i = 0; i < velocity.size(); i++) {
			builder.add("v" + i);
		}
		return String.join(", ", builder);
	}

	/**
	 * a string representation of this particle's position and velocity
	 *
	 * @return a string with position elements, then velocity elements
	 */
	@Override
	public String toString() {
		return position.stream().map(aDouble -> String.format("%.3f", aDouble)).collect(Collectors.joining(", ")) + ", " +
				velocity.stream().map(aDouble -> String.format("%.3f", aDouble)).collect(Collectors.joining(", "));
	}

	/**
	 * get best fitness this particle has seen
	 */
	public double getBestFitness() {
		return bestFitness;
	}

	/**
	 * return a deep copy of the array at the best fitness this particle has seen
	 */
	public ArrayList<Double> getBestPosition() {
		return Utilities.deepCopy(bestPosition);
	}

	/**
	 * compare based on fitness to another particle
	 */
	@Override
	public int compareTo(Particle other) {
		return objectiveFunction.compareFitnesses(this.fitness, other.fitness);
	}

}
