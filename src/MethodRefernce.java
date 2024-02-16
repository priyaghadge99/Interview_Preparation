

@FunctionalInterface // optional
interface Sample {
	void say();
}


public class MethodRefernce {
	
	public static void main(String[] args) {
		
		Sample sample = MethodRefernce :: samplefunction;
		sample.say();  
		
		
	}
	
	public static void samplefunction() {
		System.out.println("calling in static method");
	}
	
	

}
