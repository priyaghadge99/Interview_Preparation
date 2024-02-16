package javaLeetcode.Interview;

public class FindMagicNumber {
	public static void main(String[] args) {

		int result = findmagicNumber(163);

		if (result == 1) {
			System.out.println("magic");
		} else {
			System.out.println("not");

		}

	}

	private static int findmagicNumber(int num) {
		int sumofdigit = 0;
		while (num > 0 || sumofdigit > 9) {

			if (num == 0) {
				num = sumofdigit;
				sumofdigit = 0;
			}
			System.out.println(sumofdigit);

			sumofdigit += num % 10; // 10
			System.out.println("After"+sumofdigit);
			num /= 10;

		}

		return sumofdigit;

	}

}
