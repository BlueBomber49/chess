package server.websocket;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class WebsocketHandler {

  public ConcurrentHashMap<Integer, ConnectionManager> gameList = new ConcurrentHashMap<>();

  private DataAccess data;

  public WebsocketHandler(DataAccess data){
    this.data = data;
  }

  @OnWebSocketMessage
  public void messageHandler(Session session, String message) {
    try {
      UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
      String token = command.getAuthToken();
      Integer gameID = command.getGameID();
      UserGameCommand.CommandType commandType = command.getCommandType();
      String user = data.getAuth(token).username();
      if(commandType == UserGameCommand.CommandType.CONNECT){
        joinGame(gameID, user, session);
      }

      //session.getRemote().sendString("Message received on server: " + message);
      //System.out.println("Handled websocket message:" + message);
    }
    catch(Exception e){
      System.out.println("Error:" + e.getMessage());
    }
  }

  public void joinGame(Integer gameID, String user, Session session) throws IOException {
    if (!gameList.containsKey(gameID)) {
      gameList.put(gameID, new ConnectionManager());
    }
    ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, user + " has joined the game!");
    gameList.get(gameID).add(user, session);
    gameList.get(gameID).broadcast(user, message);

    ServerMessage loadMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
    gameList.get(gameID).broadcast("", loadMessage);
  }

  public void leaveGame(Integer gameID, String user){
    gameList.get(gameID).remove(user);
  }

}
