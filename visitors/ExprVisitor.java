/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package visitors;

import promela.analysis.*;
import promela.node.*;

import java.util.HashMap;

public class ExprVisitor extends DepthFirstAdapter {

	private HashMap<String, String> typeMap;
	private MyLinkedList out = new MyLinkedList();
	private Position pos = new Position();
	
	
	public ExprVisitor(HashMap<String, String> typeMap) {
		this.typeMap = typeMap;
	}
	
	
	@Override
	public void inASimpleExpr(ASimpleExpr node) {
		out.add("expr(");
	}
	
	@Override
	public void outASimpleExpr(ASimpleExpr node) {
		out.add(")");
	}
	
	@Override
    public void caseACompoundOrExpr(ACompoundOrExpr node)
    {
		out.add("or(");
		
        inACompoundOrExpr(node);
        if(node.getAndExpr() != null)
        {
            node.getAndExpr().apply(this);
        }
        
        out.add(",");
        
        if(node.getOr() != null)
        {
            node.getOr().apply(this);
        }
        if(node.getOrExpr() != null)
        {
        	ExprVisitor v = new ExprVisitor(typeMap);
        	node.getOrExpr().apply(v);
        	out.addAll(v.getList());
        	pos.setEndPos(v.getPos());
        }
        outACompoundOrExpr(node);
        
        out.add(")");
    }
	
	@Override
    public void caseACompoundAndExpr(ACompoundAndExpr node)
    {
		out.add("and(");
		
        inACompoundAndExpr(node);
        if(node.getBitorExpr() != null)
        {
            node.getBitorExpr().apply(this);
        }
        
        out.add(",");
        
        if(node.getAnd() != null)
        {
            node.getAnd().apply(this);
        }
        if(node.getAndExpr() != null)
        {
        	ExprVisitor v = new ExprVisitor(typeMap);
        	node.getAndExpr().apply(v);
        	out.addAll(v.getList());
        	pos.setEndPos(v.getPos());
        }
        outACompoundAndExpr(node);
        
        out.add(")");
    }
    
	@Override
    public void caseACompoundBitorExpr(ACompoundBitorExpr node)
    {
		out.add("bitor(");
		
        inACompoundBitorExpr(node);
        if(node.getBitxorExpr() != null)
        {
            node.getBitxorExpr().apply(this);
        }
        
        out.add(",");
        
        if(node.getBitor() != null)
        {
            node.getBitor().apply(this);
        }
        if(node.getBitorExpr() != null)
        {
        	ExprVisitor v = new ExprVisitor(typeMap);
        	node.getBitorExpr().apply(v);
        	out.addAll(v.getList());
        	pos.setEndPos(v.getPos());
        }
        outACompoundBitorExpr(node);
        
        out.add(")");
    }
    
	@Override
    public void caseACompoundBitxorExpr(ACompoundBitxorExpr node)
    {
		out.add("bitxor(");
		
        inACompoundBitxorExpr(node);
        if(node.getBitandExpr() != null)
        {
            node.getBitandExpr().apply(this);
        }
        
        out.add(",");
        
        if(node.getBitxor() != null)
        {
            node.getBitxor().apply(this);
        }
        if(node.getBitxorExpr() != null)
        {
        	ExprVisitor v = new ExprVisitor(typeMap);
        	node.getBitxorExpr().apply(v);
        	out.addAll(v.getList());
        	pos.setEndPos(v.getPos());
        }
        outACompoundBitxorExpr(node);
        
        out.add(")");
    }
    
	@Override
    public void caseACompoundBitandExpr(ACompoundBitandExpr node)
    {
		out.add("bitand(");
		
        inACompoundBitandExpr(node);
        if(node.getEqExpr() != null)
        {
            node.getEqExpr().apply(this);
        }
        
        out.add(",");
        
        if(node.getBitand() != null)
        {
            node.getBitand().apply(this);
        }
        if(node.getBitandExpr() != null)
        {
        	ExprVisitor v = new ExprVisitor(typeMap);
        	node.getBitandExpr().apply(v);
        	out.addAll(v.getList());
        	pos.setEndPos(v.getPos());
        }
        outACompoundBitandExpr(node);
        
        out.add(")");
    }
    
