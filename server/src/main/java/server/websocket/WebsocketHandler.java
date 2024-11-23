package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class WebsocketHandler {

  @OnWebSocketMessage
  public void messageHandler(Session session, String message) {
    try {
      session.getRemote().sendString("Message received on server: " + message);
      System.out.println("Handled websocket message:" + message);
    }
    catch(Exception e){
      System.out.println("Error:" + e.getMessage());
    }
  }
}
