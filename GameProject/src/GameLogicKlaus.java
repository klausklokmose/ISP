import java.util.PriorityQueue;

public class GameLogicKlaus implements IGameLogic {
	private int cols;
	private int rows;
	private int playerID;
	private int[][] board;
	private int turns;

	private PriorityQueue<Pair> queueOne;
	private PriorityQueue<Pair> queueTwo;
	
	
	
	public GameLogicKlaus() {
		// TODO Write your implementation for this method
	}

	public void initializeGame(int cols, int rows, int playerID) {
		this.cols = cols;
		this.rows = rows;
		this.playerID = playerID;
		// TODO Write your implementation for this method
		board = new int[cols][rows];
		queueOne = new PriorityQueue<>();
		queueTwo = new PriorityQueue<>();
	}

	public Winner gameFinished() {
		// TODO Write your implementation for this method
		if(turns >= 7){
			return Winner.NOT_FINISHED;
		}else{
			return Winner.NOT_FINISHED;
		}
	}

	public void insertCoin(int column, int playerID) {
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
		}else{
			queueTwo.add(p);
		}
		printQueues();
	}

	public int decideNextMove() {
		// TODO Write your implementation for this method
		return 0;
	}
	
	private void printBoard() {
		System.out.println("........turn "+turns+".......");
		for (int i = 0; i < board.length; i++) {
			String str = "";
			for (int j = 0; j < board.length; j++) {
				str += " "+board[j][i];
			}
			System.out.println(str+"\n");
		}
	}

	private void printQueues() {
		System.out.println("\nPlayer 1:");
		for (Pair pa : queueOne) {
			System.out.println(pa);
		}
		System.out.println(".......................\nPlayer 2:");
		for (Pair pa : queueTwo) {
			System.out.println(pa);
		}
	}
}
