import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class GameLogicKlaus implements IGameLogic {
	private int noCols;
	private int noRows;
	private int playerID;
	private int[][] board;
	private int turns;

	private PriorityQueue<Pair> queueOne;
	private PriorityQueue<Pair> queueTwo;
	
	
	
	public GameLogicKlaus() {
		// TODO Write your implementation for this method
	}

	public void initializeGame(int noCols, int noRows, int playerID) {
		this.noCols = noCols;
		this.noRows = noRows;
		this.playerID = playerID;
		// TODO Write your implementation for this method
		board = new int[noCols][noRows];
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
			
			if(playerID == 1){
				queueOne.add(p);
			}else{
				queueTwo.add(p);
			}
			printQueues();
		}else{
			System.out.println("you can't insert a coin here, because it is full!");
		}
	}
	
	
	private int alpha_beta_search(int[][] state){
		if(gameFinished() != Winner.NOT_FINISHED) return utility(state);
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		int v = max_value(state, alpha, beta);
		//get actions(state)
		//return action with value v
		return 0;
	}

	private int max_value(int[][] state, int alpha, int beta){
		int[][] s = state.clone();
		if(gameFinished() != Winner.NOT_FINISHED) return utility(s);
		int v = Integer.MIN_VALUE;
		List<Pair> actions = actions(s);
		for (int i = 0; i < actions.size(); i++) {
			v = Math.max(v, min_value(result(s, actions.get(i)), alpha, beta));
			if(v >= beta) return v;
			alpha = Math.max(alpha, v);
		}
		return v;
	}
	
	private int[][] result(int[][] state, Pair pair) {
		state[pair.getColumn()][pair.getRow()] = playerID;
		return state;
	}

	private int min_value(int[][] state, int alpha, int beta){
		int[][] s = state.clone();
		if(gameFinished() != Winner.NOT_FINISHED) return utility(s);
		int v = Integer.MAX_VALUE;
		List<Pair> actions = actions(s);
		for (int i = 0; i < actions.size(); i++) {
			v = Math.min(v, max_value(result(s, actions.get(i)), alpha, beta));
			if(v <= alpha) return v;
			beta = Math.min(beta, v);
		}
		return v;
	}
	
	private int utility(int[][] state){
		int utility = -1; //not found
		
		PriorityQueue<Pair> player1 = new PriorityQueue<>();
		PriorityQueue<Pair> player2 = new PriorityQueue<>();
		for (int column = 0; column < state.length; column++) {
			for (int row = 0; row < state[column].length; row++) {
				int player = state[column][row];
				if(player==playerID){
					player1.add(new Pair(column, row));
				}else {
					player2.add(new Pair(column, row));
				}
			}
		}
		Pair[] pairs = (Pair[]) player1.toArray();
		/*
		 * for each column i in player1 check if they have 4 connected coins
		 * 
		 * iterate through each column and for each entry try to find a diagonal path
		 */
		int match = findVerticalMatch(pairs);
		
		
		return 0;
	}

	private int findVerticalMatch(Pair[] pairs) {
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
				return 1;
			}
		}
		return -2;
	}

	
	private List<Pair> actions(int[][] state){
		int[][] s = state.clone();
		List<Pair> actions = new ArrayList<Pair>();
		
		for (int column = 0; column < s.length; column++) {
			if(s[column][0] == 0){ //the column is not full!!
				Pair p = null;
				for (int row = 0; row < s[column].length; row++) {
					if(s[column][row] != 0){
						//add the prior row action
						p = new Pair(column, row-1);
						break;
					}
				}
				if(p == null){ //column is be empty
					//add this action
					int lastItemInRow = s[column].length-1;
					p = new Pair(column, lastItemInRow);
				}
				actions.add(p);
			}
			
		}
		return actions;
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
