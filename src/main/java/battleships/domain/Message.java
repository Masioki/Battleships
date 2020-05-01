package battleships.domain;

import battleships.dto.MoveDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Message {
    private MoveDTO move;
    private MessageType type;
    private String text;

    public enum MessageType {
        OK,
        OPPONENT_DISCONNECTED,
        WRONG_MOVE,
        SHIP_DESTROYED,
        GAME_FINISHED
    }

    public Message() {
        type = MessageType.OK;
    }

    public Message(MoveDTO move, MessageType type) {
        this.move = move;
        this.type = type;
    }
}
