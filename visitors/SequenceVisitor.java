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

public class SequenceVisitor extends DepthFirstAdapter {

	private HashMap<String, String> typeMap;
	private MyLinkedList out = new MyLinkedList();
	private Position pos = new Position();
	private boolean unless = false;
	private LinkedList<Integer> unlessIndexList;
	
	
	public SequenceVisitor(HashMap<String, String> typeMap) {
		this.typeMap = typeMap;
	}
	
	
	@Override
	public void inAOneSequence(AOneSequence node) {
	
		if (PromelaVisitor.constraint != null) {
			out.addInst("provided("+PromelaVisitor.constraint+")");
			PromelaVisitor.lineNr++;
		}
	}
	
	@Override
	public void caseAManySequence(AManySequence node)
    {
		if (PromelaVisitor.constraint != null) {
			out.addInst("provided("+PromelaVisitor.constraint+")");
			PromelaVisitor.lineNr++;
		}
		
        inAManySequence(node);
        if(node.getStep() != null)
        {
            node.getStep().apply(this);
        }
        
        PromelaVisitor.guardCondition = false;
        
        if(node.getSeparator() != null)
        {
            node.getSeparator().apply(this);
        }
        if(node.getSequence() != null)
        {
            node.getSequence().apply(this);
        }
        outAManySequence(node);
    }
	
	@Override
	 public void caseAAtomicSequence(AAtomicSequence node)
    {
		if (PromelaVisitor.constraint != null) {
			out.addInst("provided("+PromelaVisitor.constraint+")");
			PromelaVisitor.lineNr++;
		}
		
		if (unless == true) {
        	unlessIndexList.add(out.size());
        	out.addBegin("unless("+(PromelaVisitor.lineNr+1)+",");
        	PromelaVisitor.lineNr++;
        }
		
        inAAtomicSequence(node);
        if(node.getAtomic() != null)
        {
            node.getAtomic().apply(this);
        }
        if(node.getLBrace() != null)
        {
            node.getLBrace().apply(this);
        }
        if(node.getMain() != null)
        {
        	PromelaVisitor.mode = PromelaVisitor.ATOMIC;
        	PromelaVisitor.atomicStart = "_start";
        	SequenceVisitor v = new SequenceVisitor(typeMap);
            node.getMain().apply(v);
            out.addAll(v.getList());
            PromelaVisitor.mode = PromelaVisitor.NORMAL;
        }
        if(node.getRBrace() != null)
        {
            node.getRBrace().apply(this);
        }
        
        PromelaVisitor.guardCondition = false;
        
        if(node.getSeparator() != null)
        {
            node.getSeparator().apply(this);
        }
        if(node.getNext() != null)
        {
            node.getNext().apply(this);
        }
        outAAtomicSequence(node);
    }
	
	@Override
	 public void caseADstepSequence(ADstepSequence node)
    {
		if (PromelaVisitor.constraint != null) {
			out.addInst("provided("+PromelaVisitor.constraint+")");
			PromelaVisitor.lineNr++;
		}
		
		if (unless == true) {
        	unlessIndexList.add(out.size());
        	out.addBegin("unless("+(PromelaVisitor.lineNr+1)+",");
        	PromelaVisitor.lineNr++;
        }
		
        inADstepSequence(node);
        if(node.getDStep() != null)
        {
            node.getDStep().apply(this);
        }
        if(node.getLBrace() != null)
        {
            node.getLBrace().apply(this);
        }
        if(node.getMain() != null)
        {
        	PromelaVisitor.mode = PromelaVisitor.D_STEP;
        	PromelaVisitor.atomicStart = "_start";
        	SequenceVisitor v = new SequenceVisitor(typeMap);
            node.getMain().apply(v);
            out.addAll(v.getList());
            PromelaVisitor.mode = PromelaVisitor.NORMAL;
        }
        if(node.getRBrace() != null)
        {
            node.getRBrace().apply(this);
        }
        
        PromelaVisitor.guardCondition = false;
        
        if(node.getSeparator() != null)
        {
            node.getSeparator().apply(this);
        }
        if(node.getNext() != null)
        {
            node.getNext().apply(this);
        }
        outADstepSequence(node);
    }
	
