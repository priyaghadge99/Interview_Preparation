package javaLeetcode.feature18;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SampleInterviewQuetions {

	public static void main(String[] args) {
		// sepearte off even numbers
		System.out.println("1. sepearte off even numbers ");

		List<Integer> listofInteger = new ArrayList<>();
		listofInteger.add(22);
		listofInteger.add(22);
		listofInteger.add(20);
		listofInteger.add(21);
		listofInteger.add(9);
		listofInteger.add(8);

		listofInteger.add(22);

		Map<Boolean, List<Integer>> collect = listofInteger.stream()
				.collect(Collectors.partitioningBy(i -> i % 2 == 0));

		for (Map.Entry<Boolean, List<Integer>> collectmap : collect.entrySet()) {
			Boolean key = collectmap.getKey();
			List<Integer> val = collectmap.getValue();
			System.out.println("Key=" + key + " Value= " + val);

		}

		System.out.println("2. Remove Duplicate Element from list");
		List<Integer> collect2 = listofInteger.stream().distinct().collect(Collectors.toList());
		collect2.forEach((n) -> System.out.print(n + " "));

		System.out.println();

		System.out.println("3. Frequency of each character in a string");
		String strSample = "SampleString";
		Map<Character, Long> collect3 = strSample.chars().mapToObj(c -> (char) c)
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		for (Map.Entry<Character, Long> entry : collect3.entrySet()) {
			Character key = entry.getKey();
			Long val = entry.getValue();
			System.out.println("Key=" + key + " Value= " + val);
		}

		System.out.println("4. Frequency of each element in a Array");
		Map<Integer, Long> collect4 = listofInteger.stream()
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		for (Entry<Integer, Long> entry : collect4.entrySet()) {
			Integer key = entry.getKey();
			Long val = entry.getValue();
			System.out.println("Key=" + key + " Value= " + val);
		}

		System.out.println("5. Sort List in ReverseOrder");

		listofInteger.stream().sorted(Comparator.reverseOrder()).forEach(System.out::println);

		System.out.println("5. Join List of String with suffix prefix nad delimter");
		List<String> listOfString = new ArrayList<>();
		listOfString.add("12Poonam");
		listOfString.add("2Sadhnnaccccccc");
		listOfString.add("Chandramukhi");

		String collect5 = listOfString.stream().collect(Collectors.joining("-", "A", "Z"));
		System.out.println(collect5);

		System.out.println("6. Print Multiples of Five");

		listofInteger.stream().filter((i) -> i % 5 == 0).forEach(System.out::println);

		System.out.println("7. Maximum and mininum in a list ");
		Integer integer = listofInteger.stream().max(Comparator.naturalOrder()).get();
		System.out.println("Max : " + integer);
		Integer min = listofInteger.stream().min(Comparator.naturalOrder()).get();
		System.out.println("Min : " + min);

		System.out.println("7. Merge two unsorted array into Single sorted Array ");

		List<Integer> listofInteger2 = new ArrayList<>();
		listofInteger2.add(25);
		listofInteger2.add(19);
		listofInteger2.add(17);
		listofInteger2.add(1);
		listofInteger2.add(2);
		listofInteger2.add(3);
		listofInteger2.add(8);
		int[] unsortedArray1 = { 11, 33, 1, 2 };
		int[] unsortedArray2 = { 10, 31, 9, 8 };

		int[] array = IntStream.concat(Arrays.stream(unsortedArray1), Arrays.stream(unsortedArray2)).sorted().toArray();
		System.out.println(Arrays.toString(array));

		System.out.println("8. Print 3 Max/Min of Numbers from the list");
		List<Integer> collect6 = listofInteger2.stream().sorted().limit(3).collect(Collectors.toList());
		collect6.forEach(System.out::println);
		List<Integer> collect7 = listofInteger2.stream().sorted(Comparator.reverseOrder()).limit(3)
				.collect(Collectors.toList());
		collect7.forEach(System.out::println);

		System.out.println("8. Sum Of all Digit of Numbers");
		Integer collect8 = Stream.of(String.valueOf(20101).split("")).collect(Collectors.summingInt(Integer::parseInt));
		System.out.println("Sum Of all digit : " + collect8);

		System.out.println("9. Second LargestNumber in an array ");
		Integer integer2Largest = listofInteger2.stream().sorted(Comparator.reverseOrder()).skip(1).findFirst().get();
		System.out.println("Integer2Largest : " + integer2Largest);

		System.out.println("10. Sort List Of String in increasing Order of there length");
		List<String> collect9 = listOfString.stream().sorted(Comparator.comparing(String::length))
				.collect(Collectors.toList());
		collect9.forEach(System.out::println);
		
		System.out.println("11. Common Element between two Array");
		 listofInteger.stream().filter(listofInteger2::contains).forEach(System.out::println);
		 
		System.out.println("12. Sum and average of All element of an Array "); 
		int sum = Arrays.stream(unsortedArray2).sum();
		double asDouble = Arrays.stream(unsortedArray2).average().getAsDouble();
		System.out.println("Sum : "+sum);
		System.out.println("asDouble" +asDouble);
		
		System.out.println("13. Reverse each word of String ");
		String fruit = "Apple"; 
		String collect10 = Arrays.stream(fruit.split(" ")).map(word-> new StringBuffer(word).reverse()).collect(Collectors.joining(" "));
		System.out.println("Reverse of Word: "+ collect10);
		
		System.out.println("14. Reverse an integer array ***************** ");
		int [] array1 = {1,2,3,4};
		int[] array2 = IntStream.rangeClosed(1, array1.length).map(i-> array1[array1.length-i]).toArray();
		System.out.println("Array Reverse: "+Arrays.toString(array1));
		
		System.out.println("15. Sum of first 20 Natural numbers ");
		int sum2 = IntStream.range(1, 11).sum();
		System.out.println("Sum :"+sum2);
		
		System.out.println("16. Find String which start with number");
		listOfString.stream().filter(str -> Character.isDigit(str.charAt(0))).forEach(System.out::println);
	
		System.out.println("17. Find Duplicate Element of an array ");
		List<Integer> set = new ArrayList<>();
		Set<Integer> collect11 = listofInteger.stream().filter(i-> !set.add(i)).collect(Collectors.toSet());
		collect11.forEach((n)-> System.out.println(n));
		
		System.out.println("18. Last Element of an array  ");
		String last = listOfString.stream().skip(listOfString.size()-1).findFirst().get();
		System.out.println("Last Element of an Array : "+last);
		
		System.out.println("19. Fibonnaci Series ");
		Stream.iterate(new int[] {0,1}, f-> new int[] {f[1],f[0]+f[1]}).limit(10).map(f->f[0]).forEach(i-> System.out.print(i +" "));
		
		System.out.println("20. Find Date of Birth");
		LocalDate date = LocalDate.of(1993, 7, 7);
		LocalDate today = LocalDate.now();
		System.out.println(ChronoUnit.YEARS.between(date, today));
	
	}

}
