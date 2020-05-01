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
        AbstractUser user = (AbstractUser) authentication.getPrincipal();

        //Custom authentication should prevent this
        if (user == null || "anonymousUser".equals(user.getUsername())) return ResponseEntity.badRequest().build();

        try {
            ships.forEach(shipDTO -> shipDTO.setUsername(user.getUsername()));
            int id = gameService.createGame(user.getName(), ships);
            return ResponseEntity.ok(id);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

    }

    @PostMapping("/join/{gameID}")
    public ResponseEntity<String> joinGame(Authentication authentication, @PathVariable("gameID") int gameID, @RequestBody List<ShipDTO> ships) {
        AbstractUser user = (AbstractUser) authentication.getPrincipal();

        //Custom authentication should prevent this
        if (user == null || "anonymousUser".equals(user.getUsername())) return ResponseEntity.badRequest().build();

        try {
            ships.forEach(shipDTO -> shipDTO.setUsername(user.getUsername()));
            if (gameService.joinGame(user, gameID, ships))
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
            // new java switch statements
            Move move = switch (moveDTO.getType()) {
                case MOVE -> gameService.move(principal, moveDTO, gameID);
                case SURRENDER -> gameService.surrender(principal, gameID);
            };
            messaging.convertAndSend("/topic/game/" + gameID, new Message(move.getDTO(), OK));
        } catch (WrongMoveException wme) {
            Message message = new Message();
            message.setType(WRONG_MOVE);
            message.setText(wme.getMessage());
            messaging.convertAndSendToUser(principal.getName(), "/topic/" + gameID, message);
        } catch (ShipDestroyedException sde) {
            messaging.convertAndSend("/topic/game/" + gameID, new Message(sde.getMove().getDTO(), SHIP_DESTROYED));
        } catch (GameFinishedException gfe) {
            Message message = new Message(gfe.getMove().getDTO(), GAME_FINISHED);
            message.setText(gfe.getWinnerUsername());
            messaging.convertAndSend("/topic/game/" + gameID, message);
        }
    }
}
