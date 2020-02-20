package battleships.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class MoveDTO {
    private int x;
    private int y;
    private String username;
    private MoveType type;

    public enum MoveType {
        MOVE,
        SURRENDER,
        OPPONENT_DISCONNECT
    }

    public MoveDTO() {
        type = MoveType.MOVE;
    }

    public MoveDTO(MoveType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MoveDTO)) return false;
        MoveDTO moveDTO = (MoveDTO) o;
        return x == moveDTO.x &&
                y == moveDTO.y &&
                username.equals(moveDTO.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, username);
    }
}
