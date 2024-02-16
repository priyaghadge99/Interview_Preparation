import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ForEachIterable {

	public static void main(String[] args) {

		List<String> list = new ArrayList<>();
		list.add("Apple");
		list.add("Bannana");
		list.add("Mango");
		list.add("Mango");
		int count=0;
		int duplicateIndex=0;
//		list.forEach((n)-> System.out.println(n));
//		list.forEach((n)-> System.out.println(n));
		for (String string : list) {
			
			for (int i = 0; i < list.size(); i++) {
				if(list.contains(string)) {
					
					count++;
					if (count>1) {
						System.out.println(string);
						
					}
				
		        	}
					
				}
			}
		
		
		System.out.println("========treeset=========="); //maintain natural ordering abcd
		Set<String> treeset = new TreeSet<>();
		treeset.add("Apple");
		treeset.add("Mango");
		treeset.add("Bannana");
		
		treeset.forEach((n)-> System.out.println(n));
		
		System.out.println("========linkedhashset==========");
		
		Set<String> linkedhashset = new LinkedHashSet<>(); //maintain insertion order
		linkedhashset.add("Apple");
		linkedhashset.add("Mango");
		linkedhashset.add("Mango");
		linkedhashset.add("Bannana");
		
		linkedhashset.forEach((n)-> System.out.println(n));
		
		
	}

}
