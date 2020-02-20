package battleships.domain.Game;

import battleships.dto.MoveDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Entity
@Getter
@Setter
public class Move {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int moveID;

    @NotEmpty
    private String username;

    @Min(0)
    @Max(9)
    private int x;

    @Min(0)
    @Max(9)
    private int y;

    @NotEmpty
    private boolean success;

    @ManyToOne
    private BattleshipGameImpl game;


    public MoveDTO getDTO() {
        MoveDTO result = new MoveDTO();
        result.setX(x);
        result.setY(y);
        result.setUsername(username);
        return result;
    }
}
