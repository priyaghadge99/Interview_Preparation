

// show basic on name 

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Product{
	
	int id ;
	String name;
	int price;
	public Product(int id, String name, int price) {
		super();
		this.id = id;
		this.name = name;
		this.price = price;
	}
	
	
	
	
}

public class JavaComparator {
	
	public static void main(String[] args) {
		
		List<Product> listofProduct = new ArrayList<>();
		listofProduct.add(new Product(1, "Laptop", 45000));
		listofProduct.add(new Product(2, "Cellphone", 8000));
		listofProduct.add(new Product(3, "BoatWatch", 1800));
		
		
		//sorting basic of name using lamda expression
		Collections.sort(listofProduct ,(p1,p2) -> {
			return p1.name.compareTo(p2.name);
		});
		
		
		for (Product product : listofProduct) {
			System.out.println(product.id +" "+product.name +" "+product.price);
		}
	}

}
