
//import java.util.Optional;
public class Optional {
	public static void main(String[] args) {
		String str = null;
		java.util.Optional<String> checknull = java.util.Optional.ofNullable(str);
		System.out.println("checknull "+checknull);
		
		System.out.println("=======================");
		String[] str1 = new String[10];
		java.util.Optional<String> checknull2 = java.util.Optional.ofNullable(str1[0]);
		
		System.out.println(checknull2);
	}

}


//Optional can help to prevent NullPointerException errors 
//by making it explicit when a variable may or may not contain a value. 
//This can lead to cleaner and more readable code