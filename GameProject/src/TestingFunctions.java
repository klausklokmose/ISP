import java.util.List;
import java.util.PriorityQueue;



public class TestingFunctions {

	static int noRows = 6;
	static int noCols = 7;
	static int FOUR = 4;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		GameLogicKlaus game = new GameLogicKlaus();
//		game.initializeGame(7, 6, 1);
		
//		queueOne.add(new Pair(0, 2));
//		queueOne.add(new Pair(0, 1));
//		queueOne.add(new Pair(1, 1));
//		game.insertCoin(1, 2);
//		game.insertCoin(0, 2);
//		game.insertCoin(0, 2);
//		game.insertCoin(0, 1);
//		System.out.println(game.max_value(game.result(game.board, new Action(0, 2), 1), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 3));
//		System.out.println(game.max_value(game.result(game.board, new Action(1, 3), 1), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 3));
//		System.out.println(game.max_value(game.result(game.board, new Action(2, 3), 1), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 3));
//		System.out.println(game.max_value(game.result(game.board, new Action(3, 3), 1), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 3));
//		game.insertCoin(1, 1);
//		game.insertCoin(1, 2);
//		game.insertCoin(1, 1);
//		game.insertCoin(1, 2);
//		
//		game.insertCoin(2, 2);
//		game.insertCoin(2, 2);
//		game.insertCoin(2, 1);
//		game.insertCoin(2, 1);
//		
//		game.insertCoin(3, 1);
//		game.insertCoin(3, 2);
//		game.insertCoin(3, 1);
//		game.insertCoin(3, 2);
//		System.out.println();
//		List<Action> l = GameLogicKlaus.actions(game.board);
//		for (int i = 0; i < l.size(); i++) {
//			System.out.println(l.get(i));
//		}
//		System.out.println(game.utility(game.board));
//		System.out.println("NEW EVAL: "+game.NEWEVAL(game.board));
		
//		game.printBoard();
		
		int[][] board = new int[][]{
				{0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 1},
				{0, 0, 0, 0, 42, 42},
				{0, 0, 0, 0, 0, 0},
				{0, 1, 42, 1, 42, 1},
				{42, 1, 42, 1 ,42, 1},
				{0, 0, 0, 0, 0, 1}};
		System.out.println(utility(board));
	}
	
	public static int utility(int[][] s) {
		int[][] state = s;
		PriorityQueue<Action> AIqueue = new PriorityQueue<>();
		PriorityQueue<Action> player2 = new PriorityQueue<>();
		for (int column = 0; column < state.length; column++) {
			for (int row = 0; row < state[column].length; row++) {
				int player = state[column][row];
				if (player == 1) {
					AIqueue.add(new Action(column, row));
				} else if (player == 42) {
					player2.add(new Action(column, row));
				}
			}
		}
		Action[] AICoins = AIqueue.toArray(new Action[AIqueue.size()]);
		Action[] adversaryCoins = player2.toArray(new Action[player2.size()]);

		int maxAIcoins = findMatch(AICoins);
		System.out.println("AI="+maxAIcoins);
		int maxPlayer2 = findMatch(adversaryCoins);
		System.out.println("adv="+maxPlayer2);

		if (maxAIcoins == 4) {
			return 1;
		}
		if (maxPlayer2 == 4) {
			return -1;
		}
		if ((AICoins.length + adversaryCoins.length) == (noCols * noRows)) {
			return 0;
		}
		return -2; // game not ended
	}

	/**
	 * @param actions
	 * @param playerID
	 * @return 1 if 4 coins are connected, otherwise -2
	 */
	private static int findMatch(Action[] actions) {
		// the higher the barrier is, the more connected coins there are
		int maxConnected = 0;
		int v_column = 0;
		int v_counter = 0;
		int v_lastRow = -1;
	
		int[] h_barrier = new int[noRows];
		int[] h_last = new int[noRows];
		for (int i = 0; i < h_last.length; i++) {
			h_last[i] = -1; // init
//			h_barrier[i] = 0;
		}
	
		for (Action action : actions) {
			// Action p = a;
			int r = action.getRow();
			int c = action.getColumn();
//			print("LOOKING AT ACTION" + action);
	
			// VERTICAL
			if (c != v_column) { // reset vertical
//				print("reset vertical");
				v_column = c;
				v_counter = 0;
				v_lastRow = -1;
			}
	
			if (v_lastRow == -1) {
//				print("set row");
				v_lastRow = r;
			}
	
			int v_diff = v_lastRow - r;
			if (v_diff == 1 || v_diff == 0) {
//				print("VERTICAL: decrease barrier");
				v_lastRow = r;
				v_counter++;
			} else {
				v_lastRow = -1;
				v_counter = 0;
			}
	
			if (v_counter == FOUR) {
//				print("found 4 coins!! VERTICAL in column " + v_column);
				System.out.println("VERTICAL: "+FOUR);
				return v_counter;
			} else if (maxConnected < v_counter) {
				maxConnected = v_counter;
				System.out.println("VERTICAL: "+maxConnected);
			}
	
			// HORIZONTAL
			if (h_barrier[r] == FOUR) {
//				print("found 4 coins!! HORIZONTAL in row " + r);
				System.out.println("HORIZONTAL: "+FOUR);
				return FOUR;
			} else if (maxConnected < h_barrier[r]) {
				maxConnected = h_barrier[r];
				System.out.println("HORIZONTAL: "+maxConnected);
			}
			
			if (h_last[r] == -1) {
//				print("inital h_how at row=" + r);
				h_last[r] = r;
//				h_barrier[r] = 1;
			}
	
			int h_diff = h_last[r] - r;
			if ( h_diff == 1 || h_diff == 0) {
				h_last[r] = r;
				h_barrier[r]++;
			} else { // reset
				if (maxConnected < h_barrier[r]) {
					maxConnected = h_barrier[r];
					System.out.println("HORIZONTAL: "+maxConnected);
				}
				h_last[r] = -1;
				h_barrier[r] = 0;
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
//						print("DIAGONAL UP: decrease barrier: "
//								+ upDiagonalBarrier + "\t" + a);
						break;
					} else if (iColumn == diagonal_dwn[j].getColumn()
							&& iRow == diagonal_dwn[j].getRow()) {
						dwnDiagonalBarrier++;
//						print("DIAGONAL DWN: decrease barrier: "
//								+ dwnDiagonalBarrier + "\t" + a);
						break;
					}
				}
				if (upDiagonalBarrier == FOUR || dwnDiagonalBarrier == FOUR) { // early
																				// return!
																				// we
																				// do
					// not need to
					// iterate more
//					print("FOUND DIAGONAL early MATCH");
					return FOUR;
				}
			}
			if (maxConnected < upDiagonalBarrier) {
				System.out.println("DIAG UP: "+maxConnected);
				maxConnected = upDiagonalBarrier;
			}
			if (maxConnected < dwnDiagonalBarrier) {
				System.out.println("DIAG DWN: "+maxConnected);
				maxConnected = dwnDiagonalBarrier;
			}
	
		}// END LOOP THROUGH
	
		return maxConnected;
	}
	
}
