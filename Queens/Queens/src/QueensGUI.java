import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * This class creates the UI of the board and the mouse events listeners.
 * You do not have to change anything in this file. 
 * 
 * @author Stavros Amanatidis
 * 
 * */
public class QueensGUI extends JComponent implements MouseListener
{
	final static long serialVersionUID = 1234567890;
	private int 		chosenColumn;	// The value of the last column chosen by the user.
    private int         chosenRow;   // The value of the last row chosen by the user.
	private QueensLogic 	logic;			
	
	// Images for drawing the board
	private Image 		part, queen, invalid, backgroundW, backgroundB;
	private Image 		border_left,border_right,border_top,border_bottom;
	private Image 		corner_left_top, corner_left_bottom,corner_right_top,corner_right_bottom;
	
	/**
	 * @param logic The implementation of the game logic  
	 */
	public QueensGUI(QueensLogic logic)
	{
		part = Toolkit.getDefaultToolkit().getImage("imgs/maze.png");
		queen = Toolkit.getDefaultToolkit().getImage("imgs/queen.png");
		invalid = Toolkit.getDefaultToolkit().getImage("imgs/invalid.png");
		backgroundW = Toolkit.getDefaultToolkit().getImage("imgs/backgroundWhite.png");
        backgroundB = Toolkit.getDefaultToolkit().getImage("imgs/backgroundBlack.png");
		
		border_left = Toolkit.getDefaultToolkit().getImage("imgs/board_left.png");
		border_right = Toolkit.getDefaultToolkit().getImage("imgs/board_right.png");
		border_top = Toolkit.getDefaultToolkit().getImage("imgs/board_top.png");
		border_bottom = Toolkit.getDefaultToolkit().getImage("imgs/board_bottom.png");
		corner_left_top = Toolkit.getDefaultToolkit().getImage("imgs/corner_top_left.png");
		corner_left_bottom = Toolkit.getDefaultToolkit().getImage("imgs/corner_bottom_left.png");
		corner_right_top = Toolkit.getDefaultToolkit().getImage("imgs/corner_top_right.png");
		corner_right_bottom = Toolkit.getDefaultToolkit().getImage("imgs/corner_bottom_right.png");
		
		this.logic = logic;
		this.addMouseListener(this);
	}

	/*
	 * Draws the current game board and shows if someone won.
	 */
	public void paint(Graphics g){
		this.setDoubleBuffered(true);
		Insets in = getInsets();               
		g.translate(in.left, in.top);            

		int[][] gameboard = logic.getGameBoard();
		int cols = gameboard.length;
		int rows = gameboard[0].length;
		
        //draw borders
        for (int i = 0;i<cols;i++) {
            g.drawImage(border_left, 0, 100+100*i, this);
            g.drawImage(border_right, 100 + gameboard.length *100, 100+100*i, this); 
            g.drawImage(border_top, 100+100*i, 0, this);
            g.drawImage(border_bottom, 100+100*i, 100+gameboard.length*100, this);
        }

        //draw board
		for (int c = 0; c < cols; c++){
			for (int r = 0; r < rows; r++){
				int player = gameboard[c][r];
                
                if ((c+r)%2==0) g.drawImage(backgroundW, 100+100*c, 100+100*r, this);
                else g.drawImage(backgroundB, 100+100*c, 100+100*r, this);
                
                if (player == 1) // red = computer
					g.drawImage(queen, 100+100*c, 100+100*r, this);
				if (player == -1)//invalid
					g.drawImage(invalid, 100+100*c, 100+100*r, this);
				
                g.drawImage(part, 100+100*c, 100+100*r, this);
				
			}
		}
    
		//draw corners
		g.drawImage(corner_left_top, 0, 0, this);
		g.drawImage(corner_left_bottom, 0, 100+rows*100, this);
		g.drawImage(corner_right_top, 100+100*cols, 0, this);
		g.drawImage(corner_right_bottom, 100+100*cols, 100+rows*100, this);
		
 	}

	/*
	 * When the user clicks on one of the board cells, the corresponding
	 * column and row is kept and parsed to the logic. 
	 */
	public void mouseClicked(MouseEvent e){
        chosenColumn = e.getX()/100 - 1;
        chosenRow = e.getY()/100 - 1;
        
        //this method connects the UI with the logic
        int size = logic.getGameBoard().length;
		if ((chosenColumn >= 0) && (chosenColumn < size) && (chosenRow >= 0) && (chosenRow < size)) {
			
			//this is the main method that connects the UI with the Logic. 
			logic.insertQueen(chosenColumn, chosenRow);
			repaint();
			
		}
	}

	// Not used methods from the interface of MouseListener 
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mousePressed(MouseEvent e){}
	public void mouseReleased(MouseEvent e){}

}

