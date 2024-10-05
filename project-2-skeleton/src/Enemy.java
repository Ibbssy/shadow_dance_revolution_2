import bagel.Input;
import bagel.util.Point;
import java.util.Random;

/**
 * Class representing Enemy
 */
public class Enemy extends Character{
    private final static double ENEMY_XY_LB = 100;
    private final static double ENEMY_X_UB = 900;
    private final static double ENEMY_Y_UB = 500;
    // enemy speed (60Hz screen: value doubled)
    private final static double ENEMY_SPEED = 2;
    private double enemyMove;
    /**
     * Attribute representing enemy hit box
     */
    public final static double ENEMY_HIT_BOX = 104;
    private boolean hit = false;

    /**
     *
     * @param charImage
     */
    public Enemy(String charImage) {
        super(charImage);
        Point newPoint = genRandPos();
        super.setX(newPoint.x);
        super.setY(newPoint.y);
        enemyMove = genStartMovement();
    }

    /**
     * Method used to change enemy state to hit
     */
    public void getHit() {
        this.hit = true;
    }

    @Override
    public void render() {
        super.getCharImage().draw(super.getX(), super.getY());
        super.addX(enemyMove);
        if (super.getX() <= ENEMY_XY_LB) {
            enemyMove = ENEMY_SPEED;
        } else if (super.getX() >= ENEMY_X_UB) {
            enemyMove = -ENEMY_SPEED;
        }
    }

    @Override
    public void reset() {
        hit = false;
    }

    /**
     * Method used to generate random spawn point for enemy
     * @return Point
     */
    private static Point genRandPos() {
        Random rd = new Random();
        return new Point(ENEMY_XY_LB + (ENEMY_X_UB - ENEMY_XY_LB) * rd.nextDouble(),
                ENEMY_XY_LB + (ENEMY_Y_UB - ENEMY_XY_LB) * rd.nextDouble());
    }

    /**
     * Method used to randomly choose movement; left/right
     * @return double
     */
    private static double genStartMovement() {
        Random rd = new Random();
        return (rd.nextBoolean()) ? ENEMY_SPEED : -ENEMY_SPEED;
    }

    /**
     * Method that continuously checks for collisions by calling steal event
     * @param level This is corresponding level
     */
    public void collisionCheck(Level<Renderable> level) {
        level.stealEvent(this);
    }

    @Override
    public void update(Input input, Level<Renderable> level) {
        if (isOnScreen(level)) {
            render();
            collisionCheck(level);
        }
    }

    @Override
    public boolean isOnScreen(Level<Renderable> level) {
        return !hit;
    }
}
