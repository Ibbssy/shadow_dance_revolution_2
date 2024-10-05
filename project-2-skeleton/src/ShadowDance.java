import bagel.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Fully-Implemented Code for SWEN20003 Project 2, Semester 2, 2023
 * Please enter your name below
 * @IbrahimBilal
 */
public class ShadowDance extends AbstractGame  {
    private final static int WINDOW_WIDTH = 1024;
    private final static int WINDOW_HEIGHT = 768;
    private final static String GAME_TITLE = "SHADOW DANCE";
    private final Image BACKGROUND_IMAGE = new Image("res/background.png");
    private final Font FONT = new Font("res/FSO8BITR.ttf", 64);
    private final Font SUBTITLE_FONT = new Font("res/FSO8BITR.ttf", 24);

    private final Level<Renderable> LEVEL1 =
            readCSV("res/level1-60.csv", TargetScores.LEVEL1);
    private Track track1;
    private final Level<Renderable> LEVEL2 =
            readCSV("res/level2-60.csv", TargetScores.LEVEL2);
    private Track track2;
    private final Level<Renderable> LEVEL3 =
            readCSV("res/level3-60.csv", TargetScores.LEVEL3);
    private Track track3;

    private enum GameStates {
        TITLE,
        LEVEL1,
        LEVEL2,
        LEVEL3,
        PAUSE,
        WIN,
        LOSE
    }

    private GameStates gameState = GameStates.TITLE;
    private GameStates currLevel;

    /**
     * This is ShadowDance game constructor
     */
    public ShadowDance(){
        super(WINDOW_WIDTH, WINDOW_HEIGHT, GAME_TITLE);
    }

