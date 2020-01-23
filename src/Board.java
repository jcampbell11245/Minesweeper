public class Board 
{
	private int[][] board;
	
	/**
	 * Randomly generates the board based on the size and where and how the user clicked
	 * @param widith
	 * @param height
	 * @param row
	 * @param col
	 * @param click
	 */
	public void generateBoard(int width, int height, int row, int col, String click)
	{
		board = new int[height][width];
	}

	/**
	 * Updates the board based on where and how the user clicked
	 * @param row
	 * @param col
	 * @param click
	 */
	public void updateBoard(int row, int col, String click)
	{
		
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
	 * Returns the state of the board, 0 if game still progressing, 1 if player blew up, 2 if all tiles cleared
	 * @return
	 */
	public int getBoardState()
	{
		int state = 0;
		
	
		
		return state;
	}
}
