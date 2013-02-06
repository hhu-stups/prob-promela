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
 * This visitor is used for user-defined types.
 */
public class DeclLst3Visitor extends DepthFirstAdapter {

	private HashMap<String,String> typeMap;
	private MyLinkedList out = new MyLinkedList();
	private String type;
	private String name;
	private String typedefName;
	private int typedefCount;
	private Position pos = new Position();
	
	public DeclLst3Visitor(HashMap<String,String> typeMap, String typedefName) {
		this.typeMap = typeMap;
		this.typedefName = typedefName;
		typedefCount = 0;
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
            pos.setStartPos(v.getPos());
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
		
		out.addBegin("typedef("+typedefName+","
				+typedefCount+",def("
				+type+","+getPrologString(name));
		
		if (node.getWidth() != null) {
			out.add(","+node.getWidth().toString().trim());
		}
		
		out.addEnd("))");
		
		typeMap.put(name, type);
		
		pos.setEndRow(node.getName().getLine());
		pos.setEndCol(node.getName().getPos() + node.getName().getText().length());
		PromelaVisitor.lineMap.put(PromelaVisitor.lineNr, pos);
		PromelaVisitor.lineNr++;
		pos = new Position();
		typedefCount++;
	}
	
	@Override
	public void inAArrayIvar(AArrayIvar node) {
		
		name = node.getName().getText();
		
		out.addBegin("typedef("+typedefName+","
				+typedefCount+",def("
				+type+",array("+getPrologString(name)+","
				+node.getConst().toString().trim());
		
		out.addEnd(")))");
		
		typeMap.put(name, type);
		
		pos.setEndRow(node.getName().getLine());
		pos.setEndCol(node.getName().getPos() + node.getName().getText().length());
		PromelaVisitor.lineMap.put(PromelaVisitor.lineNr, pos);
		PromelaVisitor.lineNr++;
		pos = new Position();
		typedefCount++;
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