	@Override
	// nothing changed.
	// useful together with unless.
	 public void caseABracesSequence(ABracesSequence node)
    {	
        inABracesSequence(node);
        if(node.getLBrace() != null)
        {
            node.getLBrace().apply(this);
        }
        if(node.getMain() != null)
        {
            node.getMain().apply(this);
        }
        if(node.getRBrace() != null)
        {
            node.getRBrace().apply(this);
        }
        if(node.getSeparator() != null)
        {
            node.getSeparator().apply(this);
        }
        if(node.getNext() != null)
        {
            node.getNext().apply(this);
        }
        outABracesSequence(node);
    }
	
	@Override
	public void caseALabelSequence(ALabelSequence node)
	{
		if (unless == true) {
        	unlessIndexList.add(out.size());
        	out.addBegin("unless("+(PromelaVisitor.lineNr+1)+",");
        	PromelaVisitor.lineNr++;
        }
		
		out.addInst("label("+getPrologString(node.getName().getText())+")");
		
		pos.setStartRow(node.getName().getLine());
		pos.setStartCol(node.getName().getPos());
		pos.setEndRow(node.getColon().getLine());
		pos.setEndCol(node.getColon().getPos() + 1);
		PromelaVisitor.lineMap.put(PromelaVisitor.lineNr, pos);
		PromelaVisitor.lineNr++;
		pos = new Position();
		
        inALabelSequence(node);
        if(node.getName() != null)
        {
            node.getName().apply(this);
        }
        if(node.getColon() != null)
        {
            node.getColon().apply(this);
        }
        if(node.getSequence() != null)
        {
        	/*
        	SequenceVisitor v = new SequenceVisitor(typeMap);
            node.getSequence().apply(v);
            out.addAll(v.getList());
            pos.setEndPos(v.getPos());
            */
            node.getSequence().apply(this);
        }
        outALabelSequence(node);
        
    }
	
	@Override
	 public void caseAElseSequence(AElseSequence node)
    {
		if (unless == true) {
        	unlessIndexList.add(out.size());
        	out.addBegin("unless("+(PromelaVisitor.lineNr+1)+",");
        	PromelaVisitor.lineNr++;
        }
		
        inAElseSequence(node);
        if(node.getElse() != null)
        {
            node.getElse().apply(this);
            out.addInst("else");
        }
        if(node.getSeparator() != null)
        {
            node.getSeparator().apply(this);
        }
        
        pos.setStartRow(node.getElse().getLine());
		pos.setStartCol(node.getElse().getPos());
		pos.setEndRow(node.getElse().getLine());
		pos.setEndCol(node.getElse().getPos() + node.getElse().getText().length());
		PromelaVisitor.lineMap.put(PromelaVisitor.lineNr, pos);
		PromelaVisitor.lineNr++;
		pos = new Position();
		
        if(node.getSequence() != null)
        {
            node.getSequence().apply(this);
        }
        outAElseSequence(node);
    }
	
	@Override
	public void caseADeclarationStep(ADeclarationStep node) {
		DeclLstVisitor v = new DeclLstVisitor(typeMap);
		node.getOneDecl().apply(v);
		out.addAll(v.getList());
	}
	
