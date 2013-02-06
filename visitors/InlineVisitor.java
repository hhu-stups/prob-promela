/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package visitors;

import java.util.HashMap;
import java.util.LinkedList;

import promela.analysis.*;
import promela.node.*;

import promela.node.AInline;

public class InlineVisitor extends DepthFirstAdapter {
	
	private HashMap<String, String> typeMap;
	private MyLinkedList out = new MyLinkedList();
	private LinkedList<String> argList;
	private LinkedList<String> givenArgList;
	
	
	public InlineVisitor(HashMap<String, String> typeMap, LinkedList<String> givenArgList) {
		this.typeMap = typeMap;
		this.givenArgList = givenArgList;
		this.argList = new LinkedList<String>();
	}
	
	@Override
	public void caseAInline(AInline node)
    {	
        inAInline(node);
        if(node.getInlinetok() != null)
        {
            node.getInlinetok().apply(this);
        }
        if(node.getName() != null)
        {
            node.getName().apply(this);
        }
        if(node.getLParenthese() != null)
        {
            node.getLParenthese().apply(this);
        }
        if(node.getArgLst() != null)
        {	
            node.getArgLst().apply(this);
        }
        if(node.getRParenthese() != null)
        {
            node.getRParenthese().apply(this);
        }
        if(node.getLBrace() != null)
        {
            node.getLBrace().apply(this);
        }
        if(node.getSequence() != null)
        {
        	PromelaVisitor.substitutionMap = new HashMap<String,String>(4);
        	
        	try {
        		// do the actual mapping of the arguments.
        		for (int i = 0; i < argList.size(); i++) {
        			//System.out.println("a: "+argList.get(i));
        			//System.out.println("b: "+givenArgList.get(i));
        			PromelaVisitor.substitutionMap.put(argList.get(i), givenArgList.get(i));
        		}
        	}
        	catch (Exception e) {
        		System.out.println("Error with Argument mapping in inline function '"
        				+node.getName().getText()+"'. Line: "+node.getName().getLine());
        	}
        	
        	// run the body of the inline function.
        	SequenceVisitor v2 = new SequenceVisitor(typeMap);
        	node.getSequence().apply(v2);
            out.addAll(v2.getList());
            
            PromelaVisitor.substitutionMap = null;
        }
        if(node.getRBrace() != null)
        {
            node.getRBrace().apply(this);
        }
        outAInline(node);
    }
	
	/**
	@Override 
	public void inASingleVarref(ASingleVarref node) {
		String name = node.toString();
		argList.add(name.substring(0, name.length() - 1));
	}
	
	@Override 
	public void inARecordVarref(ARecordVarref node) {
		String name = node.toString();
		argList.add(name.substring(0, name.length() - 1));
	}
	**/
	/*
	 * So far, there is only one example, where this alternative is used:
	 *     critical_section('p');
	 *     inline critical_section(proc) {
	 *         printf("MSC: %c in CS\n", proc);
	 *     }
	 */
	/**
	@Override
	public void inAStringVarref(AStringVarref node) {
		String name = node.getName().getText();
		argList.add(name);
		typeMap.put(name, "string");
	}
	**/
	
	@Override
	public void inAVarrefFactor(AVarrefFactor node) {
		String name = node.toString();
		argList.add(name.substring(0, name.length() - 1));
	}
	
	
	public LinkedList<String> getArgList() {
		return argList;
	}
	
	public MyLinkedList getList() {
		return out;
	}
}
