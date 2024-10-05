import bagel.Image;
import bagel.Input;
import bagel.Window;
import bagel.util.Point;
import java.util.ArrayList;

/**
 * Class representing Lanes
 */
public class Lane implements Renderable{
    private final Image laneImage;
    /**
     * Attribute representing where notes start
     */
    public final static double LANE_START = 384;
    /**
     * Attribute representing centre of note symbol
     */
    public final static double LANE_END = 657;
    private double x;
    private double y;
    private final ArrayList<Note> notes = new ArrayList<>();
    private double currNote = 0;

    /**
     *
     * @param laneImage
     * @param x
     * @param y
     */
    public Lane(String laneImage, double x, double y) {
        this.laneImage = new Image(laneImage);
        this.x = x;
        this.y = y;
    }

    /**
     *
     * @param laneImage
     */
    public Lane(String laneImage) {
        this.laneImage = new Image(laneImage);
        this.x = 0;
        this.y = LANE_START;
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
     * Method used to return current note rounded down to nearest integer
     * @return int
     */
    public int getCurrNote() {
        // return to nearest index
        return (int) Math.floor(currNote);
    }

    /**
     * Method that advances lane current note
     * @param advance Length to advance note progression
     */
    public void advanceCurrNote(double advance) {
        // compares within index representation
        if ((int) Math.floor(currNote) < notes.size()) {
            currNote += advance;
        } else {
            System.out.println("Error: lane has reached end");
        }
    }

    /**
     * Method to add note to lane
     * @param note Note to add
     */
    public void addNote(Note note) {
        notes.add(note);
    }

    /**
     * Method used to render lane
     */
    @Override
    public void render() {
        laneImage.draw(x,y);
    }

    // everything lane does in an update

    /**
     * Method used to update lane state
     * @param input This is player input
     * @param level This is corresponding level
     */
    public void update(Input input, Level<Renderable> level) {
        render();
        // update lane notes
        for (Note note : notes) {
            note.update(input, level, this, notes.indexOf(note));
        }
    }

    /**
     * Method used to reset lane to initial state
     */
    @Override
    public void reset() {
        currNote = 0;
        for (Note note : notes) {
            note.reset();
        }
    }

    /**
     * Method used to clear lane of all on-screen notes
     * @param level This is corresponding level
     */
    public void bombLane(Level<Renderable> level) {
        for (Note note : notes) {
            if (note.isOnScreen(level)) {
                note.getHit(level, Note.NO_SCORE);
                // moves any on-screen special notes off-screen
                if (note instanceof SpecialNote) {
                    note.setY(Level.OFF_SCREEN_ASSET);
                }
            }
        }
    }

    /**
     * Method that checks if enemy is within range and steals normal notes
     * @param level This is corresponding level
     * @param enemy This is the enemy to check
     */
    public void stealNote(Level<Renderable> level, Enemy enemy) {
        Point enemyPoint = new Point(enemy.getX(), enemy.getY());
        for (Note note : notes) {
            if (note.isOnScreen(level) && !(note instanceof SpecialNote)) {
                Point notePoint = new Point(note.getX(), note.getY());
                double dist = enemyPoint.distanceTo(notePoint);
                if (dist <= Enemy.ENEMY_HIT_BOX) {
                    if (note instanceof TapNote) {
                        ((TapNote) note).getStolen();
                    } else if (note instanceof HoldNote) {
                        ((HoldNote) note).getStolen();
                    }
                }
            }
        }
    }
}
