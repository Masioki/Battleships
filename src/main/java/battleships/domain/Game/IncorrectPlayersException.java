package battleships.domain.Game;

public class IncorrectPlayersException extends Exception {

    public IncorrectPlayersException(String message) {
        super(message);
    }
}
