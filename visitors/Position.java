/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package visitors;

public class Position {

	private int startRow;
	private int endRow;
	private int startCol;
	private int endCol;
	
	public Position() {	
		startRow = 0;
		endRow = 0;
		startCol = 0;
		endCol = 0;
	}
	
	public void  setStartRow(int x) {
		if (PromelaVisitor.currentFile.equals(PromelaVisitor.fileIn)) {
			startRow = x - PromelaVisitor.adjustLine;
		}
		else {
			startRow = 0;
		}
	}
	
	public void  setEndRow(int x) {
		if (PromelaVisitor.currentFile.equals(PromelaVisitor.fileIn)) {
			endRow = x - PromelaVisitor.adjustLine;
		}
		else {
			endRow = 0;
		}
	}

	public void  setStartCol(int x) {
		if (PromelaVisitor.currentFile.equals(PromelaVisitor.fileIn)) {
			startCol = x;
		}
		else {
			startCol = 0;
		}
	}

	public void  setEndCol(int x) {
		if (PromelaVisitor.currentFile.equals(PromelaVisitor.fileIn)) {
			endCol = x;
		}
		else {
			endCol = 0;
		}
	}
	
	public void setStartPos(Position pos) {
		if (PromelaVisitor.currentFile.equals(PromelaVisitor.fileIn)) {
			this.startRow = pos.startRow;
			this.startCol = pos.startCol;
		}
		else {
			this.startRow = 0;
			this.startCol = 0;
		}
	}
	
	public void setEndPos(Position pos) {
		if (PromelaVisitor.currentFile.equals(PromelaVisitor.fileIn)) {
			this.endRow = pos.endRow;
			this.endCol = pos.endCol;
		}
		else {
			this.endRow = 0;
			this.endCol = 0;
		}
	}
	
	public int getStartRow() {
		return startRow;
	}
	public int getStartCol() {
		return startCol;
	}
	public int getEndRow() {
		return endRow;
	}
	public int getEndCol() {
		return endCol;
	}
}
