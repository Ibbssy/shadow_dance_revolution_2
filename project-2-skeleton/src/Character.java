import bagel.Image;
import bagel.Input;

/**
 * Class representing game Character
 */
public abstract class Character implements Renderable{
    private final Image charImage;
    private double x;
    private double y;

    /**
     *
     * @param charImage
     */
    public Character(String charImage) {
        this.charImage = new Image(charImage);
    }

    /**
     *
     * @return Image
     */
    public Image getCharImage() {
        return charImage;
    }

    /**
     *
     * @return double
     */
    public double getX() {
        return x;
    }

    /**
     *
     * @param x
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     *
     * @param add
     */
    public void addX(double add) {
        this.x += add;
    }

    /**
     *
     * @return double
     */
    public double getY() {
        return y;
    }

    /**
     *
     * @param y
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     *
     * @param add
     */
    public void addY(double add) {
        this.y += add;
    }

    /**
     * Method used to update character state
     * @param input This is player input
     * @param level This is corresponding level
     */
    public abstract void update(Input input, Level<Renderable> level);

    /**
     * Method used to check if note is on-screen
     * @param level this is corresponding level
     * @return boolean
     */
    public abstract boolean isOnScreen(Level<Renderable> level);
}