	@Override
    public void caseACompoundEqExpr(ACompoundEqExpr node)
    {
		if (node.getEqop().getText().equals("==")) {
        	out.add("eq(");
        }
        if (node.getEqop().getText().equals("!=")) {
        	out.add("uneq(");
        }
        
        inACompoundEqExpr(node);
        if(node.getRelExpr() != null)
        {
        	node.getRelExpr().apply(this);
        }
        
        out.add(",");
        
        if(node.getEqop() != null)
        {
            node.getEqop().apply(this);    
        }
        if(node.getEqExpr() != null)
        {
        	ExprVisitor v = new ExprVisitor(typeMap);
        	node.getEqExpr().apply(v);
        	out.addAll(v.getList());
        	pos.setEndPos(v.getPos());
        }
        outACompoundEqExpr(node);
        
        out.add(")");
    }
    
	@Override
    public void caseACompoundltRelExpr(ACompoundltRelExpr node)
    {
		out.add("lt(");
		
        inACompoundltRelExpr(node);
        if(node.getShiftExpr() != null)
        {
            node.getShiftExpr().apply(this);
        }
        
        out.add(",");
        
        if(node.getLt() != null)
        {
            node.getLt().apply(this);
        }
        if(node.getRelExpr() != null)
        {
        	ExprVisitor v = new ExprVisitor(typeMap);
        	node.getRelExpr().apply(v);
        	out.addAll(v.getList());
        	pos.setEndPos(v.getPos());
        }
        outACompoundltRelExpr(node);
        
        out.add(")");
    }
    
	@Override
    public void caseACompoundgtRelExpr(ACompoundgtRelExpr node)
    {
		out.add("gt(");
		
        inACompoundgtRelExpr(node);
        if(node.getShiftExpr() != null)
        {
            node.getShiftExpr().apply(this);
        }
        
        out.add(",");
        
        if(node.getGt() != null)
        {
            node.getGt().apply(this);
        }
        if(node.getRelExpr() != null)
        {
        	ExprVisitor v = new ExprVisitor(typeMap);
        	node.getRelExpr().apply(v);
        	out.addAll(v.getList());
        	pos.setEndPos(v.getPos());
        }
        outACompoundgtRelExpr(node);
        
        out.add(")");
    }
    
	@Override
    public void caseACompoundrelopRelExpr(ACompoundrelopRelExpr node)
    {
		if (node.getRelop().getText().equals("<=")) {
        	out.add("leqt(");
        }
        if (node.getRelop().getText().equals(">=")) {
        	out.add("geqt(");
        }
        
        inACompoundrelopRelExpr(node);
        if(node.getShiftExpr() != null)
        {
            node.getShiftExpr().apply(this);
        }
        
        out.add(",");
        
        if(node.getRelop() != null)
        {
            node.getRelop().apply(this);    
        }
        if(node.getRelExpr() != null)
        {
        	ExprVisitor v = new ExprVisitor(typeMap);
        	node.getRelExpr().apply(v);
        	out.addAll(v.getList());
        	pos.setEndPos(v.getPos());
        }
        outACompoundrelopRelExpr(node);
        
        out.add(")");
    }
    
	@Override
    public void caseACompoundShiftExpr(ACompoundShiftExpr node)
    {
		if (node.getShiftop().getText().equals("<<")) {
        	out.add("lshift(");
        }
        if (node.getShiftop().getText().equals(">>")) {
        	out.add("rshift(");
        }
        
        inACompoundShiftExpr(node);
        if(node.getAddExpr() != null)
        {
            node.getAddExpr().apply(this);
        }
        
        out.add(",");
        
        if(node.getShiftop() != null)
        {
            node.getShiftop().apply(this);
        }
        if(node.getShiftExpr() != null)
        {
        	ExprVisitor v = new ExprVisitor(typeMap);
        	node.getShiftExpr().apply(v);
        	out.addAll(v.getList());
        	pos.setEndPos(v.getPos());
        }
        outACompoundShiftExpr(node);
        
        out.add(")");
    }
    
