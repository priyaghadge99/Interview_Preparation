package javaLeetcode;
class Solution {
	public static void main(String[] args) {
		
		String str1 = "ABABAB";
		String str2 = "ABAB";
		
		String gcdOfStrings = gcdOfStrings(str1, str2);
		System.out.println("gcdOfStrings : "+gcdOfStrings);
	}
	
  public static String gcdOfStrings(String str1, String str2) {
    if (str1.length() < str2.length())
      return gcdOfStrings(str2, str1);
    if (!str1.startsWith(str2))
      return "";
    if (str2.isEmpty()) {
    	System.err.println("Empty");
      return str1;
    }
    return gcdOfStrings(str2, mod(str1, str2));
  }

  private static String mod(String s1, final String s2) {
	  System.out.println(s1);
    while (s1.startsWith(s2))
      s1 = s1.substring(s2.length());
    System.out.println("s1: "+s1);
    return s1;
  }
}
