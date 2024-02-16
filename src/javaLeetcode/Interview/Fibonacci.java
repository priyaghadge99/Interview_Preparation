package javaLeetcode.Interview;

public class Fibonacci {
	public static void main(String[] args) {
		
//		 System.out.print("0 1 1 2 ");
		int num =10;
		
//		printfibonacci(1,2,10);
//		int printwithoutRecursion = printwithoutRecursion(num);  //printing nth fibbonaaci numbers
		
		printwithoutRecursion(num);
	}

	private static void printwithoutRecursion(int num) {
		int a=0, b=1 , c ;
		if (num==0) {
			return ;
			
		}
		
		
		for(int i=2;i<num;i++) {
			c=a+b;
			a= b;
			b= c;
			System.out.println(c +" ");
		}
		return ;
		
		
	}

	private static void printfibonacci(int i, int j, int k) {
		
		if(k==0) {
			return ;
		}
		
		System.out.printf(i+j +" ");
		printfibonacci(j, i+j, --k);  //using recursion
	}

}

