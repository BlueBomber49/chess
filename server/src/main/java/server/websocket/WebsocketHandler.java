package server.websocket;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import exception.ResponseException;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.rmi.RemoteException;
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

    }
    catch(Exception e){
      System.out.println("Error:" + e.getMessage());
    }
  }

  public void joinGame(Integer gameID, String user, Session session) throws ResponseException {
    try {
      if (!gameList.containsKey(gameID)) {
        gameList.put(gameID, new ConnectionManager());
      }
      ServerMessage message=new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, user + " has joined the game!");
      gameList.get(gameID).add(user, session);
      gameList.get(gameID).broadcast(user, message);

      ServerMessage loadMessage=new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, data.getGame(gameID).game());
      gameList.get(gameID).send(user, loadMessage);
    }
    catch(Exception e){
      throw new ResponseException(500, e.getMessage());
    }
  }

  public void leaveGame(Integer gameID, String user){
    gameList.get(gameID).remove(user);
  }

}
