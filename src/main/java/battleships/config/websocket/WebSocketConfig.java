package battleships.config.websocket;

import battleships.controllers.SubscriptionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private SubscriptionInterceptor interceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        assert config != null;
        config.enableSimpleBroker("/topic");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        assert registration != null;
        registration.setInterceptors(interceptor);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        assert registry != null;
        registry.addEndpoint("/stomp")
                //.setHandshakeHandler(new CustomHandshakeHandler())
                .withSockJS();
    }

}
