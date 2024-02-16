package javaLeetcode.Interview;

public class Custom  {
	
	

	public static void main(String[] args) throws CustomException {
		throw new CustomException(" this is my custom exception");
		
	}
	
}
	
	
	class CustomException extends Exception{

		public CustomException(String msg) {
			super(msg);
		}

		
	}

