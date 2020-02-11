package battleships.domain.Game;

import battleships.domain.User;
import battleships.domain.ship.Ship;
import battleships.domain.ship.WrongShipSetException;

import java.util.List;

public interface Game {
    void start() throws WrongShipSetException, IncorrectPlayersException;

    boolean addShipSet(List<Ship> ships);

    GameStatus getGameStatus();

    String getWinnerUsername();

    boolean attack(String username, int x, int y);

    boolean isOpponentShipDestroyed(String username, int x, int y);

}
