package com.sockets.webSocket.controlador;

import com.sockets.webSocket.dto.Mensaje;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Controller
public class ControladorChat {

    private static final List<String> usuariosConectados = new ArrayList<>();

    public static void agregarUsuario(String ip) {
        String ipFormateada = formatIp(ip);
        if (!usuariosConectados.contains(ipFormateada)) {
            usuariosConectados.add(ipFormateada);
        }
    }

    public static void quitarUsuario(String ip) {
        String ipFormateada = formatIp(ip);
        usuariosConectados.remove(ipFormateada);
    }

    

    public static String getLocalIp() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            return localHost.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "Unknown IP";
        }
    }

    private static String formatIp(String ip) {
        if (ip.equals("0:0:0:0:0:0:0:1")) {
            // Obtener la IP local
            return   getLocalIp();
        } else {
            return  ip;
        }
    }
    
    @MessageMapping("/enviarMensaje")
    @SendTo("/tema/mensajes")
    public Mensaje enviarMensaje(Mensaje mensaje, SimpMessageHeaderAccessor cabecera) {
        String ip = (String) cabecera.getSessionAttributes().get("ip");
        mensaje.setContenido(mensaje.getContenido() + " (conectado desde IP: " + ip + ")");
        return mensaje;
    }

    @MessageMapping("/listar")
    @SendTo("/tema/mensajes")
    public Mensaje listarUsuarios() {
        StringBuilder lista = new StringBuilder("Conectados: ");
        for (String usuario : usuariosConectados) {
            lista.append(usuario).append(" ");
        }
        return new Mensaje("Sistema", lista.toString(), String.valueOf(System.currentTimeMillis()));
    }

    @MessageMapping("/quitar")
    @SendTo("/tema/mensajes")
    public Mensaje salir(SimpMessageHeaderAccessor cabecera) {
        String ip = (String) cabecera.getSessionAttributes().get("ip");
        quitarUsuario(ip);
        return new Mensaje("Sistema", "Usuario desconectado: " + formatIp(ip), String.valueOf(System.currentTimeMillis()));
    }

    @MessageMapping("/conectar")
    @SendTo("/tema/mensajes")
    public Mensaje conectar(SimpMessageHeaderAccessor cabecera) {
        String ip = (String) cabecera.getSessionAttributes().get("ip");
        agregarUsuario(ip);

        // Obtener geolocalización
        String geolocalizacion = obtenerGeolocalizacion(ip);
        System.out.println("Geolocalización de " + ip + ": " + geolocalizacion);
        // Obtener obtenerInfoHardware;
         Map<String, String> infoHardware = obtenerInfoHardware();
    System.out.println("Información de hardware: " + infoHardware);
        return new Mensaje("Sistema", "Usuario conectado: " + formatIp(ip), String.valueOf(System.currentTimeMillis()));
    }

    private String obtenerGeolocalizacion(String ip) {
        try {
            String url = "http://ipinfo.io/" + ip + "/json"; // Cambiar a HTTP
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            return response.getBody();
        } catch (Exception e) {
            return "No se pudo obtener la geolocalización";
        }
    }

    private Map<String, String> obtenerInfoHardware() {
        Map<String, String> info = new HashMap<>();
        info.put("Hostname", executeCommand("hostname"));

        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            info.put("Memoria Libre", executeCommand("wmic os get FreePhysicalMemory"));
            info.put("Dirección IP", executeCommand("ipconfig"));
            // Agregar otros comandos para Windows según sea necesario
        } else {
            info.put("Carga Promedio", executeCommand("cat /proc/loadavg"));
            info.put("Memoria Libre", executeCommand("free -h"));
            info.put("Procesos", executeCommand("ps -aux"));
            info.put("Dirección IP", executeCommand("ip address"));
            info.put("Sistema Operativo", executeCommand("hostnamectl"));
        }
        return info;
    }

    // Método para obtener el comando de memoria dependiendo del sistema operativo
    private String getFreeCommand() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return "wmic os get FreePhysicalMemory"; // Windows
        } else {
            return "free -h"; // Linux
        }
    }

    // Método para ejecutar un comando del sistema
    private String executeCommand(String command) {
        StringBuilder output = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error ejecutando el comando: " + e.getMessage();
        }
        return output.toString();
    }

}
