import java.util.Iterator;
import java.util.PriorityQueue;

public class TestingFunctions {

	static int noRows = 6;
	static int noCols = 7;
	static int FOUR = 4;
	static int playerID = 1;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// GameLogicKlaus game = new GameLogicKlaus();
		// game.initializeGame(7, 6, 1);

		PriorityQueue<Action> AIqueue = new PriorityQueue<>();
		AIqueue.add(new Action(4, 1));
		AIqueue.add(new Action(4, 2));
		AIqueue.add(new Action(3, 2));
		AIqueue.add(new Action(3, 1));
		AIqueue.add(new Action(3, 3));
		int size = AIqueue.size();
		System.out.println(AIqueue.contains(new Action(4, 2)));

//		PriorityQueue<Action> answer = new PriorityQueue();
//		for (Iterator<Action> it = AIqueue.iterator(); it.hasNext();) {
//			answer.add( (Action) it.next().clone());
//		}
//		for (int i = 0; i < size; i++) {
//			System.out.println(AIqueue.poll());
//		}
//		System.out.println(AIqueue.size());
//		for (int i = 0; i < size; i++) {
//			System.out.println(answer.poll());
//		}
		
		// int[][] board = new int[][]{
		// {0, 0, 0, 0, 0, 0},
		// {0, 0, 0, 0, 0, 1},
		// {0, 0, 0, 0, 42, 42},
		// {0, 0, 0, 0, 0, 0},
		// {0, 1, 42, 1, 42, 1},
		// {42, 1, 42, 1 ,42, 1},
		// {0, 0, 0, 0, 0, 1}};
		 int[][] board = new int[][] {
		 { 42, 1, 42, 1 },
		 { 42, 1, 1, 42 },
		 { 1, 1, 42, 1 },
		 { 42, 42, 1, 1 },
		 { 1, 42, 1, 1 } };
		 printBoard(board);
		
