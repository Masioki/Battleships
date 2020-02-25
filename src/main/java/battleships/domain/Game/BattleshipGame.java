package battleships.domain.Game;

import battleships.domain.ship.Ship;
import battleships.dto.MoveDTO;
import battleships.exceptions.GameFinishedException;
import battleships.exceptions.ShipDestroyedException;
import battleships.exceptions.WrongMoveException;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity(name = "game")
@Getter
@Setter
public class BattleshipGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int gameID;

    @Enumerated(value = EnumType.STRING)
    private GameStatus gameStatus;

    @OneToMany(mappedBy = "battleshipGame", fetch = FetchType.EAGER)
    private List<Ship> ships;

    @NotEmpty
    private String turn;

    @OneToMany(mappedBy = "game")
    @Fetch(value = FetchMode.SELECT)
    private List<Move> moves;

    /**
     * Only for hibernate
     */
    public BattleshipGame() {
        ships = new ArrayList<>();
        gameStatus = GameStatus.WAITING;
    }

    public BattleshipGame(String creatorUsername) {
        turn = creatorUsername;
        ships = new ArrayList<>();
        gameStatus = GameStatus.WAITING;
    }


    private List<String> getUsers() {
        List<String> result = new ArrayList<>();
        for (Ship s : ships) {
            if (!result.contains(s.getUsername())) result.add(s.getUsername());
        }
        return result;
    }

    private List<Ship> getUsersShips(String user) {
        return getShips()
                .stream()
                .filter(ship -> ship.getUsername().equals(user))
                .collect(Collectors.toList());
    }

    private int howManyUsers() {
        List<String> present = new ArrayList<>();
        int result = 0;
        for (Ship s : getShips()) {
            if (!present.contains(s.getUsername())) {
                result++;
                present.add(s.getUsername());
            }
        }
        return result;
    }

    private boolean isShipListCorrect() {
        List<String> users = getUsers();
        for (String name : users)
            if (isShipListWrong(getUsersShips(name))) return false;
        return true;
    }

    private boolean isShipListWrong(List<Ship> ships) {
        int doub = (int) ships.stream().filter(ship -> ship.getSize() == 2).count();
        int triple = (int) ships.stream().filter(ship -> ship.getSize() == 3).count();
        int quadr = (int) ships.stream().filter(ship -> ship.getSize() == 4).count();
        int five = (int) ships.stream().filter(ship -> ship.getSize() == 5).count();
        return doub != 2 || triple != 2 || quadr != 2 || five != 1;
    }

    private boolean isGameReady() {
        return howManyUsers() == 2 && isShipListCorrect();
    }

    private void changeTurn() {
        for (Ship s : ships) {
            if (!s.getUsername().equals(turn)) turn = s.getUsername();
        }
    }

    private boolean executeMove(MoveDTO move) throws WrongMoveException, ShipDestroyedException, GameFinishedException {
        List<MoveDTO> dtos = moves.stream().map(Move::getDTO).collect(Collectors.toList());
        if (dtos.contains(move)) throw new WrongMoveException("Repeated move");

        int x, y;
        List<Ship> opponentShips = ships.stream()
                .filter(ship -> !ship.getUsername().equals(move.getUsername()))
                .collect(Collectors.toList());
        for (Ship s : opponentShips) {
            x = move.getX();
            y = move.getY();
            if (s.contains(x, y)) {
                s.destroy(x, y);
                if (s.isDestroyed()) {
                    boolean result = true;
                    for (Ship ship : opponentShips) if (!ship.isDestroyed()) result = false;
                    if (result) {
                        gameStatus = GameStatus.FINISHED;
                        throw new GameFinishedException(turn, move.getX(), move.getY());
                    }
                    changeTurn();
                    throw new ShipDestroyedException(x, y, move.getUsername());
                }
                changeTurn();
                return true;
            }
        }
        changeTurn();
        return false;
    }


    //PUBLIC

    public boolean containsUser(String username) {
        return getUsers().contains(username);
    }

    public synchronized Move attack(MoveDTO moveDTO) throws WrongMoveException, ShipDestroyedException, GameFinishedException {
        if (getGameStatus() != GameStatus.IN_PROGRESS) throw new WrongMoveException("Game is not ready");
        if (!containsUser(moveDTO.getUsername())) throw new WrongMoveException("Incorrect player");
        Move move = new Move();
        boolean result = false;
        boolean shipDestroyed = false;
        boolean gameFinished = false;
        try {
            result = executeMove(moveDTO);
        } catch (ShipDestroyedException e) {
            shipDestroyed = true;
            result = true;
        } catch (GameFinishedException gfe) {
            result = true;
            gameFinished = true;
        }
        move.setSuccess(result);
        move.setGame(this);
        move.setUsername(moveDTO.getUsername());
        move.setMoveType(MoveType.MOVE);
        move.setX(moveDTO.getX());
        move.setY(moveDTO.getY());
        moves.add(move);
        if (gameFinished) throw new GameFinishedException(getWinnerUsername(), move);
        if (shipDestroyed) throw new ShipDestroyedException(move);
        return move;
    }

    public synchronized Move surrender(String username) throws WrongMoveException {
        if (!containsUser(username)) throw new WrongMoveException("Incorrect player");
        if (gameStatus != GameStatus.IN_PROGRESS) throw new WrongMoveException("Game is already finished");
        turn = username;
        changeTurn();
        gameStatus = GameStatus.FINISHED;
        Move move = new Move();
        move.setSuccess(true);
        move.setGame(this);
        move.setUsername(turn);
        move.setMoveType(MoveType.SURRENDER);
        moves.add(move);
        return move;
    }

    public boolean joinGame(List<Ship> ships) {
        if (ships.size() != 7 || isGameReady()) return false;
        String name = null;
        for (Ship s : ships) {
            if (name != null) {
                if (!s.getUsername().equals(name)) return false;
            } else name = s.getUsername();
        }
        if (isShipListWrong(ships)) return false;
        this.ships.addAll(ships);
        if (isGameReady()) gameStatus = GameStatus.IN_PROGRESS;
        return true;
    }

    public String getWinnerUsername() {
        if (gameStatus == GameStatus.FINISHED) return turn;
        return null;
    }

    public String getOpponentUsername(String username) {
        for (Ship s : ships) {
            if (!s.getUsername().equals(username)) return s.getUsername();
        }
        return null;
    }

    public synchronized Board getBoard(String username) {
        List<Point2D> ownShipsPoints = new ArrayList<>();
        List<Ship> ownShips = ships
                .stream()
                .filter(s -> s.getUsername().equals(username))
                .collect(Collectors.toList());

        ownShips.forEach(ship -> ship.getParts()
                .forEach(part -> ownShipsPoints.add(new Point2D.Double(part.getX(), part.getY()))));

        return new Board(username, moves, ownShipsPoints);
    }

}
