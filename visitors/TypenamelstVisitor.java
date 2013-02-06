/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package visitors;

import promela.analysis.*;
import promela.node.*;

import java.util.HashMap;


public class TypenamelstVisitor extends DepthFirstAdapter {

	private MyLinkedList out = new MyLinkedList();
	private Position pos = new Position();

	
	public TypenamelstVisitor(HashMap<String,String> typeMap) {
	}
	
	@Override
	public void caseAManyTypenamelst(AManyTypenamelst node)
    {
        inAManyTypenamelst(node);
        if(node.getTypename() != null)
        {
            node.getTypename().apply(this);
        }
        if(node.getComma() != null)
        {
        	out.add(",");
            node.getComma().apply(this);
        }
        if(node.getTypenamelst() != null)
        {
            node.getTypenamelst().apply(this);
        }
        outAManyTypenamelst(node);
    }
	
	@Override
	public void inABitTypename(ABitTypename node) {
		out.add(node.getBit().getText());
		pos.setStartRow(node.getBit().getLine());
		pos.setStartCol(node.getBit().getPos());
	}
	@Override
	public void inABoolTypename(ABoolTypename node) {
		out.add(node.getBool().getText());
		pos.setStartRow(node.getBool().getLine());
		pos.setStartCol(node.getBool().getPos());
	}
	
	@Override
	public void inAByteTypename(AByteTypename node) {
		out.add(node.getByte().getText());
		pos.setStartRow(node.getByte().getLine());
		pos.setStartCol(node.getByte().getPos());
	}
	@Override
	public void inAPidTypename(APidTypename node) {
		out.add(node.getPid().getText());
		pos.setStartRow(node.getPid().getLine());
		pos.setStartCol(node.getPid().getPos());
	}
	@Override
	public void inAShortTypename(AShortTypename node) {
		out.add(node.getShort().getText());
		pos.setStartRow(node.getShort().getLine());
		pos.setStartCol(node.getShort().getPos());
	}
	@Override
	public void inAIntTypename(AIntTypename node) {
		out.add(node.getInt().getText());
		pos.setStartRow(node.getInt().getLine());
		pos.setStartCol(node.getInt().getPos());
	}
	@Override
	public void inAMtypeTypename(AMtypeTypename node) {
		out.add(node.getMtypetok().getText());
		pos.setStartRow(node.getMtypetok().getLine());
		pos.setStartCol(node.getMtypetok().getPos());
	}
	@Override
	public void inAChanTypename(AChanTypename node) {
		out.add(node.getChan().getText());
		pos.setStartRow(node.getChan().getLine());
		pos.setStartCol(node.getChan().getPos());
	}
	@Override
	public void inAUnameTypename(AUnameTypename node) {
		String name = getPrologString(node.getName().getText());
		out.add(name);
		pos.setStartRow(node.getName().getLine());
		pos.setStartCol(node.getName().getPos());
	}
	@Override
	public void inAUnsignedTypename(AUnsignedTypename node) {
		out.add(node.getUnsigned().getText());
		pos.setStartRow(node.getUnsigned().getLine());
		pos.setStartCol(node.getUnsigned().getPos());
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