	@Override
    public void caseACompoundminusAddExpr(ACompoundminusAddExpr node)
    {	
        inACompoundminusAddExpr(node);
        if(node.getMultExpr() != null)
        {
            node.getMultExpr().apply(this);
        }
        
        out.add(" - ");
        
        if(node.getMinus() != null)
        {
            node.getMinus().apply(this);
        }
        if(node.getAddExpr() != null)
        {
        	ExprVisitor v = new ExprVisitor(typeMap);
        	node.getAddExpr().apply(v);
        	out.addAll(v.getList());
        	pos.setEndPos(v.getPos());
        }
        outACompoundminusAddExpr(node);
        
    }
	
	@Override
    public void caseACompoundplusAddExpr(ACompoundplusAddExpr node)
    {
        inACompoundplusAddExpr(node);
        if(node.getMultExpr() != null)
        {
            node.getMultExpr().apply(this);
        }
        
        out.add(" + ");
        
        if(node.getPlus() != null)
        {
            node.getPlus().apply(this);
        }
        if(node.getAddExpr() != null)
        {
        	ExprVisitor v = new ExprVisitor(typeMap);
        	node.getAddExpr().apply(v);
        	out.addAll(v.getList());
        	pos.setEndPos(v.getPos());
        }
        outACompoundplusAddExpr(node);
        
    }
    
	@Override
    public void caseACompoundMultExpr(ACompoundMultExpr node)
    {
		
        
        inACompoundMultExpr(node);
        if(node.getUnExpr() != null)
        {
            node.getUnExpr().apply(this);
        }
        
        if (node.getMultop().getText().equals("*")) {
        	out.add(" * ");
        }
        if (node.getMultop().getText().equals("/")) {
        	out.add(" / ");
        }
        if (node.getMultop().getText().equals("%")) {
        	out.add(" mod ");
        }
        
        if(node.getMultop() != null)
        {
            node.getMultop().apply(this);    
        }
        if(node.getMultExpr() != null)
        {
        	ExprVisitor v = new ExprVisitor(typeMap);
        	node.getMultExpr().apply(v);
        	out.addAll(v.getList());
        	pos.setEndPos(v.getPos());
        }
        outACompoundMultExpr(node);
        
    }
    
	@Override
    public void inAComplementUnExpr(AComplementUnExpr node) {
    	out.add("compl(");
    }
	@Override
    public void outAComplementUnExpr(AComplementUnExpr node) {
    	out.add(")");
		pos.setStartRow(node.getComplement().getLine());
    	pos.setStartCol(node.getComplement().getPos());
    }
    
	@Override
    public void inANotUnExpr(ANotUnExpr node) {
    	out.add("not(");
    }
	@Override
    public void outANotUnExpr(ANotUnExpr node) {
		out.add(")");
    	pos.setStartRow(node.getBang().getLine());
    	pos.setStartCol(node.getBang().getPos());
    }
   
    /*
     * Factor
     */
	@Override
    public void inAParentheseFactor(AParentheseFactor node) {
    	out.add("(");
    }
    
	@Override
    public void outAParentheseFactor(AParentheseFactor node) {
    	out.add(")");
    	pos.setStartRow(node.getLParenthese().getLine());
    	pos.setStartCol(node.getLParenthese().getPos());
    	pos.setEndRow(node.getRParenthese().getLine());
		pos.setEndCol(node.getRParenthese().getPos() + 1);
    }
    
