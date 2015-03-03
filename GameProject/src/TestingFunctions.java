import java.util.Arrays;
import java.util.PriorityQueue;


public class TestingFunctions {

	private static int[][] board;
	private static PriorityQueue<Action> queueOne;
	private static int turns = 0;
	private final static int noRows = 6;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		board = new int[4][4];
		
//		queueOne = new PriorityQueue<>();
//		queueOne.add(new Pair(0, 2));
//		queueOne.add(new Pair(0, 1));
//		queueOne.add(new Pair(1, 1));
//		insertCoin(0, 1);
//		insertCoin(0, 2);
//		insertCoin(0, 1);
//		insertCoin(0, 1);
//		printBoard();
//		printQueueOne();
		
		Action[] pairs_h = new Action[]{new Action(0, 5), new Action(0, 4), new Action(0, 3), 
								new Action(1, 5), new Action(1, 4), new Action(1, 3),
								new Action(2, 5), new Action(2, 3), 
								new Action(3, 4)};
//		Pair[] pairs_v = new Pair[]{new Pair(0, 5), new Pair(0, 3), new Pair(0, 2), 
//				new Pair(1, 5), new Pair(1, 4), new Pair(1, 3),new Pair(1, 2)};
		findMatch(pairs_h, 1);
	}

	private static int findMatch(Action[] actions, int playerID) {
		int v_column = 0;
		int v_barrier = 4;
		int v_lastRow = -1;
		
		int[] h_barrier = new int[noRows];
		int[] h_last = new int[noRows];
		for (int i = 0; i < h_last.length; i++) {
			h_last[i] = -1; //init
			h_barrier[i] = 4;
		}
		
		for (Action action : actions) {
//			Action p = a;
			int r = action.getRow();
			int c = action.getColumn();
			System.out.println("LOOKING AT ACTION"+action);
			
			//VERTICAL
			if(c != v_column){ 	//reset vertical
				System.out.println("reset vertical");
				v_column = c;
				v_barrier = 4;
				v_lastRow = -1;
			}
		
			if(v_lastRow == -1){
				System.out.println("set row");
				v_lastRow = r;
			}

			int v_diff = v_lastRow - r;
			if(v_diff == 1 || v_diff == 0){
				System.out.println("VERTICAL: decrease barrier");
				v_lastRow = r;
				v_barrier--;
			}else{
				v_lastRow = -1;
			}
			
			if(v_barrier == 0){
				System.out.println("found 4 coins!! VERTICAL in column "+v_column);
				return 1;
			}
			
			
			//HORIZONTAL
			if(h_last[r] == -1){
				System.out.println("inital h_how at row="+r);
				h_last[r] = r;
			}
			
			int h_diff = h_last[r]-r;
			if(h_diff == 0){
				h_last[r] = r;
				h_barrier[r]--;
			}else{ //reset
				h_last[r] = -1;
				h_barrier[r] = 4;
			}
			
			if(h_barrier[r]==0){
				System.out.println("found 4 coins!! HORIZONTAL in row "+r);
				return 1;
			}
			
			
			//DIAGONAL running time = noPairs*4
			Action[] d_up = new Action[]{new Action(c+1, r-1), new Action(c+2, r-2), new Action(c+3, r-3)};
			Action[] d_dwn = new Action[]{new Action(c+1, r+1), new Action(c+2, r+2), new Action(c+3, r+3)};
			int upBarrier, dwnBarrier;
			upBarrier = dwnBarrier = 4-1; //4 coins minus 1 (the one we are looking at)
			
			for (Action a : actions) {
				int iRow = a.getRow();
				int iColumn = a.getColumn();
				for (int j = 0; j < d_up.length; j++) {
					if( iColumn == d_up[j].getColumn() && iRow == d_up[j].getRow()){
						upBarrier--;
						System.out.println("DIAGONAL UP: decrease barrier: "+upBarrier+"\t"+a);
						break;
					}else if(iColumn == d_dwn[j].getColumn() && iRow == d_dwn[j].getRow() ){
						dwnBarrier--;
						System.out.println("DIAGONAL DWN: decrease barrier: "+dwnBarrier+"\t"+a);
						break;
					}
				}
				if(upBarrier == 0 || dwnBarrier == 0){ //early return! we do not need to iterate more
					System.out.println("FOUND DIAGONAL early MATCH");
					return 1;
				}
			}
			if(upBarrier == 0 || dwnBarrier == 0){
				System.out.println("FOUND DIAGONAL MATCH"+action+", "+Arrays.toString(d_up));
				return 1;
			}
			System.out.println();
			
		}//END LOOP THROUGH 
		
		
		System.out.println("NO LUCK...");
		return -2;
	}
	
	
	private static void printBoard() {
		System.out.println("........turn "+turns+".......");
		for (int i = 0; i < board.length; i++) {
			String str = "";
			for (int j = 0; j < board.length; j++) {
				str += " "+board[j][i];
			}
			System.out.println(str);
		}
	}

	public static void insertCoin(int column, int playerID) {
		// TODO Write your implementation for this method
		int[] col = board[column];
		//[x, y] column, row
		Action p = null ;
		//the column is non-empty
		if (col[0] == 0) {
			boolean inserted = false;
			for (int row = 0; row < col.length; row++) {
				if (col[row] != 0) {
					col[row - 1] = playerID;
					inserted = true;
					p = new Action(column, row-1);
					break;
				}
			}
			//the column must be empty
			if (!inserted) {
				col[col.length - 1] = playerID;
				p = new Action(column, col.length-1);
				inserted = true;
			}
		}else{
			System.out.println("you can't insert a coin here, because it is full!");
		}
		//Testing print queues
		if(playerID == 1){
			queueOne.add(p);
		}
	}

	private static void printQueueOne() {
		for (Action pa : queueOne) {
			System.out.println(pa);
		}
	}
	
}
