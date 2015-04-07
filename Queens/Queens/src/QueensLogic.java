/**
 * This class implements the logic behind the BDD for the n-queens problem
 * You should implement all the missing methods
 * 
 * @author Stavros Amanatidis
 *
 */
import java.util.*;
import net.sf.javabdd.*;

public class QueensLogic {
	private int x = 0;
	private int y = 0;
	private int[][] board = null;

	public QueensLogic() {
		
		// Initialize T table of ROBDD
		// initialize H table of ROBDD
		// perhaps these tables could be some sort of Objects.
		
		// consider board as x*y amount of variables  
	}

	/**
	 * Create a game board.
	 * 
	 * @param size
	 *            of the board is applied vertically and horizontally
	 */
	public void initializeGame(int size) {
		this.x = size;
		this.y = size;
		this.board = new int[x][y];
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
