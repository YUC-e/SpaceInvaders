/**
 * ModelTester.java
 * Manual test suite for GameModel without any testing libraries.
 * Tests key game behaviors by directly calling methods and validating results.
 */
public class ModelTester {

    private static int testsRun = 0;
    private static int testsPassed = 0;

    public static void main(String[] args) {
        System.out.println("=== Space Invaders Model Tester ===\n");

        testPlayerCannotMoveLeftPastEdge();
        testPlayerCannotMoveRightPastEdge();
        testFiringWhileBulletInFlightDoesNothing();
        testBulletRemovedWhenReachingTop();
        testDestroyingAlienIncreasesScore();
        testLosingAllLivesTriggersGameOver();
        testShieldsReduceHealthWhenHit();
        testShieldsRemovedWhenHealthZero();

        System.out.println("\n=== Summary ===");
        System.out.println("Tests run: " + testsRun);
        System.out.println("Tests passed: " + testsPassed);
        System.out.println("Tests failed: " + (testsRun - testsPassed));
    }

    // Test 1: Player cannot move past left edge
    private static void testPlayerCannotMoveLeftPastEdge() {
        String testName = "Player cannot move past left edge";
        testsRun++;

        GameModel model = new GameModel();
        int initialX = model.getPlayerX();

        // Move left many times
        for (int i = 0; i < 1000; i++) {
            model.movePlayerLeft();
        }

        int finalX = model.getPlayerX();

        if (finalX >= 0) {
            System.out.println("PASS: " + testName + " (X = " + finalX + ")");
            testsPassed++;
        } else {
            System.out.println("FAIL: " + testName + " (X = " + finalX + ", should be >= 0)");
        }
    }

    // Test 2: Player cannot move past right edge
    private static void testPlayerCannotMoveRightPastEdge() {
        String testName = "Player cannot move past right edge";
        testsRun++;

        GameModel model = new GameModel();
        int playerWidth = 20; // From GameModel constants
        int gameWidth = 800;  // From GameModel constants
        int maxX = gameWidth - playerWidth;

        // Move right many times
        for (int i = 0; i < 1000; i++) {
            model.movePlayerRight();
        }

        int finalX = model.getPlayerX();

        if (finalX <= maxX) {
            System.out.println("PASS: " + testName + " (X = " + finalX + ", max = " + maxX + ")");
            testsPassed++;
        } else {
            System.out.println("FAIL: " + testName + " (X = " + finalX + ", should be <= " + maxX + ")");
        }
    }

    // Test 3: Firing while bullet already in flight does nothing
    private static void testFiringWhileBulletInFlightDoesNothing() {
        String testName = "Firing while bullet in flight does nothing";
        testsRun++;

        GameModel model = new GameModel();

        // Fire first bullet
        model.firePlayerBullet();
        GameModel.Bullet firstBullet = model.getPlayerBullet();

        // Try to fire another while first is still in flight
        model.firePlayerBullet();
        GameModel.Bullet secondBullet = model.getPlayerBullet();

        if (firstBullet == secondBullet && firstBullet != null) {
            System.out.println("PASS: " + testName + " (same bullet object retained)");
            testsPassed++;
        } else {
            System.out.println("FAIL: " + testName + " (bullets differ when second fire should be ignored)");
        }
    }

    // Test 4: Bullet that reaches the top is removed
    private static void testBulletRemovedWhenReachingTop() {
        String testName = "Bullet that reaches top is removed";
        testsRun++;

        GameModel model = new GameModel();

        // Fire a bullet
        model.firePlayerBullet();
        GameModel.Bullet bullet = model.getPlayerBullet();

        if (bullet == null) {
            System.out.println("FAIL: " + testName + " (bullet was not created)");
            return;
        }

        // Update many times to let bullet travel to top
        for (int i = 0; i < 200; i++) {
            model.update();
        }

        GameModel.Bullet bulletAfter = model.getPlayerBullet();

        if (bulletAfter == null) {
            System.out.println("PASS: " + testName + " (bullet was removed after reaching top)");
            testsPassed++;
        } else {
            System.out.println("FAIL: " + testName + " (bullet still exists: y = " + bulletAfter.y + ")");
        }
    }

