package battleships.exceptions;

import battleships.domain.Game.Move;
import battleships.domain.Game.MoveType;
import lombok.Getter;

@Getter
public class GameFinishedException extends Exception {
    private final String winnerUsername;
    private final Move move;

    public GameFinishedException(String winnerUsername, Move move) {
        this.winnerUsername = winnerUsername;
        this.move = move;
    }

    public GameFinishedException(String winnerUsername, int x, int y){
        this.winnerUsername = winnerUsername;
        move = new Move();
        move.setSuccess(true);
        move.setMoveType(MoveType.MOVE);
        move.setX(x);
        move.setY(y);
    }

}
