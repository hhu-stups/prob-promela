/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package visitors;

import promela.analysis.*;
import promela.node.*;

import java.util.HashMap;

public class ConstVisitor extends DepthFirstAdapter {

	private MyLinkedList out = new MyLinkedList();
	private Position pos = new Position();
	
	
	public ConstVisitor(HashMap<String,String> typeMap) {
	}
	
	@Override
    public void inATrueConst(ATrueConst node) {
    	out.add("true");
    	pos.setStartRow(node.getTrue().getLine());
		pos.setStartCol(node.getTrue().getPos());
		pos.setEndRow(node.getTrue().getLine());
		pos.setEndCol(node.getTrue().getPos() + node.getTrue().getText().length());
    }
	@Override
    public void inAFalseConst(AFalseConst node) {
    	out.add("false");
    	pos.setStartRow(node.getFalse().getLine());
		pos.setStartCol(node.getFalse().getPos());
		pos.setEndRow(node.getFalse().getLine());
		pos.setEndCol(node.getFalse().getPos() + node.getFalse().getText().length());
    }
	@Override
    public void inASkipConst(ASkipConst node) {
    	out.add("skip");
    	pos.setStartRow(node.getSkip().getLine());
		pos.setStartCol(node.getSkip().getPos());
		pos.setEndRow(node.getSkip().getLine());
		pos.setEndCol(node.getSkip().getPos() + node.getSkip().getText().length());
    }
	@Override
    public void inANumberConst(ANumberConst node) {
    	if (node.getMinus() != null) {
    		out.add("-");
    	}
    	out.add(node.getNumber().getText());
    	pos.setStartRow(node.getNumber().getLine());
		pos.setStartCol(node.getNumber().getPos());
		pos.setEndRow(node.getNumber().getLine());
		pos.setEndCol(node.getNumber().getPos() + node.getNumber().getText().length());
    }
	@Override
    public void inAPidConst(APidConst node) {
    	out.add("pid");
    	pos.setStartRow(node.getProcessid().getLine());
		pos.setStartCol(node.getProcessid().getPos());
		pos.setEndRow(node.getProcessid().getLine());
		pos.setEndCol(node.getProcessid().getPos() + node.getProcessid().getText().length());
    }
	@Override
	public void inANrPrConst(ANrPrConst node) {
		out.add("nr_pr");
		pos.setStartRow(node.getNrPr().getLine());
		pos.setStartCol(node.getNrPr().getPos());
		pos.setEndRow(node.getNrPr().getLine());
		pos.setEndCol(node.getNrPr().getPos() + node.getNrPr().getText().length());
	}
	
	
	public Position getPos() {
		return pos;
	}
	
	public MyLinkedList getList() {
		 return out;
	}	 
}
