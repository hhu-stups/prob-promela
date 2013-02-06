/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package visitors;

import promela.analysis.*;
import promela.node.*;

import java.util.HashMap;

/*
 * This visitor is used for record structures.
 * It will be instantiated from VarrefVisitor only.
 */
public class Varref2Visitor extends DepthFirstAdapter {

	private HashMap<String,String> typeMap;
	private MyLinkedList out = new MyLinkedList();
	private Position pos = new Position();
	
	
	public Varref2Visitor(HashMap<String,String> typeMap) {
		this.typeMap = typeMap;
	}
	
	@Override
	public void inASingleVarref(ASingleVarref node) {
	    	
		if (node.getArrayref() != null) {
			out.add("array(");
	    }
		
		String name = node.getName().getText();
		if (PromelaVisitor.substitutionMap != null) {
			String name2 = PromelaVisitor.substitutionMap.get(name);
			if (name2 != null) {
				name = name2;
			}
		}
	    	
	    String type = typeMap.get(name);
	    if (type == null) {
	    	throw new IllegalArgumentException("No known type for variable '"
	    			+name
	    			+"'. Line: "+(node.getName().getLine() - PromelaVisitor.adjustLine)
	    			+" File: "+PromelaVisitor.currentFile);
	    }
	    	
	    if (type.equals("chan")) {
	        out.add("chan("+getPrologString(name)+")");
	    }
	    else if (type.equals("ctype")) {
	        out.add("ctype("+getPrologString(name)+")");
	    }
	    else {
	    	out.add(getPrologString(name));
	    }
	    
	    pos.setStartRow(node.getName().getLine());
		pos.setStartCol(node.getName().getPos());
	}
	    
	
	@Override
	public void caseARecordVarref(ARecordVarref node)
    {	
        inARecordVarref(node);
        if(node.getVarref() != null)
        {
        	Varref2Visitor v = new Varref2Visitor(typeMap);
            node.getVarref().apply(v);
            out.addAll(v.getList());
            pos.setStartPos(v.getPos());
        }
        if(node.getDot() != null)
        {
            node.getDot().apply(this);
            out.add(",");
        }
        if(node.getArrayref() != null)
        {
            out.add("array(");
        }
        if(node.getName() != null)
        {
            node.getName().apply(this);
            
            String name = node.getName().getText();
    		if (PromelaVisitor.substitutionMap != null) {
    			String name2 = PromelaVisitor.substitutionMap.get(name);
    			if (name2 != null) {
    				name = name2;
    			}
    		}
            
            String type = typeMap.get(name);
            if (type == null) {
    	    	throw new IllegalArgumentException("No known type for variable '"
    	    			+name
    	    			+"'. Line: "+(node.getName().getLine() - PromelaVisitor.adjustLine)
    	    			+" File: "+PromelaVisitor.currentFile);
    	    }
    	
            if (type.equals("chan")) {
            	out.add("chan("+getPrologString(name)+")");
            }
            else if (type.equals("ctype")) {
            	out.add("ctype("+getPrologString(name)+")");
            }
            else {
            	out.add(getPrologString(name));
            }
        }
        
        if(node.getArrayref() != null)
        {
            node.getArrayref().apply(this);
        }
        outARecordVarref(node);
    }
	 
	
	@Override
	 public void caseAArrayref(AArrayref node)
	 {
		 inAArrayref(node);
		 if(node.getLBracket() != null)
		 {
			 node.getLBracket().apply(this);
		 }
		 
		 out.add(",");
		 
		 if(node.getExpr() != null)
		 {
			 ExprVisitor v = new ExprVisitor(typeMap);
			 node.getExpr().apply(v);
			 out.addAll(v.getList());
		 }
		 if(node.getRBracket() != null)
		 {
			 node.getRBracket().apply(this);
		 }
		 outAArrayref(node);
		 
		 out.add(")");
	 }
 
	
	private String getPrologString(String name) {
		
		int c = name.charAt(0);
		// test, if name starts with a capital letter.
		if (c >= 65 && c <= 90) {
			name = "'"+name+"'";
		}
		return name;
	}
	
	public Position getPos() {
		return pos;
	}

	 public MyLinkedList getList() {
		 return out;
	 }
	 
}