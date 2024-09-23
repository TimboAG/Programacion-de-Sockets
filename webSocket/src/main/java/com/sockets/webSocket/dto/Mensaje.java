
package com.sockets.webSocket.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Mensaje {
    private String remitente;
    private String contenido;
    private String timestamp;

}
