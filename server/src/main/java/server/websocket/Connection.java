package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class Connection {

  public Session session;
  public String user;

  public Connection(Session session, String user){
    this.session = session;
    this.user = user;
  }

  public void send(ServerMessage message) throws IOException {
    session.getRemote().sendString(new Gson().toJson(message));
  }
}
