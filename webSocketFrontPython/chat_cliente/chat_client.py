import json
import socket
import requests
import websocket

# websocket_url = "ws://192.168.0.53:8080/chat-websocket"
websocket_url = "ws://localhost:8080/chat-websocket"


def obtener_ip_publica():
    try:
        response = requests.get("https://api64.ipify.org?format=json")
        return response.json()['ip']
    except Exception as e:
        print(f"Error al obtener la IP pública: {e}")
        return None

# Función que se llama al abrir la conexión WebSocket


def on_open(ws):
    ip_publica = obtener_ip_publica()
    ws.send(json.dumps({
        "tipo": "conectar",
        "ip": ip_publica
    }))
    print(f"Conectado: {ip_publica}")


ws = websocket.WebSocketApp("ws://localhost:8080/chat-websocket",
                            on_open=on_open)


def on_close(ws, close_status_code, close_msg):
    # Enviar un mensaje para quitar la IP de la lista
    ws.send(json.dumps({
        "tipo": "quitar"
    }))
    print("Connection closed")


ws = websocket.WebSocketApp(websocket_url,
                            on_open=on_open,
                            on_close=on_close,
                            )


def on_error(ws, error):
    print("Error: ", error)


websocket_url = "ws://localhost:8080/chat-websocket"
ws = websocket.WebSocketApp(websocket_url,
                            on_open=on_open,
                            on_close=on_close,
                            on_error=on_error)

ws.run_forever()
