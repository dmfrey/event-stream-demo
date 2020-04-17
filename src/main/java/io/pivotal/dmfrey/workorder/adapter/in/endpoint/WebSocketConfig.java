package io.pivotal.dmfrey.workorder.adapter.in.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
//@EnableWebSocketMessageBroker
public class WebSocketConfig implements /* WebSocketMessageBrokerConfigurer, */ WebSocketConfigurer {

    @Autowired
    private WebSocketHandler webSocketHandler;

//    @Override
//    public void configureMessageBroker( MessageBrokerRegistry config ) {
//
//        config.setApplicationDestinationPrefixes( "/workorders" );
//        config.enableSimpleBroker("/topic", "/queue" );
//
//    }
//
//    @Override
//    public void registerStompEndpoints( StompEndpointRegistry registry ) {
//
//        registry.addEndpoint("/workorders-websocket" ).withSockJS();
//
//    }

    @Override
    public void registerWebSocketHandlers( WebSocketHandlerRegistry registry ) {

        registry.addHandler( webSocketHandler, "/socket" ).setAllowedOrigins( "*" );

    }

}
