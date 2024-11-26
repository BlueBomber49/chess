package server.websocket;

import websocket.messages.ServerMessage;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

  // A new connection Manager is needed for each chess game
  public final ConcurrentHashMap<String, Connection> connections;

  public ConnectionManager(){
    connections = new ConcurrentHashMap<>();
  }

  public void add(String name, Session session){
    Connection c = new Connection(session, name);
    connections.put(name, c);
  }

  public void remove(String name){
    connections.remove(name);
  }

  public void broadcast(String excludedUser, ServerMessage message) throws IOException {
    var removeList = new ArrayList<Connection>();
    for(var connection : connections.values()){
      if(connection.session.isOpen()){
        if(connection.user != excludedUser){
          connection.send(message);
        }
      }
      else{
        removeList.add(connection);
      }
    }
    for(var connection : removeList){
      connections.remove(connection.user);
    }
  }

  public void send(String user, ServerMessage message) throws IOException {
    var removeList = new ArrayList<Connection>();
    for(var connection : connections.values()){
      if(connection.session.isOpen()){
        if(connection.user == user){
          connection.send(message);
        }
      }
      else{
        removeList.add(connection);
      }
    }
    for(var connection : removeList){
      connections.remove(connection.user);
    }
  }
}
