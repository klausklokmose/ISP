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
	private int n = 0;
	private int[][] board = null;
	private BDD queens;
	private BDD restricted;
	private BDDFactory fact;
	private int numberOfVariables;

	// private BDD boardRule;

	public QueensLogic() {

	}

	/**
	 * Create a game board.
	 * 
	 * @param n of the board is applied vertically and horizontally
	 */
	public void initializeGame(int n) {
		this.n = n;
		this.board = new int[n][n];
		this.numberOfVariables = n * n;

		//initialize factory
		fact = JFactory.init(2_000_000, 200_000);
		fact.setVarNum(n * n);
		
		//The BDD - conjunctions of the implications Xij -> the rules of Xij
		queens = fact.one();
		//variables are changed to "constants" true/false during execution
		restricted = fact.one();
		
		// ordered by x0 < x1 < x2 ... < x(n*n)-1
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				queens.andWith(build(i, j));
			}
		}
	}

	public BDD build(int i, int j) {
		// horizontal loop
		BDD cell = fact.one();
		for (int l = 0; l < n; l++) {
			if (l != j) {
				cell = cell.and(fact.nithVar(getVarNumber(n, i, l)));
			}

			// vertical loop
			for (int k = 0; k < n; k++) {
				if (k != i) {
					cell = cell.and(fact.nithVar(getVarNumber(n, k, j)));
				}
			}

			// diagonal down
			for (int k = 0; k < n; k++) {
				int diag = j + k - i;
				if (diag >= 0 && diag < n) {
					if (k != i) {
						cell = cell.and(fact.nithVar(getVarNumber(n, k, diag)));
					}
				}
			}

			// diagonal up
			for (int k = 0; k < n; k++) {
				int diag = j + i - k;
				if (diag >= 0 && diag < n) {
					if (k != i) {
						cell = cell.and(fact.nithVar(getVarNumber(n, k, diag)));
					}
				}
			}

		}
		//there should only be one queen per row
			cell.andWith(oneQueenPerRow());
		return fact.ithVar(getVarNumber(n, i, j)).imp(cell);
	}

	public BDD oneQueenPerRow(){
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
	 * @return
	 */
	public boolean insertQueen(int column, int row) {

		// position invalid
		if (board[column][row] == -1 || board[column][row] == 1) {
			return true;
		}

		// insert queen
		board[column][row] = 1;

		restricted = queens.restrict(getRestrictions());
		boolean finished = restricted.pathCount() == 1;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				int varNum = getVarNumber(n, i, j);
				if(restricted.restrict(fact.ithVar(varNum)).isZero()){
					board[i][j] = -1;
				}else if(finished){
					board[i][j] = 1;
				}
			}
		}
		return true;
	}

	private int getVarNumber(int n, int i, int j) {
		return (j * n) + i;
	}

	private BDD getRestrictions(){
		BDD res = fact.one();
		//for all the variables, make conjunction of the variables (i.e. their constraints)
		for (int i = 0; i < numberOfVariables; i++) {
			if(board[getRowPosition(i)][getColumnPosition(i)] == 1){
				res.andWith(fact.ithVar(i));
			}
		}
		return res;
	}

	private int getRowPosition(int index){
		return index % n;
	}

	private int getColumnPosition(int index){
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
