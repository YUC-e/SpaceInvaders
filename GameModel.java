import java.util.*;

/**
 * GameModel.java
 * This class handles the game logic for Space Invaders.
 * It manages the state of the game, including positions of invaders,
 * the player, bullets, score, and game rules.
 * No Swing imports are used here to keep the model separate from the view.
 */
public class GameModel {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int PLAYER_WIDTH = 20;
    private static final int ALIEN_WIDTH = 20;
    private static final int ALIEN_HEIGHT = 20;
    private static final int PLAYER_SPEED = 5;
    private static final int BULLET_SPEED = 10;
    private static final int ALIEN_MOVE_SPEED = 2;
    private static final int ALIEN_DROP = 10;

    private int playerX = WIDTH / 2;
    private Alien[][] aliens = new Alien[5][11];
    private Bullet playerBullet = null;
    private List<Bullet> alienBullets = new ArrayList<>();
    private int score = 0;
    private int lives = 3;
    private int alienDirection = 1;
    private Random random = new Random();

    public GameModel() {
        // Initialize alien formation
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 11; col++) {
                aliens[row][col] = new Alien(50 + col * 50, 50 + row * 30);
            }
        }
    }

    public void movePlayerLeft() {
        if (playerX > 0) {
            playerX -= PLAYER_SPEED;
        }
    }

    public void movePlayerRight() {
        if (playerX < WIDTH - PLAYER_WIDTH) {
            playerX += PLAYER_SPEED;
        }
    }

    public void firePlayerBullet() {
        if (playerBullet == null) {
            playerBullet = new Bullet(playerX + PLAYER_WIDTH / 2, HEIGHT - 50, true);
        }
    }

    public void update() {
        // Advance player bullet
        if (playerBullet != null) {
            playerBullet.y -= BULLET_SPEED;
            if (playerBullet.y < 0) {
                playerBullet = null;
            }
        }

        // Advance alien bullets
        List<Bullet> toRemove = new ArrayList<>();
        for (Bullet b : alienBullets) {
            b.y += BULLET_SPEED / 2; // Slower alien bullets
            if (b.y > HEIGHT) {
                toRemove.add(b);
            }
        }
        alienBullets.removeAll(toRemove);

        // Move aliens
        boolean hitEdge = false;
        for (Alien[] row : aliens) {
            for (Alien a : row) {
                if (a.alive) {
                    a.x += alienDirection * ALIEN_MOVE_SPEED;
                    if (a.x <= 0 || a.x >= WIDTH - ALIEN_WIDTH) {
                        hitEdge = true;
                    }
                }
            }
        }
        if (hitEdge) {
            for (Alien[] row : aliens) {
                for (Alien a : row) {
                    if (a.alive) {
                        a.y += ALIEN_DROP;
                    }
                }
            }
            alienDirection = -alienDirection;
        }

        // Fire alien bullet randomly
        if (random.nextInt(100) < 2) { // 2% chance per update
            List<Alien> aliveAliens = new ArrayList<>();
            for (Alien[] row : aliens) {
                for (Alien a : row) {
                    if (a.alive) {
                        aliveAliens.add(a);
                    }
                }
            }
            if (!aliveAliens.isEmpty()) {
                Alien shooter = aliveAliens.get(random.nextInt(aliveAliens.size()));
                alienBullets.add(new Bullet(shooter.x + ALIEN_WIDTH / 2, shooter.y + ALIEN_HEIGHT, false));
            }
        }

        // Check collisions
        // Player bullet vs aliens
        if (playerBullet != null) {
            for (Alien[] row : aliens) {
                for (Alien a : row) {
                    if (a.alive && Math.abs(a.x - playerBullet.x) < ALIEN_WIDTH && Math.abs(a.y - playerBullet.y) < ALIEN_HEIGHT) {
                        a.alive = false;
                        playerBullet = null;
                        score += 10;
                        break;
                    }
                }
            }
        }

        // Alien bullets vs player
        toRemove.clear();
        for (Bullet b : alienBullets) {
            if (!b.isPlayer && Math.abs(b.x - playerX) < PLAYER_WIDTH && Math.abs(b.y - (HEIGHT - 50)) < 20) {
                lives--;
                toRemove.add(b);
            }
        }
        alienBullets.removeAll(toRemove);
    }

    // Getters for view access
    public int getPlayerX() {
        return playerX;
    }

    public Alien[][] getAliens() {
        return aliens;
    }

    public Bullet getPlayerBullet() {
        return playerBullet;
    }

    public List<Bullet> getAlienBullets() {
        return alienBullets;
    }

    public int getScore() {
        return score;
    }

    public int getLives() {
        return lives;
    }

    public boolean isGameOver() {
        if (lives <= 0) {
            return true; // Player lost all lives
        }

        // Check if all aliens are destroyed
        for (Alien[] row : aliens) {
            for (Alien a : row) {
                if (a.alive) {
                    return false; // At least one alien still alive
                }
            }
        }

        return true; // All aliens destroyed - player won
    }

    // Inner classes
    public static class Alien {
        public int x, y;
        public boolean alive = true;

        public Alien(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public static class Bullet {
        public int x, y;
        public boolean isPlayer;

        public Bullet(int x, int y, boolean isPlayer) {
            this.x = x;
            this.y = y;
            this.isPlayer = isPlayer;
        }
    }
}