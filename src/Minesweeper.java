import java.util.ArrayList;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class Minesweeper extends Application
{
	Stage window;
	Board board;
	BorderPane gameLayout;
	boolean[][] marked;
	
	/**
	 * Sets up the GUI by launching the javafx window
	 * @param args
	 */
	public static void main(String[] args) 
	{
		launch(args);
	}

	/**
	 * Sets up the stage and starts the Gui
	 * @param primaryStage
	 * @throws Exception
	 */
	@Override
	public void start(Stage primaryStage) throws Exception 
	{
		window = primaryStage;
		
		window.setResizable(false);
		window.setWidth(700);
		window.setHeight(850);
		window.setScene(gameScene());
		window.setTitle("Minesweeper"); 
		window.show();
	}
	
	/**
	 * Returns a scene for the title screen
	 * @return Scene
	 */
	public Scene titleScene()
	{
		VBox layout = new VBox();
		
		Scene scene = new Scene(layout, 700, 750);
		return scene;
	}

	/**
	 * Returns a scene for the game
	 * @return Scene
	 */
	public Scene gameScene()
	{
		gameLayout = new BorderPane();
		marked = new boolean[10][10];
		
		board = new Board();
//		int[][] boardP = board.getBoard();
//		for(int r = 0; r < 16; r++) {
//			for(int c = 0; c < 30; c++) {
//				System.out.print(boardP[r][c] + " ");
//			}
//			System.out.println();
//		}
		
		gameLayout.setCenter(getBoardLayout(10, 10, 10, true));
		gameLayout.setPadding(new Insets(20, 20, 20, 20));
		
		Scene scene = new Scene(gameLayout, 700, 750);
		return scene;
	}

	/**
	 * Returns a scene for the end screen
	 * @return Scene
	 */
	public Scene endScene()
	{
		VBox layout = new VBox();
		
		
		Scene scene = new Scene(layout, 700, 750);
		return scene;
	}

	/**
	 * Generates a visual for the board based on the corresponding 2d array
	 * @return VBox
	 */
	public VBox getBoardLayout(int height, int width, int bombs, boolean firstClick)
	{
		boolean[][] clickedArr = board.getClicked();
		ArrayList<Button> buttons = new ArrayList<Button>();
		ArrayList<Label> labels = new ArrayList<Label>();
		
		int[][] boardArr;
		if(!firstClick) {
			boardArr = board.getBoard();
		}
		else {
			boardArr = new int[height][width];
		}
		
		HBox[] rows = new HBox[boardArr.length];
		for(int i = 0; i < rows.length; i++) {
			rows[i] = new HBox();
		}
		
		VBox layout = new VBox();
		for(int i = 0; i < rows.length; i++) {
			layout.getChildren().add(rows[i]);
		}
		
		for(int r = 0; r < boardArr.length; r++) {
			for(int c = 0; c < boardArr[0].length; c++) {
				if(!firstClick && clickedArr[r][c]) {
					labels.add(new Label());
					labels.get(labels.size() - 1).setText(boardArr[r][c] + "");
					labels.get(labels.size() - 1).setTextAlignment(TextAlignment.CENTER);
					labels.get(labels.size() - 1).setMinSize(660/boardArr[0].length, 660/boardArr.length);
					rows[r].getChildren().add(labels.get(labels.size() - 1));
				}
				else {
					final int row = r;
					final int col = c;
					buttons.add(new Button());
					Button b = buttons.get(buttons.size() - 1);
					b.setMinSize(660/boardArr[0].length, 660/boardArr.length);
					b.setTextAlignment(TextAlignment.CENTER);
					if(marked[row][col]) {
						b.setText("F");
					}
					b.setOnMouseClicked(e -> {
						if(e.getButton() == MouseButton.PRIMARY) {
							if(firstClick) {
								board.generateBoard(height, width, bombs, row, col, marked);
							}
							else {
								board.updateBoard(row, col, marked);
							}
							gameLayout.setCenter(getBoardLayout(height, width, bombs, false));
						}
						else if (e.getButton() == MouseButton.SECONDARY) {
							if(b.getText() == "") {
								b.setText("F");
								marked[row][col] = true;
							}
							else {
								b.setText("");
								marked[row][col] = false;
							}
						}
					});
					rows[r].getChildren().add(buttons.get(buttons.size() - 1));
				}
			}
		}
		
		return layout;
	}

}
