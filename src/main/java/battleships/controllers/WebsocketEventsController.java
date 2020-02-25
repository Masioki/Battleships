package battleships.controllers;

import battleships.domain.Game.BattleshipGame;
import battleships.domain.Game.GameStatus;
import battleships.domain.Game.Move;
import battleships.domain.Message;
import battleships.domain.user.AbstractUser;
import battleships.exceptions.WrongMoveException;
import battleships.services.ActiveGamesAndUsersRegistry;
import battleships.services.GameService;
import battleships.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.security.Principal;

@Controller
public class WebsocketEventsController {

    @Autowired
    private ActiveGamesAndUsersRegistry registry;

    @Autowired
    private GameService gameService;

    @Autowired
    private UserService userService;

    @Autowired
    private SimpMessagingTemplate messaging;


    private void handleUserLeftGame(Principal principal, String destination) {
        if (principal == null) return; //TODO: fragile point
        AbstractUser user = registry.getUser(principal.getName());
        if (user == null) return;
        if (destination != null) {
            int gameID = Integer.parseInt(destination.substring(6)); //get gameID
            BattleshipGame game = registry.getGame(gameID);
            if (game == null || !game.containsUser(user.getName())) return; //check if user belongs to this game

            if (game.getGameStatus() == GameStatus.IN_PROGRESS) {
                String opponent = game.getOpponentUsername(user.getName());
                boolean opponentIsGuest = !userService.userExists(opponent);

                if (!user.isAutoGenerated() && !opponentIsGuest) { //if both players are registered don't end game and save it for later
                    gameService.saveGame(game);
                    registry.removeGame(gameID);
                    Message message = new Message();
                    message.setText(user.getName());
                    message.setMessageType(Message.MessageType.OPPONENT_DISCONNECT);
                    messaging.convertAndSend("/topic/game/" + gameID, message);
                } else { //if one of players is guest end game, update stats and remove game from registry and db
                    try {
                        Move move = game.surrender(user.getName());
                        gameService.removeGame(gameID);
                        userService.updatePoints(opponent, user.getName());
                        messaging.convertAndSend("/topic/game/" + gameID, new Message(move.getDTO(), Message.MessageType.OK));
                    } catch (WrongMoveException e) {
                        //user belongs to this game
                        e.printStackTrace();
                    }
                }
            } else gameService.removeGame(gameID);
        }
    }


    @EventListener
    public void handleDisconnected(SessionDisconnectEvent event) {
        String destination = StompHeaderAccessor.wrap(event.getMessage()).getDestination();
        Principal principal = event.getUser();
        if (principal != null && destination != null) {
            handleUserLeftGame(principal, destination);
            registry.removeUser(principal.getName());
        }
    }

    @EventListener
    public void handleUnsubscribe(SessionUnsubscribeEvent event) {
        String destination = StompHeaderAccessor.wrap(event.getMessage()).getDestination();
        if (destination != null && destination.startsWith("/game/"))
            handleUserLeftGame(event.getUser(), destination);
    }

    @EventListener
    public void handleConnected(SessionConnectedEvent event) {
        Principal principal = event.getUser();
        if (principal == null) return;
        AbstractUser user = (AbstractUser) principal;
        registry.addUser(user);
        /*
        if (principal.getName().startsWith("AUTO")) {
            registry.addUser(new AnonymousUser(principal.getName(), true));
        } else {
            registry.addUser(new AnonymousUser(principal.getName(), false));
        }
         */
    }

    @EventListener
    public void handleSubscribe(SessionSubscribeEvent event) {

        Principal principal = event.getUser();
        String destination = StompHeaderAccessor.wrap(event.getMessage()).getDestination();
        if (principal == null || destination == null) return;

        int gameID = Integer.parseInt(destination.substring(6));
        BattleshipGame game = registry.getGame(gameID);
        if (game == null || !game.containsUser(principal.getName())) {
            //TODO: force unsubscribe
        }

    }

}
