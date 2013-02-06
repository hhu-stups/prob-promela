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

public class DoVisitor extends DepthFirstAdapter {

	private HashMap<String,String> typeMap;
	
	private MyLinkedList out = new MyLinkedList();
	private int doIndex;
	private LinkedList<Integer> optionNrs = new LinkedList<Integer>();
	
	public DoVisitor(HashMap<String,String> typeMap) {
		this.typeMap = typeMap;
	}
	
	@Override
	public void inADoStmnt(ADoStmnt node) {
		doIndex = out.size();
		out.add(""+PromelaVisitor.lineNr);
		PromelaVisitor.lineNr++;
		
		PromelaVisitor.offset += "    ";
	}
	
	@Override
	public void outADoStmnt(ADoStmnt node) {
		
		String doLineNr = out.get(doIndex);
		String options = "";
		
		ListIterator<Integer> iter1 = optionNrs.listIterator(0);
		while(iter1.hasNext()) {
			options += iter1.next() + ",";
		}
		/*
		for (int i = 0; i < optionNrs.size(); i++) {
			options += optionNrs.get(i) + ",";
		}
		*/
		options = options.substring(0, options.length() - 1);
		
		// fill in jump statements for 'do'
		String offset = PromelaVisitor.offset;
		String sub = offset.substring(0, offset.length() - 4);
		out.setInst(doIndex, sub, doLineNr, "do(["+options+"])");
		
		// complete break-statements with the correct jump-number
		ListIterator<String> iter2 = out.listIterator(0);
		while (iter2.hasNext()) {
			String inst = iter2.next();
			if (inst.endsWith("break")) {
				inst += "("+PromelaVisitor.lineNr+")";
				out.setEnd(iter2.previousIndex(), inst);
			}
		}
		
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
		
		// add a goto-statement to continue do-loop
		String doLineNr = out.get(doIndex);
		out.addInst("igoto("+doLineNr+")");
		PromelaVisitor.lineNr++;
		
		if (node.getOptions() != null) {
			node.getOptions().apply(this);
		}
	}
	
	
	public MyLinkedList getList() {
		return out;
	}

}