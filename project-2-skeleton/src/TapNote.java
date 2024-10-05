import bagel.Input;
import bagel.Keys;
import bagel.Window;

/**
 * Class representing Tap Notes
 */
public class TapNote extends Note {
    private final static double TAP_START = 100;
    private final static double TAP_LEN = 1;
    // tap note states and data
    private boolean hit = false;
    private String noteScore;
    private double hitFrame;
    private boolean stolen = false;

    /**
     *
     * @param noteImage
     * @param key
     * @param x
     * @param y
     * @param frameNum
     */
    public TapNote(String noteImage, Keys key, double x, double y, double frameNum) {
        super(noteImage, key, x, y, frameNum);
    }

    /**
     *
     * @param noteImage
     * @param key
     * @param x
     * @param frameNum
     */
    public TapNote(String noteImage, Keys key, double x, double frameNum) {
        super(noteImage, key, x, frameNum);
        super.setY(TAP_START);
    }

    /**
     * Activates notes stolen state
     */
    public void getStolen() {
        this.stolen = true;
    }

    @Override
    public void render() {
        super.getNoteImage().draw(super.getX(), super.getY());
        super.addY(noteSpeed);
    }

    @Override
    public void update(Input input, Level<Renderable> level, Lane lane, int playNote) {
        if (isOnScreen(level)) {
            render();
        }

        if (stolen && lane.getCurrNote() == playNote) {
            getHit(level, NO_SCORE);
        }

        if (!hit && lane.getCurrNote() == playNote &&
                input.wasPressed(super.getKey())) {
            double dist = getDist(Lane.LANE_END, super.getY());
            if (dist <= ScoreCriteria.PERFECT.dist) {
                noteScore = "PERFECT";
                getHit(level, ScoreCriteria.PERFECT.points);
            } else if (dist <= ScoreCriteria.GOOD.dist &&
                    dist > ScoreCriteria.PERFECT.dist) {
                noteScore = "GOOD";
                getHit(level, ScoreCriteria.GOOD.points);
            } else if (dist <= ScoreCriteria.BAD.dist &&
                    dist > ScoreCriteria.GOOD.dist) {
                noteScore = "BAD";
                getHit(level, ScoreCriteria.BAD.points);
            } else if (dist <= ScoreCriteria.MISS.dist &&
                    dist > ScoreCriteria.BAD.dist) {
                noteScore = "MISS";
                getHit(level, ScoreCriteria.MISS.points);
            }
        }

        if (!hit && super.getY() > Window.getHeight()) {
            noteScore = "MISS";
            getHit(level, ScoreCriteria.MISS.points);
        }

        if (hit) {
            scoreMessage(level.getFrameCount());
            inputDelay(lane, level.getFrameCount());
        }
    }

    @Override
    public void reset() {
        super.setY(TAP_START);
        hit = false;
        noteScore = null;
        hitFrame = 0;
        stolen = false;
    }

    @Override
    public boolean isOnScreen(Level<Renderable> level) {
       return !hit && !stolen && level.getFrameCount() >= super.getFrameNum();
    }

    @Override
    public void scoreMessage(double frameCount) {
        if (noteScore != null && frameCount < hitFrame + MESSAGE_TIME) {
            super.MESSAGE_FONT.drawString(noteScore, Window.getWidth() / 2.0
                    - noteScore.length() * 16, Window.getHeight() / 2.0);
        }
    }

    @Override
    public void inputDelay(Lane lane, double frameCount) {
        if (!stolen && frameCount == hitFrame + INPUT_DELAY) {
            lane.advanceCurrNote(TAP_LEN);
        } else if (stolen && frameCount == hitFrame + NO_DELAY) {
            lane.advanceCurrNote(TAP_LEN);
        }
    }

    @Override
    public void getHit(Level<Renderable> level, int points) {
        hitFrame = level.getFrameCount();
        level.advLevel(points, TAP_LEN);
        hit = true;
    }
}