	@Override
	public void caseAUnlessStep(AUnlessStep node)
    {	
		if (unless == true) {
        	unlessIndexList.add(out.size());
        	out.addBegin("unless("+(PromelaVisitor.lineNr+1)+",");
        	PromelaVisitor.lineNr++;
        }
		
		unless = true;
		// myUnlessIndexList is necessary to build nested unless constructs.
		LinkedList<Integer> myUnlessIndexList = new LinkedList<Integer>();
		unlessIndexList = myUnlessIndexList;
		
        inAUnlessStep(node);
        if(node.getMain() != null)
        {
            node.getMain().apply(this);    
        }
        
        int igotoIndex = out.size();
        out.addBegin("igoto(");
        PromelaVisitor.lineNr++;
        
        if(node.getUnless() != null)
        {
            node.getUnless().apply(this);
        }
        
        int escapeLine = PromelaVisitor.lineNr;
        unless = false;
        
        if(node.getEscape() != null)
        {
            node.getEscape().apply(this);
        }
        outAUnlessStep(node);
        
        for (int i = 0; i < myUnlessIndexList.size(); i++) {
        	String s = out.get(myUnlessIndexList.get(i));
        	s += escapeLine+")";
        	out.setEnd(myUnlessIndexList.get(i), s);
        }
        
        String s = out.get(igotoIndex);
        s += PromelaVisitor.lineNr+")";
        out.setEnd(igotoIndex, s);
    }
	
	
	@Override
	 public void caseARunInlineStmnt(ARunInlineStmnt node)
    {
        inARunInlineStmnt(node);
        if(node.getName() != null)
        {
            node.getName().apply(this);
        }
        if(node.getLParenthese() != null)
        {
            node.getLParenthese().apply(this);
        }
        
        // stores the arguments, the inline variables will be mapped to.
        LinkedList<String> argList = null;
        if(node.getArgLst() != null)
        {
        	InlineArgLstVisitor v = new InlineArgLstVisitor(typeMap);
        	node.getArgLst().apply(v);
        	argList = v.getArgList();
        }
        
        if(node.getRParenthese() != null)
        {
            node.getRParenthese().apply(this);
        }
        outARunInlineStmnt(node);
        
        // applys the inline-function from the matching inline node.
        AInline inlineNode = PromelaVisitor.inlineMap.get(node.getName().getText());
        if (inlineNode == null) {
	    	throw new IllegalArgumentException("No inline function with name '"
	    			+node.getName().toString()
	    			+"'. Line: "+(node.getName().getLine() - PromelaVisitor.adjustLine)
	    			+" File: "+PromelaVisitor.currentFile);
	    }
        
        InlineVisitor v2 = new InlineVisitor(typeMap, argList);
		inlineNode.apply(v2);
		out.addAll(v2.getList());
    }
	
	@Override
	public void caseAIfStmnt(AIfStmnt node) {
		
		if (unless == true) {
        	unlessIndexList.add(out.size());
        	out.addBegin("unless("+(PromelaVisitor.lineNr+1)+",");
        	PromelaVisitor.lineNr++;
        }
		
		IfVisitor v = new IfVisitor(typeMap);
		node.apply(v);
		out.addAll(v.getList());
	}
	
	@Override
	public void caseADoStmnt(ADoStmnt node) {
		
		if (unless == true) {
        	unlessIndexList.add(out.size());
        	out.addBegin("unless("+(PromelaVisitor.lineNr+1)+",");
        	PromelaVisitor.lineNr++;
        }
		
		DoVisitor v = new DoVisitor(typeMap);
		node.apply(v);
		out.addAll(v.getList());
	}
	
	
	/*send*/
	@Override
	public void inASendStmnt(ASendStmnt node) {
	
		if (unless == true) {
        	unlessIndexList.add(out.size());
        	out.addBegin("unless("+(PromelaVisitor.lineNr+1)+",");
        	PromelaVisitor.lineNr++;
        }
	}
	
	@Override
    public void caseAFifoSend(AFifoSend node)
    {
    	out.addBegin("send(");
    	
        inAFifoSend(node);
        if(node.getVarref() != null)
        {
        	VarrefVisitor v = new VarrefVisitor(typeMap);
            node.getVarref().apply(v);
            out.addAll(v.getList());
            pos.setStartPos(v.getPos());
        }
        if(node.getBang() != null)
        {
            node.getBang().apply(this);
        }
        
        out.add(",[");
        
        if(node.getSendArgs() != null)
        {
            node.getSendArgs().apply(this);
        }
        outAFifoSend(node);
        
        out.addEnd("])");
        
        PromelaVisitor.lineMap.put(PromelaVisitor.lineNr, pos);
        PromelaVisitor.lineNr++;
        pos = new Position();
    }
	 
	@Override
    public void caseASortedSend(ASortedSend node)
    {
	 	out.addBegin("sorted_send(");
	 	
        inASortedSend(node);
        if(node.getVarref() != null)
        {
        	VarrefVisitor v = new VarrefVisitor(typeMap);
            node.getVarref().apply(v);
            out.addAll(v.getList());
            pos.setStartPos(v.getPos());
        }
        if(node.getBangBang() != null)
        {
            node.getBangBang().apply(this);
        }
        
        out.add(",[");
        
        if(node.getSendArgs() != null)
        {
            node.getSendArgs().apply(this);
        }
        outASortedSend(node);
        
        out.addEnd("])");
        
        PromelaVisitor.lineMap.put(PromelaVisitor.lineNr, pos);
        PromelaVisitor.lineNr++;
        pos = new Position();
    }

