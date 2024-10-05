/**
 * Enum representing level target scores
 */
public enum TargetScores {
    LEVEL1 (150),
    LEVEL2 (400),
    LEVEL3 (350);

    final int targetScore;
    TargetScores(int targetScore) {
        this.targetScore = targetScore;
    }
}
