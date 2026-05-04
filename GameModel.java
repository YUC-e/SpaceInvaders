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
    private static final int ALIEN_MOVE_SPEED = 1;
    private static final int ALIEN_DROP = 10;
    private static final int SHIELD_WIDTH = 40;
    private static final int SHIELD_HEIGHT = 15;
    private static final int SHIELD_HEALTH = 3;
    private static final int POWERUP_SPEED = 2;
    private static final int POWERUP_RADIUS = 8;

    private int playerX = WIDTH / 2;
    private Alien[][] aliens = new Alien[5][11];
    private Bullet playerBullet = null;
    private List<Bullet> alienBullets = new ArrayList<>();
    private List<PowerUp> powerUps = new ArrayList<>();
    private List<Shield> shields = new ArrayList<>();
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

        // Initialize shields positioned between aliens and player
        int shieldSpacing = WIDTH / 5;
        for (int i = 0; i < 4; i++) {
            shields.add(new Shield(shieldSpacing * (i + 1) - SHIELD_WIDTH / 2, 350, SHIELD_HEALTH));
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

        // Advance power-ups
        List<PowerUp> powerUpsToRemove = new ArrayList<>();
        for (PowerUp p : powerUps) {
            p.y += POWERUP_SPEED;
            if (p.y > HEIGHT) {
                powerUpsToRemove.add(p);
            }
        }
        powerUps.removeAll(powerUpsToRemove);

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
            boolean bulletHit = false;
            for (Alien[] row : aliens) {
                for (Alien a : row) {
                    if (a.alive && Math.abs(a.x - playerBullet.x) < ALIEN_WIDTH && Math.abs(a.y - playerBullet.y) < ALIEN_HEIGHT) {
                        a.alive = false;
                        playerBullet = null;
                        score += 10;
                        // 30% chance to drop power-up when alien is killed
                        if (random.nextInt(100) < 30) {
                            powerUps.add(new PowerUp(a.x + ALIEN_WIDTH / 2, a.y + ALIEN_HEIGHT));
                        }
                        bulletHit = true;
                        break;
                    }
                }
                if (bulletHit) break;
            }
        }

        // Player bullet vs shields
        if (playerBullet != null) {
            boolean shieldHit = false;
            for (Shield s : shields) {
                if (Math.abs(s.x + SHIELD_WIDTH / 2 - playerBullet.x) < SHIELD_WIDTH / 2 && 
                    Math.abs(s.y + SHIELD_HEIGHT / 2 - playerBullet.y) < SHIELD_HEIGHT / 2) {
                    s.health--;
                    playerBullet = null;
                    shieldHit = true;
                    break;
                }
            }
        }

        // Alien bullets vs shields
        toRemove.clear();
        List<Shield> shieldsToRemove = new ArrayList<>();
        for (Bullet b : alienBullets) {
            boolean bulletHit = false;
            for (Shield s : shields) {
                if (Math.abs(s.x + SHIELD_WIDTH / 2 - b.x) < SHIELD_WIDTH / 2 && 
                    Math.abs(s.y + SHIELD_HEIGHT / 2 - b.y) < SHIELD_HEIGHT / 2) {
                    s.health--;
                    toRemove.add(b);
                    bulletHit = true;
                    break;
                }
            }
        }
        alienBullets.removeAll(toRemove);

        // Remove destroyed shields
        for (Shield s : shields) {
            if (s.health <= 0) {
                shieldsToRemove.add(s);
            }
        }
        shields.removeAll(shieldsToRemove);

        // Alien bullets vs player
        toRemove.clear();
        for (Bullet b : alienBullets) {
            if (!b.isPlayer && Math.abs(b.x - playerX) < PLAYER_WIDTH && Math.abs(b.y - (HEIGHT - 50)) < 20) {
                lives--;
                toRemove.add(b);
            }
        }
        alienBullets.removeAll(toRemove);

        // Power-ups vs player
        powerUpsToRemove.clear();
        for (PowerUp p : powerUps) {
            if (Math.abs(p.x - (playerX + PLAYER_WIDTH / 2)) < PLAYER_WIDTH + POWERUP_RADIUS && 
                Math.abs(p.y - (HEIGHT - 50)) < 20 + POWERUP_RADIUS) {
                // Player collected power-up
                powerUpsToRemove.add(p);
            }
        }
        powerUps.removeAll(powerUpsToRemove);
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

    public List<Shield> getShields() {
        return shields;
    }

    public List<PowerUp> getPowerUps() {
        return powerUps;
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

    public static class Shield {
        public int x, y;
        public int health;

        public Shield(int x, int y, int health) {
            this.x = x;
            this.y = y;
            this.health = health;
        }
    }

    public static class PowerUp {
        public int x, y;

        public PowerUp(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}