    /**
     * Method used to read file and create lanes and notes
     * @param filename This is the name of the file to read from
     * @param targetScore This is the corresponding level id
     * @return level This renders a level with all its assets
     */
    private Level<Renderable> readCSV(String filename, TargetScores targetScore) {
        Level<Renderable> level = new Level<>(targetScore);
        Lane specLane = new Lane("res/laneSpecial.png");
        Lane leftLane = new Lane("res/laneLeft.png");
        Lane rightLane = new Lane("res/laneRight.png");
        Lane upLane = new Lane("res/laneUp.png");
        Lane downLane = new Lane("res/laneDown.png");
        final int ENTITY_TYPE = 0;
        final int ENTITY_DES = 1;
        final int X_OR_FRAME = 2;
        int trackLen = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String command;

            while ((command = br.readLine()) != null) {
                String[] noteStats = command.split(",");

                if (noteStats[ENTITY_TYPE].equals("Lane")) {
                    switch (noteStats[ENTITY_DES]) {
                        case "Special":
                            specLane.setX(Double.parseDouble(noteStats[X_OR_FRAME]));
                            level.addEntity(specLane);
                            break;
                        case "Left":
                            leftLane.setX(Double.parseDouble(noteStats[X_OR_FRAME]));
                            level.addEntity(leftLane);
                            break;
                        case "Right":
                            rightLane.setX(Double.parseDouble(noteStats[X_OR_FRAME]));
                            level.addEntity(rightLane);
                            break;
                        case "Up":
                            upLane.setX(Double.parseDouble(noteStats[X_OR_FRAME]));
                            level.addEntity(upLane);
                            break;
                        case "Down":
                            downLane.setX(Double.parseDouble(noteStats[X_OR_FRAME]));
                            level.addEntity(downLane);
                            break;
                    }

                } else {
                    trackLen++;
                    switch (noteStats[ENTITY_TYPE]) {
                        case "Special":
                            switch (noteStats[ENTITY_DES]) {
                                case "DoubleScore":
                                    SpecialNote new2x = new SpecialNote("res/note2x.png", Keys.SPACE,
                                            specLane.getX(), Double.parseDouble(noteStats[X_OR_FRAME]),
                                            SpecialNote.SpecTypes.DOUBLE_SCORE);
                                    specLane.addNote(new2x);
                                    break;
                                case "SpeedUp":
                                    SpecialNote newSpeedUp = new SpecialNote("res/noteSpeedUp.png", Keys.SPACE,
                                            specLane.getX(), Double.parseDouble(noteStats[X_OR_FRAME]),
                                            SpecialNote.SpecTypes.SPEED_UP);
                                    specLane.addNote(newSpeedUp);
                                    break;
                                case "SlowDown":
                                    SpecialNote newSlowDown = new SpecialNote("res/noteSlowDown.png", Keys.SPACE,
                                            specLane.getX(), Double.parseDouble(noteStats[X_OR_FRAME]),
                                            SpecialNote.SpecTypes.SLOW_DOWN);
                                    specLane.addNote(newSlowDown);
                                    break;
                            }
                            break;
                        case "Left":
                            switch (noteStats[ENTITY_DES]) {
                                case "Normal":
                                    TapNote newTap = new TapNote("res/noteLeft.png", Keys.LEFT,
                                            leftLane.getX(), Double.parseDouble(noteStats[X_OR_FRAME]));
                                    leftLane.addNote(newTap);
                                    break;
                                case "Hold":
                                    HoldNote newHold = new HoldNote("res/holdNoteLeft.png", Keys.LEFT,
                                            leftLane.getX(), Double.parseDouble(noteStats[X_OR_FRAME]));
                                    leftLane.addNote(newHold);
                                    break;
                                case "Bomb":
                                    SpecialNote newBomb = new SpecialNote("res/noteBomb.png", Keys.LEFT,
                                            leftLane.getX(), Double.parseDouble(noteStats[X_OR_FRAME]),
                                            SpecialNote.SpecTypes.BOMB);
                                    leftLane.addNote(newBomb);
                                    break;
                            }
                            break;
                        case "Right":
                            switch (noteStats[ENTITY_DES]) {
                                case "Normal":
                                    TapNote newTap = new TapNote("res/noteRight.png", Keys.RIGHT,
                                            rightLane.getX(), Double.parseDouble(noteStats[X_OR_FRAME]));
                                    rightLane.addNote(newTap);
                                    break;
                                case "Hold":
                                    HoldNote newHold = new HoldNote("res/holdNoteRight.png", Keys.RIGHT,
                                            rightLane.getX(), Double.parseDouble(noteStats[X_OR_FRAME]));
                                    rightLane.addNote(newHold);
                                    break;
                                case "Bomb":
                                    SpecialNote newBomb = new SpecialNote("res/noteBomb.png", Keys.RIGHT,
                                            rightLane.getX(), Double.parseDouble(noteStats[X_OR_FRAME]),
                                            SpecialNote.SpecTypes.BOMB);
                                    rightLane.addNote(newBomb);
                                    break;
                            }
                            break;
                        case "Up":
                            switch (noteStats[ENTITY_DES]) {
                                case "Normal":
                                    TapNote newTap = new TapNote("res/noteUp.png", Keys.UP,
                                            upLane.getX(), Double.parseDouble(noteStats[X_OR_FRAME]));
                                    upLane.addNote(newTap);
                                    break;
                                case "Hold":
                                    HoldNote newHold = new HoldNote("res/holdNoteUp.png", Keys.UP,
                                            upLane.getX(), Double.parseDouble(noteStats[X_OR_FRAME]));
                                    upLane.addNote(newHold);
                                    break;
                                case "Bomb":
                                    SpecialNote newBomb = new SpecialNote("res/noteBomb.png", Keys.UP,
                                            upLane.getX(), Double.parseDouble(noteStats[X_OR_FRAME]),
                                            SpecialNote.SpecTypes.BOMB);
                                    upLane.addNote(newBomb);
                                    break;
                            }
                            break;
                        case "Down":
                            switch (noteStats[ENTITY_DES]) {
                                case "Normal":
                                    TapNote newTap = new TapNote("res/noteDown.png", Keys.DOWN,
                                            downLane.getX(), Double.parseDouble(noteStats[X_OR_FRAME]));
                                    downLane.addNote(newTap);
                                    break;
                                case "Hold":
                                    HoldNote newHold = new HoldNote("res/holdNoteDown.png", Keys.DOWN,
                                            downLane.getX(), Double.parseDouble(noteStats[X_OR_FRAME]));
                                    downLane.addNote(newHold);
                                    break;
                                case "Bomb":
                                    SpecialNote newBomb = new SpecialNote("res/noteBomb.png", Keys.DOWN,
                                            downLane.getX(), Double.parseDouble(noteStats[X_OR_FRAME]),
                                            SpecialNote.SpecTypes.BOMB);
                                    downLane.addNote(newBomb);
                                    break;
                            }
                            break;
                    }
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        level.setTrackLen(trackLen);
        return level;
    }

    /**
     * The entry point for the program.
     */
    public static void main(String[] args) {
        ShadowDance game = new ShadowDance();
        game.run();
    }

    /**
     * Performs a state update.
     * Allows the game to exit when the escape key is pressed.
     */
    @Override
    protected void update(Input input) {

        if (input.wasPressed(Keys.ESCAPE)){
            Window.close();
        }
        BACKGROUND_IMAGE.draw(Window.getWidth()/2.0, Window.getHeight()/2.0);

        switch (gameState) {
            case TITLE:
                FONT.drawString("SHADOW DANCE", 220, 250);
                SUBTITLE_FONT.drawString("SELECT LEVELS WITH", 320, 440);
                SUBTITLE_FONT.drawString("NUMBER KEYS", 386, 466);
                SUBTITLE_FONT.drawString("1      2      3", 388, 536);

                if (input.wasPressed(Keys.NUM_1)) {
                    gameState = GameStates.LEVEL1;
                    currLevel = GameStates.LEVEL1;
                    track1 = new Track("res/track1.wav");
                    track1.start();
                } else if (input.wasPressed(Keys.NUM_2)) {
                    gameState = GameStates.LEVEL2;
                    currLevel = GameStates.LEVEL2;
                    track2 = new Track("res/track2.wav");
                    track2.start();
                } else if (input.wasPressed(Keys.NUM_3)) {
                    gameState = GameStates.LEVEL3;
                    currLevel = GameStates.LEVEL3;
                    track3 = new Track("res/track3.wav");
                    track3.start();
                }
                break;
            case LEVEL1:
                LEVEL1.play(input);

                if (input.wasPressed(Keys.TAB)) {
                    gameState = GameStates.PAUSE;
                    track1.pause();
                }

                if (LEVEL1.winCondition()) {
                    gameState = GameStates.WIN;
                    LEVEL1.reset();
                } else if (LEVEL1.loseCondition()) {
                    gameState = GameStates.LOSE;
                    LEVEL1.reset();
                }
                break;
            case LEVEL2:
                LEVEL2.play(input);

                if (input.wasPressed(Keys.TAB)) {
                    gameState = GameStates.PAUSE;
                    track2.pause();
                }

                if (LEVEL2.winCondition()) {
                    gameState = GameStates.WIN;
                    LEVEL2.reset();
                } else if (LEVEL2.loseCondition()) {
                    gameState = GameStates.LOSE;
                    LEVEL2.reset();
                }
                break;
            case LEVEL3:
                LEVEL3.play(input);

                if (input.wasPressed(Keys.TAB)) {
                    gameState = GameStates.PAUSE;
                    track3.pause();
                }

                if (LEVEL3.winCondition()) {
                    gameState = GameStates.WIN;
                    LEVEL3.reset();
                } else if (LEVEL3.loseCondition()) {
                    gameState = GameStates.LOSE;
                    LEVEL3.reset();
                }
                break;
            case PAUSE:
                FONT.drawString("PAUSE", 386, Window.getHeight() / 2.0);

                if (input.wasPressed(Keys.TAB)) {
                    gameState = currLevel;
                    switch (currLevel) {
                        case LEVEL1:
                            track1.run();
                            break;
                        case LEVEL2:
                            track2.run();
                            break;
                        case LEVEL3:
                            track3.run();
                            break;
                    }
                }
                break;
            case WIN:
                FONT.drawString("CLEAR!", 364, 300);
                SUBTITLE_FONT.drawString("PRESS SPACE TO RETURN TO LEVEL SELECTION", 150, 500);
                if (input.wasPressed(Keys.SPACE)) {
                    gameState = GameStates.TITLE;
                }
                break;
            case LOSE:
                FONT.drawString("TRY AGAIN", 298, 300);
                SUBTITLE_FONT.drawString("PRESS SPACE TO RETURN TO LEVEL SELECTION", 150, 500);
                if (input.wasPressed(Keys.SPACE)) {
                    gameState = GameStates.TITLE;
                }
                break;
        }
    }
}