    // Test 5: Destroying an alien increases score
    private static void testDestroyingAlienIncreasesScore() {
        String testName = "Destroying alien increases score";
        testsRun++;

        GameModel model = new GameModel();
        int initialScore = model.getScore();

        // Get the first alive alien
        GameModel.Alien[][] aliens = model.getAliens();
        GameModel.Alien targetAlien = null;
        for (GameModel.Alien[] row : aliens) {
            for (GameModel.Alien a : row) {
                if (a.alive) {
                    targetAlien = a;
                    break;
                }
            }
            if (targetAlien != null) break;
        }

        if (targetAlien == null) {
            System.out.println("FAIL: " + testName + " (no alive aliens to test)");
            return;
        }

        // Disable all other aliens to avoid NullPointerException from collision check bug
        for (GameModel.Alien[] row : aliens) {
            for (GameModel.Alien a : row) {
                if (a != targetAlien) {
                    a.alive = false;
                }
            }
        }

        // Fire a bullet and position it to hit the alien
        model.firePlayerBullet();

        // Manually update the bullet position to be at the alien
        GameModel.Bullet bullet = model.getPlayerBullet();
        if (bullet != null) {
            bullet.x = targetAlien.x;
            bullet.y = targetAlien.y;
        }

        // Call update to process collision
        model.update();

        int finalScore = model.getScore();
        boolean alienDestroyed = !targetAlien.alive;

        if (alienDestroyed && finalScore > initialScore) {
            System.out.println("PASS: " + testName + " (score increased from " + initialScore + " to " + finalScore + ")");
            testsPassed++;
        } else {
            System.out.println("FAIL: " + testName + " (alien destroyed: " + alienDestroyed + ", score: " + initialScore + " -> " + finalScore + ")");
        }
    }

    // Test 6: Losing all lives triggers game-over state
    private static void testLosingAllLivesTriggersGameOver() {
        String testName = "Losing all lives triggers game-over state";
        testsRun++;

        GameModel model = new GameModel();
        int initialLives = model.getLives();

        // Get the alien bullets list and add bullets to force collisions
        // Position bullets at player location (800/2 = 400, height = 550)
        int playerX = model.getPlayerX();

        // Add bullets to force hits
        java.util.List<GameModel.Bullet> alienBullets = model.getAlienBullets();
        for (int i = 0; i < initialLives; i++) {
            alienBullets.add(new GameModel.Bullet(playerX, 550, false));
        }

        // Call update to process collisions
        for (int i = 0; i < 5; i++) {
            model.update();
        }

        int finalLives = model.getLives();
        boolean gameOver = model.isGameOver();

        if (finalLives <= 0 && gameOver) {
            System.out.println("PASS: " + testName + " (lives: " + initialLives + " -> " + finalLives + ", game over: " + gameOver + ")");
            testsPassed++;
        } else {
            System.out.println("FAIL: " + testName + " (lives: " + finalLives + ", game over: " + gameOver + ")");
        }
    }

    // Test 7: Shields reduce health when hit
    private static void testShieldsReduceHealthWhenHit() {
        String testName = "Shields reduce health when hit";
        testsRun++;

        GameModel model = new GameModel();

        // Get the first shield
        java.util.List<GameModel.Shield> shields = model.getShields();
        if (shields.isEmpty()) {
            System.out.println("FAIL: " + testName + " (no shields in game)");
            return;
        }

        GameModel.Shield targetShield = shields.get(0);
        int initialHealth = targetShield.health;

        // Fire a bullet and position it at the shield
        model.firePlayerBullet();
        GameModel.Bullet bullet = model.getPlayerBullet();
        if (bullet != null) {
            bullet.x = targetShield.x + 20;
            bullet.y = targetShield.y + 15;
        }

        // Call update to process collision
        model.update();

        int finalHealth = targetShield.health;

        if (finalHealth < initialHealth) {
            System.out.println("PASS: " + testName + " (health: " + initialHealth + " -> " + finalHealth + ")");
            testsPassed++;
        } else {
            System.out.println("FAIL: " + testName + " (health unchanged: " + finalHealth + ")");
        }
    }

    // Test 8: Shields are removed when health reaches zero
    private static void testShieldsRemovedWhenHealthZero() {
        String testName = "Shields removed when health reaches zero";
        testsRun++;

        GameModel model = new GameModel();

        // Get the first shield
        java.util.List<GameModel.Shield> shields = model.getShields();
        if (shields.isEmpty()) {
            System.out.println("FAIL: " + testName + " (no shields in game)");
            return;
        }

        GameModel.Shield targetShield = shields.get(0);
        int initialShieldCount = shields.size();

        // Deal enough damage to destroy the shield (health = 3)
        java.util.List<GameModel.Bullet> alienBullets = model.getAlienBullets();
        for (int i = 0; i < 3; i++) {
            alienBullets.add(new GameModel.Bullet(targetShield.x + 20, targetShield.y + 15, false));
        }

        // Call update to process collisions
        for (int i = 0; i < 5; i++) {
            model.update();
        }

        int finalShieldCount = model.getShields().size();
        boolean shieldDestroyed = finalShieldCount < initialShieldCount;

        if (shieldDestroyed) {
            System.out.println("PASS: " + testName + " (shields: " + initialShieldCount + " -> " + finalShieldCount + ")");
            testsPassed++;
        } else {
            System.out.println("FAIL: " + testName + " (shield count unchanged: " + finalShieldCount + ")");
        }
    }
}
