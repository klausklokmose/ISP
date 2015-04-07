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
	private BDD[][] boardRule;
	private BDD bdd;
	private BDDFactory fact;
	private int numVar;

	// private BDD boardRule;

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
		this.boardRule = new BDD[n][n];
		this.numVar = n * n;

		fact = JFactory.init(2000000, 200000);
		fact.setVarNum(n * n);

		// init bdd
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				boardRule[i][j] = fact.ithVar(i * n + j);
			}
		}
		// ordered by x0 < x1 < x2 ... < x(n*n)-1
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				System.out.println("build: ["+i+", "+j+"]");
				build(i, j);
			}
		}
		

	}

	public void build(int i, int j){
			BDD cell = boardRule[i][j];
			
			// horizontal loop
			for (int l = 0; l < n; l++) {
				if (l != j) {
					cell = cell.andWith(boardRule[i][l].apply(cell, BDDFactory.nand));
				}
			}
			
			// vertical loop
			for (int k = 0; k < n; k++) {
				if(k != i){
					cell = cell.andWith(boardRule[k][j].apply(cell, BDDFactory.nand));
				}
			}
			
			//diagonal down
			for (int k = 0; k < n; k++) {
				int diag = j + k - i;
				if(diag >= 0 && diag < n){
					if(k != i){
						cell = cell.andWith(boardRule[k][diag].apply(cell, BDDFactory.nand));
					}
				}
			}
			
			//diagonal up
			for (int k = 0; k < n; k++) {
				int diag = j + i - k;
				if(diag >= 0 && diag < n){
					if(k != i){
						cell = cell.andWith(boardRule[k][diag].apply(cell, BDDFactory.nand));
					}
				}
			}
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
		printBoard();

		//TODO put some logic here..
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if(board[i][j] == 1){
					int varNum = i * n + j;
					
					BDD restriction = fact.ithVar(varNum);
					boardRule[i][j].restrict(restriction);
				}
			}
		}
		
		
		return true;
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
