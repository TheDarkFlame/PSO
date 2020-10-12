package com.parker.david;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * a set of utility functions to make code more readable
 */
public class Utilities {

	/**
	 * a random number generator
	 */
	private static final Random randomNumberGenerator = ThreadLocalRandom.current();

	/**
	 * make a deep copy of an arraylist, that is, the elements in this array are the same, but the array itself occupies a new space in memory,
	 * so removing an element from this array will not affect the source array
	 *
	 * @param <T>             the type of the array
	 * @param sourceArrayList the array which we wish to deep copy
	 */
	public static <T> ArrayList<T> deepCopy(ArrayList<T> sourceArrayList) {
		ArrayList<T> copy = new ArrayList<>();
		for (T element : sourceArrayList) {
			copy.add(element);
		}
		return copy;
	}

	/**
	 * wrapper around the random number generator to make code more readable
	 */
	public static double getRandom0To1() {
		return randomNumberGenerator.nextDouble();
	}

	/**
	 * generate a random double with min value = min, max value = max
	 *
	 * @param min the minimum value
	 * @param max the maximum value
	 */
	public static double constrainedRandom(double min, double max) {
		return min + randomNumberGenerator.nextDouble() * (max - min);
	}

	/**
	 * if the valid search range can be seen as a pool table,
	 * this method constrains each value to the pool table search space by bouncing it from the edges until it comes to rest
	 *
	 * @param input the input value which we wish to constrain
	 * @param min   the minimum value which the input may take
	 * @param max   the maximum value which the input may take
	 */
	public static double constrainDouble(double input, double min, double max) {
		if (input >= min && input <= max)
			return input;

		//search space size
		double range = max - min;

		//when remove any instance the particle bounces off both sides and comes back to the start
		input = input % (range * 2);

		//if input < min, input is min + (min - input)
		input = input < min ? 2 * min - input : input;

		//if input > max, input is max - (input - max)
		input = input > max ? 2 * max - input : input;

		//if input < min, input is min + (min - input)
		input = input < min ? 2 * min - input : input;

		//if input > max, input is max - (input - max)
		input = input > max ? 2 * max - input : input;

		return input;
	}
}
