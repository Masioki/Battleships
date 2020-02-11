package battleships.domain.Game;

import battleships.domain.ship.Ship;
import battleships.domain.ship.WrongShipSetException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BattleshipGameFacade implements Game {

    private final BattleshipGame game;


    public BattleshipGameFacade(BattleshipGame game) {
        this.game = game;
    }


    private List<String> getUsers() {
        List<String> result = new ArrayList<>();
        for (Ship s : game.getShips()) {
            if (!result.contains(s.getUsername())) result.add(s.getUsername());
        }
        return result;
    }

    private boolean isShipListCorrect() {
        List<String> users = getUsers();
        for (String name : users) {
            List<Ship> ships = getUsersShips(name);
            int doub = (int) ships.stream().filter(ship -> ship.getSize() == 2).count();
            int triple = (int) ships.stream().filter(ship -> ship.getSize() == 3).count();
            int quadr = (int) ships.stream().filter(ship -> ship.getSize() == 4).count();
            int five = (int) ships.stream().filter(ship -> ship.getSize() == 5).count();
            if (doub != 2 || triple != 2 || quadr != 2 || five != 1) return false;
        }
        return true;
    }

    private boolean isShipListCorrect(List<Ship> ships) {
        int doub = (int) ships.stream().filter(ship -> ship.getSize() == 2).count();
        int triple = (int) ships.stream().filter(ship -> ship.getSize() == 3).count();
        int quadr = (int) ships.stream().filter(ship -> ship.getSize() == 4).count();
        int five = (int) ships.stream().filter(ship -> ship.getSize() == 5).count();
        return doub == 2 && triple == 2 && quadr == 2 && five == 1;
    }

    private List<Ship> getUsersShips(String user) {
        return game.getShips()
                .stream()
                .filter(ship -> ship.getUsername().equals(user))
                .collect(Collectors.toList());
    }

    private int howManyUsers() {
        List<String> present = new ArrayList<>();
        int result = 0;
        for (Ship s : game.getShips()) {
            if (!present.contains(s.getUsername())) {
                result++;
                present.add(s.getUsername());
            }
        }
        return result;
    }

    private boolean containsUser(String username) {
        for (Ship s : game.getShips()) {
            if (s.getUsername().equals(username)) return true;
        }
        return false;
    }


    public boolean isGameReady() {
        return howManyUsers() == 2 && isShipListCorrect();
    }

    @Override
    public void start() throws WrongShipSetException, IncorrectPlayersException {
        if (isGameReady()) game.setGameStatus(GameStatus.IN_PROGRESS);
        else {
            //TODO: throw correct exception
        }
    }

    @Override
    public boolean addShipSet(List<Ship> ships) {
        if (ships.size() != 7) return false;
        String name = null;
        for (Ship s : ships) {
            if (name != null) {
                if (!s.getUsername().equals(name)) return false;
            } else name = s.getUsername();
        }
        if (!isShipListCorrect(ships)) return false;
        for (Ship s : ships) game.addShip(s);
        return true;
    }

    @Override
    public GameStatus getGameStatus() {
        return game.getGameStatus();
    }

    @Override
    public String getWinnerUsername() {
        if (game.getGameStatus() == GameStatus.FINISHED) return game.getTurn();
        return null;
    }

    @Override
    public boolean attack(String username, int x, int y) {
        return game.attack(username, x, y);
    }

    @Override
    public boolean isOpponentShipDestroyed(String username, int x, int y) {
        return game.isOpponentShipDestroyed(username, x, y);
    }
}
