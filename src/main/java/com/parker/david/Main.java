package com.parker.david;

import java.io.FileWriter;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import static java.lang.Math.*;

/**
 * program entry point function
 */
public class Main {
	private static final String tempOutputPathPrefix = "output/temp/";

	/**
	 * the entry point, runs everything and outputs the results
	 */
	public static void main(String[] args) throws IOException {

		//an object to hold the search information
		SearchMeta meta = new SearchMeta();

		//create the low level objective function, that contains the mathematical representation of the function we wish to optimise.
		//expects 2 parameters, x and y
		ObjectiveFunction lowLevelObjective = new ObjectiveFunction(ObjectiveFunction.Type.Minimisation) {
			@Override
			public double getFitness(ArrayList<Double> parameters) {
				double x = parameters.get(0);
				double y = parameters.get(1);
				return -(y + 47) * sin(sqrt(abs(y + x / 2.0 + 47))) - x * sin(sqrt(abs(x - (y + 47))));
			}
		};

		//create the high level objective function, the high level objective function creates a low level swarm and optimises it
		//the optimal fitness from the low level swarm is thus the fitness for that set of parameters. Parameters that this
		//objective functions expects are the w, c1, c2, and max iterations
		ObjectiveFunction highLeveObjective = new ObjectiveFunction(ObjectiveFunction.Type.Minimisation) {

			@Override
			public double getFitness(ArrayList<Double> parameters) {
				ArrayList<Double> minConstraints = new ArrayList<>(Arrays.asList(-512.0, -512.0));
				ArrayList<Double> maxConstraints = new ArrayList<>(Arrays.asList(+512.0, +512.0));

				//get the parameters for this swarm
				double weight = parameters.get(0);
				double cPersonal = parameters.get(1);
				double cNeighbour = parameters.get(2);
				Double completionIterations = parameters.get(3);

				//create and optimise the low level swarm
				Swarm lowLevelSwarm = new Swarm(lowLevelObjective, 50, cNeighbour, cPersonal, weight, minConstraints, maxConstraints, 0.3);
				lowLevelSwarm.optimise(completionIterations.intValue());

				//if we observe an improvement in the swarm, save the swarm
				if (meta.bestLowLevelSwarm == null ||
						lowLevelObjective.compareFitnesses(lowLevelSwarm.getBestFitness(), meta.bestLowLevelSwarm.getBestFitness()) == 1) {
					meta.bestLowLevelSwarm = lowLevelSwarm;
				}

				return lowLevelSwarm.getBestFitness();
			}
		};

		// set the constraints on the meta parameters for the low level search
		ArrayList<Double> minConstraints = new ArrayList<>(Arrays.asList(-50.0, -10.0, -10.0, 1.0));
		ArrayList<Double> maxConstraints = new ArrayList<>(Arrays.asList(+50.0, +10.0, +10.0, 100.0));

		//create and optimise the high level swarm
		Swarm highLevelSwarm = new Swarm(highLeveObjective, 10, 1, 1, 1, minConstraints, maxConstraints, 1);
		highLevelSwarm.optimise(25);

		//store the results
		ArrayList<Double> results = highLevelSwarm.getBestPosition();
		meta.w = results.get(0);
		meta.c1 = results.get(1);
		meta.c2 = results.get(2);
		meta.lowLevelMaxIterationsWithoutImprovement = results.get(3);

		String date = ZonedDateTime.now(ZoneId.of("GMT+2")).format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));

		//low level swarm to filesystem
		FileWriter lowLevelSwarmOutput = new FileWriter(tempOutputPathPrefix + "swarm_" + date + ".csv", true);
		lowLevelSwarmOutput.append(meta.bestLowLevelSwarm.toString()).close();

		FileWriter lowLevelBestOutput = new FileWriter(tempOutputPathPrefix + "best_" + date + ".csv", true);
		lowLevelBestOutput.append(meta.bestLowLevelSwarm.getBestPosition().stream().map(aDouble -> String.format("%.3f", aDouble)).collect(Collectors.joining(", "))).close();

		//high level info to console
		FileWriter runOutput = new FileWriter(tempOutputPathPrefix + "run_data.csv", true);
		runOutput.append(meta.toString()).append("\nfile = " + tempOutputPathPrefix + "swarm_").append(date).append(".csv").append("\n").close();
		System.out.println(meta);
	}
}
