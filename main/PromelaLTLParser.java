/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package main;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;

import promela.lexer.*;
import promela.node.*;
import promela.parser.*;
import visitors.ExprVisitor;
import visitors.PromelaVisitor;

public class PromelaLTLParser {

	private Start start;

	public PromelaLTLParser(String fileIn, String fileOut) {
	
		try {
		
			FileReader fr = new FileReader(fileIn);
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			String exprs = "";
		
			while (line != null) {
				line = br.readLine();
				if (line != null) {
					exprs += line+"\n";
				}
			}
			
			br.close();
			fr.close();

			start(exprs, fileIn, fileOut);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void start(String exprs, String fileIn, String fileOut) {

		try {
			LinkedList exprList = prologListParser(exprs);

			FileWriter fw = new FileWriter(fileOut);
			BufferedWriter bw = new BufferedWriter(fw);

			bw.write("[");

			for (int i = 0; i < exprList.size(); i++) {

				String dummyProgram = "init { "+exprList.get(i)+" }";
				
				Lexer lexer = new Lexer(new PushbackReader(new StringReader(dummyProgram)));
				Parser parser = new Parser(lexer);
				start = parser.parse();

				bw.write(parse(dummyProgram, fileIn));

				if ((i+1) < exprList.size()) {
					bw.write(",");
				}
			}
	
			bw.write("].");
			
			bw.close();
	        fw.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
    }

    private String parse(String dummyProgram, String fileIn) {
    	
		String result = "";

    	try {
	
	    	// get the typeMap from typeMap.dat
			String tmpDir = System.getProperty("java.io.tmpdir");
	    	FileInputStream fis = new FileInputStream(tmpDir+"/typeMap.dat");
	    	ObjectInputStream ois = new ObjectInputStream(fis);
	    	HashMap<String, String> typeMap = (HashMap<String, String>) ois.readObject();
	    	ois.close();
	    	fis.close();
	    	
			
			PromelaVisitor.fileIn = fileIn;
			PromelaVisitor.currentFile = fileIn;
			ExprVisitor exprVisitor = new ExprVisitor(typeMap);
	        start.apply(exprVisitor);
	        LinkedList<String> list = exprVisitor.getList();
	        
	        for (int i = 0; i < list.size(); i++) {
				result += list.get(i);
			}
	        
    	}
    	catch (FileNotFoundException e) {
    		e.printStackTrace();
    	}
    	catch (ClassNotFoundException e) {
    		e.printStackTrace();
    	}
    	catch (IOException e) {
    		System.err.println("Invalid Expression.");
    		e.printStackTrace();
    	}
		
		return result;
	}


	/*
	* Little Parser
	* @return elements from a prolog list.
	*/
	private LinkedList<String> prologListParser(String exprs) {
  
		int openParenthese = 0;
		LinkedList<String> exprList = new LinkedList<String>();
		int start = 0;

		for (int i = 0; i < exprs.length(); i++) {
			switch(exprs.charAt(i)) {
				case '(':	openParenthese++;
							break;
				case ')':	openParenthese--;
							break;
				case ',':	if (openParenthese == 0) {
								exprList.add(exprs.substring(start,i));
								start = i + 1;
							}
							break;
				default : //skip
			}
		}

		return exprList;
	}

}

