import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class GameLogicCemKlausMindaugas implements IGameLogic {
	// holds the state of the currently already played board.
	public int[][] board;
	private int noCols;
	private int noRows;
	// each time a turn has been taken, the turns is incremented
	private int turns;

	// constants
	private static final int FOUR = 4;
	private static final int NOT_FOUND = -2;

	// player ids. The adversary is not 1 or 2, because we don't know if the ID
	// this instance will get is 1 or 2.
	private int playerID;
	private int ADVERSARY = 42;

	// queues to hold already played actions, such that finding connected coins
	// is easier.
	private PriorityQueue<Action> queueMAX;
	private PriorityQueue<Action> queueMIN;

	// true if prints should be done, false otherwise
	private boolean trace = false;

	private static int DEPTH = 10;

	/**
	 * initializes the game with a fresh board and sets the playerID of this
	 * instance.
	 * 
	 */
	public void initializeGame(int noCols, int noRows, int playerID) {
		// TODO Write your implementation for this method
		this.noCols = noCols;
		this.noRows = noRows;
		this.playerID = playerID;
		board = new int[noCols][noRows];
		queueMAX = new PriorityQueue<>();
		queueMIN = new PriorityQueue<>();
		
		print(noCols + ", " + noRows);
	}

	/**
	 * returns if the game has finished either by a draw, or MAX or MIN have
	 * won, or it was a tie. Else it returns Winner.NOT_FINISHED (so the game
	 * will continue).
	 */
	public Winner gameFinished() {
		int utility = UTILITY(board);
		if (utility == 1) {
			System.out.println("win");
			printBoard();
			if (this.playerID == 1) {
				return Winner.PLAYER1;
			} else {
				return Winner.PLAYER2;
			}
		} else if (utility == -1) {
			System.out.println("win");
			printBoard();
			if (this.playerID == 1) {
				return Winner.PLAYER2;
			} else {
				return Winner.PLAYER1;
			}
		} else if (utility == 0) {
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

		// the column is not full
		if (col[0] == 0) {
			boolean inserted = false;
			for (int row = 0; row < col.length; row++) {
				// if the element is not vacant, the prior one was vacant
				if (col[row] != 0) {
					int lastRow = row - 1;
					col[lastRow] = playerID; // set the id to the board
					inserted = true;
					p = new Action(column, lastRow); // create the action
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
		if(trace)
			printBoard();
		// start a timer for this turn
		long startTime = System.currentTimeMillis(); 
		if (turns == 0) {
			return noCols / 2;
		}

		Action a = alpha_beta_search(board, DEPTH);
		print("" + a);
		int i = a.getColumn();
		print("try column: " + i);

		updateDepth(startTime);
		return i;
	}

	private void updateDepth(long startTime) {
		if (turns > 6) {
			long timeForTurn = (long) (System.currentTimeMillis() - startTime);
			// time is higher than 10 sec
			if (timeForTurn > 10_000) {
				DEPTH -= 1;
			}
			if (timeForTurn < 3_000) {
				// time is lower than 3 seconds
				DEPTH += 1;
			}
//			System.out.println("new depth = " + DEPTH
//					+ ". Time for last turn = " + (double) timeFoTurn
//					/ 1000 + " sec");
		}
	}

	/**
	 * search for the "best" next move to take based on the current state of the
	 * game.
	 * 
	 * @param stat
	 * @return
	 */
	private Action alpha_beta_search(int[][] state, int depth) {
		// initial variables for alpha-beta pruning
		double alpha = Double.NEGATIVE_INFINITY;
		double beta = Double.POSITIVE_INFINITY;

		double max = Double.NEGATIVE_INFINITY;
		// get a list of possible actions to take.
		List<Action> actions = ACTIONS(state); 
		Action takeAction = null;
		// for each action, find out which of those actions will lead to the
		// highest possible value
		for (Action action : actions) {
			// check if action is a win or will block the adversary
			if (UTILITY(result(state, action, this.playerID)) == 1)
				return action;
			if (UTILITY(result(state, action, ADVERSARY)) == -1)
				return action;

			double value = min_value(result(state, action, this.playerID), alpha,
					beta, depth);
			// if the value is higher than the current maximum, it must be a
			// better action.
			if (value > max) {
				max = value;
				takeAction = action;
			}
		}
		return takeAction;
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
	private double max_value(int[][] state, double alpha, double beta, int depth) {
		// TODO
		depth = depth - 1;
		// if we have reached the limit
		if (cutoff_test(state, depth))
			return EVAL(state);

		//terminal-test
		int utility = UTILITY(state);
		if (utility == 1) {
			return Integer.MAX_VALUE;
		} else if (utility == -1) {
			return utility;
		} else if (utility == 0) {
			return 0;
		}
		// possible actions from current state
		List<Action> actions = ACTIONS(state);
		
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
	private double min_value(int[][] state, double alpha, double beta, int depth) {
		// TODO
		depth = depth - 1;
		// if we have reached the limit
		if (cutoff_test(state, depth))
			return EVAL(state);
		int utility = UTILITY(state);
		// if (utility >= 0) {
		if (utility == 1) {
			return Integer.MAX_VALUE;
		} else if (utility == -1) {
			return utility;
		} else if (utility == 0) {
			return 0;
		}
		// }

		List<Action> actions = ACTIONS(state);
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
		// TODO
		if (depth == 0) {
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
	private double EVAL(final int[][] state) {
		// TODO
		int max_numberof_possibilities = 0;
		int min_numberof_possibilities = 0;
		
		// number of possibilities to place 4 coins vertically (in same row)
		int noVerticalPosibilities = winningPositions(noRows);

		for (int col = 0; col < state.length; col++) {
			// VERTICAL
			// Find out how many vertical possibilities there are in this column
			for (int j = 0; j < noVerticalPosibilities; j++) {
				int max_barrier = 0;
				int min_barrier = 0;
				for (int k = j; k < FOUR + j; k++) {
					//the player can be a playerID or 0
					int player = state[col][k];
					if (isThisPlayerOrIndexIsVacant(player)) {
						max_barrier++;
					}
					if (isAdversaryOrIndexVacant(player)) {
						min_barrier++;
					}
				}
				if (isFour(max_barrier)) {
					max_numberof_possibilities++;
				}
				if (isFour(min_barrier)) {
					min_numberof_possibilities++;
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
						if (isThisPlayerOrIndexIsVacant(player)) {
							max_down_barrier++;
						}
						if (isAdversaryOrIndexVacant(player)) {
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
						if (isThisPlayerOrIndexIsVacant(player)) {
							max_up_barrier++;
						}
						if (isAdversaryOrIndexVacant(player)) {
							min_up_barrier++;
						}
					}

				}
				// If the barrier reached FOUR, then add to the total number of
				// possibilities
				if (isFour(max_down_barrier)) {
					max_numberof_possibilities++;
				}
				if (isFour(min_down_barrier)) {
					min_numberof_possibilities++;
				}
				// If the barrier reached FOUR, then add to the total number of
				// possibilities
				if (isFour(max_up_barrier)) {
					max_numberof_possibilities++;
				}
				if (isFour(min_up_barrier)) {
					min_numberof_possibilities++;
				}
			}

		}

		// HORIZONTAL
		// Find out how many horizontal possibilities there are for each row.
		
		// number of possibilities to place 4 coins horizontally (in same column)
		int noHorizontalPosibilities = winningPositions(noCols);
		
		for (int r = 0; r < noRows; r++) {
			for (int i = 0; i < noHorizontalPosibilities; i++) {
				int max_barrier = 0;
				int min_barrier = 0;
				for (int j = i; j < FOUR + i; j++) {
					int player = state[j][r];
					if (isThisPlayerOrIndexIsVacant(player)) {
						max_barrier++;
					}
					if (isAdversaryOrIndexVacant(player)) {
						min_barrier++;
					}
				}
				if (isFour(max_barrier)) {
					max_numberof_possibilities++;
				}
				if (isFour(min_barrier)) {
					min_numberof_possibilities++;
				}
			}
		}
		// the evaluation
		return max_numberof_possibilities - min_numberof_possibilities;
	}

	/**
	 * Returns the utility of the state.
	 * 
	 * @param s
	 * @return -2 if game has not ended, 1 if this instance won, -1 if the
	 *         adversary won, 0 if the game has finished with a tie.
	 */
	private int UTILITY(int[][] state) {
		// TODO

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
		int maxplayer1coins = findMaxConnectedCoins(player1queue);
		int maxPlayer2coins = findMaxConnectedCoins(player2queue);

		if (isFour(maxplayer1coins)) {
			return 1;
		}
		if (isFour(maxPlayer2coins)) {
			return -1;
		}
		if (isBoardFilled(oneSize, twoSize)) {
			return 0;
		}
		return NOT_FOUND; // game not ended
	}

	private boolean isBoardFilled(int oneSize, int twoSize) {
		return (oneSize + twoSize) == (noCols * noRows);
	}

	/**
	 * finds the maximum number of connected nodes based on the given actions
	 * 
	 * @param actions
	 *            a sorted array (column ascending, row descending)
	 * @return
	 */
	private int findMaxConnectedCoins(PriorityQueue<Action> actions) {
		// TODO
		//keep a consistent version of the actions
		PriorityQueue<Action> consistenQueue = Action.cloneQueue(actions);
		int maxConnectedCoins = 0;

		int size = actions.size();
		for (int i = 0; i < size; i++) {
			//take the top action
			Action action = actions.poll();
			int row = action.getRow();
			int column = action.getColumn();

			/**
			 * VERTICAL checks for max number of connected coins upwards.
			 */
			// now we have at least one out of four connected
			int vertical_barrier = 1;
			
			//create vertical actions that will give 4 connected
			Action[] vertical = new Action[] { 
					new Action(column, row - 1), new Action(column, row - 2), new Action(column, row - 3) };
			
			//check if the actions exist in the queue
			for (int j = 0; j < vertical.length; j++) {
				Action v_action = vertical[j];
				
				int player = (consistenQueue.contains(v_action)) ? 1 : 0;
				if (player == 1) {
					vertical_barrier++;
					// set the max connected if it is bigger
					if (vertical_barrier > maxConnectedCoins) {
						maxConnectedCoins = vertical_barrier;
					}
				} else {
					vertical_barrier = 0;
				}
			}
			
			if (isFour(vertical_barrier)) {
				return FOUR;
			} 

			/**
			 * HORIZONTAL checks for max number of connected coins to the right.
			 * 
			 */
			Action[] horizontal_right = new Action[] { 
					new Action(column + 1, row), new Action(column + 2, row), new Action(column + 3, row) };
			
			int horizontal_barrier = 1;
			for (int k = 0; k < horizontal_right.length; k++) {
				Action actio_right = horizontal_right[k];
				int player_right = (consistenQueue.contains(actio_right)) ? 1
						: 0;
				if (player_right == 1) {
					horizontal_barrier++;
					// set the max connected if it is bigger
					if (horizontal_barrier > maxConnectedCoins) {
						maxConnectedCoins = horizontal_barrier;
					}
				} else {
					horizontal_barrier = 0;
				}
			}
			// if there was a win, then return it
			if (isFour(horizontal_barrier)) {
				return FOUR;
			}

			/**
			 * DIAGONAL
			 */
			Action[] diagonal_up = new Action[] { 
					new Action(column + 1, row - 1), new Action(column + 2, row - 2), new Action(column + 3, row - 3) };
			Action[] diagonal_dwn = new Action[] { 
					new Action(column + 1, row + 1), new Action(column + 2, row + 2), new Action(column + 3, row + 3) };
			
			int up_diagonal_barrier, down_diagonal_barrier;
			up_diagonal_barrier = down_diagonal_barrier = 1;

			for (int j = 0; j < diagonal_up.length; j++) {
				Action action_up = diagonal_up[j];
				if (consistenQueue.contains(action_up)) {
					up_diagonal_barrier++;
					// set the max connected if it is bigger
					if (maxConnectedCoins < up_diagonal_barrier) {
						maxConnectedCoins = up_diagonal_barrier;
					}
				}else{
					up_diagonal_barrier = 0;
				}
				Action a_down = diagonal_dwn[j];
				if (consistenQueue.contains(a_down)) {
					down_diagonal_barrier++;
					// set the max connected if it is bigger
					if (maxConnectedCoins < down_diagonal_barrier) {
						maxConnectedCoins = down_diagonal_barrier;
					}
				}else{
					down_diagonal_barrier = 0;
				}
			}
			if (isFour(up_diagonal_barrier) || isFour(down_diagonal_barrier)) {
				return FOUR;
			}
		}
		return maxConnectedCoins;
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
	public static int[][] result(int[][] state, Action pair, int playerID) {
		// TODO
		int[][] s = deepCopyIntMatrix(state);
		s[pair.getColumn()][pair.getRow()] = playerID;
		return s;
	}

	private boolean isAdversaryOrIndexVacant(int player) {
		return player == ADVERSARY || player == 0;
	}

	private boolean isThisPlayerOrIndexIsVacant(int player) {
		return player == this.playerID || player == 0;
	}

	/**
	 * returns the number of ways FOUR coins can be placed in an n possible places
	 * 
	 * @param n
	 * @return
	 */
	private int winningPositions(int n) {
		return (n % FOUR) + 1;
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
	 * get the possible actions
	 * 
	 * @param state
	 * @return as list of possible actions on the given state.
	 */
	public static List<Action> ACTIONS(int[][] state) {
		
		List<Action> actions = new ArrayList<Action>();

		for (int column = 0; column < state.length; column++) {
			if (isColumnNotFull(state, column)) {
				Action p = null;
				for (int row = 0; row < state[column].length; row++) {
					if (hasCoin(state, column, row)) {
						// add the prior row action
						p = new Action(column, row - 1);
						break;
					}
				}
				// if column is empty
				if (p == null) {
					// add this action
					int lastItemInRow = state[column].length - 1;
					p = new Action(column, lastItemInRow);
				}
				actions.add(p);
			} else {

			}
		}
		return actions;
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
	 * print a string to stout if trace is true
	 * 
	 * @param str
	 */
	public void print(String str) {
		if (trace)
			System.out.println(str);
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

	private boolean isFour(int count) {
		return count == FOUR;
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

	public static int[][] deepCopyIntMatrix(int[][] matrix) {
		if (matrix == null)
			return null;
		int[][] result = new int[matrix.length][];
		for (int r = 0; r < matrix.length; r++) {
			result[r] = matrix[r].clone();
		}
		return result;
	}
}
