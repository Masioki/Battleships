package battleships.domain.Game;

import battleships.domain.ship.Ship;
import battleships.dto.MoveDTO;
import battleships.exceptions.IncorrectPlayersException;
import battleships.exceptions.ShipDestroyedException;
import battleships.exceptions.WrongMoveException;
import battleships.exceptions.WrongShipSetException;
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
public class BattleshipGameImpl implements BattleshipGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int gameID;

    @Enumerated(value = EnumType.STRING)
    private GameStatus gameStatus;

    @OneToMany(mappedBy = "battleshipGameImpl", fetch = FetchType.EAGER)
    private List<Ship> ships;

    @NotEmpty
    private String turn;

    @OneToMany(mappedBy = "game")
    @Fetch(value = FetchMode.SELECT)
    private List<Move> moves;


    public BattleshipGameImpl() {
        ships = new ArrayList<>();
        gameStatus = GameStatus.WAITING;
    }

    public BattleshipGameImpl(String creatorUsername) {
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

    private boolean isGameReady() {
        return howManyUsers() == 2 && isShipListCorrect();
    }

    private void changeTurn() {
        for (Ship s : ships) {
            if (!s.getUsername().equals(turn)) turn = s.getUsername();
        }
    }

    private boolean executeMove(MoveDTO move) throws WrongMoveException, ShipDestroyedException {
        List<MoveDTO> dtos = new ArrayList<>();
        for (Move m : moves) dtos.add(m.getDTO());
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
                    if (result) gameStatus = GameStatus.FINISHED;
                    throw new ShipDestroyedException(x, y);
                }
                changeTurn();
                return true;
            }
        }
        changeTurn();
        return false;
    }


    @Override
    public Move attack(MoveDTO moveDTO) throws WrongMoveException, ShipDestroyedException {
        if (getGameStatus() != GameStatus.IN_PROGRESS) throw new WrongMoveException("Game is not ready");
        Move move = new Move();
        move.setSuccess(executeMove(moveDTO));
        move.setGame(this);
        move.setUsername(moveDTO.getUsername());
        move.setX(moveDTO.getX());
        move.setY(moveDTO.getY());
        moves.add(move);
        return move;
    }

    @Override
    public void start() throws WrongShipSetException, IncorrectPlayersException {
        if (howManyUsers() != 2) throw new IncorrectPlayersException("Wrong players number");
        if (!isShipListCorrect()) throw new WrongShipSetException("");
        gameStatus = GameStatus.IN_PROGRESS;
    }

    @Override
    public boolean addShipSet(List<Ship> ships) {
        if (ships.size() != 7 || isGameReady()) return false;
        String name = null;
        for (Ship s : ships) {
            if (name != null) {
                if (!s.getUsername().equals(name)) return false;
            } else name = s.getUsername();
        }
        if (isShipListWrong(ships)) return false;
        this.ships.addAll(ships);
        return true;
    }

    @Override
    public String getWinnerUsername() {
        if (gameStatus == GameStatus.FINISHED) return turn;
        return null;
    }

    @Override
    public String getOpponentUsername(String username) {
        for (Ship s : ships) {
            if (!s.getUsername().equals(username)) return s.getUsername();
        }
        return null;
    }

    @Override
    public Board getBoard(String username) {
        List<Point2D> ownShipsPoints = new ArrayList<>();
        List<Ship> ownShips = ships
                .stream()
                .filter(s -> s.getUsername().equals(username))
                .collect(Collectors.toList());

        ownShips.forEach(ship -> ship.getParts()
                .forEach(part -> ownShipsPoints.add(new Point2D.Double(part.getX(), part.getY()))));

        return new Board(username, moves, ownShipsPoints);
    }

    @Override
    public void surrender(String username) {
        turn = username;
        changeTurn();
        gameStatus = GameStatus.FINISHED;
    }

}
