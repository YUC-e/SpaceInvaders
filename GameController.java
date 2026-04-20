import javax.swing.JFrame;

/**
 * GameController.java
 * This class acts as the controller for the Space Invaders game.
 * It contains the main method to start the application, creates the JFrame,
 * instantiates the GameModel and GameView, and wires them together.
 * It handles user input and updates the model and view accordingly.
 */
public class GameController {
    private GameModel model;
    private GameView view;
    private JFrame frame;

    // Constructor to wire the model and view
    public GameController() {
        model = new GameModel();
        view = new GameView();
        // Placeholder for wiring: pass model to view if needed
        // e.g., view.setModel(model);

        frame = new JFrame("Space Invaders");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(view);
        frame.setSize(800, 600);
        frame.setVisible(true);
    }

    // Main method to start the game
    public static void main(String[] args) {
        new GameController();
    }
}