package battleships.exceptions;

import lombok.Getter;

@Getter
public class GameFinishedException extends Exception {
    private final String winnerUsername;

    public GameFinishedException(String winnerUsername) {
        this.winnerUsername = winnerUsername;
    }

}
