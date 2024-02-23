package javaLeetcode.String;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ReplaceStringintextFile {
	 static BufferedReader bufferReader=null;
	 static FileWriter writer = null;
	public static void main(String[] args) throws IOException {
		File filetomodefied = new File("F:\\filetest.txt");
		
		
		String replace = "85";
		String newreplace = "95";
		
		ReplaceStringintextFile(replace,newreplace,filetomodefied);
		
		
       
		
		
		
		
		
	}

	private static void ReplaceStringintextFile(String replace, String newreplace, File filetomodefied) throws IOException {
		try {
		String oldcontent = "";
		bufferReader = new BufferedReader(new FileReader(filetomodefied));
		

		String readLine = bufferReader.readLine();
		while (readLine!=null) {
			oldcontent = oldcontent+readLine+System.lineSeparator();
			System.out.println(oldcontent);
			readLine =bufferReader.readLine();
			
		}
		
		String replaceAll = oldcontent.replaceAll(replace, newreplace);
		
		writer = new FileWriter(filetomodefied);
		writer.write(replaceAll);
		
		} finally {
			bufferReader.close();
			writer.close();
			
		}
		
	}
	
	
	
}
