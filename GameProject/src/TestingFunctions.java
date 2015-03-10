import java.util.Iterator;
import java.util.PriorityQueue;

public class TestingFunctions {

	static int noRows = 6;
	static int noCols = 7;
	static int FOUR = 4;

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

		PriorityQueue<Action> answer = new PriorityQueue();
		for (Iterator<Action> it = AIqueue.iterator(); it.hasNext();) {
			answer.add( (Action) it.next().clone());
		}
		for (int i = 0; i < size; i++) {
			System.out.println(AIqueue.poll());
		}
		System.out.println(AIqueue.size());
		for (int i = 0; i < size; i++) {
			System.out.println(answer.poll());
		}
		
		// int[][] board = new int[][]{
		// {0, 0, 0, 0, 0, 0},
		// {0, 0, 0, 0, 0, 1},
		// {0, 0, 0, 0, 42, 42},
		// {0, 0, 0, 0, 0, 0},
		// {0, 1, 42, 1, 42, 1},
		// {42, 1, 42, 1 ,42, 1},
		// {0, 0, 0, 0, 0, 1}};
		// int[][] board = new int[][] {
		// { 42, 1, 42, 1 },
		// { 42, 1, 1, 42 },
		// { 1, 1, 42, 1 },
		// { 42, 42, 1, 1 },
		// { 1, 42, 1, 1 } };
		// printBoard(board);
		//
		// System.out.println(utility(board) + " "+(utility(board)==0));
		// System.out.println();
		// board = new int[][] {
		// { 42, 1, 42, 1 },
		// { 42, 1, 1, 1 },
		// { 42, 1, 42, 42 },
		// { 42, 42, 1, 42 },
		// { 1, 42, 1, 1 } };
		// printBoard(board);
		// System.out.println(utility(board) + " "+(utility(board)==-1));
	}

	/**
	 * Returns the utility of the state.
	 * 
	 * @param s
	 * @return -2 if game has not ended, 1 if this instance won, -1 if the
	 *         adversary won, 0 if the game has finished with a tie.
	 */
	public static int utility(int[][] s) {
		int[][] state = s;

		// find all the actions taken by this instance and the adversary on the
		// given state.
		PriorityQueue<Action> AIqueue = new PriorityQueue<>();
		PriorityQueue<Action> player2 = new PriorityQueue<>();
		for (int column = 0; column < state.length; column++) {
			for (int row = 0; row < state[column].length; row++) {
				int player = state[column][row]; // get the element at the
													// coordinate
													// System.out.println("["+column+" "+row+"] "+player);

				if (player == 1) { // TODO this.playerID
					AIqueue.add(new Action(column, row));
				} else if (player == 42) {
					player2.add(new Action(column, row));
				}
			}
		}
		// System.out.println("player 2\n" + player2);
		int oneSize = AIqueue.size();
		int twoSize = player2.size();
		// Convert the queues into arrays
		// Action[] AICoins = AIqueue.toArray(new Action[AIqueue.size()]);
		// Action[] adversaryCoins = player2.toArray(new
		// Action[player2.size()]);

		// find the maximum connected nodes each player has.
		System.out.println("max");
		int maxAIcoins = findMaxConnectedCoins(AIqueue);
		System.out.println("min");
		int maxPlayer2 = findMaxConnectedCoins(player2);
		System.out.println("max max: " + maxAIcoins);
		System.out.println("max min: " + maxPlayer2);
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
	private static int findMaxConnectedCoins(PriorityQueue<Action> actions) {
		// the higher the maxConnected is, the more connected coins there are
		int maxConnected = 0;

		// variables for vertical counting is done with a 'v_' prefix to the
		// variables
		int v_column = -1; // keeps track of which column we are looking at now.
							// Changes when a new column is discovered
		int v_counter = 0;
		int v_lastRow = -1; // the last row we have looked at. -1 initially
							// because we have no last row.

		// variables for horizontal counting is done with a 'h_' prefix to the
		// variables
		int[] h_counter = new int[noRows];
		int[] h_lastColumn = new int[noRows];
		// initialize h_last to -1 because we have not seen any last rows yet
		for (int i = 0; i < noRows; i++) {
			h_lastColumn[i] = -1; // init
			h_counter[i] = 0;
		}
		int size = actions.size();
		for (int i = 0; i < size; i++) {

			Action action = actions.poll();
			System.out.println(action);
			// Action p = a;
			int r = action.getRow();
			int c = action.getColumn();
			// print("LOOKING AT ACTION" + action);

			// VERTICAL
			if (c != v_column) { // reset vertical
				// print("reset vertical");
				v_column = c;
				v_counter = 0;
				v_lastRow = -1;
			}

			if (v_lastRow == -1) {
				// print("set row");
				v_lastRow = r;
			}

			int v_diff = v_lastRow - r;
			if (v_diff == 1 || v_diff == 0) {
				// print("VERTICAL: decrease barrier");
				v_lastRow = r;
				v_counter++;
			} else {
				v_lastRow = -1;
				v_counter = 0;
			}

			if (v_counter == FOUR) {
				// print("found 4 coins!! VERTICAL in column " + v_column);
				// System.out.println("VERTICAL: "+FOUR);
				return v_counter;
			} else if (maxConnected < v_counter) {
				maxConnected = v_counter;
				// System.out.println("VERTICAL: "+maxConnected);
			}

			// HORIZONTAL
			if (h_lastColumn[r] == -1) {
				// print("inital h_how at row=" + r);
				h_lastColumn[r] = c;
				// h_counter[r]++;
			}

			int h_diff = Math.abs(h_lastColumn[r] - c);
			if (h_diff == 1 || h_diff == 0) {
				h_lastColumn[r] = c;
				h_counter[r] = h_counter[r] + 1;
				// System.out.println(action + " counter is: " + h_counter[r]);
			} else { // reset
				if (maxConnected < h_counter[r]) {
					maxConnected = h_counter[r];
					// System.out.println("HORIZONTAL: "+maxConnected);
				}
				// System.out.println("reset h counter at " + action);
				h_lastColumn[r] = -1;
				h_counter[r] = 0;
			}
			if (isFour(h_counter[r])) {
				// print("found 4 coins!! HORIZONTAL in row " + r);
				System.out.println("HORIZONTAL WIN on row " + r);
				return FOUR;
			} else if (maxConnected < h_counter[r]) {
				maxConnected = h_counter[r];
				// System.out.println("HORIZONTAL: "+maxConnected);
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

		}// END LOOP THROUGH

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
