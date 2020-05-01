package battleships.controllers;


import battleships.domain.Game.BattleshipGame;
import battleships.services.ActiveGamesAndUsersRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
public class SubscriptionInterceptor implements ChannelInterceptor {

    @Autowired
    private ActiveGamesAndUsersRegistry registry;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.SUBSCRIBE.equals(headerAccessor.getCommand())) {
            Principal userPrincipal = headerAccessor.getUser();
            if (!validateSubscription(userPrincipal, headerAccessor.getDestination())) {
                throw new IllegalArgumentException("No permission for this topic");
            }
        }
        return message;
    }

    private boolean validateSubscription(Principal principal, String destination) {
        if (principal == null || destination == null) return false;
        int gameID = Integer.parseInt(destination.substring(6));
        BattleshipGame game = registry.getGame(gameID);
        return game != null && game.containsUser(principal.getName());
    }
}
