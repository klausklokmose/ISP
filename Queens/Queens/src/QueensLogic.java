/**
 * This class implements the logic behind the BDD for the n-queens problem
 * You should implement all the missing methods
 * 
 * @author Stavros Amanatidis
 *
 */
import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;
import net.sf.javabdd.JFactory;

public class QueensLogic {
	private int n = 0; // size of the board
	private int[][] board = null;
	private BDD queens; // all game rules
	private BDD restricted; // restrictions of the game
	private BDDFactory fact;
	private int numberOfVariables;

	public QueensLogic() {

	}

	/**
	 * Create a game board.
	 * 
	 * @param n
	 *            of the board is applied vertically and horizontally
	 */
	public void initializeGame(int n) {
		this.n = n;
		this.board = new int[n][n];
		this.numberOfVariables = n * n;

		// initialize factory
		fact = JFactory.init(2_000_000, 200_000);
		fact.setVarNum(n * n);

		// The BDD - conjunctions of the implications Xij -> the rules of Xij
		queens = fact.one();
		// variables are changed to "constants" true/false during execution
		restricted = fact.one();

		// ordered by x0 < x1 < x2 ... < x(n*n)-1
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				queens.andWith(build(i, j));
			}
		}
	}

	/**
	 * Method that defines the rules of nQueen game for each cell
	 * 
	 * @param i
	 *            column of the cell
	 * @param j
	 *            row of the cell
	 * @return BDD for a specific cell
	 */
	public BDD build(int i, int j) {
		BDD cell = fact.one();

		for (int l = 0; l < n; l++) {

			// defining horizontal rules
			if (l != j) {
				cell = cell.and(fact.nithVar(getVarNumber(n, i, l)));
			}

			// defining vertical rules
			for (int k = 0; k < n; k++) {
				if (k != i) {
					cell = cell.and(fact.nithVar(getVarNumber(n, k, j)));
				}
			}

			// defining diagonal down rules
			for (int k = 0; k < n; k++) {
				int diag = j + k - i;
				if (diag >= 0 && diag < n) {
					if (k != i) {
						cell = cell.and(fact.nithVar(getVarNumber(n, k, diag)));
					}
				}
			}

			// defining diagonal up rules
			for (int k = 0; k < n; k++) {
				int diag = j + i - k;
				if (diag >= 0 && diag < n) {
					if (k != i) {
						cell = cell.and(fact.nithVar(getVarNumber(n, k, diag)));
					}
				}
			}

		}
		// add one queen per row rule
		cell.andWith(oneQueenPerRow());
		return fact.ithVar(getVarNumber(n, i, j)).imp(cell);
	}

	/**
	 * Defines a one queen per row rule
	 * 
	 * @return BDD
	 */
	public BDD oneQueenPerRow() {
		BDD rule = fact.one();
		for (int i = 0; i < n; i++) {
			BDD innerRule = fact.zero();
			for (int j = 0; j < n; j++) {
				innerRule.orWith(fact.ithVar(getVarNumber(n, i, j)));
			}

			rule.andWith(innerRule);
		}
		return rule;
	}

	/**
	 * Return a game board.
	 * 
	 * @return board.
	 */
	public int[][] getGameBoard() {
		return board;
	}

	/**
	 * Method that inserts a queen in the board. The queen is marked as number
	 * 1, while invalid position is marked as -1. Neutral is marked as 0.
	 * 
	 * @param column
	 * @param row
	 * @return boolean
	 */
	public boolean insertQueen(int column, int row) {

		// position invalid
		if (board[column][row] == -1 || board[column][row] == 1) {
			return true;
		}

		board[column][row] = 1; // insert queen

		restricted = queens.restrict(getRestrictions()); // restrict game rules with
															// restrictions

		// boolean true if number of paths leading to the true terminal is 1.
		boolean finished = restricted.pathCount() == 1;

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				int varNum = getVarNumber(n, i, j); // get number of variables

				// if restricted BDD leads to state 0 (False), then add invalid
				// cell to the board.
				if (restricted.restrict(fact.ithVar(varNum)).isZero()) {
					board[i][j] = -1;
				}
				// if there only left one path to terminal state 1, add queen to
				// the board
				else if (finished) {
					board[i][j] = 1;
				}
			}
		}
		return true;
	}

	/**
	 * Gets number of variables in BDD of the cell
	 * 
	 * @param n
	 *            board size
	 * @param i
	 *            cell's column
	 * @param j
	 *            cell's row
	 * @return number of variables
	 */
	private int getVarNumber(int n, int i, int j) {
		return (j * n) + i;
	}

	/**
	 * Creates restrictions based on the state of the board
	 * 
	 * @return BDD of restrictions
	 */
	private BDD getRestrictions() {
		BDD res = fact.one();
		// for all the variables, make conjunction of the variables (i.e. their
		// constraints)
		for (int i = 0; i < numberOfVariables; i++) {
			// if the cell in the board has a queen add restriction rule
			if (board[getRowPosition(i)][getColumnPosition(i)] == 1) {
				res.andWith(fact.ithVar(i));
			}
		}
		return res;
	}

	/**
	 * Get row position of the variable in the board
	 * 
	 * @param index
	 * @return row position
	 */
	private int getRowPosition(int index) {
		return index % n;
	}

	/**
	 * Get column position of the variable in the board
	 * 
	 * @param index
	 * @return column position
	 */
	private int getColumnPosition(int index) {
		return index / n;
	}

	/**
	 * Prints the board.
	 */
	private void printBoard() {
		System.out.println("\t\tNew board state");
		for (int col = 0; col < board[0].length; col++) {
			String str = "";
			for (int j = 0; j < board.length; j++) {
				str += "\t" + board[j][col];
			}
			System.out.println(str + "\n");
		}
	}
}