	@Override
    public void caseAHeadedlistSendArgs(AHeadedlistSendArgs node)
    {
        inAHeadedlistSendArgs(node);
        if(node.getExpr() != null)
        {
        	ExprVisitor v = new ExprVisitor(typeMap);
            node.getExpr().apply(v);
            out.addAll(v.getList());
        }
        
        out.add(",");
        
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
        outAHeadedlistSendArgs(node);
        
        pos.setEndRow(node.getRParenthese().getLine());
        pos.setEndCol(node.getRParenthese().getPos() + 1);
    }
    
	@Override
    public void caseAOneArgLst(AOneArgLst node)
    {
        inAOneArgLst(node);
        if(node.getExpr() != null)
        {
        	ExprVisitor v = new ExprVisitor(typeMap);
            node.getExpr().apply(v);
            out.addAll(v.getList());
            pos.setEndPos(v.getPos());
        }
        outAOneArgLst(node);
    }

	@Override
    public void caseAManyArgLst(AManyArgLst node)
    {
        inAManyArgLst(node);
        if(node.getExpr() != null)
        {
        	ExprVisitor v = new ExprVisitor(typeMap);
            node.getExpr().apply(v);
            out.addAll(v.getList());
        }
        
        out.add(",");
        
        if(node.getComma() != null)
        {
            node.getComma().apply(this);
        }
        if(node.getArgLst() != null)
        {
            node.getArgLst().apply(this);
        }
        outAManyArgLst(node);
    }

    /*receive*/
	@Override
	public void inAReceiveStmnt(AReceiveStmnt node) {
		
		if (unless == true) {
        	unlessIndexList.add(out.size());
        	out.addBegin("unless("+(PromelaVisitor.lineNr+1)+",");
        	PromelaVisitor.lineNr++;
        }
	}
	
	@Override
	public void caseAFifoReceive(AFifoReceive node)
    {
		out.addBegin("recv(");
    	
        inAFifoReceive(node);
        if(node.getVarref() != null)
        {
        	VarrefVisitor v = new VarrefVisitor(typeMap);
            node.getVarref().apply(v);
            out.addAll(v.getList());
            pos.setStartPos(v.getPos());
        }
        if(node.getQuery() != null)
        {
            node.getQuery().apply(this);
        }
        
        out.add(",[");
        
        if(node.getRecvArgs() != null)
        {
            node.getRecvArgs().apply(this);
        }
        outAFifoReceive(node);
        
        out.addEnd("])");
        
        PromelaVisitor.lineMap.put(PromelaVisitor.lineNr, pos);
        PromelaVisitor.lineNr++;
        pos = new Position();
    }
    
	@Override
	public void caseARandomReceive(ARandomReceive node)
    {
		out.addBegin("random_recv(");
    	
        inARandomReceive(node);
        if(node.getVarref() != null)
        {
        	VarrefVisitor v = new VarrefVisitor(typeMap);
            node.getVarref().apply(v);
            out.addAll(v.getList());
            pos.setStartPos(v.getPos());
        }
        if(node.getQueryQuery() != null)
        {
            node.getQueryQuery().apply(this);
        }
        
        out.add(",[");
        
        if(node.getRecvArgs() != null)
        {
            node.getRecvArgs().apply(this);
        }
        outARandomReceive(node);
        
        out.addEnd("])");
        
        PromelaVisitor.lineMap.put(PromelaVisitor.lineNr, pos);
        PromelaVisitor.lineNr++;
        pos = new Position();
    }
	
	@Override
	public void caseAFifopollReceive(AFifopollReceive node)
    {
		out.addBegin("poll(");
    	
        inAFifopollReceive(node);
        if(node.getVarref() != null)
        {
        	VarrefVisitor v = new VarrefVisitor(typeMap);
            node.getVarref().apply(v);
            out.addAll(v.getList());
            pos.setStartPos(v.getPos());
        }
        if(node.getQuery() != null)
        {
            node.getQuery().apply(this);
        }
        
        out.add(",[");
        
        if(node.getLt() != null)
        {
            node.getLt().apply(this);
        }
        if(node.getRecvArgs() != null)
        {
            node.getRecvArgs().apply(this);
        }
        if(node.getGt() != null)
        {
            node.getGt().apply(this);
        }
        outAFifopollReceive(node);
        
        out.addEnd("])");
        
        pos.setEndRow(node.getGt().getLine());
        pos.setEndCol(node.getGt().getPos() + node.getGt().getText().length());
        PromelaVisitor.lineMap.put(PromelaVisitor.lineNr, pos);
        PromelaVisitor.lineNr++;
        pos = new Position();
    }

