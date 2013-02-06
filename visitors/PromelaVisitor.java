/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package visitors;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import promela.analysis.DepthFirstAdapter;
import promela.node.AAnarresCppComment;
import promela.node.AInit;
import promela.node.AInline;
import promela.node.ALongCppComment;
import promela.node.AManyNameLst;
import promela.node.AMtype;
import promela.node.AOneNameLst;
import promela.node.AProctypeModule;
import promela.node.AShortCppComment;
import promela.node.ASpec;
import promela.node.AUtype;
import promela.node.AVarschansModule;
import promela.node.PUdecl;

/**
 * 
 * @author dennis.winter@uni-duesseldorf.de
 * @version 2008.03.20
 * 
 */

public class PromelaVisitor extends DepthFirstAdapter {

	public static int lineNr = 1;
	public static String offset = "";
	// maps line numbers from the .pml file to the .pl file
	public static HashMap<Integer, Position> lineMap = new HashMap<Integer, Position>(
			20);

	public static int NORMAL = 0;
	public static int ATOMIC = 1;
	public static int D_STEP = 2;
	public static int mode = NORMAL;
	// to mark first atomic or d_step statement
	public static String atomicStart = "";
	public static boolean guardCondition = false;
	public static String constraint = null;
	// maps inline-function to their inline-node.
	public static HashMap<String, AInline> inlineMap = new HashMap<String, AInline>(
			2);
	// for inline substitutions
	public static HashMap<String, String> substitutionMap = null;
	// the promela filename
	public static String fileIn;
	// current File (when include's are used)
	public static String currentFile;
	// offset, to adjust the line number (when include's are used)
	public static int adjustLine = 0;

	// maps variables to their types
	private HashMap<String, String> typeMap = new HashMap<String, String>(10);
	private BufferedWriter bw;
	private Position pos = new Position();

	public PromelaVisitor(String fileIn, BufferedWriter bw) {
		PromelaVisitor.fileIn = fileIn;
		this.bw = bw;
	}

	@Override
	public void inALongCppComment(ALongCppComment node) {
		int number1 = Integer.parseInt(node.getFirst().getText());
		int number2 = node.getString().getLine();
		String fileWithQuotes = node.getString().getText();
		currentFile = fileWithQuotes.substring(1, fileWithQuotes.length() - 1);
		adjustLine = number2 - number1 + 1;
	}

	@Override
	public void inAShortCppComment(AShortCppComment node) {
		int number1 = Integer.parseInt(node.getNumber().getText());
		int number2 = node.getString().getLine();
		String fileWithQuotes = node.getString().getText();
		currentFile = fileWithQuotes.substring(1, fileWithQuotes.length() - 1);
		adjustLine = number2 - number1 + 1;
	}

	@Override
	public void inAAnarresCppComment(AAnarresCppComment node) {
		int number1 = Integer.parseInt(node.getFirst().getText());
		int number2 = node.getString().getLine();
		String fileWithQuotes = node.getString().getText();
		currentFile = fileWithQuotes.substring(1, fileWithQuotes.length() - 1);
		adjustLine = number2 - number1 + 1;
	}

	@Override
	public void caseAUtype(AUtype node) {
		inAUtype(node);
		if (node.getTypedef() != null) {
			node.getTypedef().apply(this);
		}
		if (node.getName() != null) {
			node.getName().apply(this);
		}
		if (node.getLBrace() != null) {
			node.getLBrace().apply(this);
		}
		{
			print("\n");
			String typedefName = getPrologString(node.getName().getText());
			DeclLst3Visitor v = new DeclLst3Visitor(typeMap, typedefName);

			List<PUdecl> copy = new ArrayList<PUdecl>(node.getUdecl());
			for (PUdecl e : copy) {
				e.apply(v);
			}
			print(v.getList());
		}
		if (node.getRBrace() != null) {
			node.getRBrace().apply(this);
		}
		outAUtype(node);
	}

	@Override
	public void caseAInline(AInline node) {
		// save the inline node in a HashMap.
		inlineMap.put(node.getName().getText(), node);
		// do not visit the inline node now.
	}

