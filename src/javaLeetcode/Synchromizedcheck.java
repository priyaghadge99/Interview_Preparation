package javaLeetcode;

public class Synchromizedcheck extends Thread  implements Cloneable{
	
	  private int increase_counter;
      public synchronized int increase() {
              increase_counter = increase_counter + 1;
              return increase_counter;
      }
      
      
      
      
   




	public static void main(String[] args) throws CloneNotSupportedException {
		
    	  Thread thread1 = new Thread();
    	  Synchromizedcheck obj = new Synchromizedcheck();
    	  Object clone = obj.clone();
    	 
    	  
	}

}
