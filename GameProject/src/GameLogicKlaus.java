import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class GameLogicKlaus implements IGameLogic {
	// holds the state of the currently already played board.
	public int[][] board;
	private int noCols;
	private int noRows;
	// each time a turn has been taken, the turns is incremented
	private int turns;

	// holds the number of connected that the game needs.
	private final int FOUR = 4;

	// player ids. The adversary is not 1 or 2, because we don't know if the ID
	// this instance will get is 1 or 2.
	private int playerID;
	private final int ADVERSARY = 42;
	// public int playerScore;
	// public int adversaryScore;

	// queues to hold already played actions, such that finding connected coins
	// is easier.
	private PriorityQueue<Action> queueMAX;
	private PriorityQueue<Action> queueMIN;

	// true if prints should be done, false otherwise
	private boolean trace = false;

	// overridden each time a turn has to be taken by this instance.
	private long startTime;

	public GameLogicKlaus() {
		// TODO Write your implementation for this method
	}

	/**
	 * print a string to stout if trace is true
	 * 
	 * @param str
	 */
	public void print(String str) {
		if (trace)
			System.out.println(str);
	}

	/**
	 * initializes the game with a fresh board and sets the playerID of this
	 * instance.
	 * 
	 */
	public void initializeGame(int noCols, int noRows, int playerID) {
		this.noCols = noCols;
		this.noRows = noRows;
		print(noCols + ", " + noRows);
		this.playerID = playerID;
		// TODO Write your implementation for this method
		board = new int[noCols][noRows];
		queueMAX = new PriorityQueue<>();
		queueMIN = new PriorityQueue<>();
		// playerScore = adversaryScore = winningPositions(noCols)
		// * winningPositions(noRows) * 2 // DIAGONAL (UP AND DOWN)
		// + noRows * winningPositions(noCols) // HORIZONTAL
		// + noCols * winningPositions(noRows); // VERTICAL
	}

	/**
	 * returns the number of ways FOUR coins can be placed in an n size row or
	 * column
	 * 
	 * @param n
	 * @return
	 */
	private int winningPositions(int n) {
		return (n % FOUR) + 1;
	}

	/**
	 * returns if the game has finished either by a draw, or MAX or MIN have
	 * won, or it was a tie. Else it returns Winner.NOT_FINISHED (so the game
	 * will continue).
	 */
	public Winner gameFinished() {
		int util = utility(board);
		if (util == 1) {
			return Winner.PLAYER1;
		} else if (util == -1) {
			return Winner.PLAYER2;
		} else if (util == 0) {
			return Winner.TIE;
		}
		return Winner.NOT_FINISHED;
	}

	/**
	 * when a coin is placed, either by this instance or the adversary, the
	 * coordinate is saved in an action which is put into the queue of the
	 * corresponding player.
	 */
	public void insertCoin(int column, int playerID) {
		if (playerID != this.playerID) {
			// if the player is not this instance, it must be the adversary
			playerID = ADVERSARY;
		}

		int[] col = board[column];
		Action p = null;

		if (col[0] == 0) {// the column is not full
			boolean inserted = false;
			for (int row = 0; row < col.length; row++) {
				// if the element is not vacant, the prior one was vacant
				if (col[row] != 0) {
					int newRow = row - 1;
					col[newRow] = playerID; // set the id to the board
					inserted = true;
					p = new Action(column, newRow); // create the action
					break; // no need to search more
				}
			}
			// if the coin was not inserted, the column must have been empty
			if (!inserted) {
				// set the coin to the bottom element
				col[col.length - 1] = playerID;
				p = new Action(column, col.length - 1);
				inserted = true;
			}
			// add the action to the queue
			(playerID == this.playerID ? queueMAX : queueMIN).add(p);
			if (trace)
				printQueues();

			printBoard();
			turns++;
		} else {
			print("you can't insert a coin here, because it is full!");
		}
	}

	/**
	 * find out which move to take based on the current board return the column
	 * to insert a coin in to
	 */
	public int decideNextMove() {
		// TODO Write your implementation for this method
		// TODO call search
		printBoard();
		Action a = alpha_beta_search(board);
		print("" + a);
		int i = a.getColumn();
		print("try column: " + i);
		return i;
	}

	/**
	 * search for the "best" next move to take based on the current state of the
	 * game.
	 * 
	 * @param stat
	 * @return
	 */
	private Action alpha_beta_search(int[][] stat) {
		int[][] state = deepCopyIntMatrix(stat); // TODO maybe we don't need to
													// deep copy the state here.

		startTime = System.currentTimeMillis(); // start a timer for this turn
		// initial variables for alpha-beta pruning
		double alpha = Double.NEGATIVE_INFINITY;
		double beta = Double.POSITIVE_INFINITY;

		int depth = 10; // TODO not used anymore

		double max = Double.NEGATIVE_INFINITY;
		List<Action> actions = actions(state); // get a list of possible actions
												// to take.
		Action action = null;
		// for each action, find out which of those actions will lead to the
		// highest possible value.
		for (Action a : actions) {
			double value = min_value(result(state, a, this.playerID), alpha,
					beta, depth);
			// if the value is higher than the current maximum, it must be a
			// better action.
			if (value > max) {
				max = value;
				action = a;
			}
		}

		if (trace)
			printBoard();
		return action;
	}

	/**
	 * max_value will return the maximum value of all actions taken by the
	 * adversary. Pruning is also added here, to make a cutoff, because a limit
	 * is found.
	 * 
	 * @param s
	 * @param alpha
	 * @param beta
	 * @param depth
	 * @return
	 */
	public double max_value(int[][] s, double alpha, double beta, int depth) {
		int[][] state = deepCopyIntMatrix(s);
		depth = depth - 1;
		// if we have reached the limit
		if (cutoff_test(state, depth))
			return NEWEVAL(state);

		int utility = utility(state);
		if (utility >= 0) {
			return utility;
		}
		List<Action> actions = actions(state); // possible actions from current
												// state
		double maximum = Double.NEGATIVE_INFINITY;
		for (Action a : actions) {
			double minValue = min_value(result(state, a, ADVERSARY), alpha,
					beta, depth);
			maximum = Math.max(maximum, minValue);
			if (maximum >= beta)
				return maximum;
			alpha = Math.max(alpha, maximum);
		}
		return maximum;
	}

	/**
	 * min_value will return the minimum value of all actions taken by the
	 * max-player (this instance). Pruning is also added here, to make a cutoff,
	 * because a limit is found.
	 * 
	 * @param s
	 * @param alpha
	 * @param beta
	 * @param depth
	 * @return
	 */
	public double min_value(int[][] s, double alpha, double beta, int depth) {
		int[][] state = deepCopyIntMatrix(s);
		depth = depth - 1;
		// if we have reached the limit
		if (cutoff_test(state, depth))
			return NEWEVAL(state);
		int utility = utility(state);
		if (utility >= 0) {
			return utility;
		}

		List<Action> actions = actions(state);
		double minimum = Double.POSITIVE_INFINITY;
		for (Action a : actions) {
			double maxValue = max_value(result(state, a, this.playerID), alpha,
					beta, depth);
			minimum = Math.min(minimum, maxValue);

			if (minimum <= alpha)
				return minimum;
			beta = Math.min(beta, minimum);
		}
		return minimum;
	}

	/**
	 * check if time has run out, and it needs to stop making the game tree.
	 * 
	 * @param state
	 * @param depth
	 * @return
	 */
	private boolean cutoff_test(int[][] state, int depth) {
		long end = System.currentTimeMillis();
		if ((end - startTime) >= 10_000) {
			return true;
		}
		// if (depth <= 0) {
		// return true;
		// }
		return false;
	}

	/**
	 * The evaluation function takes a state as input and returns a heuristic
	 * value of the current game node state.
	 * 
	 * @param s
	 * @return
	 */
	public double NEWEVAL(int[][] s) {
		int[][] state = deepCopyIntMatrix(s);
		int totalPossibilities = 0;
		int noHorizontalPosibilities = winningPositions(noCols); // number of
																	// possibilities
																	// to place
																	// 4 coins
																	// horizontally
																	// (in one
																	// column)
		int noVerticalPosibilities = winningPositions(noRows); // number of
																// possibilities
																// to place 4
																// coins
																// vertically
																// (in one row)

		for (int col = 0; col < state.length; col++) {
			// VERTICAL
			// Find out how many vertical possibilities there are in this column
			for (int j = 0; j < noVerticalPosibilities; j++) {
				int barrier = 0;
				for (int k = j; k < FOUR + j; k++) {
					if (state[col][k] != ADVERSARY) {
						barrier++;
					}
				}
				if (barrier == FOUR) {
					totalPossibilities++;
				}
			}

			// DIAGONAL
			// Find out how many diagonal possibilities there are (checking both
			// diagonal up and diagonal down).
			for (int r = 0; r < state[col].length; r++) {

				Action[] diagonal_up = new Action[] { new Action(col, r),
						new Action(col + 1, r - 1), new Action(col + 2, r - 2),
						new Action(col + 3, r - 3) };
				Action[] diagonal_dwn = new Action[] { new Action(col, r),
						new Action(col + 1, r + 1), new Action(col + 2, r + 2),
						new Action(col + 3, r + 3) };

				// Barriers that need to reach 4 in order to verify that the
				// possibility exists.
				int up_barrier = 0;
				int down_barrier = 0;

				// Go through the 2 arrays from above and add to the barrier
				// when there is a match.
				for (int i = 0; i < diagonal_dwn.length; i++) {

					// coordinates from diagonal_dwn action.
					int colDown = diagonal_dwn[i].getColumn();
					int rowDown = diagonal_dwn[i].getRow();

					// if the coordinates are valid in terms of the size of the
					// game board. AND the player in the coordinate is not the
					// adversary.
					if (validBounds(colDown, rowDown)
							&& state[colDown][rowDown] != ADVERSARY) {
						down_barrier++;
					}

					// coordinates from diagonal_up action.
					int colUp = diagonal_up[i].getColumn();
					int rowUp = diagonal_up[i].getRow();
					// if the coordinates are valid in terms of the size of the
					// game board. AND the player in the coordinate is not the
					// adversary.
					if (validBounds(colUp, rowUp)
							&& state[colUp][rowUp] != ADVERSARY) {
						up_barrier++;
					}
				}
				// If the barrier reached FOUR, then add to the total number of
				// possibilities
				if (down_barrier == FOUR) {
					totalPossibilities++;
				}
				// If the barrier reached FOUR, then add to the total number of
				// possibilities
				if (up_barrier == FOUR) {
					totalPossibilities++;
				}
			}

		} // END COL LOOP

		// HORIZONTAL
		// Find out how many horizontal possibilities there are for each row.
		for (int r = 0; r < noRows; r++) {
			for (int i = 0; i < noHorizontalPosibilities; i++) {
				int barrier = 0;
				for (int j = i; j < FOUR + i; j++) {
					if (state[j][r] != ADVERSARY) {
						barrier++;
					}
				}
				if (barrier == FOUR) {
					totalPossibilities++;
				}
			}
		}
		return totalPossibilities;
	}

	/**
	 * check if the coordinates are inside the bounds of the board. (noCols x
	 * noRows)
	 * 
	 * @param colUp
	 * @param rowUp
	 * @return true or false
	 */
	private boolean validBounds(int colUp, int rowUp) {
		return !(colUp < 0 || colUp > noCols - 1 || rowUp < 0 || rowUp > noRows - 1);
	}

	// public double EVAL(int[][] s) {
	// int[][] state = deepCopyIntMatrix(s);
	// // find all actions that MAX has taken
	// PriorityQueue<Action> pairs = new PriorityQueue<>();
	// for (int col = 0; col < state.length; col++) {
	// for (int row = 0; row < state[col].length; row++) {
	// if (state[col][row] == this.playerID) {
	// pairs.add(new Action(col, row));
	// }
	// }
	// }
	//
	// int maxConnectedNodes = findMatch(pairs
	// .toArray(new Action[pairs.size()]));
	// return maxConnectedNodes / 4;
	// }

	/**
	 * result function returns a new state of applying an action to the state.
	 * The playerID is used for putting the value on the board.
	 * 
	 * @param state
	 * @param pair
	 * @param playerID
	 * @return new state
	 */
	public int[][] result(int[][] state, Action pair, int playerID) {
		// int playerID = (turns % 2+1)
		int[][] s = deepCopyIntMatrix(state);
		s[pair.getColumn()][pair.getRow()] = playerID;
		return s;
	}

	/**
	 * Returns the utility of the state.
	 * 
	 * @param s
	 * @return -2 if game has not ended, 1 if this instance won, -1 if the
	 *         adversary won, 0 if the game has finished with a tie.
	 */
	public int utility(int[][] s) {
		int[][] state = s;

		// find all the actions taken by this instance and the adversary on the
		// given state.
		PriorityQueue<Action> AIqueue = new PriorityQueue<>();
		PriorityQueue<Action> player2 = new PriorityQueue<>();
		for (int column = 0; column < state.length; column++) {
			for (int row = 0; row < state[column].length; row++) {
				int player = state[column][row]; // get the element at the
													// coordinate
				if (player == this.playerID) {
					AIqueue.add(new Action(column, row));
				} else if (player != 0) {
					player2.add(new Action(column, row));
				}
			}
		}

		// Convert the queues into arrays
		Action[] AICoins = AIqueue.toArray(new Action[AIqueue.size()]);
		Action[] adversaryCoins = player2.toArray(new Action[player2.size()]);

		// find the maximum connected nodes each player has.
		int maxAIcoins = findMaxConnectedCoins(AICoins);
		int maxPlayer2 = findMaxConnectedCoins(adversaryCoins);

		if (maxAIcoins == FOUR) {
			return 1;
		}
		if (maxPlayer2 == FOUR) {
			return -1;
		}
		if ((AICoins.length + adversaryCoins.length) == (noCols * noRows)) {
			return 0;
		}
		return -2; // game not ended
	}

	/**
	 * get the possible actions
	 * 
	 * @param state
	 * @return as list of possible actions on the given state.
	 */
	public static List<Action> actions(int[][] state) {
		int[][] s = deepCopyIntMatrix(state);
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
			} else {

			}
		}
		return actions;
	}

	/**
	 * 
	 * 
	 * @param s
	 * @param column
	 * @param row
	 * @return if the given coordinate has a coin.
	 */
	private static boolean hasCoin(int[][] s, int column, int row) {
		return s[column][row] != 0;
	}

	/**
	 * check if the given column is full
	 * 
	 * @param s
	 * @param column
	 * @return
	 */
	private static boolean isColumnNotFull(int[][] s, int column) {
		return s[column][0] == 0;
	}

	/**
	 * prints the current board and the turn number
	 */
	public void printBoard() {
		System.out.println("........turn " + turns + ".......");
		for (int col = 0; col < board[0].length; col++) {
			String str = "";
			for (int j = 0; j < board.length; j++) {
				str += "\t" + board[j][col];
			}
			System.out.println(str + "\n");
		}
	}

	/**
	 * prints the taken actions of each player
	 */
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

	/**
	 * finds the maximum number of connected nodes based on the given actions
	 * 
	 * @param actions a sorted array (column ascending, row descending)
	 * @return 1 if 4 coins are connected, otherwise -2
	 */
	private int findMaxConnectedCoins(Action[] actions) {
		// the higher the maxConnected is, the more connected coins there are
		int maxConnected = 0;
		
		//variables for vertical counting is done with a 'v_' prefix to the variables
		int v_column = 0; //keeps track of which column we are looking at now. Changes when a new column is discovered
		int v_counter = 0;
		int v_lastRow = -1; //the last row we have looked at. -1 initially because we have no last row.

		//variables for horizontal counting is done with a 'h_' prefix to the variables
		int[] h_counter = new int[noRows];
		int[] h_last = new int[noRows];
		//initialize h_last to -1 because we have not seen any last rows yet
		for (int i = 0; i < h_last.length; i++) {
			h_last[i] = -1; // init
		}

		for (Action action : actions) {
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
			if (h_counter[r] == FOUR) {
				// print("found 4 coins!! HORIZONTAL in row " + r);
				// System.out.println("HORIZONTAL: "+FOUR);
				return FOUR;
			} else if (maxConnected < h_counter[r]) {
				maxConnected = h_counter[r];
				// System.out.println("HORIZONTAL: "+maxConnected);
			}

			if (h_last[r] == -1) {
				// print("inital h_how at row=" + r);
				h_last[r] = r;
			}

			int h_diff = h_last[r] - r;
			if (h_diff == 1 || h_diff == 0) {
				h_last[r] = r;
				h_counter[r]++;
			} else { // reset
				if (maxConnected < h_counter[r]) {
					maxConnected = h_counter[r];
					// System.out.println("HORIZONTAL: "+maxConnected);
				}
				h_last[r] = -1;
				h_counter[r] = 0;
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

	public static int[][] deepCopyIntMatrix(int[][] input) {
		if (input == null)
			return null;
		int[][] result = new int[input.length][];
		for (int r = 0; r < input.length; r++) {
			result[r] = input[r].clone();
		}
		return result;
	}
}
