
public class Action implements Comparable<Action>{
	private final int column;
	private final int row;
	
	/**
	 * Action is a holder for a coordinate on the game board.
	 * @param column
	 * @param row
	 */
	public Action(int column, int row){
		this.column = column;
		this.row = row;
	}

	public int getColumn() {
		return column;
	}

	public int getRow() {
		return row;
	}
	
	public String toString(){
		return "["+column+", "+row+"] ";
	}

	/**
	 * This method is used, because the Action class is used in a priority queue. 
	 * It will first sort by column (ascending), and secondly by row (descending)
	 */
	@Override
	public int compareTo(Action o) {
		
		if(column == o.column){
			return o.row - row;
		}
		return o.column - column;
	}
}
