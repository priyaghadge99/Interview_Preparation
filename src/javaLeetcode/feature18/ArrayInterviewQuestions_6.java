package javaLeetcode.feature18;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.stream.IntStream;

public class ArrayInterviewQuestions_6 {
	//merge two unsorted array
	
	public static void main(String[] args) {
		
		
		
		int [] arrayA = new int [] {2,3,4,5,7};
		int [] arrayB = new int [] {2,3,4,5,7};
		int [] mergeArray = new int [arrayA.length+arrayB.length];
		System.out.println("arrayA : "+Arrays.toString(arrayA));
		System.out.println("arrayB : "+Arrays.toString(arrayB));
		int[] mergeTwoUnsortedArray = mergeTwoUnsortedArray(arrayA,arrayB);
		System.out.println("merge Array : "+Arrays.toString(mergeTwoUnsortedArray));
		
		
		
	}

	private static int[] mergeTwoUnsortedArray(int[] arrayA, int[] arrayB) {
		int [] mergeArray = new int [arrayA.length+arrayB.length];
		
		int i=0,j=0,k=0;
		while (i<arrayA.length) {
			mergeArray[k] = arrayA[i];
			i++;
			k++;
		}
		while (j<arrayB.length) {
			mergeArray[k]=arrayB[j];
			j++;
			k++;
		}
		
		//========================to remove Duplicates===================
		HashSet<Integer> settoRemoveDuplicates = new HashSet<>();
		for (Integer integer : mergeArray) {
			settoRemoveDuplicates.add(integer);
		}
		
		Iterator<Integer> iterator = settoRemoveDuplicates.iterator();
		int mergearray[] = new int[settoRemoveDuplicates.size()];
		int n=0;
		while (iterator.hasNext()) {
			mergearray[n]= (Integer) iterator.next();
			n++;
			
		}
		
		//===================================================================
		
		Arrays.sort(mergearray);
		
		
		
		return mergearray;
		
	}
	
	
	

}
