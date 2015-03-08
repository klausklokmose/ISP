
public class Action implements Comparable<Action>{
	private final int column;
	private final int row;
	private final int value;
	
	public Action(int column, int row){
		this.column = column;
		this.row = row;
		this.value = Integer.MIN_VALUE;
	}
	
	public Action(int column, int row, int val){
		this.column = column;
		this.row = row;
		this.value = val;
	}

	public int getValue(){
		return value;
	}
	public int getColumn() {
		return column;
	}

	public int getRow() {
		return row;
	}
	
	public String toString(){
		return "["+column+", "+row+"] "+value;
	}

	@Override
	public int compareTo(Action o) {
		if(column == o.column){
			return o.row - row;
		}
		return o.column - column;
	}
}