    @Override
    public void caseALengthFactor(ALengthFactor node)
    {
        out.add("len(");
        
        inALengthFactor(node);
        if(node.getLen() != null)
        {
            node.getLen().apply(this);
        }
        if(node.getLParenthese() != null)
        {
            node.getLParenthese().apply(this);
        }
        if(node.getVarref() != null)
        {
        	VarrefVisitor v = new VarrefVisitor(typeMap);
            node.getVarref().apply(v);
            out.addAll(v.getList());
        }
        if(node.getRParenthese() != null)
        {
            node.getRParenthese().apply(this);
        }
        outALengthFactor(node);

    	out.add(")");
    	pos.setStartRow(node.getLParenthese().getLine());
    	pos.setStartCol(node.getLParenthese().getPos());
    	pos.setEndRow(node.getRParenthese().getLine());
		pos.setEndCol(node.getRParenthese().getPos() + 1);
    }

    
    /*recv_poll*/
	@Override
    public void caseAFifoRecvPoll(AFifoRecvPoll node)
    {
    	out.add("expr_poll(");
    	
        inAFifoRecvPoll(node);
        if(node.getVarref() != null)
        {
        	VarrefVisitor v = new VarrefVisitor(typeMap);
            node.getVarref().apply(v);
            out.addAll(v.getList());
            pos.setStartPos(v.getPos());
        }
        
        out.add(",");
        
        if(node.getQuery() != null)
        {
            node.getQuery().apply(this);
        }
        
        out.add("[");
        
        if(node.getLBracket() != null)
        {
            node.getLBracket().apply(this);
        }
        if(node.getRecvArgs() != null)
        {
            node.getRecvArgs().apply(this);
        }
        
        out.add("]");
        
        if(node.getRBracket() != null)
        {
            node.getRBracket().apply(this);
        }
        outAFifoRecvPoll(node);
        
        out.add(")");
        
        pos.setEndRow(node.getRBracket().getLine());
		pos.setEndCol(node.getRBracket().getPos() + 1);
    }
    
	@Override
    public void caseARandomRecvPoll(ARandomRecvPoll node)
    {
    	out.add("expr_random_poll(");
    	
        inARandomRecvPoll(node);
        if(node.getVarref() != null)
        {
        	VarrefVisitor v = new VarrefVisitor(typeMap);
            node.getVarref().apply(v);
            out.addAll(v.getList());
            pos.setStartPos(v.getPos());
        }
        
        out.add(",");
        
        if(node.getQueryQuery() != null)
        {
            node.getQueryQuery().apply(this);
        }
        
        out.add("[");
        
        if(node.getLBracket() != null)
        {
            node.getLBracket().apply(this);
        }
        if(node.getRecvArgs() != null)
        {
            node.getRecvArgs().apply(this);
        }
        
        out.add("]");
        
        if(node.getRBracket() != null)
        {
            node.getRBracket().apply(this);
        }
        outARandomRecvPoll(node);
        
        out.add(")");
        
        pos.setEndRow(node.getRBracket().getLine());
		pos.setEndCol(node.getRBracket().getPos() + 1);
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
    }

	
	@Override
	public void caseAVarRecvArg(AVarRecvArg node)
    {
        inAVarRecvArg(node);
        if(node.getVarref() != null)
        {
        	VarrefVisitor v = new VarrefVisitor(typeMap);
            node.getVarref().apply(v);
            out.addAll(v.getList());
        }
        outAVarRecvArg(node);
    }
    
	@Override
    public void inAEvalRecvArg(AEvalRecvArg node) {
    	out.add("eval(");
    }
    