	@Override
	public void caseARandompollReceive(ARandompollReceive node)
    {
		out.addBegin("random_poll(");
    	
        inARandompollReceive(node);
        if(node.getVarref() != null)
        {
        	VarrefVisitor v = new VarrefVisitor(typeMap);
            node.getVarref().apply(v);
            out.addAll(v.getList());
            pos.setStartPos(v.getPos());
        }
        if(node.getQueryQuery() != null)
        {
            node.getQueryQuery().apply(this);
        }
        
        out.add(",[");
        
        if(node.getLt() != null)
        {
            node.getLt().apply(this);
        }
        if(node.getRecvArgs() != null)
        {
            node.getRecvArgs().apply(this);
        }
        if(node.getGt() != null)
        {
            node.getGt().apply(this);
        }
        outARandompollReceive(node);
        
        out.addEnd("])");
        
        pos.setEndRow(node.getGt().getLine());
        pos.setEndCol(node.getGt().getPos() + node.getGt().getText().length());
        PromelaVisitor.lineMap.put(PromelaVisitor.lineNr, pos);
        PromelaVisitor.lineNr++;
        pos = new Position();
    }
	
	@Override
	public void caseAManyRecvArgs(AManyRecvArgs node)
    {
        inAManyRecvArgs(node);
        if(node.getRecvArg() != null)
        {
            node.getRecvArg().apply(this);
        }
        
        out.add(",");
        
        if(node.getComma() != null)
        {
            node.getComma().apply(this);
        }
        if(node.getRecvArgs() != null)
        {
            node.getRecvArgs().apply(this);
        }
        outAManyRecvArgs(node);
    }
	
	@Override
	public void caseAManyheaded1RecvArgs(AManyheaded1RecvArgs node)
    {
        inAManyheaded1RecvArgs(node);
        if(node.getRecvArg() != null)
        {
            node.getRecvArg().apply(this);
        }
        
        out.add(",");
        
        if(node.getLParenthese() != null)
        {
            node.getLParenthese().apply(this);
        }
        if(node.getRecvArgs() != null)
        {
            node.getRecvArgs().apply(this);
        }
        if(node.getRParenthese() != null)
        {
            node.getRParenthese().apply(this);
        }
        outAManyheaded1RecvArgs(node);
        
        pos.setEndRow(node.getRParenthese().getLine());
        pos.setEndCol(node.getRParenthese().getPos() + 1);
    }
	
	@Override
	public void caseAManyheaded2RecvArgs(AManyheaded2RecvArgs node)
    {
        inAManyheaded2RecvArgs(node);
        if(node.getLParenthese() != null)
        {
            node.getLParenthese().apply(this);
        }
        if(node.getRecvArgs() != null)
        {
            node.getRecvArgs().apply(this);
        }
        if(node.getRParenthese() != null)
        {
            node.getRParenthese().apply(this);
        }
        outAManyheaded2RecvArgs(node);
        
        pos.setEndRow(node.getRParenthese().getLine());
        pos.setEndCol(node.getRParenthese().getPos() + 1);
    }
	
	@Override
	public void caseAVarRecvArg(AVarRecvArg node)
    {
        inAVarRecvArg(node);
        if(node.getVarref() != null)
        {
        	boolean withVarType = true;
        	VarrefVisitor v = new VarrefVisitor(typeMap,withVarType);
            node.getVarref().apply(v);
            out.addAll(v.getList());
            pos.setEndPos(v.getPos());
        }
        outAVarRecvArg(node);
    }
	
	public void caseAEvalRecvArg(AEvalRecvArg node)
    {
		
		out.add("eval(");
		
        inAEvalRecvArg(node);
        if(node.getEval() != null)
        {
            node.getEval().apply(this);
        }
        if(node.getLParenthese() != null)
        {
            node.getLParenthese().apply(this);
        }
        if(node.getExpr() != null)
        {
        	ExprVisitor v = new ExprVisitor(typeMap);
            node.getExpr().apply(v);
            out.addAll(v.getList());
        }
        if(node.getRParenthese() != null)
        {
            node.getRParenthese().apply(this);
        }
        outAEvalRecvArg(node);
        
        out.add(")");
        pos.setEndRow(node.getRParenthese().getLine());
        pos.setEndCol(node.getRParenthese().getPos() + 1);
    }

