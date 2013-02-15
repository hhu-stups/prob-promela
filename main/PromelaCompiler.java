/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package main;

import java.io.*;
import java.util.HashMap;

import promela.lexer.*;
import promela.node.*;
import promela.parser.*;
import visitors.ASTPrinter;
import visitors.PromelaVisitor;

public class PromelaCompiler {

private Start start;

public PromelaCompiler(String fileIn, String appDirectory) {
	
        try {
		//File tmpFile = new File(fileIn+".pre");

		// run the C-Preprocessor
		//Process process = Runtime.getRuntime().exec("cpp -C "+fileIn+" "+tmpFile);
		Process process = Runtime.getRuntime().exec("java -cp "+ appDirectory +"/lib/anarres-cpp/anarres-cpp.jar;"+ appDirectory +"/lib/anarres-cpp/gnu.getopt.jar;"+ appDirectory +"/lib/anarres-cpp/log4j-1.2.13.jar org.anarres.cpp.Main "+fileIn);
		
		BufferedReader br = new BufferedReader(new
			InputStreamReader(process.getErrorStream()));
		String s = br.readLine();
		while (s != null) {
			System.out.println(s);
			s = br.readLine();
		}										 

		BufferedReader br2 = new BufferedReader(new
			InputStreamReader(process.getInputStream()));
		String expression = "";
		String s2 = br2.readLine();
		while (s2 != null) {
			expression += s2+"\n";
			s2 = br2.readLine();
		}

		process.waitFor();
        	
        	Lexer lexer = new Lexer(new PushbackReader(new StringReader(expression)));
        	Parser parser = new Parser(lexer);
        	start = parser.parse();

			
	    } catch (ParserException e) {
	    	e.printStackTrace();
	    } catch (LexerException e) {
	    	e.printStackTrace();
	    } catch (IOException e) {
	    	e.printStackTrace();
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
    }

    public void start(String fileIn, String fileOut) {
    	
    	try {
	    	FileWriter fw = new FileWriter(fileOut);
	    	BufferedWriter bw = new BufferedWriter(fw);
	    	
	        PromelaVisitor promelaVisitor = new PromelaVisitor(fileIn,bw);
	        start.apply(promelaVisitor);
	        bw.close();
	        fw.close();
	        
	        // save the typeMap for LTL-checking
       		String tmpDir = System.getProperty("java.io.tmpdir");
	        FileOutputStream fos = new FileOutputStream(tmpDir+"/typeMap.dat");
	        ObjectOutputStream oos = new ObjectOutputStream(fos);
	        oos.writeObject(promelaVisitor.getTypeMap());
	        oos.close();
	        fos.close();	        
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    }

    public static void printUsage() {
		System.out.println("Usage:");
		System.out.println("java Main <promela-file> <app_dir>");
		System.out.println("  Will generate <promela-file>.pl");
		System.out.println("java Main -ltl <expression-file>");
		System.out.println("  will generate /tmp/ltl_prob[num].pl");
		System.out.println("  java \"Main\" <promela-file> <app_dir> must be run");
		System.out.println("  in first place to generate typeMap.dat.");
	}

    /*
     * @param path to a promela file.
     */
	public static void main(String[] args) throws Exception {
	  
		ArgumentParser p = new ArgumentParser(args);

		if (p.hasOption("h") || p.hasOption("-help")) {
			printUsage();
		}
		else if (p.hasOption("ltl") && p.hasNextParam()) {
			String arg = p.nextParam();
			String fileOut = arg.substring(0,arg.indexOf('.'))+".pl";
			System.out.println("Generating output for ltl-checking: "+fileOut);
			new PromelaLTLParser(arg, fileOut);
		}
		else if (p.hasNextParam()) {
			String fileIn = p.nextParam();
            if (p.hasNextParam()) {
                String appDirectory = p.nextParam();
                PromelaCompiler m = new PromelaCompiler(fileIn, appDirectory);
                System.out.println("Generating output for interpreter: "+fileIn+".pl");
                m.start(fileIn, fileIn+".pl");
            }
            else {
                printUsage();
            }
		}
		else {
			printUsage();
		}
	}

}