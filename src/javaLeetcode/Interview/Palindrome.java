package javaLeetcode.Interview;

import java.util.Scanner;

public class Palindrome {
	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter numbers");
		int nextInt = sc.nextInt();
		
		System.out.println( "number" + nextInt);
		
		int reverse=0,num=nextInt;
		
		while(num!=0) {
			
			reverse= (reverse*10 ) + (num%10);
			num=num/10;
		}
		
		if(nextInt==reverse) {
			System.out.println("Palindrome" + reverse);
		}
		
	}

}
