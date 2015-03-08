import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class GameLogicKlaus implements IGameLogic {
	private int noCols;
	private int noRows;
	private int playerID;
	public int[][] board;
	private int turns;
	private final int FOUR = 4;
	private final int ADVERSARY = 42;
	public int playerScore;
	public int adversaryScore;

	private PriorityQueue<Action> queueMAX;
	private PriorityQueue<Action> queueMIN;
	private boolean trace = false;
	private long startTime;

	public GameLogicKlaus() {
		// TODO Write your implementation for this method
	}

	public void print(String str) {
		if (trace)
			System.out.println(str);
	}

	public void initializeGame(int noCols, int noRows, int playerID) {
		this.noCols = noCols;
		this.noRows = noRows;
		print(noCols + ", " + noRows);
		this.playerID = playerID;
		// TODO Write your implementation for this method
		board = new int[noCols][noRows];
		queueMAX = new PriorityQueue<>();
		queueMIN = new PriorityQueue<>();
		playerScore = adversaryScore = winningPositions(noCols)
				* winningPositions(noRows) * 2 // DIAGONAL (UP AND DOWN)
				+ noRows * winningPositions(noCols) // HORIZONTAL
				+ noCols * winningPositions(noRows); // VERTICAL
	}

	private int winningPositions(int n) {
		return (n % FOUR) + 1;
	}

	public Winner gameFinished() {
		// TODO Write your implementation for this method
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

	public void insertCoin(int column, int playerID) {
		if (playerID != this.playerID) {
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
			if (trace)
				printQueues();
			printBoard();
		} else {
			print("you can't insert a coin here, because it is full!");
		}
		turns++;
	}

	private Action alpha_beta_search(int[][] stat) {
		startTime = System.currentTimeMillis();

		int[][] state = deepCopyIntMatrix(stat);
		double alpha = Double.NEGATIVE_INFINITY;
		double beta = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;
		List<Action> actions = actions(state);
		Action action = null;
		for (Action a : actions) {
			double value = min_value(result(state, a, this.playerID), alpha,
					beta, 10);
			if (value > max) {
				max = value;
				action = a;
			}
		}

		if (trace)
			printBoard();
		return action;
	}

	public double max_value(int[][] s, double alpha, double beta, int depth) {
		int[][] state = deepCopyIntMatrix(s);
		depth = depth-1;
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

	public double min_value(int[][] s, double alpha, double beta, int depth) {
		int[][] state = deepCopyIntMatrix(s);
		depth = depth-1;
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

	private boolean cutoff_test(int[][] state, int depth) {
		long end = System.currentTimeMillis();
		if((end-startTime) >= 10_000){
			return true;
		}
//		if (depth <= 0) {
//			return true;
//		}
		return false;
	}

	public double NEWEVAL(int[][] s) {
		int[][] state = deepCopyIntMatrix(s);
		int result = 0;
		int noHorizontalPosibilities = winningPositions(noCols);
		int noVerticalPosibilities = winningPositions(noRows);

		for (int col = 0; col < state.length; col++) {
			// vertical
			for (int j = 0; j < noVerticalPosibilities; j++) {
				int barrier = 0;
				for (int k = j; k < FOUR + j; k++) {
					if (state[col][k] != ADVERSARY) {
						barrier++;
					}
				}
				if (barrier == FOUR) {
					result++;
				}
			}

			//DIAGONAL
			for (int r = 0; r < state[col].length; r++) {
				
				Action[] diagonal_up = new Action[] {new Action(col, r), new Action(col + 1, r - 1),
						new Action(col + 2, r - 2), new Action(col + 3, r - 3) };
				Action[] diagonal_dwn = new Action[] {new Action(col, r), new Action(col + 1, r + 1),
						new Action(col + 2, r + 2), new Action(col + 3, r + 3) };
			
				int d_barrier = 0;
				int u_barrier = 0;
				for (int i = 0; i < diagonal_dwn.length; i++) {
					int colD = diagonal_dwn[i].getColumn();
					int rowD = diagonal_dwn[i].getRow();
					if(validBounds(colD, rowD) && state[colD][rowD]!=ADVERSARY){
						d_barrier++;
					}
					int colUp = diagonal_up[i].getColumn();
					int rowUp = diagonal_up[i].getRow();
					if(validBounds(colUp, rowUp) && state[colUp][rowUp] != ADVERSARY){
						u_barrier++;
					}
				}
				if(d_barrier == FOUR){
					result++;
				}
				if(u_barrier == FOUR){
					result++;
				}
			}

		} //END COL LOOP
		
		// HORIZONTAL
		for (int r = 0; r < noRows; r++) {
			for (int i = 0; i < noHorizontalPosibilities; i++) {
				int barrier = 0;
				for (int j = i; j < FOUR+i; j++) {
					if (state[j][r] != ADVERSARY) {
						barrier++;
					}
				}
				if(barrier == FOUR){
					result++;
				}
			}
		}
		return result;
	}

	private boolean validBounds(int colUp, int rowUp) {
		return !(colUp < 0 || colUp > noCols-1 || rowUp < 0 || rowUp > noRows-1);
	}

	public double EVAL(int[][] s) {
		int[][] state = deepCopyIntMatrix(s);
		// find all actions that MAX has taken
		PriorityQueue<Action> pairs = new PriorityQueue<>();
		for (int col = 0; col < state.length; col++) {
			for (int row = 0; row < state[col].length; row++) {
				if (state[col][row] == this.playerID) {
					pairs.add(new Action(col, row));
				}
			}
		}

		int maxConnectedNodes = findMatch(pairs
				.toArray(new Action[pairs.size()]));
		return maxConnectedNodes / 4;
	}

	public int[][] result(int[][] state, Action pair, int playerID) {
//		int playerID = (turns % 2+1)
		int[][] s = deepCopyIntMatrix(state);
		s[pair.getColumn()][pair.getRow()] = playerID;
		return s;
	}

	public int utility(int[][] s) {
		int[][] state = s;
		PriorityQueue<Action> AIqueue = new PriorityQueue<>();
		PriorityQueue<Action> player2 = new PriorityQueue<>();
		for (int column = 0; column < state.length; column++) {
			for (int row = 0; row < state[column].length; row++) {
				int player = state[column][row];
				if (player == this.playerID) {
					AIqueue.add(new Action(column, row));
				} else if (player != 0) {
					player2.add(new Action(column, row));
				}
			}
		}
		Action[] AICoins = AIqueue.toArray(new Action[AIqueue.size()]);
		Action[] adversaryCoins = player2.toArray(new Action[player2.size()]);

		int maxAIcoins = findMatch(AICoins);
		int maxPlayer2 = findMatch(adversaryCoins);

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
		print("" + a);
		int i = a.getColumn();
		print("try column: " + i);
		return i;
	}

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
//					System.out.println("VERTICAL: "+FOUR);
					return v_counter;
				} else if (maxConnected < v_counter) {
					maxConnected = v_counter;
//					System.out.println("VERTICAL: "+maxConnected);
				}
		
				// HORIZONTAL
				if (h_barrier[r] == FOUR) {
	//				print("found 4 coins!! HORIZONTAL in row " + r);
//					System.out.println("HORIZONTAL: "+FOUR);
					return FOUR;
				} else if (maxConnected < h_barrier[r]) {
					maxConnected = h_barrier[r];
//					System.out.println("HORIZONTAL: "+maxConnected);
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
//						System.out.println("HORIZONTAL: "+maxConnected);
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
//					System.out.println("DIAG UP: "+maxConnected);
					maxConnected = upDiagonalBarrier;
				}
				if (maxConnected < dwnDiagonalBarrier) {
//					System.out.println("DIAG DWN: "+maxConnected);
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
