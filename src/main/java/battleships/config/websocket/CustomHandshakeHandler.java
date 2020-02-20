package battleships.config.websocket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        Principal principal = request.getPrincipal();
        if (principal == null) return new CustomPrincipal(UUID.randomUUID().toString(), true);
        return new CustomPrincipal(principal.getName(), false);
    }
}
