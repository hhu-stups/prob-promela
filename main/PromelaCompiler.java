/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PushbackReader;
import java.io.StringReader;

import org.anarres.cpp.Feature;
import org.anarres.cpp.FileLexerSource;
import org.anarres.cpp.Preprocessor;
import org.anarres.cpp.PreprocessorListener;
import org.anarres.cpp.Token;
import org.anarres.cpp.Warning;

import promela.lexer.Lexer;
import promela.lexer.LexerException;
import promela.node.Start;
import promela.parser.Parser;
import promela.parser.ParserException;
import visitors.PromelaVisitor;

public class PromelaCompiler {

	private Start start;

	public PromelaCompiler(String fileIn) throws org.anarres.cpp.LexerException {

		try {
			// File tmpFile = new File(fileIn+".pre");

			// run the C-Preprocessor
			// Process process =
			// Runtime.getRuntime().exec("cpp -C "+fileIn+" "+tmpFile);
			/*
			 * Process process = Runtime .getRuntime() .exec("java -cp " +
			 * appDirectory + "/lib/anarres-cpp/anarres-cpp.jar;" + appDirectory
			 * + "/lib/anarres-cpp/gnu.getopt.jar;" + appDirectory +
			 * "/lib/anarres-cpp/log4j-1.2.13.jar org.anarres.cpp.Main " +
			 * fileIn);
			 * 
			 * 
			 * BufferedReader br = new BufferedReader(new InputStreamReader(
			 * process.getErrorStream())); String s = br.readLine(); while (s !=
			 * null) { System.out.println(s); s = br.readLine(); }
			 * 
			 * BufferedReader br2 = new BufferedReader(new InputStreamReader(
			 * process.getInputStream())); String expression = ""; String s2 =
			 * br2.readLine(); while (s2 != null) { expression += s2 + "\n"; s2
			 * = br2.readLine(); }
			 * 
			 * process.waitFor();
			 */

			// System.out.println(expression);

			// FileReader fr = new FileReader(tmpFile);
			// BufferedReader br = new BufferedReader(fr);
			// String line = "";
			// String expression = "";

			// ignore first two lines, which were added by the C-Preprocessor
			// br.readLine();
			// br.readLine();

			// while (line != null) {
			// line = br.readLine();
			// if (line != null) {
			// expression += line+"\n";
			// }
			// }

			// br.close();
			// fr.close();
			// tmpFile.delete();
			// System.out.println("expression:\n"+expression);

			/* call anarres cpp without system call - krings */
			Preprocessor pp = new Preprocessor();
			pp.addFeature(Feature.DIGRAPHS);
			pp.addFeature(Feature.TRIGRAPHS);
			pp.addFeature(Feature.LINEMARKERS);
			pp.addWarning(Warning.IMPORT);
			pp.setListener(new PreprocessorListener());
			pp.addMacro("__JCPP__");
			pp.getSystemIncludePath().add("/usr/local/include");
			pp.getSystemIncludePath().add("/usr/include");
			pp.getFrameworksPath().add("/System/Library/Frameworks");
			pp.getFrameworksPath().add("/Library/Frameworks");
			pp.getFrameworksPath().add("/Local/Library/Frameworks");

			pp.addInput(new FileLexerSource(new File(fileIn)));

			StringBuilder sbuild = new StringBuilder();

			try {
				for (;;) {
					Token tok = pp.token();
					if (tok == null) {
						break;
					}
					if (tok.getType() == Token.EOF) {
						// char eof = 26;
						// sbuild.append(eof);
						break;
					}
					sbuild.append(tok.getText());
				}
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}

			String expression = sbuild.toString();
			// expression = expression.substring(expression.indexOf("\n") + 1);
			// System.out.println(expression);

			Lexer lexer = new Lexer(new PushbackReader(new StringReader(
					expression)));
			Parser parser = new Parser(lexer);
			start = parser.parse();

		} catch (ParserException e) {
			e.printStackTrace();
		} catch (LexerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void start(String fileIn, String fileOut) {

		try {
			FileWriter fw = new FileWriter(fileOut);
			BufferedWriter bw = new BufferedWriter(fw);

			// print AST
			// ASTPrinter astPrinter = new ASTPrinter();
			// start.apply(astPrinter);

			PromelaVisitor promelaVisitor = new PromelaVisitor(fileIn, bw);
			start.apply(promelaVisitor);
			bw.close();
			fw.close();

			// save the typeMap for LTL-checking
			String tmpDir = System.getProperty("java.io.tmpdir");
			FileOutputStream fos = new FileOutputStream(tmpDir + "/typeMap.dat");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(promelaVisitor.getTypeMap());
			oos.close();
			fos.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void printUsage() {

		System.out.println("Usage:");
		System.out.println("java Main <promela-file>");
		System.out.println("  Will generate <promela-file>.pl");
		System.out.println("java Main -ltl <expression-file>");
		System.out.println("  will generate /tmp/ltl_prob[num].pl");
		System.out.println("  java \"Main\" <promela-file> must be run");
		System.out.println("  in first place to generate typeMap.dat.");
	}

	/*
	 * @param path to a promela file.
	 */
	public static void main(String[] args) throws Exception {

		ArgumentParser p = new ArgumentParser(args);

		if (p.hasOption("h") || p.hasOption("-help")) {
			printUsage();
		} else if (p.hasOption("ltl") && p.hasNextParam()) {
			String arg = p.nextParam();
			String fileOut = arg.substring(0, arg.indexOf('.')) + ".pl";
			System.out
					.println("Generating output for ltl-checking: " + fileOut);
			new PromelaLTLParser(arg, fileOut);
		} else if (p.hasNextParam()) {
			String fileIn = p.nextParam();

			PromelaCompiler m = new PromelaCompiler(fileIn);
			System.out.println("Generating output for interpreter: " + fileIn
					+ ".pl");
			m.start(fileIn, fileIn + ".pl");
		} else {
			printUsage();
		}
	}

}
