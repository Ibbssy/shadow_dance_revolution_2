import bagel.Input;
import bagel.Keys;
import bagel.Window;

/**
 * Class representing Special Notes
 */
public class SpecialNote extends Note {
    private final static double SPEC_START = 100;
    private final static double SPEC_LEN = 1;
    // duration of timed effects (60Hz screens value halved)
    private final static double EFFECT_DUR = 240;
    // special note states and data
    private boolean hit = false;
    private String noteScore;
    private double hitFrame;

    /**
     * Enum representing special note types
     */
    public enum SpecTypes {
        DOUBLE_SCORE,
        SPEED_UP,
        SLOW_DOWN,
        BOMB;
    }

    private final SpecTypes type;
    private boolean effectActive = false;

    /**
     *
     * @param noteImage
     * @param key
     * @param x
     * @param y
     * @param frameNum
     * @param type
     */
    public SpecialNote(String noteImage, Keys key, double x, double y, double frameNum, SpecTypes type) {
        super(noteImage, key, x, y, frameNum);
        this.type = type;
    }

    /**
     *
     * @param noteImage
     * @param key
     * @param x
     * @param frameNum
     * @param type
     */
    public SpecialNote(String noteImage, Keys key, double x, double frameNum, SpecTypes type) {
        super(noteImage, key, x, frameNum);
        super.setY(SPEC_START);
        this.type = type;
    }

    /**
     * Method used to double score points
     */
    public void doubleScore() {
        Level.scoreMultiplier *= 2;
        effectActive = true;
        // move 2x note off-screen
        super.setY(Level.OFF_SCREEN_ASSET);
    }

    /**
     * Method used to undo double score effect when timer runs out
     * @param frameCount This is the frame count of the level
     */
    public void reverseDouble(double frameCount) {
        if (frameCount == hitFrame + EFFECT_DUR) {
            Level.scoreMultiplier /= 2;
            effectActive = false;
        }
    }

    /**
     * Method used to speed up all notes
     */
    public void speedUp() {
        noteSpeed++;
        effectActive = true;
    }

    /**
     * Method used to slow down all notes
     */
    public void slowDown() {
        noteSpeed--;
        effectActive = true;
    }

    /**
     * Method used to call a bomb event
     * @param level This is the corresponding level
     * @param bombLane This is the bombed lane
     */
    public void bomb(Level<Renderable> level, Lane bombLane) {
        level.bombEvent(bombLane);
        effectActive = true;
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

        if (!hit && lane.getCurrNote() == playNote &&
                input.wasPressed(super.getKey())) {
            double dist = getDist(Lane.LANE_END, super.getY());
            if (dist <= ScoreCriteria.SPEC.dist) {
                switch (type) {
                    case DOUBLE_SCORE:
                        noteScore = "DOUBLE SCORE";
                        getHit(level, NO_SCORE);
                        doubleScore();
                        break;
                    case SPEED_UP:
                        noteScore = "SPEED UP";
                        getHit(level, ScoreCriteria.SPEC.points);
                        speedUp();
                        break;
                    case SLOW_DOWN:
                        noteScore = "SLOW DOWN";
                        getHit(level, ScoreCriteria.SPEC.points);
                        slowDown();
                        break;
                    case BOMB:
                        noteScore = "LANE CLEAR";
                        // bomb hits all on-screen lane notes, including itself
                        bomb(level, lane);
                        break;
                }
            }
        }

        if (!hit && super.getY() > Lane.LANE_END + ScoreCriteria.SPEC.dist) {
            getHit(level, NO_SCORE);
        }

        if (hit) {
            scoreMessage(level.getFrameCount());
            inputDelay(lane, level.getFrameCount());
            if (type == SpecTypes.DOUBLE_SCORE && effectActive) {
                reverseDouble(level.getFrameCount());
            }
        }
    }

    @Override
    public void reset() {
        super.setY(SPEC_START);
        hit = false;
        noteScore = null;
        hitFrame = 0;
        effectActive = false;
    }

    @Override
    // on-screen based on effect status instead of hit status
    public boolean isOnScreen(Level<Renderable> level) {
        return !effectActive && level.getFrameCount() >= super.getFrameNum() &&
                super.getY() <= Window.getHeight();
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
        if (frameCount == hitFrame + NO_DELAY) {
            lane.advanceCurrNote(SPEC_LEN);
        }
    }

    @Override
    public void getHit(Level<Renderable> level, int points) {
        hitFrame = level.getFrameCount();
        level.advLevel(points, SPEC_LEN);
        hit = true;
    }
}
