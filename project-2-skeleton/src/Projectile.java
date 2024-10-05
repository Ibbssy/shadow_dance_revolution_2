import bagel.DrawOptions;
import bagel.Input;
import bagel.Window;

/**
 * Class representing Projectiles
 */
public class Projectile extends Character{
    /**
     * Attribute representing projectile speed
     * (60Hz screen: value doubled)
     */
    public final static double PROJECTILE_SPEED = 12;
    private double projectileDirX;
    private double projectileDirY;
    private double rotation;
    /**
     * Attribute representing projectile hit box
     */
    public final static double PROJECTILE_HIT_BOX = 62;
    private boolean hit = false;

    /**
     *
     * @param charImage
     */
    public Projectile(String charImage) {
        super(charImage);
        super.setX(Guardian.DEFAULT_X);
        super.setY(Guardian.DEFAULT_Y);
    }

    /**
     *
     * @param projectileDirX
     */
    public void setProjectileDirX(double projectileDirX) {
        this.projectileDirX = projectileDirX;
    }

    /**
     *
     * @param projectileDirY
     */
    public void setProjectileDirY(double projectileDirY) {
        this.projectileDirY = projectileDirY;
    }

    /**
     *
     * @param rotation
     */
    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    /**
     * Method used to change projectile state to hit
     */
    public void getHit() {
        this.hit = true;
    }

    @Override
    public void render() {
        DrawOptions drawOpt = new DrawOptions();
        super.getCharImage().draw(super.getX(), super.getY(), drawOpt.setRotation(rotation));
        super.addX(projectileDirX);
        super.addY(projectileDirY);
    }

    @Override
    public void reset() {
        super.setX(Guardian.DEFAULT_X);
        super.setY(Guardian.DEFAULT_Y);
        projectileDirX = -PROJECTILE_SPEED;
        projectileDirY = -PROJECTILE_SPEED;
        rotation = 0;
        hit = false;
    }

    /**
     *
     * @param level
     */
    public void collisionCheck(Level<Renderable> level) {
        level.arrowEvent(this);
    }

    @Override
    public void update(Input input, Level<Renderable> level) {
        if (isOnScreen(level)) {
            render();
            collisionCheck(level);
        } else {
            // arrow hits edge of screen
            hit = true;
        }
    }

    @Override
    public boolean isOnScreen(Level<Renderable> level) {
        // check if arrow has not reached edge of window
        return !hit && super.getX() >= 0 && super.getX() <= Window.getWidth() &&
                super.getY() >= 0 && super.getY() <= Window.getHeight();
    }
}
