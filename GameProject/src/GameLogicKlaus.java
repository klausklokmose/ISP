import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class GameLogicKlaus implements IGameLogic {
	private int noCols;
	private int noRows;
	private int playerID;
	public int[][] board;
	private int turns;
	private final int FOUR = 4;
	private final int ADVERSARY = 1000;

	private PriorityQueue<Action> queueMAX;
	private PriorityQueue<Action> queueMIN;

	public GameLogicKlaus() {
		// TODO Write your implementation for this method
	}

	public void initializeGame(int noCols, int noRows, int playerID) {
		this.noCols = noCols;
		this.noRows = noRows;
		System.out.println(noCols + ", " + noRows);
		this.playerID = playerID;
		// TODO Write your implementation for this method
		board = new int[noCols][noRows];
		queueMAX = new PriorityQueue<>();
		queueMIN = new PriorityQueue<>();
	}

	public Winner gameFinished() {
		// TODO Write your implementation for this method
		if (turns >= 7) {
			return Winner.NOT_FINISHED;
		} else {
			return Winner.NOT_FINISHED;
		}
	}

	public void insertCoin(int column, int playerID) {
		if(playerID != this.playerID){
			playerID = ADVERSARY;
		}
		// TODO Write your implementation for this method
		int[] col = board[column];
		// [x, y] column, row
		Action p = null;
		// the column is non-empty
		if (col[0] == 0) {
			boolean inserted = false;
			for (int row = 0; row < col.length; row++) {
				if (col[row] != 0) {
					col[row - 1] = playerID;
					inserted = true;
					p = new Action(column, row - 1);
					break;
				}
			}
			// the column must be empty
			if (!inserted) {
				col[col.length - 1] = playerID;
				p = new Action(column, col.length - 1);
				inserted = true;
			}

			(playerID == this.playerID ? queueMAX : queueMIN).add(p);
			printQueues();
		} else {
			System.out
					.println("you can't insert a coin here, because it is full!");
		}
	}

	private Action alpha_beta_search(int[][] state) {
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		Action a = max_value(state, alpha, beta, 0);
		System.out.println("line 77 " + a);
		return a;
	}

	private Action max_value(int[][] state, int alpha, int beta, int depth) {
		int[][] s = state.clone();
		// if we have reached the limit
		if (cutoff_test(state, depth))
			return EVAL(state);

		List<Action> actions = actions(s); // possible actions from current
											// state
		if (!actions.isEmpty()) {
			Action maximum = actions.get(0);
			for (Action a : actions) {
				Action test = min_value(result(s, a, ADVERSARY), alpha, beta, depth);
				if (maximum.getValue() < test.getValue()) {
					maximum = test;
				}

				if (maximum.getValue() >= beta)
					return maximum;
				alpha = Math.max(alpha, maximum.getValue());
			}
			return maximum;
		}
		return EVAL(state);
	}

	private Action min_value(int[][] state, int alpha, int beta, int depth) {
		int[][] s = state.clone();
		// if we have reached the limit
		if (cutoff_test(state, depth))
			return EVAL(state);

		List<Action> actions = actions(s);
		if (!actions.isEmpty()) {

			Action minimum = actions.get(0);
			for (Action a : actions) {
				Action test = max_value(result(s, a, this.playerID), alpha, beta, ++depth);
				if (minimum.getValue() > test.getValue()) {
					minimum = test;
				}

				if (minimum.getValue() <= alpha)
					return minimum;
				beta = Math.min(beta, minimum.getValue());
			}
			return minimum;
		}
		return EVAL(state);
	}

	private boolean cutoff_test(int[][] state, int depth) {
		if (depth == 5)
			return true;
		return false;
	}

	private Action EVAL(int[][] state) {
		// find all actions that MAX has taken
		List<Action> pairs = new ArrayList<>();
		for (int col = 0; col < state.length; col++) {
			for (int row = 0; row < state[col].length; row++) {
				if (state[col][row] == this.playerID) {
					pairs.add(new Action(col, row));
				}
			}
		}
		// sort the coordinates first by column asc. and then by row desc.
		pairs.sort(new Comparator<Action>() {
			@Override
			public int compare(Action thizz, Action that) {
				return thizz.compareTo(that);
			}
		});

		int maxConnectedNodes = findMatch(pairs
				.toArray(new Action[pairs.size()]));
		System.out.println("MAX CONNECTED = " + maxConnectedNodes);
		return new Action(0, 0, maxConnectedNodes);// winner
	}

	private int[][] result(int[][] state, Action pair, int playerID) {
		state[pair.getColumn()][pair.getRow()] = playerID;
		return state;
	}

	private int utility(int[][] state) {
		int utility = -1; // not found

		PriorityQueue<Action> player1 = new PriorityQueue<>();
		PriorityQueue<Action> player2 = new PriorityQueue<>();
		for (int column = 0; column < state.length; column++) {
			for (int row = 0; row < state[column].length; row++) {
				int player = state[column][row];
				if (player == playerID) {
					player1.add(new Action(column, row));
				} else {
					player2.add(new Action(column, row));
				}
			}
		}
		Action[] pairs = (Action[]) player1.toArray();
		/*
		 * for each column i in player1 check if they have 4 connected coins
		 * 
		 * iterate through each column and for each entry try to find a diagonal
		 * path
		 */
		// int match = findMatch(pairs);

		return 0;
	}

	/**
	 * @param actions
	 * @param playerID
	 * @return 1 if 4 coins are connected, otherwise -2
	 */
	private int findMatch(Action[] actions) {
		// the higher the barrier is, the more connected coins there are
		int maxConnected = 0;
		int v_column = 0;
		int v_counter = 0;
		int v_lastRow = -1;

		int[] h_barrier = new int[noRows];
		int[] h_last = new int[noRows];
		for (int i = 0; i < h_last.length; i++) {
			h_last[i] = -1; // init
			h_barrier[i] = 0;
		}

		for (Action action : actions) {
			// Action p = a;
			int r = action.getRow();
			int c = action.getColumn();
			System.out.println("LOOKING AT ACTION" + action);

			// VERTICAL
			if (c != v_column) { // reset vertical
				System.out.println("reset vertical");
				v_column = c;
				v_counter = 0;
				v_lastRow = -1;
			}

			if (v_lastRow == -1) {
				System.out.println("set row");
				v_lastRow = r;
			}

			int v_diff = v_lastRow - r;
			if (v_diff == 1 || v_diff == 0) {
				System.out.println("VERTICAL: decrease barrier");
				v_lastRow = r;
				v_counter++;
			} else {
				v_lastRow = -1;
			}

			if (v_counter == FOUR) {
				System.out.println("found 4 coins!! VERTICAL in column "
						+ v_column);
				return v_counter;
			} else if (maxConnected < v_counter) {
				maxConnected = v_counter;
			}

			// HORIZONTAL
			if (h_last[r] == -1) {
				System.out.println("inital h_how at row=" + r);
				h_last[r] = r;
			}

			int h_diff = h_last[r] - r;
			if (h_diff == 0) {
				h_last[r] = r;
				h_barrier[r]++;
			} else { // reset
				h_last[r] = -1;
				h_barrier[r] = 0;
			}

			if (h_barrier[r] == FOUR) {
				System.out.println("found 4 coins!! HORIZONTAL in row " + r);
				return FOUR;
			} else if (maxConnected < h_barrier[r]) {
				maxConnected = h_barrier[r];
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
						System.out.println("DIAGONAL UP: decrease barrier: "
								+ upDiagonalBarrier + "\t" + a);
						break;
					} else if (iColumn == diagonal_dwn[j].getColumn()
							&& iRow == diagonal_dwn[j].getRow()) {
						dwnDiagonalBarrier++;
						System.out.println("DIAGONAL DWN: decrease barrier: "
								+ dwnDiagonalBarrier + "\t" + a);
						break;
					}
				}
				if (upDiagonalBarrier == FOUR || dwnDiagonalBarrier == FOUR) { // early
																				// return!
																				// we
																				// do
					// not need to
					// iterate more
					System.out.println("FOUND DIAGONAL early MATCH");
					return FOUR;
				}
			}
			if (maxConnected < upDiagonalBarrier) {
				maxConnected = upDiagonalBarrier;
			}
			if (maxConnected < dwnDiagonalBarrier) {
				maxConnected = dwnDiagonalBarrier;
			}
			System.out.println();

		}// END LOOP THROUGH

		return maxConnected;
	}

	public static List<Action> actions(int[][] state) {
		int[][] s = state.clone();
		List<Action> actions = new ArrayList<Action>();

		for (int column = 0; column < s.length; column++) {
			if (isColumnNotFull(s, column)) {
				Action p = null;
				for (int row = 0; row < s[column].length; row++) {
					if (hasCoin(s, column, row)) {
						// add the prior row action
						p = new Action(column, row - 1);
						break;
					}
				}
				// if column is empty
				if (p == null) {
					// add this action
					int lastItemInRow = s[column].length - 1;
					p = new Action(column, lastItemInRow);
				}
				actions.add(p);
			}
		}
		return actions;
	}

	private static boolean hasCoin(int[][] s, int column, int row) {
		return s[column][row] != 0;
	}

	private static boolean isColumnNotFull(int[][] s, int column) {
		return s[column][0] == 0;
	}

	public int decideNextMove() {
		// TODO Write your implementation for this method
		// TODO call search
		printBoard();
		Action a = alpha_beta_search(board);
		System.out.println(a);
		int i = a.getColumn();
		System.out.println("try column: " + i);
		return i;
	}

	private void printBoard() {
		System.out.println("........turn " + turns + ".......");
		for (int col = 0; col < board[0].length; col++) {
			String str = "";
			for (int j = 0; j < board.length; j++) {
				str += " " + board[j][col];
			}
			System.out.println(str + "\n");
		}
	}

	public void printQueues() {
		System.out.println("\nPlayer 1:");
		for (Action pa : queueMAX) {
			System.out.println(pa);
		}
		System.out.println(".......................\nPlayer 2:");
		for (Action pa : queueMIN) {
			System.out.println(pa);
		}
	}
}
