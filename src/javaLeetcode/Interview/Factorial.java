package javaLeetcode.Interview;

public class Factorial {
	
	public static void main(String[] args) {
		
		
		int num=4;
		int factorial= 1;
		
		for (int i = 1; i <= num; i++) {
			factorial*=i;
			System.out.println(factorial);
		}
//1*2*3*4=24
	System.out.println("Final value factorial: "+factorial +" of number "+num);
	}

}
