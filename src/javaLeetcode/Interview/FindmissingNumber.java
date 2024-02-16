package javaLeetcode.Interview;

public class FindmissingNumber {
	
	public static void main(String[] args) {
		
	    int[] array={4,3,8,7,5,2,6};
		int missingNumber = findMissingNum(array);
	       System.out.println("Missing Number is "+ missingNumber); 
		
	}

	private static int findMissingNum(int[] array) {
		int n = array.length+1;
		int sumofAll =  n*(n+1)/2;
		System.out.println("sumofAll " +sumofAll + " n :"+n );
		int add=0;
		
		for (int i = 0; i < array.length; i++) {
			
			add+= array[i];
			
			
		}
		
		
		
		return sumofAll-add;
	}

}
