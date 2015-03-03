import java.util.PriorityQueue;


public class TestingFunctions {

	private static int[][] board;
	private static PriorityQueue<Pair> queueOne;
	private static int turns = 0;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		board = new int[4][4];
		
//		queueOne = new PriorityQueue<>();
//		queueOne.add(new Pair(0, 2));
//		queueOne.add(new Pair(0, 1));
//		queueOne.add(new Pair(1, 1));
//		insertCoin(0, 1);
//		insertCoin(0, 2);
//		insertCoin(0, 1);
//		insertCoin(0, 1);
//		printBoard();
//		printQueueOne();
		
		Pair[] pairs = new Pair[]{new Pair(0, 5), new Pair(0, 3), new Pair(0, 2), 
								new Pair(1, 5), new Pair(1, 4), new Pair(1, 3),new Pair(1, 2)};
		int column = 0;
		int barrier = 4;
		int lastRow = -1;
		for (int i = 0; i < pairs.length; i++) {
			//reset
			if(pairs[i].getColumn() != column){
				System.out.println("reset");
				column = pairs[i].getColumn();
				barrier = 4;
				lastRow = -1;
			}
			if(lastRow == -1){
				System.out.println("set row");
				lastRow = pairs[i].getRow();
			}
			System.out.println("lastRow"+lastRow);
			System.out.println("thisRow"+pairs[i].getRow());
			int diff = lastRow-pairs[i].getRow();
			if(diff == 1 || diff == 0){
				System.out.println("decrease barrier");
				barrier--;
				lastRow = pairs[i].getRow();
			}else{
				lastRow = -1;
			}
			
			if(barrier == 0){
				System.out.println("found 4 coins!! in column "+column);
				break;
			}
		}
	}

	private static void printBoard() {
		System.out.println("........turn "+turns+".......");
		for (int i = 0; i < board.length; i++) {
			String str = "";
			for (int j = 0; j < board.length; j++) {
				str += " "+board[j][i];
			}
			System.out.println(str);
		}
	}

	public static void insertCoin(int column, int playerID) {
		// TODO Write your implementation for this method
		int[] col = board[column];
		//[x, y] column, row
		Pair p = null ;
		//the column is non-empty
		if (col[0] == 0) {
			boolean inserted = false;
			for (int row = 0; row < col.length; row++) {
				if (col[row] != 0) {
					col[row - 1] = playerID;
					inserted = true;
					p = new Pair(column, row-1);
					break;
				}
			}
			//the column must be empty
			if (!inserted) {
				col[col.length - 1] = playerID;
				p = new Pair(column, col.length-1);
				inserted = true;
			}
		}else{
			System.out.println("you can't insert a coin here, because it is full!");
		}
		//Testing print queues
		if(playerID == 1){
			queueOne.add(p);
		}
	}

	private static void printQueueOne() {
		for (Pair pa : queueOne) {
			System.out.println(pa);
		}
	}
	
}
