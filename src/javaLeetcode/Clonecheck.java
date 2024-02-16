
package javaLeetcode;
public class Clonecheck implements Cloneable{
	
	
	private int num=2;
	private int id =9;
	
	
	
	
	
public Clonecheck(int num, int id) {
		super();
		this.num = num;
		this.id = id;
	}





//	@Override
//	protected Object clone() throws CloneNotSupportedException {
//		// TODO Auto-generated method stub
//		return super.clone();
//	}

	public static void main(String[] args) throws CloneNotSupportedException {
		
		Clonecheck obj = new Clonecheck(73, 76);
		Object clone = obj.clone();
		
		
		System.out.println(clone.toString());
		
	}
}