	@Override
	public void caseAConstRecvArg(AConstRecvArg node)
    {
        inAConstRecvArg(node);
        if(node.getConst() != null)
        {
        	ConstVisitor v = new ConstVisitor(typeMap);
            node.getConst().apply(v);
            out.addAll(v.getList());
            pos.setEndPos(v.getPos());
        }
        outAConstRecvArg(node);
    }
	
	@Override
	public void caseAUnderscoreRecvArg(AUnderscoreRecvArg node)
    {
        inAUnderscoreRecvArg(node);
        if(node.getUnderscore() != null)
        {
            node.getUnderscore().apply(this);
        }
        outAUnderscoreRecvArg(node);
        
        out.add("underscore");
        pos.setEndRow(node.getUnderscore().getLine());
        pos.setEndCol(node.getUnderscore().getPos() + 1);
    }
	
	@Override
	public void inAAssignStmnt(AAssignStmnt node) {
		
		if (unless == true) {
        	unlessIndexList.add(out.size());
        	out.addBegin("unless("+(PromelaVisitor.lineNr+1)+",");
        	PromelaVisitor.lineNr++;
        }
	}
	
	@Override
	public void caseAAssignmentAssignment(AAssignmentAssignment node)
    {	
		out.addBegin("assign(");
		
        inAAssignmentAssignment(node);
        if(node.getVarref() != null)
        {      	
        	boolean withVarType = true;
        	VarrefVisitor v = new VarrefVisitor(typeMap,withVarType);
            node.getVarref().apply(v);
            out.addAll(v.getList());
            pos.setStartPos(v.getPos());
        }
        
        out.add(",");
        
        if(node.getAssign() != null)
        {
            node.getAssign().apply(this);
        }
        if(node.getExpr() != null)
        {
        	ExprVisitor v = new ExprVisitor(typeMap);
            node.getExpr().apply(v);
            out.addAll(v.getList());
            pos.setEndPos(v.getPos());
        }
        outAAssignmentAssignment(node);
        
        out.addEnd(")");
        
        PromelaVisitor.lineMap.put(PromelaVisitor.lineNr, pos);
        PromelaVisitor.lineNr++;
        pos = new Position();
    }
	
	@Override
	public void caseAIncrementAssignment(AIncrementAssignment node)
    {
		out.addBegin("inc(");
		
        inAIncrementAssignment(node);
        if(node.getVarref() != null)
        {
        	boolean withVarType = true;
        	VarrefVisitor v = new VarrefVisitor(typeMap,withVarType);
            node.getVarref().apply(v);
            out.addAll(v.getList());
            pos.setStartPos(v.getPos());
        }
        
        if(node.getPlusPlus() != null)
        {
            node.getPlusPlus().apply(this);
        }
        outAIncrementAssignment(node);
        
        out.addEnd(")");
        
        pos.setEndRow(node.getPlusPlus().getLine());
        pos.setEndCol(node.getPlusPlus().getPos() + node.getPlusPlus().getText().length());
        PromelaVisitor.lineMap.put(PromelaVisitor.lineNr, pos);
        PromelaVisitor.lineNr++;
        pos = new Position();
        
    }
	
	@Override
	public void caseADecrementAssignment(ADecrementAssignment node)
    {
		out.addBegin("dec(");
		
        inADecrementAssignment(node);
        if(node.getVarref() != null)
        {
        	boolean withVarType = true;
        	VarrefVisitor v = new VarrefVisitor(typeMap,withVarType);
            node.getVarref().apply(v);
            out.addAll(v.getList());
            pos.setStartPos(v.getPos());
        }
        
        if(node.getMinusMinus() != null)
        {
            node.getMinusMinus().apply(this);
        }
        outADecrementAssignment(node);
        
        out.addEnd(")");
        
        pos.setEndRow(node.getMinusMinus().getLine());
        pos.setEndCol(node.getMinusMinus().getPos() + node.getMinusMinus().getText().length());
        PromelaVisitor.lineMap.put(PromelaVisitor.lineNr, pos);
        PromelaVisitor.lineNr++;
        pos = new Position();
    }

