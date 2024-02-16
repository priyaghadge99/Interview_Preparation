import java.util.function.BiFunction;

class Arithmetic{
	
	
	private int a;
	private int b;
	public static int add (int a ,int b) {
		return a+b;
		
	}
	
	public  int substract (int a ,int b) {
		return a-b;
		
	}

	public Arithmetic(int a, int b) {
		super();
		this.a = a;
		this.b = b;
		
		System.out.println("Its Constructor calling \na=" +a +" b= " +b);
	}

	public Arithmetic() {
		// TODO Auto-generated constructor stub
	}
	
	
	

	
}

interface ForConstructor {
	
	void sum(int a ,int b);
}



public class MethodReference2 {
	public int substract(int a,int b) {
		return a-b;
	}
	
	public static void main(String[] args) {
		
		
		//using predefined funcational interface 
		BiFunction<Integer,  Integer, Integer> methodref = Arithmetic :: add;
		
		Integer apply = methodref.apply(10, 20);
		System.out.println("Apply :" +apply);
		
		
//		MethodReference2 methodReference2 = new MethodReference2();
		Arithmetic arithmetic = new Arithmetic();
		
		BiFunction<Integer,  Integer, Integer> methodref2 = arithmetic :: substract;
		
		
		System.out.println(methodref2.apply(20, 20));
		
		System.out.println("For Constructor ref");
		
		ForConstructor forConstructor = Arithmetic::new;
		forConstructor.sum(10, 20);
		System.out.println();
		
		
	}

}
