import javax.swing.JFrame;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

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
    private Timer gameLoop;

    // Constructor to wire the model and view
    public GameController() {
        model = new GameModel();
        view = new GameView(model);

        frame = new JFrame("Space Invaders");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(view);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setFocusable(true);

        // Set up key listener for player input
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e.getKeyCode());
            }
        });

        // Set up game loop (50ms = 20 FPS)
        gameLoop = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.update();
                view.repaint();
            }
        });
        gameLoop.start();
    }

    private void handleKeyPress(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_LEFT:
                model.movePlayerLeft();
                break;
            case KeyEvent.VK_RIGHT:
                model.movePlayerRight();
                break;
            case KeyEvent.VK_SPACE:
                model.firePlayerBullet();
                break;
        }
    }

    // Main method to start the game
    public static void main(String[] args) {
        new GameController();
    }
}