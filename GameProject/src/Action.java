
public class Action implements Comparable<Action>{
	private final int column;
	private final int row;
	
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

	@Override
	public int compareTo(Action o) {
		if(column == o.column){
			return o.row - row;
		}
		return o.column - column;
	}
}