	/*
	@Override
	public void inAElseStmnt(AElseStmnt node) {
		
		if (unless == true) {
        	unlessIndexList.add(out.size());
        	out.addBegin("unless("+(PromelaVisitor.lineNr+1)+",");
        	PromelaVisitor.lineNr++;
        }
		
		out.addInst("else");
		
		pos.startRow = node.getElse().getLine();
		pos.startCol = node.getElse().getPos();
		pos.endRow = node.getElse().getLine();
		pos.endCol = node.getElse().getPos() + node.getElse().getText().length();
		PromelaVisitor.lineMap.put(PromelaVisitor.lineNr, pos);
		PromelaVisitor.lineNr++;
		pos = new Position();
	}
	*/
	
	@Override
	public void inABreakStmnt(ABreakStmnt node) {
		
		if (unless == true) {
        	unlessIndexList.add(out.size());
        	out.addBegin("unless("+(PromelaVisitor.lineNr+1)+",");
        	PromelaVisitor.lineNr++;
        }
		
		if (PromelaVisitor.guardCondition == true) {
			out.addBegin("break");
		}
		else {
			out.addBegin("ibreak");
		}
		
		pos.setStartRow(node.getBreak().getLine());
		pos.setStartCol(node.getBreak().getPos());
		pos.setEndRow(node.getBreak().getLine());
		pos.setEndCol(node.getBreak().getPos() + node.getBreak().getText().length());
		PromelaVisitor.lineMap.put(PromelaVisitor.lineNr, pos);
		PromelaVisitor.lineNr++;
		pos = new Position();
	}
	
	@Override
	public void inAGotoStmnt(AGotoStmnt node) {
		
		if (unless == true) {
        	unlessIndexList.add(out.size());
        	out.addBegin("unless("+(PromelaVisitor.lineNr+1)+",");
        	PromelaVisitor.lineNr++;
        }
		
		if (PromelaVisitor.guardCondition == true) {
			out.addInst("goto("+getPrologString(node.getName().getText())+")");
		}
		else {
			out.addInst("igoto("+getPrologString(node.getName().getText())+")");
		}
		
		pos.setStartRow(node.getGoto().getLine());
		pos.setStartCol(node.getGoto().getPos());
		pos.setEndRow(node.getName().getLine());
		pos.setEndCol(node.getName().getPos() + node.getName().getText().length());
		PromelaVisitor.lineMap.put(PromelaVisitor.lineNr, pos);
		PromelaVisitor.lineNr++;
		pos = new Position();
	}
	
	/*
	@Override
	public void caseALabelStmnt(ALabelStmnt node)
    {
		if (unless == true) {
        	unlessIndexList.add(out.size());
        	out.addBegin("unless("+(PromelaVisitor.lineNr+1)+",");
        	PromelaVisitor.lineNr++;
        }
		
		out.addInst("label("+getPrologString(node.getName().getText())+")");
		
		pos.startRow = node.getName().getLine();
		pos.startCol = node.getName().getPos();
		pos.endRow = node.getColon().getLine();
		pos.endCol = node.getColon().getPos() + 1;
		PromelaVisitor.lineMap.put(PromelaVisitor.lineNr, pos);
		PromelaVisitor.lineNr++;
		pos = new Position();
		
        inALabelStmnt(node);
        if(node.getName() != null)
        {
            node.getName().apply(this);
        }
        if(node.getColon() != null)
        {
            node.getColon().apply(this);
        }
        if(node.getStmnt() != null)
        {
        	SequenceVisitor v = new SequenceVisitor(typeMap);
            node.getStmnt().apply(v);
            out.addAll(v.getList());
            pos.setEndPos(v.getPos());
        }
        outALabelStmnt(node);
    }
    */
	
	@Override
	public void caseAPrintmStmnt(APrintmStmnt node)
    {
		if (unless == true) {
        	unlessIndexList.add(out.size());
        	out.addBegin("unless("+(PromelaVisitor.lineNr+1)+",");
        	PromelaVisitor.lineNr++;
        }
		
		out.addBegin("printm(");
		
        inAPrintmStmnt(node);
        if(node.getPrintm() != null)
        {
            node.getPrintm().apply(this);
        }
        if(node.getLParenthese() != null)
        {
            node.getLParenthese().apply(this);
        }
        if(node.getExpr() != null)
        {
        	ExprVisitor v = new ExprVisitor(typeMap);
            node.getExpr().apply(v);
            out.addAll(v.getList());
        }
        if(node.getRParenthese() != null)
        {
            node.getRParenthese().apply(this);
        }
        outAPrintmStmnt(node);
        
        out.addEnd(")");
		
		pos.setStartRow(node.getPrintm().getLine());
		pos.setStartCol(node.getPrintm().getPos());
		pos.setEndRow(node.getRParenthese().getLine());
		pos.setEndCol(node.getRParenthese().getPos() + 1);
		PromelaVisitor.lineMap.put(PromelaVisitor.lineNr, pos);
		PromelaVisitor.lineNr++;
		pos = new Position();
    }
	
