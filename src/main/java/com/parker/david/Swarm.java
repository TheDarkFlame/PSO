package com.parker.david;

import java.util.ArrayList;

/**
 * the swarm itself, takes in a bunch of parameters and finds a near optimal solution using the optimise() method.
 */
public class Swarm {
	/**
	 * the objective function that this swarm uses to optimise the problem
	 */
	private final ObjectiveFunction objectiveFunction;

	/**
	 * returns the position thus far that has the best fitness
	 *
	 * @return an arraylist of the parameters for particle
	 */
	public ArrayList<Double> getBestPosition() {
		return bestPosition;
	}

	/**
	 * get the fitness of the best position found thus far
	 *
	 * @return a double corresponding to the fitness
	 */
	public double getBestFitness() {
		return bestFitness;
	}

	/**
	 * internal storage for the best position
	 */
	private ArrayList<Double> bestPosition;

	/**
	 * internal storage for the best fitness
	 */
	private double bestFitness;

	/**
	 * the set of particles that form this swarm
	 */
	private final ArrayList<Particle> particles;

	/**
	 * an internal counter of the number of iterations since a new best position
	 */
	private int iterationsSinceImprovement;

	/**
	 * the constructor, upon construction of a swarm, particles are also created and the initial bests are found
	 *
	 * @param objectiveFunction     the objective function that we use to find best values
	 * @param particleCount         the number of particles that this swarm is to contain
	 * @param cNeighbour            the coefficient for the best solution in neighbour component of velocity updates
	 * @param cPersonal             the coefficient for the best personal solution component of velocity updates
	 * @param weight                the coefficient for the weight of the current velocity for velocity updates
	 * @param minConstraints        the constraint on the minimum values for constraints
	 * @param maxConstraints        the constrain on the maximum values for constraints
	 * @param initialVelocityFactor factor of the range of the search space that the initial velocity may be initialised to. if -5 < position < 5 and factor = 0.3, then -3 < velocity < 3
	 */
	public Swarm(ObjectiveFunction objectiveFunction, int particleCount, double cNeighbour, double cPersonal, double weight, ArrayList<Double> minConstraints, ArrayList<Double> maxConstraints, double initialVelocityFactor) {
		this.objectiveFunction = objectiveFunction;
		this.particles = new ArrayList<>();

		//set the initial number since improvement to 0
		this.iterationsSinceImprovement = 0;

		//create the individual particles and add them to the swarm
		for (int particleNumber = 0; particleNumber < particleCount; particleNumber++) {
			ArrayList<Double> initialPosition = new ArrayList<>();
			ArrayList<Double> initialVelocity = new ArrayList<>();
			//randomly generate the initial position and velocity within some constraints
			for (int dimensionNumber = 0; dimensionNumber < maxConstraints.size(); dimensionNumber++) {
				initialPosition.add(Utilities.constrainedRandom(minConstraints.get(dimensionNumber), maxConstraints.get(dimensionNumber)));
				initialVelocity.add(Utilities.constrainedRandom(minConstraints.get(dimensionNumber), maxConstraints.get(dimensionNumber)) * initialVelocityFactor);
			}

			//create the new particle and add it to the swarm
			Particle particleToAdd = new Particle(cPersonal, cNeighbour, weight, initialPosition, initialVelocity, minConstraints, maxConstraints, objectiveFunction);
			particles.add(particleToAdd);

			//get the initial best fitness of the swarm and the associated position
			double particleToAddFitness = objectiveFunction.getFitness(particleToAdd.getBestPosition());
			if (bestPosition == null || objectiveFunction.compareFitnesses(particleToAddFitness, bestFitness) == 1) {
				bestPosition = Utilities.deepCopy(particleToAdd.getBestPosition());
				bestFitness = particleToAddFitness;
			}
		}

	}

	/**
	 * optimise based on the maxIterationsWithoutImprovement stopping criterion
	 *
	 * @param maxIterationsWithoutImprovement int max number of iterations we will permit without improvement before stopping
	 */
	public void optimise(int maxIterationsWithoutImprovement) {
		while (iterationsSinceImprovement <= maxIterationsWithoutImprovement)
			update();
	}

	/**
	 * a string representation of the contents of the swarm
	 */
	@Override
	public String toString() {
		StringBuilder swarmString = new StringBuilder(particles.get(0).particleHeader() + "\n");
		for (Particle particle : particles) {
			swarmString.append(particle.toString()).append("\n");
		}
		return swarmString.toString();
	}

	/**
	 * run a single update cycle
	 * updates best fitness if we find a new best fitness
	 * keeps track of number of iterations since improvement
	 */
	private void update() {
		boolean resetImprovement = false;
		for (Particle particle : particles) {

			// we use g best here to find the best position in this particle's neighbourhood
			ArrayList<Double> particleNeighbourhoodBest = bestPosition;

			//call the update function for each particle. Returns true if it finds a new best fitness for the particle
			if (particle.update(particleNeighbourhoodBest)) {

				//check if the new particle best fitness is also a global best
				if (objectiveFunction.compareFitnesses(particle.getBestFitness(), bestFitness) == 1) {
					bestFitness = particle.getBestFitness();
					bestPosition = particle.getBestPosition();
					resetImprovement = true;
				}
			}
		}

		//if we see an improvement, reset the counter, else increase the count since last improved
		if (resetImprovement) {
			iterationsSinceImprovement = 0;
		} else {
			++iterationsSinceImprovement;
		}
	}
}
