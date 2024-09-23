package com.sockets.webSocket.configuracion;

import com.sockets.webSocket.controlador.ControladorChat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.InetSocketAddress;
import java.util.Map;

@Component
public class InterceptorHandshake implements HandshakeInterceptor {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(InterceptorHandshake.class);

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        InetSocketAddress remoteAddress = request.getRemoteAddress(); //obtengo la direccion ip del cliente
      
        if (remoteAddress != null && remoteAddress.getAddress() != null) {
            String ip = remoteAddress.getAddress().getHostAddress();
            attributes.put("ip", ip);
            LOGGER.info("Dirección IP obtenida: " + ip);
            ControladorChat.agregarUsuario(ip); 
        } else {
            LOGGER.warn("No se pudo obtener la dirección IP.");
            return false;
        }
        return true; 
    }

  
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        LOGGER.info("Handshake completado");
    }
}
