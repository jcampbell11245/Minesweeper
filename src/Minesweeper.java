import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Minesweeper extends Application
{
	Stage window; //A stage for the main window
	Board board; //The board object used to play the game
	BorderPane gameLayout; //The layout for the main game scene
	boolean[][] marked; //A boolean array showing which spaces have been marked by a right click
	DecimalFormat formatter; //A decimal format object used to format the time and remaining markers counters
	int bombs; //The number of bombs on the board
	int flags; //The number of flags left to place
	int time; //The time elapsed in the game
	Label numFlags; //A label showing the number of flags left
	Label timer; //A label showing the elapsed time
	Timeline timerIncrementor; //A timeline for incremented the timer label
	static Font digital; //A font for the digital clock text
	static Font pixel; //A font for the pixelated text
	
	/**
	 * Sets up the GUI by launching the javafx window
	 * @param args
	 */
	public static void main(String[] args) 
	{
		try 
		{
			digital = Font.loadFont(new FileInputStream("digital-7.TTF"), 70);
			pixel = Font.loadFont(new FileInputStream("pixelated.TTF"), 20);
		}
		catch (FileNotFoundException e) {}
		launch(args);
	}

	/**
	 * Sets up the stage and starts the Gui
	 * @param The primary stage of the Gui
	 * @throws Exception
	 */
	@Override
	public void start(Stage primaryStage) throws Exception 
	{
		window = primaryStage;
		
		window.setResizable(false);
		window.setWidth(700);
		window.setHeight(850);
		window.setScene(titleScene());
		window.setTitle("Minesweeper"); 
		window.show();
	}
	
	/**
	 * Returns a scene for the title screen
	 * @return Scene containing the title screen
	 */
	public Scene titleScene()
	{
		Label title = new Label("MINESWEEPER");
		title.setFont(digital);
		
		Button beginner = new Button("Beginner");
		beginner.setOnAction(e -> window.setScene(gameScene(9, 9, 10, 1)));
		//beginner.setOnAction(e -> endWindow(10, 10, 10, true, 2));
		beginner.setMinSize(200, 70);
		
		Button intermediate = new Button("Intermediate");
		intermediate.setOnAction(e -> window.setScene(gameScene(15, 13, 40, 2)));
		intermediate.setMinSize(200, 70);
		
		Button expert = new Button("Expert");
		expert.setOnAction(e -> window.setScene(gameScene(30, 16, 99, 3)));
		expert.setMinSize(200, 70);
		
		Button exit = new Button("Exit");
		exit.setOnAction(e -> window.close());
		exit.setMinSize(200, 70);
		
		VBox layout = new VBox();
		layout.getChildren().addAll(title, beginner, intermediate, expert, exit);
		layout.setSpacing(50);
		layout.setAlignment(Pos.CENTER);
		
		Scene scene = new Scene(layout, 700, 750);
		return scene;
	}

	/**
	 * Returns a scene for the game
	 * @param The height of the board
	 * @param The width of the board
	 * @param The number of bombs on the board
	 * @return Scene containing the game screen
	 * @param The difficulty the user was playing at: 1 is beginner, 2 is intermediate, 3 is expert
	 */
	public Scene gameScene(int height, int width, int bombs, int difficulty)
	{
		gameLayout = new BorderPane();
		marked = new boolean[height][width];
		this.bombs = bombs;
		flags = bombs;
		time = 0;
		board = new Board();
		
		gameLayout.setTop(getHudLayout());
		gameLayout.setCenter(getBoardLayout(height, width, bombs, true, board.getBoardState(), difficulty));
		gameLayout.setPadding(new Insets(20, 20, 20, 20));
		
		Scene scene = new Scene(gameLayout, 700, 750);
		return scene;
	}

	/**
	 * Displays a popup for the ending screen
	 * @param The height of the board
	 * @param The width of the board
	 * @param The number of bombs on the board
	 * @param Whether or not the user won
	 * @param The difficulty the user was playing at: 1 is beginner, 2 is intermediate, 3 is expert
	 */
	public void endWindow(int height, int width, int bombs, boolean win, int difficulty)
	{
		Stage end = new Stage();
		
		Label winMessage = new Label();
		if(win) {
			winMessage.setText("Congradulations! You Win!");
		}
		else {
			winMessage.setText("Sorry! You lose!");
		}
		
		Label scoreMessage = new Label();
		scoreMessage.setText("You're Score: " + time);
		
		Label highScoreMessage = new Label();
		if(win)
		{
			if(getHighScore(difficulty) > time) {
				highScoreMessage.setText("New High Score!");
				setHighScore(time, difficulty);
			}
			else {
				highScoreMessage.setText("High Score: " + getHighScore(difficulty));
			}
		}
		
		Button playAgain = new Button("Play Again");
		playAgain.setOnAction(e -> {
			window.setScene(gameScene(height, width, bombs, difficulty));
			end.close();
		});
		
		Button exit = new Button("Exit");
		exit.setOnAction(e -> {
			window.setScene(titleScene());
			end.close();
		});
		
		VBox layout = new VBox();
		if (win) {layout.getChildren().addAll(winMessage, scoreMessage, highScoreMessage, playAgain, exit);}
		else {layout.getChildren().addAll(winMessage, playAgain, exit);}
		layout.setSpacing(20);
		layout.setAlignment(Pos.CENTER);
		layout.setPadding(new Insets(20, 20, 20, 20));
		
		Scene scene = new Scene(layout, 250, 200);
		
		end.setScene(scene);
		end.setTitle("Game Over");
		end.setOnCloseRequest(e -> {
			window.close();
		});
		end.show();
	}

	/**
	 * Generates a visual for the board based on the corresponding 2d array
	 * @param The height of the board
	 * @param The width of the board
	 * @param The number of bombs on the board
	 * @param Whether or not the user has just clicked for the first time
	 * @param The state of the board: 0 if game still progressing, 1 if player blew up, 2 if all tiles cleared
	 * @param The difficulty the user was playing at: 1 is beginner, 2 is intermediate, 3 is expert
	 * @return VBox of the board
	 */
	public VBox getBoardLayout(int height, int width, int bombs, boolean firstClick, int boardState, int difficulty)
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
				if(!firstClick && clickedArr[r][c] || boardState == 1) { //Spaces that have been revealed
					labels.add(new Label());
					if (boardArr[r][c] != 9) {
						labels.get(labels.size() - 1).setText(boardArr[r][c] + "");
						labels.get(labels.size() - 1).setFont(pixel);
					}
					else {labels.get(labels.size() - 1).setText("💥");}
					labels.get(labels.size() - 1).setTextFill(getColor(boardArr[r][c]));
					labels.get(labels.size() - 1).setAlignment(Pos.CENTER);
					labels.get(labels.size() - 1).setMinSize(660/boardArr[0].length, 660/boardArr.length);
					rows[r].getChildren().add(labels.get(labels.size() - 1));
				}
				else { //Spaces that have not been revealed
					final int row = r;
					final int col = c;
					
					buttons.add(new Button());
					Button b = buttons.get(buttons.size() - 1);
					b.setMinSize(660/boardArr[0].length, 660/boardArr.length);
					b.setTextAlignment(TextAlignment.CENTER);
					if(marked[row][col]) {
						b.setText("🚩");
					}
					
					b.setOnMouseClicked(e -> {
						if(e.getButton() == MouseButton.PRIMARY) { //Left click
							if(firstClick) {
								board.generateBoard(height, width, bombs, row, col, marked);
								incTime();
							}
							else {
								board.updateBoard(row, col, marked);
							}
							gameLayout.setCenter(getBoardLayout(height, width, bombs, false, board.getBoardState(), difficulty));
						}
						else if (e.getButton() == MouseButton.SECONDARY) { //Right click
							if(b.getText() == "") { //Has not been marked
								if(flags > 0) {
									b.setText("🚩");
									marked[row][col] = true;
									flags--;
									numFlags.setText(formatter.format(flags));
								}
							}
							else { //Has been marked
								b.setText("");
								marked[row][col] = false;
								flags++;
								numFlags.setText(formatter.format(flags));
							}
						}
					});
					rows[r].getChildren().add(buttons.get(buttons.size() - 1));
				}
			}
		}
		
		if(!firstClick && boardState != 0) {
			timerIncrementor.stop();
			if(boardState == 1) { //Player lost
				endWindow(height, width, bombs, false, difficulty);
			}
			else { //Player won
				endWindow(height, width, bombs, true, difficulty);
			}
		}
		
		return layout;
	}
	
	/**
	 * Generates a visual for the hud that goes above the board
	 * @return HBox of the hud
	 */
	public HBox getHudLayout()
	{
		formatter = new DecimalFormat("000");
		formatter.format(bombs);
		formatter.format(time);
		
		numFlags = new Label();
		numFlags.setText(formatter.format(flags));
		numFlags.setFont(digital);
		numFlags.setMinSize(100, 100);
		
		timer = new Label();
		timer.setText(formatter.format(time));
		timer.setFont(digital);
		timer.setMinSize(100, 100);
		
		HBox layout = new HBox();
		layout.getChildren().addAll(numFlags, timer);
		layout.setSpacing(100);
		layout.setAlignment(Pos.CENTER);
		return layout;
	}
	
	/**
	 * Increments time once every second
	 */
	public void incTime()
	{
		//Takes in action at any point if all frogs make it to the goal or the player loses all their lives
	    timerIncrementor = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
	    	time++;
	    	timer.setText(formatter.format(time));
	    }));
	    timerIncrementor.setCycleCount(Animation.INDEFINITE);
	    timerIncrementor.play();
	}
	
	/**
	 * Returns a color based on an integer value
	 * @param The integer value to be assigned a color
	 * @return The color associated with the given integer value
	 */
	public Color getColor(int num)
	{
		if(num == 1) {return Color.BLUE;}
		if(num == 2) {return Color.GREEN;}
		if(num == 3) {return Color.RED;}
		if(num == 4) {return Color.DARKBLUE;}
		if(num == 5) {return Color.DARKRED;}
		if(num == 6) {return Color.CYAN;}
		if(num == 8) {return Color.DARKGRAY;}
		else {return Color.BLACK;}
	}
	
	/**
	 * Finds the high score for a given difficulty
	 * @param The difficulty that the high score is for: 1 is beginner, 2 is intermediate, 3 is expert
	 * @return int representing the high score
	 */
	public int getHighScore(int difficulty)
	{
		BufferedReader reader = null;
		try {reader = new BufferedReader(new FileReader("scores.txt"));} 
		catch (FileNotFoundException e) {}
		
		String score = "";
		for(int i = 0; i < difficulty; i++) {
			try {score = reader.readLine();} 
			catch (IOException e) {}
		}
		
		System.out.println(score + " " + difficulty);
		if(score != null && !score.equals("")) {return Integer.parseInt(score);}
		else {return Integer.MAX_VALUE;}
	}
	
	/**
	 * Sets a new high score for a given difficulty
	 * @param The new high score
	 * @param The difficulty that the high score is for: 1 is beginner, 2 is intermediate, 3 is expert
	 */
	public void setHighScore(int score, int difficulty)
	{
		BufferedWriter writer = null;
		try {writer = new BufferedWriter(new FileWriter("scores.txt"));} 
		catch (IOException e) {}
		
		int scores[] = {getHighScore(1), getHighScore(2), getHighScore(3)};
		
		//Cleares file and writes new scores
		try {writer.flush();} catch (IOException e) {}
		for(int i = 0; i < 3; i++) {
			if(i == difficulty - 1) {
				try {
					writer.write(score + "");
				} 
				catch (IOException e) {}
			}
			else {
				try {
					if(getHighScore(difficulty) != Integer.MAX_VALUE) {writer.write(getHighScore(i + 1) + "");}
				} 
				catch (IOException e) {}
			}
			
			if(i != 2) {
				try {writer.newLine();} 
				catch (IOException e) {}
			}
		}
		
		try {writer.close();} catch (IOException e) {}
	}
}
