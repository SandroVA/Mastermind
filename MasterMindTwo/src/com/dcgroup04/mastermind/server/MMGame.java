package com.dcgroup04.mastermind.server;

import java.util.ArrayList;

import com.dcgroup04.mastermind.network.MMPacketCodes;

/**
 * The MMGame class contains the logic behind the game, including the answer
 * generation algorithm as well as the clue generation algorithm
 * 
 * @author Rytis Paulauskas, Sandro Victoria Arena, Alessandro Rodi
 * 
 */
public class MMGame {

	/**
	 * Generates a random answer
	 * 
	 * @return a byte array of length 4 containing numbers between 1 and 6
	 */
	public static byte[] generateAnswer() {
		byte[] answer = new byte[4];

		for (int i = 0; i < 4; i++)
			answer[i] = (byte) (Math.random() * 8 + 1);

		return answer;
	}

	/**
	 * Plays a round of the game and validates input
	 * 
	 * @param input
	 *            the 4 guesses of the player
	 * @param answer
	 *            the answer to the game
	 * @param roundsPlayed
	 *            the number of rounds that have already been played
	 * @return a byte array of length 3. Index 0 holds the number of guesses
	 *         that are correct both in place and value, index 1 holds the
	 *         number of guesses that are correct only in place, and index 2
	 *         holds the number of rounds played so far
	 */
	public static byte[] playRound(byte[] input, byte[] answer, int roundsPlayed) {
		for (int i = 0; i < 4; i++) {
			// Send an error message if the input was incorrect
			if (input[i] < 1 || input[i] > 8)
				return MMPacketCodes.GAMEERROR.getCode();
		}

		return processRoundResults(input, answer, roundsPlayed);
	}

	/**
	 * Finds out which of the guesses of the player were correct and/or
	 * partially correct.
	 * 
	 * @param input
	 *            the guesses of the player
	 * @param answer
	 *            the answer to the game
	 * @param roundsPlayed
	 *            the number of rounds played so far
	 * @return a byte array of length 3. Index 0 holds the number of guesses
	 *         that are correct both in place and value, index 1 holds the
	 *         number of guesses that are correct only in place, and index 2
	 *         holds the number of rounds played so far
	 */
	private static byte[] processRoundResults(byte[] input, byte[] answer,
			int roundsPlayed) {
		// Do validation prior to getting here to make sure input is correct
		int correctNumbers = 0;
		int numbersInRightPlace = 0;
		ArrayList<Byte> used = new ArrayList<Byte>();

		// Check which numbers have the right value, but are not in the right
		// place
		for (int i = 0; i < 4; i++) {
			if (!used.contains(input[i])) {
				correctNumbers += Math.min(count(answer, input[i]),
						count(input, input[i]));
				used.add(input[i]);
			}
		}

		// Check which numbers are in the right place with the right value
		for (int i = 0; i < 4; i++) {
			if (input[i] == answer[i]) {
				numbersInRightPlace++;
				correctNumbers--;
			}
		}

		roundsPlayed++;

		// Send a game over message if the player failed to find the answer in
		// 10 rounds
		if (roundsPlayed == 10 && numbersInRightPlace != 4)
			return MMPacketCodes.GAMELOST.getCode();
		else {
			// Send information relevant to the game
			byte[] output = { (byte) correctNumbers,
					(byte) numbersInRightPlace, (byte) roundsPlayed, 0 };
			return output;
		}
	}

	/**
	 * Counts the number of times the given value is in the array
	 * 
	 * @param array
	 *            an array of bytes
	 * @param value
	 *            the value that is counted
	 * @return the number of times the value the given value is in the array
	 */
	private static int count(byte[] array, byte value) {
		int count = 0;
		for (int i = 0; i < array.length; i++) {
			if (array[i] == value)
				count++;
		}
		return count;
	}
}
