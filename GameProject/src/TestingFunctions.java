import java.util.List;



public class TestingFunctions {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GameLogicKlaus game = new GameLogicKlaus();
		game.initializeGame(4, 4, 1);
		
//		queueOne.add(new Pair(0, 2));
//		queueOne.add(new Pair(0, 1));
//		queueOne.add(new Pair(1, 1));
//		game.insertCoin(1, 2);
//		game.insertCoin(0, 2);
//		game.insertCoin(0, 2);
		game.insertCoin(0, 1);
//		System.out.println(game.max_value(game.result(game.board, new Action(0, 2), 1), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 3));
//		System.out.println(game.max_value(game.result(game.board, new Action(1, 3), 1), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 3));
//		System.out.println(game.max_value(game.result(game.board, new Action(2, 3), 1), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 3));
//		System.out.println(game.max_value(game.result(game.board, new Action(3, 3), 1), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 3));
//		game.insertCoin(1, 1);
//		game.insertCoin(1, 2);
//		game.insertCoin(1, 1);
//		game.insertCoin(1, 2);
//		
//		game.insertCoin(2, 2);
//		game.insertCoin(2, 2);
//		game.insertCoin(2, 1);
//		game.insertCoin(2, 1);
//		
//		game.insertCoin(3, 1);
//		game.insertCoin(3, 2);
//		game.insertCoin(3, 1);
//		game.insertCoin(3, 2);
//		System.out.println();
//		List<Action> l = GameLogicKlaus.actions(game.board);
//		for (int i = 0; i < l.size(); i++) {
//			System.out.println(l.get(i));
//		}
//		System.out.println(game.utility(game.board));
		System.out.println("NEW EVAL: "+game.NEWEVAL(game.board));
		
		game.printBoard();
	}
	
}
