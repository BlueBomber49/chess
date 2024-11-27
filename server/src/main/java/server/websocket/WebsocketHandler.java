package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import exception.ResponseException;
import model.GameData;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Objects;
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
      if(data.getAuth(token) == null){
        ServerMessage errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "", "Error: Invalid authtoken");
        session.getRemote().sendString(new Gson().toJson(errorMessage));
        return;
      }
      String user = data.getAuth(token).username();
      switch(commandType){
        case CONNECT -> {
          joinGame(gameID, user, session);
        }
        case LEAVE -> {
          leaveGame(gameID, user);
        }
        case RESIGN -> {
          resignGame(gameID, user);
        }
        case MAKE_MOVE -> {
          ChessMove move = command.getMove();
          makeMove(gameID, user, move);
        }
        default -> {
          System.out.println("Couldn't handle that message");
        }
      }
    }
    catch(Exception e){
      System.out.println("Error:" + e.getMessage());
    }
  }



  public void joinGame(Integer gameID, String user, Session session) throws IOException {
    try {
      GameData game=data.getGame(gameID);
      if (game == null) {
        ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "", "Error: Invalid game ID");
        session.getRemote().sendString(new Gson().toJson(message));
      }
      else {
        if (!gameList.containsKey(gameID)) {
          gameList.put(gameID, new ConnectionManager());
        }
        ServerMessage message=new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, user + " has joined the game!");
        gameList.get(gameID).add(user, session);
        gameList.get(gameID).broadcast(user, message);

        ServerMessage loadMessage=new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, data.getGame(gameID).game());
        gameList.get(gameID).send(user, loadMessage);
      }
    }
    catch(Exception e){
      gameList.get(gameID).send(user, new ServerMessage(ServerMessage.ServerMessageType.ERROR, "", "Error: " + e.getMessage()));
    }
  }

  public void leaveGame(Integer gameID, String user) throws IOException {
    try {
      GameData game = data.getGame(gameID);
      GameData newGame;
      if(Objects.equals(game.whiteUsername(), user)){
        newGame=new GameData(gameID, null, game.blackUsername(), game.gameName(), game.game());
        data.updateGame(newGame);
      }
      else if(Objects.equals(game.blackUsername(), user)){
        newGame=new GameData(gameID, game.whiteUsername(), null, game.gameName(), game.game());
        data.updateGame(newGame);
      }
      var x = data.getGame(gameID);
      gameList.get(gameID).remove(user);
      ServerMessage leaveMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, user + " has left the game");
      gameList.get(gameID).broadcast(user, leaveMessage);
    }
    catch (Exception e){
      gameList.get(gameID).send(user, new ServerMessage(ServerMessage.ServerMessageType.ERROR, "", "Error: " + e.getMessage()));
    }
  }

  public void resignGame(Integer gameID, String user) throws IOException {
    try {
      var game=data.getGame(gameID);
      if(game.game().isFinished){
        gameList.get(gameID).send(user, new ServerMessage(ServerMessage.ServerMessageType.ERROR, "", "Error: You can't resign, because the game is already over"));
      }
      else if (Objects.equals(game.blackUsername(), user) || Objects.equals(game.whiteUsername(), user)) {
        ChessGame chessGame = game.game();
        chessGame.setTeamTurn(null);
        chessGame.finish();
        data.updateGame(new GameData(game.gameId(), game.whiteUsername(), game.blackUsername(), game.gameName(), chessGame));
        String message = user + " has resigned";
        gameList.get(gameID).broadcast("", new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message));
      } else {
        gameList.get(gameID).send(user, new ServerMessage(ServerMessage.ServerMessageType.ERROR, "", "Error: You can't resign, because you are an observer"));
      }
    }
    catch (Exception e){
      gameList.get(gameID).send(user, new ServerMessage(ServerMessage.ServerMessageType.ERROR, "", "Error: " + e.getMessage()));
    }
  }

  public void makeMove(Integer gameID, String user, ChessMove move) throws IOException {
    try {
      GameData gameData = data.getGame(gameID);
      ChessGame game=gameData.game();
      ChessGame.TeamColor playerColor;

      if(Objects.equals(user, gameData.whiteUsername())){
        playerColor = ChessGame.TeamColor.WHITE;
      } else if(Objects.equals(user, gameData.blackUsername())){
        playerColor = ChessGame.TeamColor.BLACK;
      }
      else{
        gameList.get(gameID).send(user, new ServerMessage(ServerMessage.ServerMessageType.ERROR, "", "Error: You are an observer, and can't make moves"));
        return;
      }
      if(game.getTeamTurn() != playerColor){
        gameList.get(gameID).send(user, new ServerMessage(ServerMessage.ServerMessageType.ERROR, "", "Error: It is not your turn"));
        return;
      }
      if (game.getAllTeamMoves(playerColor).contains(move)) {
        game.makeMove(move);
        GameData updatedGame = new GameData(gameData.gameId(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
        data.updateGame(updatedGame);
        gameList.get(gameID).broadcast("", new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, game));
        String message = user + " moved " + move.getStartPosition().toString() + "to " + move.getEndPosition().toString();
        gameList.get(gameID).broadcast(user, new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message));
      } else {
        gameList.get(gameID).send(user, new ServerMessage(ServerMessage.ServerMessageType.ERROR, "", "Error: Illegal move"));
      }
    }
    catch (Exception e){
      gameList.get(gameID).send(user, new ServerMessage(ServerMessage.ServerMessageType.ERROR, "", "Error: " + e.getMessage()));
    }
  }

}
