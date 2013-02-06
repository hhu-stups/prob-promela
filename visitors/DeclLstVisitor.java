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
 * This visitor is used for global or local declarations.
 */
public class DeclLstVisitor extends DepthFirstAdapter {

	private HashMap<String,String> typeMap;
	private MyLinkedList out = new MyLinkedList();
	private String type;
	private String name;
	private Position posDefine = new Position();
	private Position posAssign = new Position();
	
	public DeclLstVisitor(HashMap<String,String> typeMap) {
		this.typeMap = typeMap;
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
            posDefine.setStartPos(v.getPos());
        }
        if(node.getIvarLst() != null)
        {
            node.getIvarLst().apply(this);
        }
        outAOneDecl(node);
    }
	
	@Override
	public void inASingleIvar(ASingleIvar node) {
		
		name = node.getName().getText();
		
		out.addBegin("def("
					+type+","+getPrologString(name));
		
		if (node.getWidth() != null) {
			out.add(","+node.getWidth().toString().trim());
		}
		
		out.addEnd(")");
		
		typeMap.put(name, type);
		
		posDefine.setEndRow(node.getName().getLine());
		posDefine.setEndCol(node.getName().getPos() + node.getName().getText().length());
		PromelaVisitor.lineMap.put(PromelaVisitor.lineNr, posDefine);
		PromelaVisitor.lineNr++;
		Position oldPos = posDefine;
		posDefine = new Position();
		posDefine.setStartPos(oldPos);
		posAssign.setStartRow(node.getName().getLine());
		posAssign.setStartCol(node.getName().getPos());
	}
	
	@Override
	public void inAArrayIvar(AArrayIvar node) {
		
		name = node.getName().getText();
		
		out.addBegin("def("
				+type+",array("+getPrologString(name)+","
				+node.getConst().toString().trim());
		
		out.addEnd("))");
		
		typeMap.put(name, type);
		
		posDefine.setEndRow(node.getRBracket().getLine());
		posDefine.setEndCol(node.getRBracket().getPos() + 1);
		PromelaVisitor.lineMap.put(PromelaVisitor.lineNr, posDefine);
		PromelaVisitor.lineNr++;
		Position oldPos = posDefine;
		posDefine = new Position();
		posDefine.setStartPos(oldPos);
		posAssign.setStartRow(node.getName().getLine());
		posAssign.setStartCol(node.getName().getPos());
	}
	
	@Override
	public void caseAVariableIvarassignment(AVariableIvarassignment node)
    {
		if (type.equals("chan")) {
			out.addBegin("assign(chan("+getPrologString(name)+"),");
		}
		else { 
			out.addBegin("assign(vt("+getPrologString(name)+","+type+"),");
		}
			
        inAVariableIvarassignment(node);
        if(node.getAssign() != null)
        {
            node.getAssign().apply(this);
        }
        if(node.getExpr() != null)
        {
        	ExprVisitor v = new ExprVisitor(typeMap);
    		node.getExpr().apply(v);
    		out.addAll(v.getList());
    		posAssign.setEndPos(v.getPos());
        }
        outAVariableIvarassignment(node);
        
        out.addEnd(")");
		
		PromelaVisitor.lineMap.put(PromelaVisitor.lineNr, posAssign);
		PromelaVisitor.lineNr++;
		posAssign = new Position();
    }
	
	
	@Override
	public void caseAChannelIvarassignment(AChannelIvarassignment node)
    {
		out.addBegin("assign("+getPrologString(name)+",chan(");
				
        inAChannelIvarassignment(node);
        if(node.getAssign() != null)
        {
            node.getAssign().apply(this);
        }
        if(node.getLBracket() != null)
        {
            node.getLBracket().apply(this);
        }
        if(node.getConst() != null)
        {
        	ConstVisitor v = new ConstVisitor(typeMap);
            node.getConst().apply(v);
            out.addAll(v.getList());
        }
        
        out.add(",[");
        
        if(node.getRBracket() != null)
        {
            node.getRBracket().apply(this);
        }
        if(node.getOf() != null)
        {
            node.getOf().apply(this);
        }
        if(node.getLBrace() != null)
        {
            node.getLBrace().apply(this);
        }
        if(node.getTypenamelst() != null)
        {
        	TypenamelstVisitor v = new TypenamelstVisitor(typeMap);
            node.getTypenamelst().apply(v);
            out.addAll(v.getList());
        }
        if(node.getRBrace() != null)
        {
            node.getRBrace().apply(this);
        }
        outAChannelIvarassignment(node);
        
        out.addEnd("]))");
        
        posAssign.setEndRow(node.getRBrace().getLine());
        posAssign.setEndCol(node.getRBrace().getPos() + 1);
        PromelaVisitor.lineMap.put(PromelaVisitor.lineNr, posAssign);
		PromelaVisitor.lineNr++;
		posAssign = new Position();
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
