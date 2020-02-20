package battleships.domain.Game;

import battleships.domain.ship.Ship;
import battleships.dto.MoveDTO;
import battleships.exceptions.IncorrectPlayersException;
import battleships.exceptions.ShipDestroyedException;
import battleships.exceptions.WrongMoveException;
import battleships.exceptions.WrongShipSetException;

import java.util.List;

public interface BattleshipGame {
    void start() throws WrongShipSetException, IncorrectPlayersException;

    boolean addShipSet(List<Ship> ships);

    GameStatus getGameStatus();

    String getWinnerUsername();

    String getOpponentUsername(String username);

    Move attack(MoveDTO moveDTO) throws WrongMoveException, ShipDestroyedException;

    Board getBoard(String username);

    void surrender(String username);
}
