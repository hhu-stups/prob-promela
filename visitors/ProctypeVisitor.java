/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package visitors;

import java.util.HashMap;

import promela.analysis.*;
import promela.node.*;


public class ProctypeVisitor extends DepthFirstAdapter {
	
	private HashMap<String, String> typeMap;
	private MyLinkedList out = new MyLinkedList();
	private Position pos = new Position();
	
	
	public ProctypeVisitor(HashMap<String, String> typeMap) {
		this.typeMap = typeMap;
	}
	
	@Override
	public void caseAProctype(AProctype node) {
	
		out.add("\n");
		out.addBegin("proctype(");
		PromelaVisitor.lineNr++;
		
		/*
		// proctype ist keine atomare Anweisung!
		pos.startRow = node.getProctypetok().getLine();
        pos.startCol = node.getProctypetok().getPos();
        pos.endRow = node.getRParenthese().getLine();
        pos.endCol = node.getRParenthese().getPos();
        PromelaVisitor.lineMap.put(PromelaVisitor.lineNr, pos);
        PromelaVisitor.lineNr++;
        pos = new Position();
        */
        
		inAProctype(node);
		if(node.getActive() != null)
        {
            node.getActive().apply(this);
        }
        else {
        	out.add("inactive,");
        }
        if(node.getProctypetok() != null)
        {
            node.getProctypetok().apply(this);
        }
        if(node.getName() != null)
        {
            node.getName().apply(this);
        }
        if(node.getLParenthese() != null)
        {
            node.getLParenthese().apply(this);
        }
        
        out.add(getPrologString(node.getName().getText())+",[");
        
        if(node.getDeclLst() != null)
        {
        	DeclLst2Visitor v = new DeclLst2Visitor(typeMap);
            node.getDeclLst().apply(v);
            out.addAll(v.getList());
        }
        
        out.add("]");
        
        if(node.getRParenthese() != null)
        {
            node.getRParenthese().apply(this);
        }
        if(node.getPriority() != null)
        {
            node.getPriority().apply(this);
        }
        if(node.getEnabler() != null)
        {
            node.getEnabler().apply(this);
        }
        
        out.addEnd(")");
        PromelaVisitor.offset += "    ";
        
        if(node.getLBrace() != null)
        {
            node.getLBrace().apply(this);
        }
        if(node.getSequence() != null)
        {
            SequenceVisitor v = new SequenceVisitor(typeMap);
            node.getSequence().apply(v);
            out.addAll(v.getList());
        }
        if(node.getRBrace() != null)
        {
            node.getRBrace().apply(this);
        }
        outAProctype(node);
        
        out.addInst("destructor");
        PromelaVisitor.offset = PromelaVisitor.offset.substring(0,
        		PromelaVisitor.offset.length() - 4);
        
        pos.setStartRow(node.getRBrace().getLine());
        pos.setStartCol(node.getRBrace().getPos());
        pos.setEndRow(node.getRBrace().getLine());
        pos.setEndCol(node.getRBrace().getPos() + 1);
        PromelaVisitor.lineMap.put(PromelaVisitor.lineNr, pos);
        PromelaVisitor.lineNr++;
        pos = new Position();
        
        PromelaVisitor.constraint = null;
	}
	
	@Override
	public void inAOneActive(AOneActive node) {
		out.add("active(1),");
	}
	
	@Override 
	public void inAManyActive(AManyActive node) {
		out.add("active("+node.getConst().toString().trim()+"),");
	}
	
	@Override
	public void caseAEnabler(AEnabler node)
    {
        inAEnabler(node);
        if(node.getProvided() != null)
        {
            node.getProvided().apply(this);
        }
        if(node.getLParenthese() != null)
        {
            node.getLParenthese().apply(this);
        }
        if(node.getExpr() != null)
        {
        	ExprVisitor v = new ExprVisitor(typeMap);
            node.getExpr().apply(v);
            PromelaVisitor.constraint = v.getList().toString();
        }
        if(node.getRParenthese() != null)
        {
            node.getRParenthese().apply(this);
        }
        outAEnabler(node);
    }
	
	
	private String getPrologString(String name) {
		
		int c = name.charAt(0);
		// test, if name starts with a capital letter.
		if (c >= 65 && c <= 90) {
			name = "'"+name+"'";
		}
		return name;
	}

	public MyLinkedList getList() {
		return out;
	}
}
