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
		System.out.println("Player status " + util);
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
			System.out.println("row " + a.getRow());
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
		double 
		
		
		
		
		
		
		
		
		System.out.println("start alpha-beta");
		int[][] state = deepCopyIntMatrix(stat);
		double alpha = Double.MIN_VALUE;
		double beta = Double.MAX_VALUE;
		double max =Double.MIN_VALUE;
		int depth = 7;
		// System.out.println("maxdouble " + max);
		List<Action> actions = actions(state); // get a list of possible actions
		Action action = null;
		for (Action a : actions) {
			if(playerWon(result(state,a, playerID))== 1){ // killer move
				return a;
			}
			if(playerWon(result(state, a, adversary))==-1){ //block opponents killer move 
				return a;
			}
			double value = min_value(result(state, a, adversary), alpha, beta, depth);
			System.out.println("value " + value + " column " + a.getColumn() + " row " + a.getRow());

			if (value > max) {// if the value is higher than the current maximum
				max = value;
				System.out.println("Selected value: " + max);
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
		int utility = playerWon(state);
		if (utility == 1 || utility == -1 || utility == 0) {
			return utility;
		}
		if (depth <= 0)// if we have reached the limit
			return connected(state, playerID, adversary);
		
		List<Action> actions = actions(state); // possible actions from current
		double maximum = Double.MIN_VALUE;
		for (Action a : actions) {
			double value = min_value(result(state, a, adversary), alpha, beta, depth);
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
		int utility = playerWon(state);
		if (utility == 1 || utility == -1 || utility == 0) {
			return utility;
		}
		if (depth <= 0)// if we have reached the limit
			return connected(state, adversary, playerID);
		
		List<Action> actions = actions(state);
		double minimum = Double.MAX_VALUE;
		for (Action a : actions) {
			double value = max_value(result(state, a, playerID), alpha, beta, depth);
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
	public static List<Action> actions(int[][] s) {
		int[][] state = deepCopyIntMatrix(s);
		List<Action> actions = new ArrayList<Action>();

		for (int column = 0; column < state.length; column++) {
			if (isColumnNotFull(state, column)) { // if column is not full
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
	 * 
	 * 
	 * @param state
	 * @param column
	 * @param row
	 * @return if the given coordinate has a coin.
	 */
	private static boolean hasCoin(int[][] state, int column, int row) {
		return state[column][row] != 0;
	}

	/**
	 * check if the given column is full
	 * 
	 * @param state
	 * @param column
	 * @return
	 */
	private static boolean isColumnNotFull(int[][] state, int column) {
		return state[column][0] == 0;
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

	public int playerWon(int[][] state) {
		int possibleColumns = state.length;
		for (int i = 0; i < state.length; i++) {
			for (int j = 0; j < state[i].length; j++) {
				// vertical win
				if (j + 3 < state[i].length) {
					if (state[i][j] == playerID && state[i][j + 1] == playerID && state[i][j + 2] == playerID
							&& state[i][j + 3] == playerID) {
						return 1;
					}
				}

				// horizontal win
				if (i + 3 < state.length) {
					if (state[i][j] == playerID && state[i + 1][j] == playerID && state[i + 2][j] == playerID
							&& state[i + 3][j] == playerID) {
						return 1;
					}
				}

				// diagonal down win
				if (i + 3 < state.length && j + 3 < state[i].length) {
					if ((state[i][j] == playerID && state[i + 1][j + 1] == playerID && state[i + 2][j + 2] == playerID && state[i + 3][j + 3] == playerID)) {
						return 1;
					}
				}

				// diagonal up win
				if (i - 3 >= 0 && j - 3 >= 0) {
					if ((state[i][j] == playerID && state[i - 1][j - 1] == playerID && state[i - 2][j - 2] == playerID && state[i - 3][j - 3] == playerID)) {
						return 1;
					}
				}
				// vertical loss
				if (j + 3 < state[i].length) {
					if (state[i][j] == adversary && state[i][j + 1] == adversary && state[i][j + 2] == adversary
							&& state[i][j + 3] == adversary) {
						return -1;
					}
				}

				// horizontal loss
				if (i + 3 < state.length) {
					if (state[i][j] == adversary && state[i + 1][j] == adversary && state[i + 2][j] == adversary
							&& state[i + 3][j] == adversary) {
						return -1;
					}
				}

				// diagonal down loss
				if (i + 3 < state.length && j + 3 < state[i].length) {
					if ((state[i][j] == adversary && state[i + 1][j + 1] == adversary
							&& state[i + 2][j + 2] == adversary && state[i + 3][j + 3] == adversary)) {
						return -1;
					}
				}

				// diagonal up loss
				if (i - 3 >= 0 && j - 3 >= 0) {
					if ((state[i][j] == adversary && state[i - 1][j - 1] == adversary
							&& state[i - 2][j - 2] == adversary && state[i - 3][j - 3] == adversary)) {
						return -1;
					}
				}

			}
			if (state[i][0] != 0) {
				--possibleColumns;
			}
		}
		if (possibleColumns == 0) {
			return 0;
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
	public double connected(int[][] state, int playerID, int adversary) {
		double oneConnected = 0;
		double twoConnected = 0;
		double threeConnected = 0;
		double fourConnected = 0;
		double stateValue = 0;
		double adversaryOneConnected = 0;
		double adversaryTwoConnected = 0;
		double adversaryThreeConnected = 0;
		double adversaryFourConnected = 0;
		for (int i = 0; i < state.length; i++) {
			for (int j = 0; j < state[i].length; j++) {
				// vertical
				if (j + 3 < state[i].length) {
					if (state[i][j] == playerID) {
						oneConnected++;
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
						oneConnected++;
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
						oneConnected++;
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
						oneConnected++;
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
				// vertical adversary
				if (j + 3 < state[i].length) {
					if (state[i][j] == adversary) {
						adversaryOneConnected++;
						if (state[i][j + 1] == adversary) {
							adversaryTwoConnected++;
							if (state[i][j + 2] == adversary) {
								adversaryThreeConnected++;
								if (state[i][j + 3] == adversary) {
									adversaryFourConnected++;
								}
							}
						}
					}
				}

				// horizontal
				if (i + 3 < state.length) {
					if (state[i][j] == adversary) {
						adversaryOneConnected++;
						if (state[i + 1][j] == adversary) {
							adversaryTwoConnected++;
							if (state[i + 2][j] == adversary) {
								adversaryThreeConnected++;
								if (state[i + 3][j] == adversary) {
									adversaryFourConnected++;
								}
							}
						}
					}
				}

				// diagonal down win
				if (i + 3 < state.length && j + 3 < state[i].length) {
					if (state[i][j] == adversary) {
						adversaryOneConnected++;
						if (state[i + 1][j + 1] == adversary) {
							adversaryTwoConnected++;
							if (state[i + 2][j + 2] == adversary) {
								adversaryThreeConnected++;
								if (state[i + 3][j + 3] == adversary) {
									fourConnected++;
								}
							}
						}
					}
				}

				// diagonal up 
				if (i - 3 >= 0 && j - 3 >= 0) {
					if (state[i][j] == adversary) {
						adversaryOneConnected++;
						if (state[i - 1][j - 1] == adversary) {
							adversaryTwoConnected++;
							if (state[i - 2][j - 2] == adversary) {
								adversaryThreeConnected++;
								if (state[i - 3][j - 3] == adversary) {
									adversaryFourConnected++;
								}
							}
						}
					}
				}
			}
		}

		stateValue = stateValue +(oneConnected) + (twoConnected * 4) + (threeConnected * 6) + (fourConnected * 10)
				-(adversaryOneConnected)- (adversaryTwoConnected * 4) - (adversaryThreeConnected * 6)-(adversaryFourConnected * 10);

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
