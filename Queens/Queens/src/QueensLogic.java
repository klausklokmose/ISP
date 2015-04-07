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
	private int x = 0;
	private int y = 0;
	private int[][] board = null;
	private BDDFactory fact;
	private BDD boardRule;
	

	public QueensLogic() {
	 
	}

	/**
	 * Create a game board.
	 * 
	 * @param n
	 *            of the board is applied vertically and horizontally
	 */
	public void initializeGame(int n) {
		this.x = n;
		this.y = n;
		this.board = new int[x][y];
		
		fact = JFactory.init(2000000, 200000);
		fact.setVarNum(x*y);
		
		BDD True = fact.one();
		BDD False = fact.zero();
		int numVar = n*n;
		// ordered by x0 < x1 < x2 ... < x(n*n)-1
		boardRule = null;
		for(int i=0; i<numVar; i++){
			BDD rule = fact.ithVar(i);
			int start_horizontal = i % n;
			//horizontal loop
			for	(int j = 0; j < numVar; j+=n) {
				if(i!=j){
					//x_i...and not x_j
					rule = rule.and(fact.nithVar(j));
				}
			}
			int start_vertical = i-start_horizontal;
			//vertical loop
			for (int j = start_vertical; j < n; j++) {
				rule = rule.and(fact.nithVar(j));
			}
			if(boardRule == null){
				boardRule = rule;
			}else{
				boardRule=boardRule.and(rule);
			}
			
//			int start_diagonal_down = (i/n)*(i%n);
//			for (start_diagonal_down = i; start_diagonal_down < n; start_diagonal_down-=(n-1) );
		
			
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
		boardRule.forAll(boardRule.getFactory().v)
		
		
		
		
		printBoard();

		
		
		
		// put some logic here..

		
		
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
