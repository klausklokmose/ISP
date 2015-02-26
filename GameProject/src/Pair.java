
public class Pair implements Comparable<Pair>{
	private final int column;
	private final int row;
	
	public Pair(int column, int row){
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
		return "["+column+", "+row+"]";
	}

	@Override
	public int compareTo(Pair o) {
		if(column == o.column){
			return o.row - row;
		}
		return o.column - column;
	}
}
