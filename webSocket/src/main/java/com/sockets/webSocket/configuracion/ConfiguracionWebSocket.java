package com.sockets.webSocket.configuracion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

@Configuration
//@EnableWebSocketMessageBroker: Habilita el uso de WebSocket y el manejo de mensajes a través de un broker (en este caso, se utiliza un broker simple).
//Su libreria es Spring WebSocket (import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;)
@EnableWebSocketMessageBroker

//la clase de configuracion implementa WebSocketMessageBrokerConfigurer que es una interfaz WebSocketMessageBrokerConfigurer, me permite personalizar la configuración del WebSocket.
public class ConfiguracionWebSocket implements WebSocketMessageBrokerConfigurer {

    private final InterceptorHandshake interceptorHandshake;

    //Inyecto mi dependencia a InterceptorHandshake para luego poder agregar la ip del cliente
    @Autowired
    public ConfiguracionWebSocket(InterceptorHandshake interceptorHandshake) {
        this.interceptorHandshake = interceptorHandshake;
    }

    //@Override sobreescrive el metodo de WebSocketMessageBrokerConfigurer, que me permite defenir las suscripciones y el destino
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registro) {
        //Habilita un broker simple que gestiona las suscripciones a los destinos que comienzan con /tema. 
        //Esto significa que cualquier mensaje enviado a un destino que comience con /tema será distribuido a todos los suscriptores.
        registro.enableSimpleBroker("/tema");
        //Define un prefijo para los destinos de la aplicación. 
        //Los mensajes que se envían a destinos que comienzan con /aplicacion se tratarán como mensajes de la aplicación y
        //serán gestionados por los métodos anotados con @MessageMapping.
        registro.setApplicationDestinationPrefixes("/aplicacion");
    }

    //StompEndpointRegistry registro: Permite registrar puntos finales STOMP para la comunicación a través de WebSocket.
    //STOMP (Simple Text Oriented Messaging Protocol) es un protocolo de mensajería que se utiliza para la comunicación en tiempo real 
    //entre clientes y servidores a través de WebSocket. 
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat-websocket")
                .setAllowedOriginPatterns("*")
                .addInterceptors(interceptorHandshake) // Agrega el interceptor
                .setHandshakeHandler(new DefaultHandshakeHandler());  // Esto es opcional, pero útil para manejar la autenticación
    }
}
