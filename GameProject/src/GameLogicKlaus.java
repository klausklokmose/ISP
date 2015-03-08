import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

public class GameLogicKlaus implements IGameLogic {
	private int noCols;
	private int noRows;
	private int playerID;
	private int[][] board;
	private int turns;

	private PriorityQueue<Action> queueOne;
	private PriorityQueue<Action> queueTwo;
	
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
		Action p = null ;
		//the column is non-empty
		if (col[0] == 0) {
			boolean inserted = false;
			for (int row = 0; row < col.length; row++) {
				if (col[row] != 0) {
					col[row - 1] = playerID;
					inserted = true;
					p = new Action(column, row-1);
					break;
				}
			}
			//the column must be empty
			if (!inserted) {
				col[col.length - 1] = playerID;
				p = new Action(column, col.length-1);
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
//		get actions(state)
		List<Action> actions = actions(state);
		
		//return action with value v
		return 0;
	}

	//TODO should maybe return a pair or both a pair and the value v
	private int max_value(int[][] state, int alpha, int beta){
		int[][] s = state.clone();
		if(gameFinished() != Winner.NOT_FINISHED) return utility(s);
		int v = Integer.MIN_VALUE;
		List<Action> actions = actions(s);
		for (Action a : actions) {
			v = Math.max(v, min_value(result(s, a), alpha, beta));
			if(v >= beta) return v;
			alpha = Math.max(alpha, v);
		}
		return v;
	}
	
	private int[][] result(int[][] state, Action pair) {
		state[pair.getColumn()][pair.getRow()] = playerID;
		return state;
	}

	private int min_value(int[][] state, int alpha, int beta){
		int[][] s = state.clone();
		if(gameFinished() != Winner.NOT_FINISHED) return utility(s);
		int v = Integer.MAX_VALUE;
		List<Action> actions = actions(s);
		for (Action a : actions) {
			v = Math.min(v, max_value(result(s, a), alpha, beta));
			if(v <= alpha) return v;
			beta = Math.min(beta, v);
		}
		return v;
	}
	
	private int utility(int[][] state){
		int utility = -1; //not found
		
		PriorityQueue<Action> player1 = new PriorityQueue<>();
		PriorityQueue<Action> player2 = new PriorityQueue<>();
		for (int column = 0; column < state.length; column++) {
			for (int row = 0; row < state[column].length; row++) {
				int player = state[column][row];
				if(player==playerID){
					player1.add(new Action(column, row));
				}else {
					player2.add(new Action(column, row));
				}
			}
		}
		Action[] pairs = (Action[]) player1.toArray();
		/*
		 * for each column i in player1 check if they have 4 connected coins
		 * 
		 * iterate through each column and for each entry try to find a diagonal path
		 */
		int match = findMatch(pairs, playerID);
		
		
		return 0;
	}
/*
 * returns -2 if nothing was found
 */
	private int findMatch(Action[] actions, int playerID) {
		int v_column = 0;
		int v_barrier = 4;
		int v_lastRow = -1;
		
		int[] h_barrier = new int[noRows];
		int[] h_last = new int[noRows];
		for (int i = 0; i < h_last.length; i++) {
			h_last[i] = -1; //init
			h_barrier[i] = 4;
		}
		
		for (Action action : actions) {
//			Action p = a;
			int r = action.getRow();
			int c = action.getColumn();
			System.out.println("LOOKING AT ACTION"+action);
			
			//VERTICAL
			if(c != v_column){ 	//reset vertical
				System.out.println("reset vertical");
				v_column = c;
				v_barrier = 4;
				v_lastRow = -1;
			}
		
			if(v_lastRow == -1){
				System.out.println("set row");
				v_lastRow = r;
			}

			int v_diff = v_lastRow - r;
			if(v_diff == 1 || v_diff == 0){
				System.out.println("VERTICAL: decrease barrier");
				v_lastRow = r;
				v_barrier--;
			}else{
				v_lastRow = -1;
			}
			
			if(v_barrier == 0){
				System.out.println("found 4 coins!! VERTICAL in column "+v_column);
				return 1;
			}
			
			
			//HORIZONTAL
			if(h_last[r] == -1){
				System.out.println("inital h_how at row="+r);
				h_last[r] = r;
			}
			
			int h_diff = h_last[r]-r;
			if(h_diff == 0){
				h_last[r] = r;
				h_barrier[r]--;
			}else{ //reset
				h_last[r] = -1;
				h_barrier[r] = 4;
			}
			
			if(h_barrier[r]==0){
				System.out.println("found 4 coins!! HORIZONTAL in row "+r);
				return 1;
			}
			
			
			//DIAGONAL running time = noPairs*4
			Action[] d_up = new Action[]{new Action(c+1, r-1), new Action(c+2, r-2), new Action(c+3, r-3)};
			Action[] d_dwn = new Action[]{new Action(c+1, r+1), new Action(c+2, r+2), new Action(c+3, r+3)};
			int upBarrier, dwnBarrier;
			upBarrier = dwnBarrier = 4-1; //4 coins minus 1 (the one we are looking at)
			
			for (Action a : actions) {
				int iRow = a.getRow();
				int iColumn = a.getColumn();
				for (int j = 0; j < d_up.length; j++) {
					if( iColumn == d_up[j].getColumn() && iRow == d_up[j].getRow()){
						upBarrier--;
						System.out.println("DIAGONAL UP: decrease barrier: "+upBarrier+"\t"+a);
						break;
					}else if(iColumn == d_dwn[j].getColumn() && iRow == d_dwn[j].getRow() ){
						dwnBarrier--;
						System.out.println("DIAGONAL DWN: decrease barrier: "+dwnBarrier+"\t"+a);
						break;
					}
				}
				if(upBarrier == 0 || dwnBarrier == 0){ //early return! we do not need to iterate more
					System.out.println("FOUND DIAGONAL early MATCH");
					return 1;
				}
			}
			if(upBarrier == 0 || dwnBarrier == 0){
				System.out.println("FOUND DIAGONAL MATCH"+action+", "+Arrays.toString(d_up));
				return 1;
			}
			System.out.println();
			
		}//END LOOP THROUGH 
		
		
		System.out.println("NO LUCK...");
		return -2;
	}

	
	private List<Action> actions(int[][] state){
		int[][] s = state.clone();
		List<Action> actions = new ArrayList<Action>();
		
		for (int column = 0; column < s.length; column++) {
			if(s[column][0] == 0){ //the column is not full!!
				Action p = null;
				for (int row = 0; row < s[column].length; row++) {
					if(s[column][row] != 0){
						//add the prior row action
						p = new Action(column, row-1);
						break;
					}
				}
				if(p == null){ //column is be empty
					//add this action
					int lastItemInRow = s[column].length-1;
					p = new Action(column, lastItemInRow);
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
		for (Action pa : queueOne) {
			System.out.println(pa);
		}
		System.out.println(".......................\nPlayer 2:");
		for (Action pa : queueTwo) {
			System.out.println(pa);
		}
	}
}
