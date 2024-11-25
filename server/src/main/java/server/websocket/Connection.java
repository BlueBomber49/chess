package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {

  public Session session;
  public String user;

  public Connection(Session session, String user){
    this.session = session;
    this.user = user;
  }

  public void send(String message) throws IOException {
    session.getRemote().sendString(message);
  }
}
