import java.util.ArrayList;
import java.util.Random;

public class Board 
{
	private int[][] board;
	private boolean[][] clicked;
	private boolean dead;
	private int numOpened;
	private int tiles;
	private int bombs;
	
	/**
	 * Sets up the board object
	 */
	public Board()
	{
		dead = false;
		numOpened = 0;
	}
	
	/**
	 * Randomly generates the board based on the size, number of bombs and where 
	 * and how the user clicked
	 * @param height
	 * @param widith
	 * @param row
	 * @param col
	 * @param click
	 */
	public void generateBoard(int height, int width, int bombs, int row, int col, boolean[][] marked)
	{
		tiles = height * width;
		this.bombs = bombs;
		
		board = new int[height][width];
		clicked = new boolean[height][width];
		
		//Places bombs at unique random spots on the board (other than the spot
		//where the user clicked)
		Random rand = new Random();
		int r1;
		int r2;
		ArrayList<String> takenSpots = new ArrayList<String>();
		for(int i = 0; i < bombs; i++) {
			r1 = rand.nextInt(height);
			r2 = rand.nextInt(width);
			while(takenSpots.contains(r1 + " " + r2) || r1 == row && r2 == col) {
				r1 = rand.nextInt(height);
				r2 = rand.nextInt(width);
			}
			
			board[r1][r2] = 9;
			takenSpots.add(r1 + " " + r2);
		}
		
		//Counts and stores the number of bombs in each squares area
		int numBombs;
		for(int r = 0; r < height; r++) {
			for (int c = 0; c < width; c++) {
				if(board[r][c] != 9) {
					numBombs = 0;
					
					if(r > 0 && c > 0 && board[r - 1][c - 1] == 9) {numBombs++;}
					if(r > 0 && board[r - 1][c] == 9) {numBombs++;}
					if(r > 0 && c < width - 1 && board[r - 1][c + 1] == 9) {numBombs++;}
					if(c > 0 && board[r][c - 1] == 9) {numBombs++;}
					if(c < width - 1 && board[r][c + 1] == 9) {numBombs++;}
					if(r < height - 1 && c > 0 && board[r + 1][c - 1] == 9) {numBombs++;}
					if(r < height - 1 && board[r + 1][c] == 9) {numBombs++;}
					if(r < height - 1 && c < width - 1 && board[r + 1][c + 1] == 9) {numBombs++;}
					
					board[r][c] = numBombs;
				}
			}
		}
		
		updateBoard(row, col, marked);
	}

	/**
	 * Updates the board based on where the user clicked
	 * @param row
	 * @param col
	 * @param click
	 */
	public void updateBoard(int row, int col, boolean[][] marked)
	{
		if(!marked[row][col]) {
			numOpened++;
			clicked[row][col] = true;
			
			int state = board[row][col];
		
			if(state != 9) {
				if(state == 0) {
					if(row > 0 && col > 0 && !clicked[row - 1][col - 1]) {updateBoard(row - 1, col - 1, marked);}
					if(row > 0 && !clicked[row - 1][col]) {updateBoard(row - 1, col, marked);}
					if(row > 0 && col < board[0].length - 1 && !clicked[row - 1][col + 1]) {updateBoard(row - 1, col + 1, marked);}
					if(col > 0 && !clicked[row][col - 1]) {updateBoard(row, col - 1, marked);}
					if(col < board[0].length - 1 && !clicked[row][col + 1]) {updateBoard(row, col + 1, marked);}
					if(row < board.length - 1 && col > 0 && !clicked[row + 1][col - 1]) {updateBoard(row + 1, col - 1, marked);}
					if(row < board.length - 1 && !clicked[row + 1][col]) {updateBoard(row + 1, col, marked);}
					if(row < board.length - 1 && col < board[0].length - 1 && !clicked[row + 1][col + 1]) {updateBoard(row + 1, col + 1, marked);}
				}
			}
			else {
				dead = true;
			}
		}
	}

	/**
	 * Returns the 2d array for the board
	 * @return 
	 */
	public int[][] getBoard()
	{
		return board;
	}
	
	/**
	 * Returns the 2d array for the spaces on the board that have been clicked
	 * @return
	 */
	public boolean[][] getClicked()
	{
		return clicked;
	}

	/**
	 * Returns the state of the board, 0 if game still progressing, 1 if player
	 * blew up, 2 if all tiles cleared
	 * @return
	 */
	public int getBoardState()
	{
		if(dead) {return 1;}
		
		if(numOpened == tiles - bombs) {return 2;}
		
		return 0;
	}
}
