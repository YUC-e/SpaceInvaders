import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.util.List;

/**
 * GameView.java
 * This class extends JPanel and handles the rendering of the Space Invaders game.
 * It is responsible for drawing the game elements such as invaders, player, bullets,
 * score, and any other visual components on the screen.
 * This view only reads from the model and never modifies game state.
 */
public class GameView extends JPanel {
    private GameModel model;

    // Constructor to initialize the view with a reference to the model
    public GameView(GameModel model) {
        this.model = model;
        setBackground(Color.BLACK);
    }

    // Setter to update the model reference (used for game reset)
    public void setModel(GameModel newModel) {
        this.model = newModel;
    }

    // Override paintComponent to draw the game
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw player
        drawPlayer(g);

        // Draw aliens
        drawAliens(g);

        // Draw shields
        drawShields(g);

        // Draw player bullet
        drawPlayerBullet(g);

        // Draw alien bullets
        drawAlienBullets(g);

        // Draw UI (score and lives)
        drawUI(g);

        // Draw game-over message if game is over
        if (isGameOver()) {
            drawGameOverMessage(g);
        }
    }

    private void drawPlayer(Graphics g) {
        g.setColor(Color.GREEN);
        int playerX = model.getPlayerX();
        g.fillRect(playerX, 550, 20, 20);
        // Draw outline for better visibility
        g.setColor(Color.LIGHT_GRAY);
        g.drawRect(playerX, 550, 20, 20);
    }

    private void drawAliens(Graphics g) {
        g.setColor(Color.RED);
        for (GameModel.Alien[] row : model.getAliens()) {
            for (GameModel.Alien a : row) {
                if (a.alive) {
                    g.fillRect(a.x, a.y, 20, 20);
                    // Draw outline for better visibility
                    g.setColor(Color.WHITE);
                    g.drawRect(a.x, a.y, 20, 20);
                    g.setColor(Color.RED);
                }
            }
        }
    }

    private void drawShields(Graphics g) {
        for (GameModel.Shield s : model.getShields()) {
            // Color based on health: green (3), yellow (2), red (1)
            if (s.health == 3) {
                g.setColor(Color.GREEN);
            } else if (s.health == 2) {
                g.setColor(Color.YELLOW);
            } else {
                g.setColor(Color.RED);
            }
            g.fillRect(s.x, s.y, 40, 30);
            // Draw outline
            g.setColor(Color.WHITE);
            g.drawRect(s.x, s.y, 40, 30);
        }
    }

    private void drawPlayerBullet(Graphics g) {
        GameModel.Bullet playerBullet = model.getPlayerBullet();
        if (playerBullet != null) {
            g.setColor(Color.YELLOW);
            g.fillRect(playerBullet.x - 2, playerBullet.y - 5, 4, 10);
        }
    }

    private void drawAlienBullets(Graphics g) {
        List<GameModel.Bullet> alienBullets = model.getAlienBullets();
        g.setColor(Color.ORANGE);
        for (GameModel.Bullet b : alienBullets) {
            g.fillRect(b.x - 1, b.y - 5, 2, 10);
        }
    }

    private void drawUI(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("Score: " + model.getScore(), 10, 20);
        g.drawString("Lives: " + model.getLives(), 10, 40);
    }

    private void drawGameOverMessage(Graphics g) {
        // Determine game-over reason
        String message = model.getLives() <= 0 ? "GAME OVER - YOU LOST" : "YOU WIN!";
        String finalScore = "Final Score: " + model.getScore();

        // Set font and color for message
        g.setColor(new Color(0, 0, 0, 200)); // Semi-transparent black background
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 48));

        // Calculate position to center the message
        int messageWidth = g.getFontMetrics().stringWidth(message);
        int x = (getWidth() - messageWidth) / 2;
        int y = (getHeight() / 2) - 40;

        g.drawString(message, x, y);

        // Draw final score below the main message
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        int scoreWidth = g.getFontMetrics().stringWidth(finalScore);
        int scoreX = (getWidth() - scoreWidth) / 2;
        g.drawString(finalScore, scoreX, y + 60);
    }

    private boolean isGameOver() {
        return model.isGameOver();
    }
}