/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package visitors;

import java.util.LinkedList;

public class MyLinkedList extends LinkedList<String> {
	
	public MyLinkedList() {
		super();
	}
	
	public void addBegin(String s) {
		
		if (PromelaVisitor.mode == PromelaVisitor.NORMAL) {
			add(PromelaVisitor.offset+"inst("+PromelaVisitor.lineNr
					+","+s);
		}
		else if (PromelaVisitor.mode == PromelaVisitor.ATOMIC) {
			add(PromelaVisitor.offset+"inst("+PromelaVisitor.lineNr
					+",p_atomic"+PromelaVisitor.atomicStart+"("+s);
			PromelaVisitor.atomicStart = "";
		}
		else if (PromelaVisitor.mode == PromelaVisitor.D_STEP) {
			add(PromelaVisitor.offset+"inst("+PromelaVisitor.lineNr
					+",d_step"+PromelaVisitor.atomicStart+"("+s);
			PromelaVisitor.atomicStart = "";
		}
	}
	
	public void addInst(String s) {
		if (PromelaVisitor.mode == PromelaVisitor.NORMAL) {
			add(PromelaVisitor.offset+"inst("+PromelaVisitor.lineNr
					+","+ s +").\n");
		}
		else if (PromelaVisitor.mode == PromelaVisitor.ATOMIC) {
			add(PromelaVisitor.offset+"inst("+PromelaVisitor.lineNr
					+",p_atomic"+PromelaVisitor.atomicStart+"("+ s +")).\n");
			PromelaVisitor.atomicStart = "";
		}
		else if (PromelaVisitor.mode == PromelaVisitor.D_STEP) {
			add(PromelaVisitor.offset+"inst("+PromelaVisitor.lineNr
					+",d_step"+PromelaVisitor.atomicStart+"("+ s +")).\n");
			PromelaVisitor.atomicStart = "";
		}
	}
	
	public void addEnd(String s) {
		
		if (PromelaVisitor.mode == PromelaVisitor.NORMAL) {
			add(s+").\n");
		}
		else if (PromelaVisitor.mode == PromelaVisitor.ATOMIC) {
			add(s+")).\n");
		}
		else if (PromelaVisitor.mode == PromelaVisitor.D_STEP) {
			add(s+")).\n");
		}
	}
	
	// this method is used by DoVisitor and IfVisitor
	public void setInst(int index, String offset, String lineNr, String s) {
		
		if (PromelaVisitor.mode == PromelaVisitor.NORMAL) {
			set(index, offset+"inst("+lineNr
					+","+ s +").\n");
		}
		else if (PromelaVisitor.mode == PromelaVisitor.ATOMIC) {
			set(index, offset+"inst("+lineNr
					+",p_atomic("+ s +")).\n");
		}
		if (PromelaVisitor.mode == PromelaVisitor.D_STEP) {
			set(index, offset+"inst("+lineNr
					+",d_step("+ s +")).\n");
		}
	}
	
	// this method is used by DoVisitor and IfVisitor
	public void setEnd(int index, String s) {
		
		if (s.contains("p_atomic")) {
			set(index, s+")).\n");
		}
		else if (s.contains("d_step")) {
			set(index, s+")).\n");
		}
		else {
			set(index, s+").\n");
		}
	}
	
	/**
	public void addNormalBegin(String s) {
		add(PromelaVisitor.offset+"inst("+PromelaVisitor.lineNr+","+s);
	}
	public void addNormalInst(String s) {
		add(PromelaVisitor.offset+"inst("+PromelaVisitor.lineNr
				+","+ s +").\n");
	}
	public void addNormalEnd(String s) {
		add(s+").\n");
	}
	public void setNormalInst(int index, String offset, String lineNr, String s) {
		set(index, offset+"inst("+lineNr
				+","+ s +").\n");
	}
	public void setNormalEnd(int index, String s) {
		set(index, s+").\n");
	}
	**/
	
	@Override
	public String toString() {
		
		String ret = "";
		
        for (int i = 0; i < this.size(); i++) {
			ret += this.get(i);
		}
        
        return ret;
	}
	
}
