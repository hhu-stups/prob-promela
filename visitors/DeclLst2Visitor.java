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

/*
 * This visitor is used for parameter-declarations in
 * the function-head.
 */
public class DeclLst2Visitor extends DepthFirstAdapter {

	private HashMap<String,String> typeMap;
	private LinkedList<String> out = new LinkedList<String>();
	private String type;
	private String name;
	
	public DeclLst2Visitor(HashMap<String,String> typeMap) {
		this.typeMap = typeMap;
	}
	
	
	@Override
	public void caseAManyDeclLst(AManyDeclLst node)
    {
        inAManyDeclLst(node);
        if(node.getOneDecl() != null)
        {
            node.getOneDecl().apply(this);
        }
        
        out.add(",");
        
        if(node.getSeparator() != null)
        {
            node.getSeparator().apply(this);
        }
        if(node.getDeclLst() != null)
        {
            node.getDeclLst().apply(this);
        }
        outAManyDeclLst(node);
    }
	
	@Override
	public void caseAOneDecl(AOneDecl node)
    {
        inAOneDecl(node);
        if(node.getVisible() != null)
        {
            node.getVisible().apply(this);
        }
        if(node.getTypename() != null)
        {
        	TypenamelstVisitor v = new TypenamelstVisitor(typeMap);
            node.getTypename().apply(v);
            
            type = v.getList().getFirst();
        }
        if(node.getIvarLst() != null)
        {
            node.getIvarLst().apply(this);
        }
        outAOneDecl(node);
    }
	
	@Override
	public void caseAManyIvarLst(AManyIvarLst node)
    {
        inAManyIvarLst(node);
        if(node.getIvar() != null)
        {
            node.getIvar().apply(this);
        }
        
        out.add(",");
        
        if(node.getComma() != null)
        {
            node.getComma().apply(this);
        }
        if(node.getIvarLst() != null)
        {
            node.getIvarLst().apply(this);
        }
        outAManyIvarLst(node);
    }
	
	@Override
	public void inASingleIvar(ASingleIvar node) {
		
		name = node.getName().getText();
		
		out.add("def("+type+","+getPrologString(name)+")");
		
		if (node.getWidth() != null) {
			// do nothing
		}
		
		typeMap.put(name, type);
	}
	
	@Override
	public void inAArrayIvar(AArrayIvar node) {
		
		name = node.getName().getText();
		
		out.add("def("+type+",array("+getPrologString(name)+","
				+node.getConst().toString().trim()+"))");
		
		typeMap.put(name, type);
	}
	
	private String getPrologString(String name) {
		
		int c = name.charAt(0);
		// test, if name starts with a capital letter.
		if (c >= 65 && c <= 90) {
			name = "'"+name+"'";
		}
		return name;
	}
	
	public LinkedList<String> getList() {
		return (LinkedList<String>) out;
	}
	
}