		 System.out.println(utility(board) + " "+(utility(board)==0));
		 System.out.println();
		 board = new int[][] {
		 { 42, 1, 42, 1 },
		 { 42, 1, 1, 1 },
		 { 42, 1, 42, 42 },
		 { 42, 42, 1, 42 },
		 { 1, 42, 1, 1 } };
		 printBoard(board);
		 System.out.println(utility(board) + " "+(utility(board)==-1));
//		System.out.println(Integer.MAX_VALUE);
//		System.out.println(Integer.MIN_VALUE);
	}

	public static int utility(int[][] s) {
		int[][] state = s;

		// find all the actions taken by this instance and the adversary on the
		// given state.
		PriorityQueue<Action> player1queue = new PriorityQueue<>();
		PriorityQueue<Action> player2queue = new PriorityQueue<>();
		for (int column = 0; column < state.length; column++) {
			for (int row = 0; row < state[column].length; row++) {
				int player = state[column][row]; // get the element at the
													// coordinate
				if (player == playerID) {
					player1queue.add(new Action(column, row));
				} else if (player == 42) {
					player2queue.add(new Action(column, row));
				}
			}
		}
		int oneSize = player1queue.size();
		int twoSize = player2queue.size();
		// find the maximum connected nodes each player has.
		int maxAIcoins = findMaxConnectedCoins(player1queue, false);
		int maxPlayer2 = findMaxConnectedCoins(player2queue, false);
System.out.println(maxAIcoins);
System.out.println(maxPlayer2);
		if (maxAIcoins == FOUR) {
			return 1;
		}
		if (maxPlayer2 == FOUR) {
			return -1;
		}
		if ((oneSize + twoSize) == (s.length * s[0].length)) {
			return 0;
		}
		return -2; // game not ended
	}
	/**
		 * finds the maximum number of connected nodes based on the given actions
		 * 
		 * @param actions
		 *            a sorted array (column ascending, row descending)
		 * @return 1 if 4 coins are connected, otherwise -2
		 */
		private static int findMaxConnectedCoins(PriorityQueue<Action> actions,
				boolean findKillerMove) {
			PriorityQueue<Action> consistenQueue = Action.cloneQueue(actions);
			// the higher the maxConnected is, the more connected coins there are
			int maxConnected = 0;
	
			int size = actions.size();
			for (int i = 0; i < size; i++) {
				Action action = actions.poll();
				int r = action.getRow();
				int c = action.getColumn();
	
				// VERTICAL
				int v_barrier = 1; //now we have at least one out of four connected
				Action[] vertical = new Action[] { new Action(c, r + 1),
						new Action(c, r + 2), new Action(c, r + 3) };
				for (int j = 0; j < vertical.length; j++) {
					Action actio = vertical[j];
					int player = (consistenQueue.contains(actio)) ? 1 : 0;
					if(player == 1){
						v_barrier++;
					}
				}
				if(v_barrier > maxConnected){
					maxConnected = v_barrier;
				}
				if(v_barrier == FOUR){
					return FOUR;
				}
				
				//HORIZONTAL
				Action[] horizontal = new Action[] { new Action(c + 1, r), new Action(c + 2, r), new Action(c + 3, r) };
				int h_barrier = 1;
				for (int k = 0; k < horizontal.length; k++) {
					Action actio = horizontal[k];
					int player = (consistenQueue.contains(actio)) ? 1 : 0;
					if(player == 1){
						h_barrier++;
					}
				}
				if(h_barrier > maxConnected){
					maxConnected = h_barrier;
				}
				if(h_barrier == FOUR){
					return FOUR;
				}
	
				// DIAGONAL running time = noPairs*4
				Action[] diagonal_up = new Action[] { new Action(c + 1, r - 1),
						new Action(c + 2, r - 2), new Action(c + 3, r - 3) };
				Action[] diagonal_dwn = new Action[] { new Action(c + 1, r + 1),
						new Action(c + 2, r + 2), new Action(c + 3, r + 3) };
				int upDiagonalBarrier, dwnDiagonalBarrier;
				upDiagonalBarrier = dwnDiagonalBarrier = 1; // 4 coins minus 1 (the
															// one we are
				// looking at)
	
				for (Action a : actions) {
					int iRow = a.getRow();
					int iColumn = a.getColumn();
					for (int j = 0; j < diagonal_up.length; j++) {
						if (iColumn == diagonal_up[j].getColumn()
								&& iRow == diagonal_up[j].getRow()) {
							upDiagonalBarrier++;
							// print("DIAGONAL UP: decrease barrier: "
							// + upDiagonalBarrier + "\t" + a);
							break;
						} else if (iColumn == diagonal_dwn[j].getColumn()
								&& iRow == diagonal_dwn[j].getRow()) {
							dwnDiagonalBarrier++;
							// print("DIAGONAL DWN: decrease barrier: "
							// + dwnDiagonalBarrier + "\t" + a);
							break;
						}
					}
					if (upDiagonalBarrier == FOUR || dwnDiagonalBarrier == FOUR) { // early
																					// return!
																					// we
																					// do
						// not need to
						// iterate more
						// print("FOUND DIAGONAL early MATCH");
						return FOUR;
					}
				}
				if (maxConnected < upDiagonalBarrier) {
					// System.out.println("DIAG UP: "+maxConnected);
					maxConnected = upDiagonalBarrier;
				}
				if (maxConnected < dwnDiagonalBarrier) {
					// System.out.println("DIAG DWN: "+maxConnected);
					maxConnected = dwnDiagonalBarrier;
				}
	
				// KILLER DIAGONAL MOVE
//				if (findKillerMove && upDiagonalBarrier == 3) {
//					if (validBounds(c + 3, r - 3)) {
//						if (board[c + 3][r - 3] == 0 && board[c + 3][r - 2] != 0) {
//							killer_move = new Action(c + 3, r - 3);
//							return 0; // nothing, because we know that it will win
//										// the game
//						}
//					}
//				}
	
			}// END LOOP THROUGH
			
			// HORIZONTAL
//			for (Action a : actions) {
//				int c = a.getColumn();
//				int r = a.getRow();
//				
				
	//					if (h_lastColumn[r] == -1) {
	//						// print("inital h_how at row=" + r);
	//						h_lastColumn[r] = c;
	//						// h_counter[r]++;
	//					}
	//
	//					int h_diff = Math.abs(h_lastColumn[r] - c);
	//					if (h_diff == 1 || h_diff == 0) {
	//						h_lastColumn[r] = c;
	//						h_counter[r] = h_counter[r] + 1;
	//						// System.out.println(action + " counter is: " + h_counter[r]);
	//					} else { // reset
	//						if (maxConnected < h_counter[r]) {
	//							maxConnected = h_counter[r];
	//							// System.out.println("HORIZONTAL: "+maxConnected);
	//						}
	//						// System.out.println("reset h counter at " + action);
	//						h_lastColumn[r] = -1;
	//						h_counter[r] = 0;
	//					}
	//					if (isFour(h_counter[r])) {
	//						// print("found 4 coins!! HORIZONTAL in row " + r);
	//						// System.out.println("HORIZONTAL WIN on row " + r);
	//						return FOUR;
	//					} else if (maxConnected < h_counter[r]) {
	//						maxConnected = h_counter[r];
	//						// System.out.println("HORIZONTAL: "+maxConnected);
	//					}
	//
	//					// KILLER HORIZONTAL
	//					if (findKillerMove && h_counter[r] == 3) {
	//						if (validBounds(c + 1, r)) {
	//							if (board[c + 1][r] == 0) {
	//								if (isACoinDiagonalDownOrOutOfBoard(c, r)) {
	//									killer_move = new Action(c + 1, r);
	//									return 0; // nothing, because we know that it will
	//												// win the game
	//								}
	//							}
	//						}
	//					}
//			}
	
			return maxConnected;
		}

	private static boolean isFour(int count) {
		return count == FOUR;
	}

	public static void printBoard(int[][] board) {
		// System.out.println("........turn " + turns + ".......");
		for (int col = 0; col < board[0].length; col++) {
			String str = "";
			for (int j = 0; j < board.length; j++) {
				str += "\t" + board[j][col];
			}
			System.out.println(str + "\n");
		}
	}
}
