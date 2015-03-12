import java.util.Iterator;
import java.util.PriorityQueue;


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
		int colDiff = Integer.compare(column, o.column);
		if(colDiff == 0){
			return Integer.compare(row, o.row);
		}
		return colDiff;
	}

	@Override
	public boolean equals(Object arg0) {
		if(!(arg0 instanceof Action)){
			return false;
		}
		// TODO Auto-generated method stub
		return column == ((Action)arg0).column && row == ((Action)arg0).row;
	}

	@Override
	protected Object clone() {
		return new Action(column, row);
	}
	
	public static PriorityQueue<Action> cloneQueue(PriorityQueue<Action> queue){
		PriorityQueue<Action> tmp = new PriorityQueue();
		for (Iterator<Action> it = queue.iterator(); it.hasNext();) {
			tmp.add((Action) it.next().clone());
		}
		return tmp;
	}
}
