package battleships.domain.Game;

import battleships.domain.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int gameID;

    @Enumerated(value = EnumType.STRING)
    private GameStatus gameStatus;

    @OneToMany(mappedBy = "game")
    private List<Ship> ships;


    private boolean containsUser(User user) {
        for (Ship s : ships) {
            if (s.getUsername().equals(user.getUsername())) return true;
        }
        return false;
    }

    public int howManyUsers(User user) {
        List<String> present = new ArrayList<>();
        int result = 0;
        for (Ship s : ships) {
            if (!present.contains(s.getUsername())) {
                result++;
                present.add(s.getUsername());
            }
        }
        return result;
    }

    public boolean addShip(Ship ship) {

        return false;//TODO
    }
    //TODO: check if users ships are ok
}
