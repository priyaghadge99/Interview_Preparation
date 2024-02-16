package javaLeetcode.feature18;

import java.util.Arrays;

public class ArrayInterviewQuestions_12_PairOfElementSum {

	public static void main(String[] args) {

		findThePairs(new int[] { 4, 6, 5, -10, 8, 5, 20 }, 10);

		findThePairs(new int[] { 4, -5, 9, 11, 25, 13, 12, 8 }, 20);

		findThePairs(new int[] { 12, 13, 10, 15, 8, 40, -15 }, 25);

	}

	private static void findThePairs(int[] arrayinput, int sum) {
		int i = 0;
		int j = arrayinput.length - 1;
		Arrays.sort(arrayinput);
		System.out.println("Pairs of elements whose sum is " + sum + " are : ");
		while (i < j) {

			if (arrayinput[i] + arrayinput[j] == sum) {
				System.out.println("[" + arrayinput[i] + " + " + arrayinput[j] + "] =" + sum);
				i++;
				j--;
			} else if (arrayinput[i] + arrayinput[j] < sum) {
				i++;

			} else if (arrayinput[i] + arrayinput[j] > sum) {
				j--;

			}

		}

	}

}