	@Override
	public void inAPrintfStmnt(APrintfStmnt node) {
		
		if (unless == true) {
        	unlessIndexList.add(out.size());
        	out.addBegin("unless("+(PromelaVisitor.lineNr+1)+",");
        	PromelaVisitor.lineNr++;
        }
		
		out.addInst("printf("+node.getString().getText()+")");
		
		pos.setStartRow(node.getPrintf().getLine());
		pos.setStartCol(node.getPrintf().getPos());
		pos.setEndRow(node.getRParenthese().getLine());
		pos.setEndCol(node.getRParenthese().getPos() + 1);
		PromelaVisitor.lineMap.put(PromelaVisitor.lineNr, pos);
		PromelaVisitor.lineNr++;
		pos = new Position();
	}
	
	@Override
	public void inAPrintwithargsStmnt(APrintwithargsStmnt node) {
		
		if (unless == true) {
        	unlessIndexList.add(out.size());
        	out.addBegin("unless("+(PromelaVisitor.lineNr+1)+",");
        	PromelaVisitor.lineNr++;
        }
		
		String text = node.getString().getText();
		text = text.replaceAll("%c", "~c");
		text = text.replaceAll("%d", "~d");
		text = text.replaceAll("%e", "~p");
		text = text.replaceAll("%o", "~p");
		text = text.replaceAll("%u", "~p");
		text = text.replaceAll("%x", "~p");
		out.addBegin("printf("+text+",[");
	}
	
	@Override
	public void outAPrintwithargsStmnt(APrintwithargsStmnt node) {
		out.addEnd("])");
		
		pos.setStartRow(node.getPrintf().getLine());
		pos.setStartCol(node.getPrintf().getPos());
		pos.setEndRow(node.getRParenthese().getLine());
		pos.setEndCol(node.getRParenthese().getPos() + 1);
		PromelaVisitor.lineMap.put(PromelaVisitor.lineNr, pos);
		PromelaVisitor.lineNr++;
		pos = new Position();
	}
	
	@Override
	public void caseAAssertStmnt(AAssertStmnt node)
    {
		if (unless == true) {
        	unlessIndexList.add(out.size());
        	out.addBegin("unless("+(PromelaVisitor.lineNr+1)+",");
        	PromelaVisitor.lineNr++;
        }
		
		out.addBegin("assert(");
		
        inAAssertStmnt(node);
        if(node.getAssert() != null)
        {
        	node.getAssert().apply(this);
        }
        
        if(node.getExpr() != null)
        {
        	ExprVisitor v = new ExprVisitor(typeMap);
            node.getExpr().apply(v);
            out.addAll(v.getList());
            pos.setEndPos(v.getPos());
        }
        outAAssertStmnt(node);
        
        out.addEnd(")");
        
        pos.setStartRow(node.getAssert().getLine());
        pos.setStartCol(node.getAssert().getPos());
        PromelaVisitor.lineMap.put(PromelaVisitor.lineNr, pos);
		PromelaVisitor.lineNr++;
		pos = new Position();
    }
	
	@Override
	public void caseAExpressionStmnt(AExpressionStmnt node)
    {
		if (unless == true) {
        	unlessIndexList.add(out.size());
        	out.addBegin("unless("+(PromelaVisitor.lineNr+1)+",");
        	PromelaVisitor.lineNr++;
        }
		
		out.addBegin("");
		
        inAExpressionStmnt(node);
        if(node.getExpr() != null)
        {
        	ExprVisitor v = new ExprVisitor(typeMap);
        	node.getExpr().apply(v);
            out.addAll(v.getList());
            pos.setStartPos(v.getPos());
            pos.setEndPos(v.getPos());
        }
        outAExpressionStmnt(node);
        
        out.addEnd("");
        
        PromelaVisitor.lineMap.put(PromelaVisitor.lineNr, pos);
		PromelaVisitor.lineNr++;
		pos = new Position();
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
