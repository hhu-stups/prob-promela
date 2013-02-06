/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package visitors;

import promela.analysis.*;
import promela.node.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

public class IfVisitor extends DepthFirstAdapter {

	private HashMap<String,String> typeMap;
	
	private MyLinkedList out = new MyLinkedList();
	private int ifIndex;
	private LinkedList<Integer> optionNrs = new LinkedList<Integer>();
	private LinkedList<Integer> gotoIndex = new LinkedList<Integer>();
	
	public IfVisitor(HashMap<String,String> typeMap) {
		this.typeMap = typeMap;
	}
	
	@Override
	public void inAIfStmnt(AIfStmnt node) {
		ifIndex = out.size();
		out.add(""+PromelaVisitor.lineNr);
		PromelaVisitor.lineNr++;
		
		PromelaVisitor.offset += "    ";
	}
	
	@Override
	public void outAIfStmnt(AIfStmnt node) {
		
		String ifLineNr = out.get(ifIndex);
		String options = "";
		
		ListIterator<Integer> iter1 = optionNrs.listIterator(0);
		while(iter1.hasNext()) {
			options += iter1.next() + ",";
		}

		options = options.substring(0, options.length() - 1);
		
		// fill in jump statements for 'if'
		String offset = PromelaVisitor.offset;
		String sub = offset.substring(0, offset.length() - 4);
		out.setInst(ifIndex, sub, ifLineNr, "if(["+options+"])");
		
		// complete goto-statements with the correct jump-number
		iter1 = gotoIndex.listIterator(0);
		while (iter1.hasNext()) {
			int index = iter1.next();
			out.setInst(index, offset, out.get(index), "igoto("+PromelaVisitor.lineNr+")");
		}
		/*
		for (int i = 0; i < gotoIndex.size(); i++) {
			String gotoLineNr = out.get(gotoIndex.get(i));
			out.setInst(gotoIndex.get(i), offset, gotoLineNr, "igoto("+PromelaVisitor.lineNr+")");
		}
		*/
		
		// complete break-statements with the correct jump-number
		/**
		 * it is an error to place a break statement where there
		 * is no surrounding repitition structure.
		for (int i = 0; i < out.size(); i++) {
			String inst = out.get(i);
			if (inst.endsWith("break")) {
				inst += "("+PromelaVisitor.lineNr+")";
				out.setEnd(i, inst);
			}
		}
		**/
		
		PromelaVisitor.offset = offset.substring(0, offset.length() - 4);
	}
	
	@Override
	public void caseAOptions(AOptions node) {
		
		optionNrs.add(PromelaVisitor.lineNr);
		
		PromelaVisitor.guardCondition = true;
		
		SequenceVisitor v = new SequenceVisitor(typeMap);
		node.getSequence().apply(v);
		out.addAll(v.getList());
		
		PromelaVisitor.guardCondition = false;
		
		gotoIndex.add(out.size());
		out.add(""+PromelaVisitor.lineNr);
		PromelaVisitor.lineNr++;
		
		if (node.getOptions() != null) {
			node.getOptions().apply(this);
		}
	}
	
	public MyLinkedList getList() {
		return out;
	}

}