package battleships.controllers;


import battleships.domain.Game.Move;
import battleships.domain.Message;
import battleships.domain.user.AbstractUser;
import battleships.dto.MoveDTO;
import battleships.dto.ShipDTO;
import battleships.exceptions.GameFinishedException;
import battleships.exceptions.ShipDestroyedException;
import battleships.exceptions.WrongMoveException;
import battleships.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.websocket.server.PathParam;
import java.util.List;

import static battleships.domain.Message.MessageType.*;


@Controller
public class GameController {

    @Autowired
    private GameService gameService;

    @Autowired
    private SimpMessagingTemplate messaging;


    @PostMapping("/create")
    public ResponseEntity<Integer> createGame(Authentication authentication, @RequestBody List<ShipDTO> ships) {
        System.out.println("ddd");//temporally for tests
        AbstractUser user = (AbstractUser) authentication.getPrincipal();

        try {
            int id = gameService.createGame(user.getName(), ships);
            return ResponseEntity.ok(id);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

    }

    @PostMapping("/join/{gameID}")
    public ResponseEntity<String> joinGame(Authentication authentication, @PathVariable("gameID") int gameID, @RequestBody List<ShipDTO> ships) {
        try {
            if (gameService.joinGame((AbstractUser) authentication.getPrincipal(), gameID, ships))
                return ResponseEntity.ok("Join request successful");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().build();
    }


    @MessageMapping("/game/{gameID}")
    //@SendTo("/topic/{gameID}") , in case of wrong move it is better to send message only to specific user
    public void move(Authentication authentication, @Payload MoveDTO moveDTO, @PathParam("gameID") int gameID) {
        AbstractUser principal = (AbstractUser) authentication.getPrincipal();
        moveDTO.setUsername(principal.getName());
        try {
            Move move = switch (moveDTO.getType()) {
                case MOVE -> gameService.move(principal, moveDTO, gameID);
                case SURRENDER -> gameService.surrender(principal, gameID);
            };
            messaging.convertAndSend("/topic/" + gameID, new Message(move.getDTO(), OK));
        } catch (WrongMoveException wme) {
            Message message = new Message();
            message.setMessageType(WRONG_MOVE);
            message.setText(wme.getMessage());
            messaging.convertAndSendToUser(principal.getName(), "/topic/" + gameID, message);
        } catch (ShipDestroyedException sde) {
            messaging.convertAndSend("/topic/" + gameID, new Message(sde.getMove().getDTO(), SHIP_DESTROYED));
        } catch (GameFinishedException gfe) {
            Message message = new Message(gfe.getMove().getDTO(), GAME_FINISHED);
            message.setText(gfe.getWinnerUsername());
            messaging.convertAndSend("/topic/" + gameID, message);
        }
    }
}
