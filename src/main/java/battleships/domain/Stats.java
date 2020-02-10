package battleships.domain;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Min;

@Entity
@Getter
@Setter
public class Stats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int statsID;

    @OneToOne
    private User user;

    @Min(0)
    private int gamesWon;

    @Min(0)
    private int gamesLost;

    private long points;

    public Stats() {
        gamesWon = gamesLost = 0;
        points = 0;
    }

    public void updatePoints(boolean win, long enemyPoints) {
        if (win) points += points > enemyPoints ? points * 0.1 : enemyPoints * 0.1;
        else points -= points > enemyPoints ? enemyPoints * 0.1 : points * 0.1;
    }

}
