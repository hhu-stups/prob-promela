/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package visitors;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

import promela.analysis.*;
import promela.node.*;


public class InlineArgLstVisitor extends DepthFirstAdapter {
	
	private HashMap<String, String> typeMap;
	private LinkedList<String> argList;

	
	public InlineArgLstVisitor(HashMap<String, String> typeMap) {
		this.typeMap = typeMap;
		this.argList = new LinkedList<String>();
	}
	
	@Override
	public void caseAVarrefFactor(AVarrefFactor node)
    {
        inAVarrefFactor(node);
        if(node.getVarref() != null)
        {
        	VarrefVisitor v = new VarrefVisitor(typeMap, true);
            node.getVarref().apply(v);
            String res = "";
            ListIterator<String> iter = v.getList().listIterator(0);
    		while (iter.hasNext()) {
    			res += iter.next();
    		}
    		
    		
    		
    		String name = null;
    		String type = null;
    		
    		if (res.startsWith("vt(")) {
    			name = res.substring(3,res.lastIndexOf(','));
    			type = res.substring(res.lastIndexOf(',')+1,res.length()-1);
    		}
    		else if (res.startsWith("chan")) {
    			name = res.substring(5,res.lastIndexOf(')'));
    			type = "chan";
    		}
    		else if (res.contains("ctype")) {
    			name = res.substring(6,res.lastIndexOf(')'));
    			type = "ctype";
    		}
    		else if (res.contains("string")) {
    			name = res.substring(8,res.lastIndexOf('\''));
    			type = "string";
    		}
    		
    		
    		argList.add(name);
    		
    		if (typeMap.get(name) == null) {
    			typeMap.put(name, type);
    		}
        }
        outAVarrefFactor(node);
    }
	
	@Override 
	public void inAConstFactor(AConstFactor node) {
		String name = node.getConst().toString();
		name = name.substring(0, name.length() - 1);
		argList.add(name);
		typeMap.put(name, "dummy");
	}
	
	
	public LinkedList<String> getArgList() {
		return argList;
	}
	
}