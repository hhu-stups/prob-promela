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
 * This Visitor will be used for Varref's.
 */
public class VarrefVisitor extends DepthFirstAdapter {

	private HashMap<String,String> typeMap;
	private MyLinkedList out = new MyLinkedList();
	private Position pos = new Position();
	private boolean withVarType = false;
	
	
	public VarrefVisitor(HashMap<String,String> typeMap) {
		this.typeMap = typeMap;
	}
	
	public VarrefVisitor(HashMap<String,String> typeMap, boolean withVarType) {
		this.typeMap = typeMap;
		this.withVarType = withVarType;
	}
	
	
	@Override
	public void caseASingleVarref(ASingleVarref node)
    {
        inASingleVarref(node);
        
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
	    
	    if (withVarType == true && !type.equals("chan") && !type.equals("ctype") 
	    		&& !type.equals("string")) {
			out.add("vt(");
		}
	    
		if (node.getArrayref() != null) {
			out.add("array(");
	    }
	    
		
	    if (type.equals("chan")) {
	        out.add("chan("+getPrologString(name)+")");
	    }
	    else if (type.equals("ctype")) {
	        out.add("ctype("+getPrologString(name)+")");
	    }
	    else if (type.equals("string")) {
	        out.add("string('"+name+"')");
	    }
	    else {
	    	out.add(getPrologString(name));
	    }
	    
	    pos.setStartRow(node.getName().getLine());
		pos.setStartCol(node.getName().getPos());
	    pos.setEndRow(node.getName().getLine());
		pos.setEndCol(node.getName().getPos() + node.getName().getText().length());
        
        if(node.getArrayref() != null)
        {
            node.getArrayref().apply(this);
        }
        outASingleVarref(node);
        
        if (withVarType == true && !type.equals("chan") && !type.equals("ctype")
        		&& !type.equals("string")) {
	    	
        	out.add(","+type+")");
	    }
   
    }
	    
	
	@Override
	public void caseARecordVarref(ARecordVarref node)
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
        
        if (withVarType == true && !type.equals("chan") && !type.equals("ctype")
        		&& !type.equals("string")) {
        	
			out.add("vt(");
		}
        
        out.add("record([");
		
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
        
	
        if (type.equals("chan")) {
        	out.add("chan("+getPrologString(name)+")");
        }
        else if (type.equals("ctype")) {
        	out.add("ctype("+getPrologString(name)+")");
        }
        else if (type.equals("string")) {
	        out.add("string('"+name+"')");
	    }
        else {
        	out.add(getPrologString(name));
        }
        
        pos.setEndRow(node.getName().getLine());
		pos.setEndCol(node.getName().getPos() + node.getName().getText().length());
		
        if(node.getArrayref() != null)
        {
            node.getArrayref().apply(this);
        }
        outARecordVarref(node);
        
        out.add("])");
        
        if (withVarType == true && !type.equals("chan") && !type.equals("ctype")
        		&& !type.equals("string")) {
        	
	    	out.add(","+type+")");
	    }
    }
	 
	@Override
	public void caseAStringVarref(AStringVarref node)
    {
		String name = "";
		
        inAStringVarref(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        if(node.getBackslash() != null)
        {
            node.getBackslash().apply(this);
            name += "\\";
        }
        if(node.getName() != null)
        {
            node.getName().apply(this);
            name += node.getName().getText();
            out.add("string('"+name+"')");
        }
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        outAStringVarref(node);
        
        pos.setStartRow(node.getLeft().getLine());
		pos.setStartCol(node.getLeft().getPos());
	    pos.setEndRow(node.getRight().getLine());
		pos.setEndCol(node.getRight().getPos() + 1);
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
		 
		 pos.setEndRow(node.getRBracket().getLine());
		 pos.setEndCol(node.getRBracket().getPos() + 1);
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