	@Override
    public void outAEvalRecvArg(AEvalRecvArg node) {
    	out.add(")");
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
        }
        outAConstRecvArg(node);
    }
    
	@Override
    public void caseAVarrefFactor(AVarrefFactor node)
    {
        inAVarrefFactor(node);
        if(node.getVarref() != null)
        {
        	VarrefVisitor v = new VarrefVisitor(typeMap);
     	   	node.getVarref().apply(v);
     	   	out.addAll(v.getList());
     	   	pos.setStartPos(v.getPos());
     	   	pos.setEndPos(v.getPos());
        }
        outAVarrefFactor(node);
    }
	
	@Override
	public void caseAConstFactor(AConstFactor node)
    {
        inAConstFactor(node);
        if(node.getConst() != null)
        {
        	ConstVisitor v = new ConstVisitor(typeMap);
            node.getConst().apply(v);
            out.addAll(v.getList());
            pos.setStartPos(v.getPos());
            pos.setEndPos(v.getPos());
        }
        outAConstFactor(node);
    }

	@Override
    public void inATimeoutFactor(ATimeoutFactor node) {
    	out.add("timeout");
    	pos.setStartRow(node.getTimeout().getLine());
    	pos.setStartCol(node.getTimeout().getPos());
    	pos.setEndRow(node.getTimeout().getLine());
    	pos.setEndCol(node.getTimeout().getPos() + node.getTimeout().getText().length());
    }
    
	@Override
    public void inANonprogressFactor(ANonprogressFactor node) {
    	out.add("np_");
    	pos.setStartRow(node.getNp().getLine());
    	pos.setStartCol(node.getNp().getPos());
    	pos.setEndRow(node.getNp().getLine());
    	pos.setEndCol(node.getNp().getPos() + node.getNp().getText().length());

    }
    
	@Override
    public void inAEnabledFactor(AEnabledFactor node) {
    	out.add("enabled(");
    }
    
	@Override
    public void outAEnabledFactor(AEnabledFactor node) {
    	out.add(")");
    	pos.setStartRow(node.getEnabled().getLine());
    	pos.setStartCol(node.getEnabled().getPos());
    	pos.setEndRow(node.getRParenthese().getLine());
    	pos.setEndCol(node.getRParenthese().getPos() + 1);
    }
    
	@Override
    public void inAPcValueFactor(APcValueFactor node) {
    	out.add("pc_value(");
    }
    
	@Override
    public void outAPcValueFactor(APcValueFactor node) {
    	out.add(")");
    	pos.setStartRow(node.getPcValue().getLine());
    	pos.setStartCol(node.getPcValue().getPos());
    	pos.setEndRow(node.getRParenthese().getLine());
    	pos.setEndCol(node.getRParenthese().getPos() + 1);
    }
    
	@Override
    public void inARemoterefFactor(ARemoterefFactor node) {
    	out.add("at("+node.getLabel().getText()+","+node.getProcess().getText()+",");
    }
    
	@Override
    public void outARemoterefFactor(ARemoterefFactor node) {
    	out.add(")");
    	pos.setStartRow(node.getProcess().getLine());
    	pos.setStartCol(node.getProcess().getPos());
    	pos.setEndRow(node.getLabel().getLine());
    	pos.setEndCol(node.getLabel().getPos() + + node.getLabel().getText().length());
    }
    
	@Override
    public void inARunFactor(ARunFactor node) {
    	out.add("run("+getPrologString(node.getName().getText())+",[");
    }
    
	@Override
    public void outARunFactor(ARunFactor node) {
    	out.add("])");
    	pos.setStartRow(node.getRun().getLine());
    	pos.setStartCol(node.getRun().getPos());
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
    
	@Override
	public void caseAChanopFactor(AChanopFactor node)
    {
		out.add(node.getChanop().getText()+"(");
		
        inAChanopFactor(node);
        if(node.getChanop() != null)
        {
            node.getChanop().apply(this);
        }
        if(node.getLParenthese() != null)
        {
            node.getLParenthese().apply(this);
        }
        if(node.getVarref() != null)
        {
        	VarrefVisitor v = new VarrefVisitor(typeMap);
            node.getVarref().apply(v);
            out.addAll(v.getList());
        }
        if(node.getRParenthese() != null)
        {
            node.getRParenthese().apply(this);
        }
        outAChanopFactor(node);
        
        out.add(")");
        pos.setStartRow(node.getChanop().getLine());
        pos.setStartCol(node.getChanop().getPos());
    	pos.setEndRow(node.getRParenthese().getLine());
    	pos.setEndCol(node.getRParenthese().getPos() + 1);
    }
	
	@Override
	public void caseAConditionalFactor(AConditionalFactor node)
    {
		out.add("cond(");
		
        inAConditionalFactor(node);
        if(node.getLParenthese() != null)
        {
            node.getLParenthese().apply(this);
        }
        if(node.getIf() != null)
        {
            node.getIf().apply(this);
        }
        
        out.add(",");
        
        if(node.getRightarrow() != null)
        {
            node.getRightarrow().apply(this);
        }
        if(node.getThen() != null)
        {
            node.getThen().apply(this);
        }
        
        out.add(",");
        
        if(node.getColon() != null)
        {
            node.getColon().apply(this);
        }
        if(node.getElse() != null)
        {
            node.getElse().apply(this);
        }
        if(node.getRParenthese() != null)
        {
            node.getRParenthese().apply(this);
        }
        outAConditionalFactor(node);
        
        out.add(")");
		pos.setStartRow(node.getLParenthese().getLine());
		pos.setStartCol(node.getLParenthese().getPos());
		pos.setEndRow(node.getRParenthese().getLine());
		pos.setEndCol(node.getRParenthese().getPos() + 1);
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
