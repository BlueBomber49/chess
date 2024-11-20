package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class WebsocketHandler {

  @OnWebSocketMessage
  public void messageHandler(String name, Session session) {
    try {
      session.getRemote().sendString("Message received on server");
      System.out.println("Handled websocket message");
    }
    catch(Exception e){
      System.out.println("Error:" + e.getMessage());
    }
  }
}
