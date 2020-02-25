package battleships.exceptions;

import battleships.domain.Game.Move;
import battleships.domain.Game.MoveType;
import lombok.Getter;

@Getter
public class ShipDestroyedException extends Exception {

    private final Move move;

    public ShipDestroyedException(Move move) {
        this.move = move;
    }

    public ShipDestroyedException(int x, int y, String username) {
        move = new Move();
        move.setX(x);
        move.setY(y);
        move.setMoveType(MoveType.MOVE);
        move.setSuccess(true);
        move.setUsername(username);
    }
}
