import bagel.Font;
import bagel.Input;
import bagel.Window;
import bagel.util.Point;
import bagel.util.Vector2;
import java.util.ArrayList;

/**
 * Class representing Levels
 */
public class Level<T extends Renderable>{
    private final TargetScores levelID;
    private int frameCount = 0;
    private final Font SCORE_FONT = new Font("res/FSO8BITR.ttf", 30);
    private final int targetScore;
    private int score = 0;
    /**
     * Attribute representing default score multiplier
     */
    public final static int DEFAULT_MULTIPLIER = 1;
    /**
     * Attribute used to multiply note score points
     */
    public static int scoreMultiplier = DEFAULT_MULTIPLIER;
    private double trackLen;
    private double trackPoint = 0;
    /**
     * Attribute representing enemy spawn rates
     * (60Hz screen value halved)
     */
    public final static double ENEMY_SPAWN = 300;
    /**
     * Attribute used to relocate off-screen assets
     */
    public final static double OFF_SCREEN_ASSET = Math.max(Window.getWidth(), Window.getHeight()) + 1;
    private final ArrayList<T> assets = new ArrayList<>();

    /**
     *
     * @param levelID
     */
    public Level(TargetScores levelID) {
        this.levelID = levelID;
        this.targetScore = levelID.targetScore;

        if (levelID.equals(TargetScores.LEVEL3)) {
            Guardian guardian = new Guardian("res/guardian.png");
            addEntity((T) guardian);
        }
    }

    /**
     *
     * @return frameCount
     */
    public int getFrameCount() {
        return frameCount;
    }

    /**
     *
     * @param trackLen
     */
    public void setTrackLen(double trackLen) {
        this.trackLen = trackLen;
    }

    /**
     * Method used to add entities to level assets
     * @param entity
     */
    public void addEntity(T entity) {
        assets.add(entity);
    }

    /**
     * Method used to add to the score and advance level progression
     * @param add Points to add to accumulated score
     * @param adv Amount you advance level
     */
    public void advLevel(int add, double adv) {
        score += add * scoreMultiplier;
        trackPoint += adv;
    }

    /**
     * Method used to check if level is finished
     * @return boolean state of level
     */
    public boolean isTrackFin() {
        return trackPoint == trackLen;
    }

    /**
     * Method used to check if player has beat the level
     * @return boolean
     */
    public boolean winCondition() {
        if (!isTrackFin()) {
            return false;
        }
        return score >= targetScore;
    }

    /**
     * Method used to check if player has lost the level
     * @return boolean
     */
    public boolean loseCondition() {
        if (!isTrackFin()) {
            return false;
        }
        return score < targetScore;
    }

    private String scoreString() {
        return "SCORE " + score;
    }

    /**
     * Method that plays level by updating level state
     * @param input This is player input
     */
    public void play(Input input) {
        frameCount++;

        SCORE_FONT.drawString(scoreString(), 35, 35);

        // update level assets; lanes, characters
        for (T asset : assets) {
            if (asset instanceof Lane) {
                ((Lane) asset).update(input, (Level<Renderable>) this);
            } else if (asset instanceof Character) {
                ((Character) asset).update(input, (Level<Renderable>) this);
            }
        }

        // spawns enemies for level 3
        if (levelID.equals(TargetScores.LEVEL3)) {
            if (frameCount % ENEMY_SPAWN == 0) {
                Enemy newEnemy = new Enemy("res/enemy.png");
                addEntity((T) newEnemy);
            }
        }
    }

    /**
     * Method used to reset level to initial state
     */
    public void reset() {
        frameCount = 0;
        score = 0;
        trackPoint = 0;
        Note.noteSpeed = Note.DEFAULT_SPEED;
        scoreMultiplier = DEFAULT_MULTIPLIER;
        for (T asset : assets) {
            asset.reset();
        }
        assets.removeIf(asset -> asset instanceof Enemy);
    }

    /**
     * Method used to check if there is an enemy on-screen
     * @return boolean
     */
    public boolean EnemyOnScreen() {
        return assets.stream().anyMatch(asset -> asset instanceof Enemy &&
                ((Enemy) asset).isOnScreen((Level<Renderable>) this));
    }

    /**
     * Method used to bomb a lane
     * @param bombLane This is the bombed lane
     */
    public void bombEvent(Lane bombLane) {
        for (T asset : assets) {
            // finds lane by space in memory
            if (asset.equals(bombLane)) {
                Lane selectLane = (Lane) asset;
                selectLane.bombLane((Level<Renderable>) this);
                break;
            }
        }
    }

    /**
     * Method used to check if enemy is within range of lanes to steal
     * @param enemy This is the enemy to check
     */
    public void stealEvent(Enemy enemy) {
        for (T asset: assets) {
            if (asset instanceof Lane) {
                Lane selectLane = (Lane) asset;
                selectLane.stealNote((Level<Renderable>) this, enemy);
            }
        }
    }

    /**
     * Method used to target arrow to nearest enemy
     * @param arrow This is the new arrow to fire
     */
    public void targetArrow(Projectile arrow) {
        // finds nearest enemy relative to guardian
        Point arrowPoint = new Point(arrow.getX(), arrow.getY());
        Point minEnemyPoint = new Point();
        double minDist = Double.MAX_VALUE;
        for (T asset: assets) {
            if (asset instanceof Enemy && ((Enemy) asset).isOnScreen((Level<Renderable>) this)) {
                Enemy currEnemy = (Enemy) asset;
                Point currEnemyPoint = new Point(currEnemy.getX(), currEnemy.getY());
                double dist = arrowPoint.distanceTo(currEnemyPoint);
                if (dist < minDist) {
                    minDist = dist;
                    minEnemyPoint = currEnemyPoint;
                }
            }
        }
        // calculate and assign target values; rotation, x-speed, y-speed
        Vector2 arrowDir = minEnemyPoint.asVector().sub(arrowPoint.asVector());
        double angleBuff = (minEnemyPoint.x >= Guardian.DEFAULT_X) ? 0 : Math.PI;
        double angle = Math.atan(arrowDir.y / arrowDir.x) + angleBuff;
        arrow.setRotation(angle);
        arrow.setProjectileDirX(Projectile.PROJECTILE_SPEED * Math.cos(angle));
        arrow.setProjectileDirY(Projectile.PROJECTILE_SPEED * Math.sin(angle));
    }

    /**
     * Method used to check if arrow has hit an enemy
     * @param arrow This is the arrow to check
     */
    public void arrowEvent(Projectile arrow) {
        Point arrowPoint = new Point(arrow.getX(), arrow.getY());
        for (T asset: assets) {
            if (asset instanceof Enemy && ((Enemy) asset).isOnScreen((Level<Renderable>) this)) {
                Enemy currEnemy = (Enemy) asset;
                Point currEnemyPoint = new Point(currEnemy.getX(), currEnemy.getY());
                double dist = arrowPoint.distanceTo(currEnemyPoint);
                if (dist <= Projectile.PROJECTILE_HIT_BOX) {
                    currEnemy.getHit();
                    arrow.getHit();
                }
            }
        }
    }
}
