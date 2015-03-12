import java.util.ArrayList;
import java.util.List;

public class GameLogicMin implements IGameLogic {
	int t = 0;
	// holds the state of the currently already played board.
	public int[][] board;
	private int turns;
	private int playerID;
	private int adversary = 42;

	// true if prints should be done, false otherwise
	private boolean trace = false;

	public GameLogicMin() {
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
		this.playerID = playerID;
		board = new int[noCols][noRows];
	}

	/**
	 * returns if the game has finished either by a draw, or MAX or MIN have
	 * won, or it was a tie. Else it returns Winner.NOT_FINISHED (so the game
	 * will continue).
	 */
	public Winner gameFinished() {
		int util = playerWon(board);
		if (util == 1) {
			if (playerID == 1) {
				return Winner.PLAYER1;
			} else {
				return Winner.PLAYER2;
			}
		} else if (util == -1) {
			if (playerID == 1) {
				return Winner.PLAYER2;
			} else {
				return Winner.PLAYER1;
			}
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
		System.out.println("Insert coin. Player " + playerID);
		if (this.playerID != playerID) {
			playerID = adversary;
		}
		int[] col = board[column];
		if (col[0] == 0) {// the column is not full
			boolean inserted = false;
			for (int row = 0; row < col.length; row++) {
				if (col[row] != 0) {// if the element is not vacant, the prior
									// one was vacant
					int newRow = row - 1;
					col[newRow] = playerID; // set the id to the board
					inserted = true;
					break; // no need to search more
				}
			}
			if (!inserted) {// if the coin was not inserted, the column must
							// have been empty
				// set the coin to the bottom element

				col[col.length - 1] = playerID;
				inserted = true;
			}
		} else {
			print("you can't insert a coin here, because it is full!");
		}
		printBoard();
	}

	/**
	 * find out which move to take based on the current board return the column
	 * to insert a coin in to
	 */
	public int decideNextMove() {
		int i = 0;
		System.out.println("decideMovecalled");
		Action a = null;
		a = alpha_beta_search(board);
		if (a != null) {
			i = a.getColumn();
			System.out.println("column " + i);
		} else {
			System.out.println("Action is null");
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
	private Action alpha_beta_search(int[][] stat) {
		System.out.println("start alpha-beta");
		int[][] state = deepCopyIntMatrix(stat);
		double alpha = Double.MIN_VALUE;
		double beta = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		int depth = 13;
		// System.out.println("maxdouble " + max);

		List<Action> actions = actions(state); // get a list of possible actions
		Action action = null;
		for (Action a : actions) {// for each action, find out which of those
									// actions will lead to the highest possible
									// value.
			double value = min_value(result(state, a, adversary), alpha, beta,
					depth);
			System.out.println("value " + value);
			if (value > max) {// if the value is higher than the current maximum
				max = value;
				action = a;// it must be a better action.
			}
		}
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
		if (depth == 0)// if we have reached the limit
			return connected(state, playerID);
		int utility = playerWon(state);
		if (utility == 1 || utility == -1) {
			return utility;
		}
		List<Action> actions = actions(state); // possible actions from current
		double maximum = Double.NEGATIVE_INFINITY;
		for (Action a : actions) {
			double value = min_value(result(state, a, adversary), alpha, beta,
					depth);
			maximum = Math.max(maximum, value);
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
		if (depth == 0)// if we have reached the limit
			return connected(state, adversary);
		int utility = playerWon(state);
		if (utility == 1 || utility == -1) {
			return utility;
		}

		List<Action> actions = actions(state);
		double minimum = Double.POSITIVE_INFINITY;
		for (Action a : actions) {
			double value = max_value(result(state, a, playerID), alpha, beta,
					depth);
			minimum = Math.min(minimum, value);
			if (minimum <= alpha)
				return minimum;
			beta = Math.min(beta, minimum);
		}

		return minimum;
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
		int[][] s = deepCopyIntMatrix(state);
		s[pair.getColumn()][pair.getRow()] = playerID;
		return s;
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

	public Action findThreeConnectedHorizontal(int[][] state, int playerID) {
		for (int i = 0; i < state.length; i++) {
			for (int j = 0; j < state[i].length; j++) {
				// horizontal 3 connected
				if (i + 2 < state.length) {
					if (state[i][j] == playerID && state[i + 1][j] == playerID
							&& state[i + 2][j] == playerID) {
						return new Action(i + 3, j);
					}
				}
			}
		}
		return null;
	}

	public Action findThreeConnectedVertical(int[][] state) {
		for (int i = 0; i < state.length; i++) {
			for (int j = 0; j < state[i].length; j++) {
				// vertical win
				if (j + 2 < state.length) {
					if (state[i][j] == playerID && state[i][j + 1] == playerID
							&& state[i][j + 2] == playerID) {
						return new Action(i, j + 3);
					}
				}
			}

		}
		return null;
	}

	public int playerWon(int[][] state) {
		for (int i = 0; i < state.length; i++) {
			for (int j = 0; j < state[i].length; j++) {
				// vertical win
				if (j + 3 < state[i].length) {
					if (state[i][j] == playerID && state[i][j + 1] == playerID
							&& state[i][j + 2] == playerID
							&& state[i][j + 3] == playerID) {
						return 1;
					}
				}

				// horizontal win
				if (i + 3 < state.length) {
					if (state[i][j] == playerID && state[i + 1][j] == playerID
							&& state[i + 2][j] == playerID
							&& state[i + 3][j] == playerID) {
						return 1;
					}
				}

				// diagonal down win
				if (i + 3 < state.length && j + 3 < state[i].length) {
					if ((state[i][j] == playerID
							&& state[i + 1][j + 1] == playerID
							&& state[i + 2][j + 2] == playerID && state[i + 3][j + 3] == playerID)) {
						return 1;
					}
				}

				// diagonal up win
				if (i - 3 >= 0 && j - 3 >= 0) {
					if ((state[i][j] == playerID
							&& state[i - 1][j - 1] == playerID
							&& state[i - 2][j - 2] == playerID && state[i - 3][j - 3] == playerID)) {
						return 1;
					}
				}
				// vertical loss
				if (j + 3 < state[i].length) {
					if (state[i][j] == adversary
							&& state[i][j + 1] == adversary
							&& state[i][j + 2] == adversary
							&& state[i][j + 3] == adversary) {
						return -1;
					}
				}

				// horizontal loss
				if (i + 3 < state.length) {
					if (state[i][j] == adversary
							&& state[i + 1][j] == adversary
							&& state[i + 2][j] == adversary
							&& state[i + 3][j] == adversary) {
						return -1;
					}
				}

				// diagonal down lossn
				if (i + 3 < state.length && j + 3 < state[i].length) {
					if ((state[i][j] == adversary
							&& state[i + 1][j + 1] == adversary
							&& state[i + 2][j + 2] == adversary && state[i + 3][j + 3] == adversary)) {
						return -1;
					}
				}

				// diagonal up loss
				if (i - 3 >= 0 && j - 3 >= 0) {
					if ((state[i][j] == adversary
							&& state[i - 1][j - 1] == adversary
							&& state[i - 2][j - 2] == adversary && state[i - 3][j - 3] == adversary)) {
						return -1;
					}
				}
			}
		}
		return -2;
	}

	/**
	 * Scans the state and assigns a value to it.
	 * 
	 * @param state
	 * @param playerID
	 * @return satte value
	 */
	public double connected(int[][] state, int playerID) {
		double twoConnected = 0;
		double threeConnected = 0;
		double fourConnected = 0;
		double stateValue = 1;
		for (int i = 0; i < state.length; i++) {
			for (int j = 0; j < state[i].length; j++) {
				// vertical
				if (j + 3 < state[i].length) {
					if (state[i][j] == playerID) {
						if (state[i][j + 1] == playerID) {
							twoConnected++;
							if (state[i][j + 2] == playerID) {
								threeConnected++;
								if (state[i][j + 3] == playerID) {
									fourConnected++;
								}
							}
						}
					}
				}

				// horizontal
				if (i + 3 < state.length) {
					if (state[i][j] == playerID) {
						if (state[i + 1][j] == playerID) {
							twoConnected++;
							if (state[i + 2][j] == playerID) {
								threeConnected++;
								if (state[i + 3][j] == playerID) {
									fourConnected++;
								}
							}
						}
					}
				}

				// diagonal down win
				if (i + 3 < state.length && j + 3 < state[i].length) {
					if (state[i][j] == playerID) {
						if (state[i + 1][j + 1] == playerID) {
							twoConnected++;
							if (state[i + 2][j + 2] == playerID) {
								threeConnected++;
								if (state[i + 3][j + 3] == playerID) {
									fourConnected++;
								}
							}
						}
					}
				}

				// diagonal up win
				if (i - 3 >= 0 && j - 3 >= 0) {
					if (state[i][j] == playerID) {
						if (state[i - 1][j - 1] == playerID) {
							twoConnected++;
							if (state[i - 2][j - 2] == playerID) {
								threeConnected++;
								if (state[i - 3][j - 3] == playerID) {
									fourConnected++;
								}
							}
						}
					}
				}
			}
		}
		
		if (twoConnected != 0) {
			stateValue = stateValue + (twoConnected * 4);
		}
		if (threeConnected != 0) {
			stateValue = stateValue + (threeConnected * 6);
		}
		if (fourConnected != 0) {
			stateValue = stateValue + (fourConnected * 8);
		}

		return stateValue / 100;
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
