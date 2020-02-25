package battleships.domain;

import battleships.dto.MoveDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Message {
    private MoveDTO move;
    private MessageType messageType;
    private String text;

    public enum MessageType {
        OK,
        OPPONENT_DISCONNECT,
        WRONG_MOVE,
        SHIP_DESTROYED,
        GAME_FINISHED
    }

    public Message() {
        messageType = MessageType.OK;
    }

    public Message(MoveDTO move, MessageType type) {
        this.move = move;
        this.messageType = type;
    }
}
