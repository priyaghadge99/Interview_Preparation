import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

interface Operation{
	int operationPerform(int i,int j);
}


public class LambdaExpression {
	
	public static void main(String[] args) {
		
		Operation add = (int a ,int b)-> a+b;
		int operationPerform = add.operationPerform(10, 20);
		System.out.println("operationPerform: Sum "+operationPerform);
		
		Operation substract = (a ,b)-> a-b;
		
		System.out.println("Substract "+substract.operationPerform(20, 10));
		
		Order order =  new Order(3989, "Completed");
		
		///Predicate
		Predicate<Order> predicateAmount = (Order t)-> t.getPrice()>100  && t.getStatuus()=="Inprogress";
//		Predicate<Order> predicateStatuc = (Order t)-> t.getStatuus()=="Inprogress";
//		Predicate<Order> combined = predicateAmount.and(predicateStatuc);
		System.out.println("predicate : test() "+predicateAmount.test(order));
		
		//use functional interface //consumer , Predicate ,supplierto invoke build in method
		Consumer<Integer> consumer = (t)-> System.out.println("Consumer accept() but return  nothing" +t);
		consumer.accept(89);
		
		Random random = new Random();
		Supplier<Integer> supplier = () -> random.nextInt(100);
		
		Integer randomNumber =  supplier.get();
		System.out.println("Supplier get() : " +randomNumber);
		
		
		Function<Order, Integer> function= ( Order orders ) ->   orders.getPrice();
		System.out.println("Function : ( , )  apply"+function.apply(order));
		
		//Remove the word which has odd length
		List<String> list = new ArrayList<>();
		
		list.add("abcd"); //4
		System.out.println(list.size());
		
		list.add("abcde"); //5
		System.out.println(list.size());
		list.add("abcdef"); //6
		
		boolean removeIf = list.removeIf((String word)-> word.length()%2 !=0);
		System.out.println(list.size());
		list.forEach((n)-> System.out.println(n));
	    System.out.println("=============================="); 
	    //Create a String that consist of first letter of list
	    
	    List<String> listofWord = new ArrayList<String>();
	    listofWord.add("Sonam"); //4
	    listofWord.add("Simba"); //5
	    listofWord.add("Kushal");
		
		StringBuilder sb = new StringBuilder();
		
		Consumer<String> consumerlist = (String s)-> sb.append(s.charAt(0));
		listofWord.forEach(consumerlist);
		System.out.println("Word : "+sb.toString());
		
		//Replace each word with uppercase 
		UnaryOperator<String > unaryOperator = (String str) -> str.toUpperCase();
		listofWord.replaceAll(unaryOperator);
		StringBuilder sbs = new StringBuilder();
		
		sbs.append(listofWord);
		System.out.println("All Word in Uppercase" +sbs.toString());
		
	}

}
