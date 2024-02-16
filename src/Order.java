import java.util.Arrays;

public class Order {

	
	int price;
	String statuus;
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public String getStatuus() {
		return statuus;
	}
	public void setStatuus(String statuus) {
		this.statuus = statuus;
	}
	public Order(int price, String statuus) {
		super();
		this.price = price;
		this.statuus = statuus;
	}
	@Override
	public String toString() {
		return "Order [price=" + price + ", statuus=" + statuus + "] \n";
	}
	
	public static void main(String[] args) {
		
		Order[] order = new Order[7];
		order[0]= new Order(3989, "Completed");
		order[1]= new Order(389, "Completed");
		order[2]= new Order(39, "Inprogress");
		order[3]= new Order(9, "Inprogress");
		
		System.out.println(Arrays.toString(order));
	}
	
	
}
