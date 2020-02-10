package battleships.domain.Game;

public enum GameStatus {
    WAITING,
    IN_PROGRESS,
    FINISHED;

    @Override
    public String toString() {
        return name();
    }
}
