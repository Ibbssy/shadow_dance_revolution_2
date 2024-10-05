import bagel.Font;
import bagel.Image;
import bagel.Input;
import bagel.Keys;

/**
 * Class representing Note characteristics
 */
public abstract class Note implements Renderable{
    private final Image noteImage;
    private final Keys key;
    /**
     * Attribute representing default note speed
     * (60Hz screen: value doubled)
     */
    public final static double DEFAULT_SPEED = 4;
    /**
     * Attribute representing note speed for ALL NOTES
     */
    public static double noteSpeed = DEFAULT_SPEED;
    private double x;
    private double y;
    // Attribute representing when note appears on-screen
    private final double frameNum;
    /**
     * Attribute representing delay values for inputs
     */
    public final static double INPUT_DELAY = 2;
    public final static double NO_DELAY = 0;

    /**
     * Enum representing note scoring distance and points
     */
    protected enum ScoreCriteria {
        PERFECT (15, 10),
        GOOD (50, 5),
        BAD (100, -1),
        MISS (200, -5),
        SPEC (50, 15);

        final int dist;
        final int points;
        ScoreCriteria(int dist, int points) {
            this.dist = dist;
            this.points = points;
        }
    }

    /**
     * Attribute representing no score for note hit
     */
    public final static int NO_SCORE = 0;

    /**
     * Attribute used for temporary score messages on-screen
     * (60Hz screen: value halved)
     */
    protected final Font MESSAGE_FONT = new Font("res/FSO8BITR.ttf", 40);
    protected final static int MESSAGE_TIME = 15;

    /**
     *
     * @param noteImage
     * @param key
     * @param x
     * @param y
     * @param frameNum
     */
    public Note(String noteImage, Keys key, double x, double y, double frameNum) {
        this.noteImage = new Image(noteImage);
        this.key = key;
        this.x = x;
        this.y = y;
        this.frameNum = frameNum;
    }

    /**
     *
     * @param noteImage
     * @param key
     * @param x
     * @param frameNum
     */
    public Note(String noteImage, Keys key, double x, double frameNum) {
        this.noteImage = new Image(noteImage);
        this.key = key;
        this.x = x;
        this.frameNum = frameNum;
    }

    /**
     *
     * @return Keys
     */
    public Keys getKey() {
        return key;
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
        y += add;
    }

    /**
     *
     * @return Image
     */
    public Image getNoteImage() {
        return noteImage;
    }

    /**
     *
     * @return frameNum
     */
    public double getFrameNum() {
        return frameNum;
    }

    /**
     * Method used to update note state
     * @param input This is player input
     * @param level This is corresponding level
     * @param lane This is corresponding lane
     * @param playNote This is index of next note played
     */
    public abstract void update(Input input, Level<Renderable> level, Lane lane, int playNote);

    /**
     * Method used to get absolute distance between two points
     * @param p1 This is point one
     * @param p2 This is point two
     * @return double This is the distance
     */
    public double getDist(double p1, double p2) {
        return Math.abs(p2 - p1);
    }

    /**
     * Method used to check if note is on-screen
     * @param level This is corresponding level
     * @return boolean
     */
    public abstract boolean isOnScreen(Level<Renderable> level);

    /**
     * Method used to temporarily display score message
     * @param frameCount This is frameCount of level
     */
    public abstract void scoreMessage(double frameCount);

    /**
     * Method used to delay note progression
     * @param lane This is corresponding lane
     * @param frameCount This is frameCount of level
     */
    public abstract void inputDelay(Lane lane, double frameCount);

    /**
     * Method used to change note state to hit and add score points
     * @param level This is corresponding level
     * @param points This is points to add
     */
    public abstract void getHit(Level<Renderable> level, int points);
}
