package javaLeetcode.feature18;

import java.util.Arrays;

public class ArrayInterviewQuestions_2 {
	public static void main(String[] args) {
		leftRotate(new int[] { 2, 3, 4, 5, 6, 7 }, 2); // 4, 5, 6, 7, 2, 3, anticlockwise
		rightRotate(new int[] { 1, 2, 3, 4, 5, 6, 7 }, 2); // 6, 7 ,1, 2, 3, 4, 5, clock
	}

	private static void leftRotate(int[] array, int count) {

		int temp;
		for (int j = 0; j < count; j++) {
			temp = array[0]; // 2

			for (int i = 0; i < array.length - 1; i++) { // 5 index
				array[i] = array[i + 1];

			}
			array[array.length - 1] = temp;

		}
		System.out.println("After " + count + " Interval" + Arrays.toString(array));

	}

	private static void rightRotate(int[] array, int count) {
		int temp;
		for (int i = 0; i < count; i++) {

			temp = array[array.length - 1];
			for (int j = array.length - 1; j > 0; j--) {

				array[j] = array[j-1];  //6-1

			}

			array[0] = temp;

		}
		System.out.println("After " + count + " Interval" + Arrays.toString(array));

	}

}
