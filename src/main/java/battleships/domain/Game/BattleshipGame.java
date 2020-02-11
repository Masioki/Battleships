package battleships.domain.Game;

import battleships.domain.ship.Ship;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class BattleshipGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int gameID;

    @Enumerated(value = EnumType.STRING)
    private GameStatus gameStatus;

    @OneToMany(mappedBy = "battleshipGame")
    private List<Ship> ships;

    @NotEmpty
    private String turn;


    public BattleshipGame() {
        ships = new ArrayList<>();
        gameStatus = GameStatus.WAITING;
    }

    public BattleshipGame(String creatorUsername) {
        turn = creatorUsername;
        ships = new ArrayList<>();
        gameStatus = GameStatus.WAITING;
    }


    private void changeTurn() {
        for (Ship s : ships) {
            if (!s.getUsername().equals(turn)) turn = s.getUsername();
        }
    }


    //TODO: change game status
    public boolean attack(String username, int x, int y) {
        for (Ship s : ships) {
            if (s.getUsername().equals(username) && s.contains(x, y)) {
                boolean result = s.destroy(x, y);
                if (result) changeTurn();
                return result;
            }
        }
        return false;
    }

    public boolean isOpponentShipDestroyed(String username, int x, int y) {
        for (Ship s : ships) {
            if (!s.getUsername().equals(username) && s.contains(x, y)) return s.isDestroyed();
        }
        return true;
    }

    public void addShip(Ship ship) {
        ships.add(ship);
    }
}
