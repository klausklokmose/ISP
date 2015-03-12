import java.util.ArrayList;
import java.util.Collections;
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
	private static final int FOUR = 4;
	private static final int NOT_FOUND = -2;

	// player ids. The adversary is not 1 or 2, because we don't know if the ID
	// this instance will get is 1 or 2.
	private int playerID;
	private int ADVERSARY = 42;
	// public int playerScore;
	// public int adversaryScore;

	// queues to hold already played actions, such that finding connected coins
	// is easier.
	private PriorityQueue<Action> queueMAX;
	private PriorityQueue<Action> queueMIN;

	// true if prints should be done, false otherwise
	private boolean trace = false;

	// overridden each time a turn has to be taken by this instance.

	private static int DEPTH = 8;

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
			System.out.println("win");
			printBoard();
			if (this.playerID == 1) {
				return Winner.PLAYER1;
			} else {
				return Winner.PLAYER2;
			}
		} else if (util == -1) {
			System.out.println("win");
			printBoard();
			if (this.playerID == 1) {
				return Winner.PLAYER2;
			} else {
				return Winner.PLAYER1;
			}
		} else if (util == 0) {
			System.out.println("tie");
			printBoard();
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
		long startTime = System.currentTimeMillis(); // start a timer for this
														// turn
		printBoard();
		if (turns == 0) {
			return noCols / 2;
		}
//		PriorityQueue<Action> tmp = Action.cloneQueue(queueMAX);
//
//		findMaxConnectedCoins(tmp, true);
//		if (killer_move != null) {
//			System.out.println("killer move at " + killer_move);
//			return killer_move.getColumn();
//		}
//		PriorityQueue<Action> tmp2 = Action.cloneQueue(queueMIN);
//
//		findMaxConnectedCoins(tmp2, true);
//		if (killer_move != null) {
//			System.out.println("prevented killer move of adversary at "
//					+ killer_move);
//			int move = killer_move.getColumn();
//			killer_move = null;
//			return move;
//		}

		Action a = alpha_beta_search(board, DEPTH);
		print("" + a);
		int i = a.getColumn();
		print("try column: " + i);

		// UPDATE DEPTH
		if(turns > 7){
		long timeForLastTurn = (long) (System.currentTimeMillis() - startTime);
//		System.out.println(timeForLastTurn);
		if (timeForLastTurn > 10_000) {
			// time is lower than 10 sec
			DEPTH -= 1 *(timeForLastTurn / 10_000);
		}
		if (timeForLastTurn < 3_000) {
			// time is higher than 10 sec
			DEPTH += 1;
		}
		System.out.println("new depth = " + DEPTH + ". Time for last turn = "
				+ (double) timeForLastTurn/1000 + " sec");
		}
		return i;
	}

	/**
	 * search for the "best" next move to take based on the current state of the
	 * game.
	 * 
	 * @param stat
	 * @return
	 */
	private Action alpha_beta_search(int[][] stat, int depth) {
		int[][] state = deepCopyIntMatrix(stat); // TODO maybe we don't need to
													// deep copy the state here.

		// initial variables for alpha-beta pruning
		double alpha = Double.NEGATIVE_INFINITY;
		double beta = Double.POSITIVE_INFINITY;

		double max = Double.NEGATIVE_INFINITY;
		List<Action> actions = actions(state); // get a list of possible actions
												// to take.
		Collections.shuffle(actions);
		Action action = null;
		// for each action, find out which of those actions will lead to the
		// highest possible value.
		for (Action a : actions) {
			if(utility(result(state, a, this.playerID)) == 1){
				return a;
			}
			if(utility(result(state, a, ADVERSARY)) == -1){
				return a;
			}
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
	public double max_value(int[][] state, double alpha, double beta, int depth) {
		// int[][] state = deepCopyIntMatrix(s);
		depth = depth - 1;
		// if we have reached the limit
		if (cutoff_test(state, depth))
			return EVAL(state);

		int utility = utility(state);
		if (utility == 1) {
			return Integer.MAX_VALUE;
		} else if (utility == -1) {
			return utility;
		} else if (utility == 0) {
			return 0;
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
	public double min_value(int[][] state, double alpha, double beta, int depth) {
		// int[][] state = deepCopyIntMatrix(s);
		depth = depth - 1;
		// if we have reached the limit
		if (cutoff_test(state, depth))
			return EVAL(state);
		int utility = utility(state);
		// if (utility >= 0) {
		if (utility == 1) {
			return Integer.MAX_VALUE;
		} else if (utility == -1) {
			return utility;
		} else if (utility == 0) {
			return 0;
		}
		// }

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
		if (depth == 0) {
			// System.out.println("time to cutoff "+(end -
			// startTime)/1000+" sec");
			return true;
		}
		return false;
	}

	/**
	 * The evaluation function takes a state as input and returns a heuristic
	 * value of the current game node state.
	 * 
	 * @param s
	 * @return
	 */
	public double EVAL(final int[][] state) {
		int max_Possibilities = 0;
		int min_Possibilities = 0;
		// number of possibilities to place 4 coins horizontally (in one column)
		int noHorizontalPosibilities = winningPositions(noCols);
		// number of possibilities to place 4 coins vertically (in one row)
		int noVerticalPosibilities = winningPositions(noRows);

		for (int col = 0; col < state.length; col++) {
			// VERTICAL
			// Find out how many vertical possibilities there are in this column
			for (int j = 0; j < noVerticalPosibilities; j++) {
				int max_barrier = 0;
				int min_barrier = 0;
				for (int k = j; k < FOUR + j; k++) {
					int player = state[col][k];
					if (isThisPlayerOrVacant(player)) {
						max_barrier++;
					}
					if (isAdversaryOrVacant(player)) {
						min_barrier++;
					}
				}
				if (max_barrier == FOUR) {
					max_Possibilities++;
					// return Integer.MAX_VALUE;
				}
				if (min_barrier == FOUR) {
					min_Possibilities++;
					// return Integer.MIN_VALUE;
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
				int max_up_barrier = 0;
				int max_down_barrier = 0;
				int min_up_barrier = 0;
				int min_down_barrier = 0;

				// Go through the 2 arrays from above and add to the barrier
				// when there is a match.
				for (int i = 0; i < diagonal_dwn.length; i++) {

					// coordinates from diagonal_dwn action.
					int colDown = diagonal_dwn[i].getColumn();
					int rowDown = diagonal_dwn[i].getRow();

					// if the coordinates are valid in terms of the size of the
					// game board. AND the player in the coordinate is not the
					// adversary.
					if (validBounds(colDown, rowDown)) {
						int player = state[colDown][rowDown];
						if (isThisPlayerOrVacant(player)) {
							max_down_barrier++;
						}
						if (isAdversaryOrVacant(player)) {
							min_down_barrier++;
						}
					}

					// coordinates from diagonal_up action.
					int colUp = diagonal_up[i].getColumn();
					int rowUp = diagonal_up[i].getRow();
					// if the coordinates are valid in terms of the size of the
					// game board. AND the player in the coordinate is not the
					// adversary.
					if (validBounds(colUp, rowUp)) {
						int player = state[colUp][rowUp];
						if (isThisPlayerOrVacant(player)) {
							max_up_barrier++;
						}
						if (isAdversaryOrVacant(player)) {
							min_up_barrier++;
						}
					}

				}
				// If the barrier reached FOUR, then add to the total number of
				// possibilities
				if (isFour(max_down_barrier)) {
					max_Possibilities++;
					// return Integer.MAX_VALUE;
				}
				if (isFour(min_down_barrier)) {
					min_Possibilities++;
					// return Integer.MAX_VALUE;
				}
				// If the barrier reached FOUR, then add to the total number of
				// possibilities
				if (isFour(max_up_barrier)) {
					max_Possibilities++;
					// return Integer.MAX_VALUE;
				}
				if (isFour(min_up_barrier)) {
					min_Possibilities++;
					// return Integer.MAX_VALUE;
				}
			}

		} // END COL LOOP

		// HORIZONTAL
		// Find out how many horizontal possibilities there are for each row.
		for (int r = 0; r < noRows; r++) {
			for (int i = 0; i < noHorizontalPosibilities; i++) {
				int max_barrier = 0;
				int min_barrier = 0;
				for (int j = i; j < FOUR + i; j++) {
					int player = state[j][r];
					if (isThisPlayerOrVacant(player)) {
						max_barrier++;
					}
					if (isAdversaryOrVacant(player)) {
						min_barrier++;
					}
				}
				if (isFour(max_barrier)) {
					max_Possibilities++;
					// return Integer.MAX_VALUE;
				}
				if (isFour(min_barrier)) {
					min_Possibilities++;
				}
			}
		}
		// the evaluation
		return max_Possibilities - min_Possibilities;
	}

	private boolean isAdversaryOrVacant(int player) {
		return player == ADVERSARY || player == 0;
	}

	private boolean isThisPlayerOrVacant(int player) {
		return player == this.playerID || player == 0;
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
		PriorityQueue<Action> player1queue = new PriorityQueue<>();
		PriorityQueue<Action> player2queue = new PriorityQueue<>();
		for (int column = 0; column < state.length; column++) {
			for (int row = 0; row < state[column].length; row++) {
				int player = state[column][row]; // get the element at the
													// coordinate
				if (player == this.playerID) {
					player1queue.add(new Action(column, row));
				} else if (player == 42) {
					player2queue.add(new Action(column, row));
				}
			}
		}
		int oneSize = player1queue.size();
		int twoSize = player2queue.size();
		// find the maximum connected nodes each player has.
		int maxAIcoins = findMaxConnectedCoins(player1queue);
		int maxPlayer2 = findMaxConnectedCoins(player2queue);

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

	private boolean isFour(int count) {
		return count == FOUR;
	}

	/**
	 * finds the maximum number of connected nodes based on the given actions
	 * 
	 * @param actions
	 *            a sorted array (column ascending, row descending)
	 * @return 1 if 4 coins are connected, otherwise -2
	 */
	private int findMaxConnectedCoins(PriorityQueue<Action> actions) {
		PriorityQueue<Action> consistenQueue = Action.cloneQueue(actions);
		// the higher the maxConnected is, the more connected coins there are
		int maxConnected = 0;

		int size = actions.size();
		for (int i = 0; i < size; i++) {
			Action action = actions.poll();
			int r = action.getRow();
			int c = action.getColumn();

			/**
			 * HORIZONTAL checks for max number of connected coins upwards. 
			 */
			int vertical_barrier = 1; // now we have at least one out of four
										// connected
			Action[] vertical = new Action[] { new Action(c, r - 1),
					new Action(c, r - 2), new Action(c, r - 3) };
			for (int j = 0; j < vertical.length; j++) {
				Action actio = vertical[j];
				int player = (consistenQueue.contains(actio)) ? 1 : 0;
				if (player == 1) {
					vertical_barrier++;
				} else {
					vertical_barrier = 0;
				}
			}
			if (vertical_barrier > maxConnected) {
				maxConnected = vertical_barrier;
			}
			if (vertical_barrier == FOUR) {
				return FOUR;
			}

			/**
			 * HORIZONTAL checks for max number of connected coins to the right. 
			 * 
			 */
			Action[] horizontal_right = new Action[] { new Action(c + 1, r),
					new Action(c + 2, r), new Action(c + 3, r) };
			int horizontal_barrier = 1;
			for (int k = 0; k < horizontal_right.length; k++) {
				Action actio_right = horizontal_right[k];
				int player_right = (consistenQueue.contains(actio_right)) ? 1
						: 0;
				if (player_right == 1) {
					horizontal_barrier++;
				} else {
					horizontal_barrier = 0;
				}
				

			}
			// if there was a win, then return it
			if (horizontal_barrier == FOUR) {
				return FOUR;
			}
			// set the max connected if it is bigger
			if (horizontal_barrier > maxConnected) {
				maxConnected = horizontal_barrier;
			}
			/**
			 * check if there is a killer move and if it is valid
			 */
			
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
