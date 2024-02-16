package javaLeetcode.feature18;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ArrayInterviewQuestions {
	public static void main(String[] args) {
//		Q5. Write a program to find the sum of elements in an array. #include <iostream> ...
//		Q6. Write a program to find the largest element in an array. #include <iostream> ...
//		Q7. Write a program to sort an array in ascending order. ...
//		Q8. Write a program to remove duplicates from an array.
		
		
		int [] array = {2,4,5,6,7,1,1};
		List<Integer> list = new ArrayList<>();
		int sum=0;
		for (int i = 0; i < array.length; i++) {
			sum+=array[i];
			list.add(array[i]);
		}
		System.out.println(sum);
		
		Integer integer = list.stream().max(Comparator.naturalOrder()).get();
		System.out.println(integer);
		
		List<Integer> collect = list.stream().sorted().collect(Collectors.toList());
		collect.forEach((n)-> System.out.print(n +" "));
//	  list.stream().sorted().collect(Collectors.toList())
//		System.out.println(array2.toString());
		
	  list.stream().distinct().collect(Collectors.toList()).forEach(System.out::print);
	
		
	}

}
