import bagel.Input;
import bagel.Keys;

import java.util.ArrayList;

/**
 * Class representing Guardian
 */
public class Guardian extends Character{
    /**
     * Attributes representing guardian default x-coordinate
     */
    public final static double DEFAULT_X = 800;
    /**
     * Attributes representing guardian default y-coordinate
     */
    public final static double DEFAULT_Y = 600;
    private final ArrayList<Projectile> quiver = new ArrayList<>();

    /**
     *
     * @param charImage
     */
    public Guardian(String charImage) {
        super(charImage);
        super.setX(DEFAULT_X);
        super.setY(DEFAULT_Y);
    }

    @Override
    public void render() {
        super.getCharImage().draw(DEFAULT_X, DEFAULT_Y);
    }

    @Override
    public void reset() {
        quiver.clear();
    }

    /**
     * Method used for guardian to fire arrows
     * @param level This is corresponding level
     */
    public void fire(Level<Renderable> level) {
        Projectile newArrow = new Projectile("res/arrow.png");
        quiver.add(newArrow);
        level.targetArrow(newArrow);
    }

    @Override
    public void update(Input input, Level<Renderable> level) {
        if (isOnScreen(level)) {
            render();
        }

        // only fires arrows if an enemy is on-screen
        if (input.wasPressed(Keys.LEFT_SHIFT) && level.EnemyOnScreen()) {
            fire(level);
        }

        for (Projectile arrow : quiver) {
            arrow.update(input, level);
        }
    }

    @Override
    public boolean isOnScreen(Level<Renderable> level) {
        return !level.isTrackFin();
    }
}
