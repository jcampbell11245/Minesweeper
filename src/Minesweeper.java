import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Minesweeper extends Application
{
	Stage window;
	
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
		window.setScene(titleScene());
		window.setTitle("Checkers"); 
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
		Pane layout = new Pane();
		
		Scene scene = new Scene(layout, 700, 750);
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
	public VBox getBoardLayout()
	{
		VBox board = new VBox();
		
		return board;
	}

}