	@Override
	public void caseAProctypeModule(AProctypeModule node) {
		HashMap<String, String> typeMap2 = new HashMap<String, String>(10);
		// put global mappings to a new (local) mapping list.
		typeMap2.putAll(typeMap);
		ProctypeVisitor v = new ProctypeVisitor(typeMap2);
		node.getProctype().apply(v);
		print(v.getList());
	}

	@Override
	public void inAMtype(AMtype node) {
		print("inst(" + lineNr + ",assign(mtype,[");

		pos.setStartRow(node.getMtypetok().getLine());
		pos.setStartCol(node.getMtypetok().getPos());
	}

	@Override
	public void outAMtype(AMtype node) {
		print("])).\n");

		pos.setEndRow(node.getRBrace().getLine());
		pos.setEndCol(node.getRBrace().getPos() + 1);
		lineMap.put(lineNr, pos);
		lineNr++;
		pos = new Position();
	}

	@Override
	public void inAOneNameLst(AOneNameLst node) {
		typeMap.put(node.getName().getText(), "ctype");
		print(getPrologString(node.getName().getText()));
	}

	@Override
	public void inAManyNameLst(AManyNameLst node) {
		typeMap.put(node.getName().getText(), "ctype");
		print(getPrologString(node.getName().getText()));
		print(",");
	}

	@Override
	public void caseAVarschansModule(AVarschansModule node) {
		inAVarschansModule(node);
		if (node.getOneDecl() != null) {
			DeclLstVisitor v = new DeclLstVisitor(typeMap);
			node.getOneDecl().apply(v);
			print(v.getList());
		}
		if (node.getSeparator() != null) {
			node.getSeparator().apply(this);
		}
		outAVarschansModule(node);
	}

	@Override
	public void caseAInit(AInit node) {
		print("\n");
		print(offset + "inst(" + lineNr + ",init).\n");
		lineNr++;
		offset += "    ";

		inAInit(node);
		if (node.getInittok() != null) {
			node.getInittok().apply(this);
		}
		if (node.getPriority() != null) {
			node.getPriority().apply(this);
		}
		if (node.getLBrace() != null) {
			node.getLBrace().apply(this);
		}
		if (node.getSequence() != null) {
			SequenceVisitor v = new SequenceVisitor(typeMap);
			node.getSequence().apply(v);
			print(v.getList());
		}
		if (node.getRBrace() != null) {
			node.getRBrace().apply(this);
		}
		outAInit(node);

		print(offset + "inst(" + lineNr + ",destructor).\n");
		offset = offset.substring(0, offset.length() - 4);

		pos.setStartRow(node.getRBrace().getLine());
		pos.setStartCol(node.getRBrace().getPos());
		pos.setEndRow(node.getRBrace().getLine());
		pos.setEndCol(node.getRBrace().getPos() + 1);
		lineMap.put(lineNr, pos);
		lineNr++;
		pos = new Position();
	}

	@Override
	public void outASpec(ASpec node) {
		print("\n");
		print(lineMap);
	}

	private String getPrologString(String name) {

		int c = name.charAt(0);
		// test, if name starts with a capital letter.
		if (c >= 65 && c <= 90) {
			name = "'" + name + "'";
		}
		return name;
	}

	private void print(HashMap<Integer, Position> lineMap) {

		Iterator<Integer> iter = lineMap.keySet().iterator();

		while (iter.hasNext()) {

			int i = iter.next();
			Position p = lineMap.get(i);
			print("line(" + i + "," + p.getStartRow() + "," + p.getStartCol()
					+ "," + p.getEndRow() + "," + p.getEndCol() + ").\n");
		}
	}

	private void print(String s) {

		try {
			bw.write(s);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void print(LinkedList<String> list) {

		Iterator<String> iter = list.listIterator(0);
		while (iter.hasNext()) {
			print(iter.next());
		}
	}

	public HashMap<String, String> getTypeMap() {
		return typeMap;
	}

}