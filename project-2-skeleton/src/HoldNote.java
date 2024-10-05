import bagel.Input;
import bagel.Keys;
import bagel.Window;

/**
 * Class representing Hold Notes
 */
public class HoldNote extends Note {
    private final static double HOLD_START = 24;
    private final static double HOLD_BUFF = 82;
    // hold notes contribute 0.5 note into track length for two parts
    private final static double HOLD_LEN = 0.5;
    // hold note states and data
    private boolean bottomHit = false;
    private String bottomNoteScore;
    private double bottomHitFrame;
    private boolean topHit = false;
    private String topNoteScore;
    private double topHitFrame;
    private boolean stolen = false;

    /**
     *
     * @param noteImage
     * @param key
     * @param x
     * @param y
     * @param frameNum
     */
    public HoldNote(String noteImage, Keys key, double x, double y, double frameNum) {
        super(noteImage, key, x, y, frameNum);
    }

    /**
     *
     * @param noteImage
     * @param key
     * @param x
     * @param frameNum
     */
    public HoldNote(String noteImage, Keys key, double x, double frameNum) {
        super(noteImage, key, x, frameNum);
        super.setY(HOLD_START);
    }

    /**
     * Activates note stolen state
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

        if (!bottomHit && lane.getCurrNote() == playNote &&
                input.wasPressed(super.getKey())) {
            double dist = getDist(Lane.LANE_END, super.getY() + HOLD_BUFF);
            if (dist <= ScoreCriteria.PERFECT.dist) {
                bottomNoteScore = "PERFECT";
                getBottomHit(level, ScoreCriteria.PERFECT.points);
            } else if (dist <= ScoreCriteria.GOOD.dist &&
                    dist > ScoreCriteria.PERFECT.dist) {
                bottomNoteScore = "GOOD";
                getBottomHit(level, ScoreCriteria.GOOD.points);
            } else if (dist <= ScoreCriteria.BAD.dist &&
                    dist > ScoreCriteria.GOOD.dist) {
                bottomNoteScore = "BAD";
                getBottomHit(level, ScoreCriteria.BAD.points);
            } else if (dist <= ScoreCriteria.MISS.dist &&
                    dist > ScoreCriteria.BAD.dist) {
                bottomNoteScore = "MISS";
                getBottomHit(level, ScoreCriteria.MISS.points);
            }
        }

        if (!bottomHit && super.getY() + HOLD_BUFF > Window.getHeight()) {
            bottomNoteScore = "MISS";
            getBottomHit(level, ScoreCriteria.MISS.points);
        }

        if (bottomHit) {
            scoreMessage(level.getFrameCount());
            inputDelay(lane, level.getFrameCount());
        }

        if (bottomHit && !topHit && lane.getCurrNote() == playNote &&
                input.wasReleased(super.getKey())) {
            double dist = getDist(Lane.LANE_END, super.getY() - HOLD_BUFF);
            if (dist <= ScoreCriteria.PERFECT.dist) {
                topNoteScore = "PERFECT";
                getTopHit(level, ScoreCriteria.PERFECT.points);
            } else if (dist <= ScoreCriteria.GOOD.dist &&
                    dist > ScoreCriteria.PERFECT.dist) {
                topNoteScore = "GOOD";
                getTopHit(level, ScoreCriteria.GOOD.points);
            } else if (dist <= ScoreCriteria.BAD.dist &&
                    dist > ScoreCriteria.GOOD.dist) {
                topNoteScore = "BAD";
                getTopHit(level, ScoreCriteria.BAD.points);
            } else if (dist <= ScoreCriteria.MISS.dist &&
                    dist > ScoreCriteria.BAD.dist) {
                topNoteScore = "MISS";
                getTopHit(level, ScoreCriteria.MISS.points);
            }
        }

        if (!topHit && super.getY() - HOLD_BUFF > Window.getHeight()) {
            topNoteScore = "MISS";
            getTopHit(level, ScoreCriteria.MISS.points);
        }

        if (topHit) {
            scoreMessage(level.getFrameCount());
            inputDelay(lane, level.getFrameCount());
        }
    }

    @Override
    public void reset() {
        super.setY(HOLD_START);
        bottomHit = false;
        bottomNoteScore = null;
        bottomHitFrame = 0;
        topHit = false;
        topNoteScore = null;
        topHitFrame = 0;
    }

    @Override
    public boolean isOnScreen(Level<Renderable> level) {
        return !topHit && !stolen && level.getFrameCount() >= super.getFrameNum();
    }

    @Override
    public void scoreMessage(double frameCount) {
        if (bottomNoteScore != null && bottomHit && frameCount < bottomHitFrame + MESSAGE_TIME) {
            super.MESSAGE_FONT.drawString(bottomNoteScore, Window.getWidth() / 2.0
                    - bottomNoteScore.length() * 16, Window.getHeight() / 2.0);
        } else if (topNoteScore != null && topHit && frameCount < topHitFrame + MESSAGE_TIME) {
            super.MESSAGE_FONT.drawString(topNoteScore, Window.getWidth() / 2.0
                    - topNoteScore.length() * 16, Window.getHeight() / 2.0);
        }
    }

    @Override
    public void inputDelay(Lane lane, double frameCount) {
        if (bottomHit && frameCount == bottomHitFrame + NO_DELAY) {
            lane.advanceCurrNote(HOLD_LEN);
        } else if (topHit && frameCount == topHitFrame + NO_DELAY) {
            lane.advanceCurrNote(HOLD_LEN);
        }
    }

    /**
     * Method that changes top-half of hold note to hit and adds score points
     * @param level This is the corresponding level
     * @param points This is the points to add
     */
    public void getTopHit(Level<Renderable> level, int points) {
        topHitFrame = level.getFrameCount();
        level.advLevel(points, HOLD_LEN);
        topHit = true;
    }

    /**
     * Method that changes bottom-half of hold note to hit and adds score points
     * @param level This is the corresponding level
     * @param points This is the points to add
     */
    public void getBottomHit(Level<Renderable> level, int points) {
        bottomHitFrame = level.getFrameCount();
        level.advLevel(points, HOLD_LEN);
        bottomHit = true;
    }

    @Override
    public void getHit(Level<Renderable> level, int points) {
        topHitFrame = bottomHitFrame = level.getFrameCount();
        level.advLevel(points, HOLD_LEN * 2);
        topHit = bottomHit = true;
    }
}
