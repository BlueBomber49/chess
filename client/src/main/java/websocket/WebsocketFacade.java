package websocket;

import com.google.gson.Gson;
import exception.ResponseException;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebsocketFacade extends Endpoint {

  Session session;
  NotificationHandler notificationHandler;

  public WebsocketFacade(String url, NotificationHandler notificationHandler){
    try {
      url=url.replace("http", "ws");
      URI socketURI=new URI(url + "/ws");
      this.notificationHandler = notificationHandler;

      WebSocketContainer container = ContainerProvider.getWebSocketContainer();
      this.session = container.connectToServer(this, socketURI);
      this.session.addMessageHandler(new MessageHandler.Whole<String>() {
        @Override
        public void onMessage(String s) {
          ServerMessage message = new Gson().fromJson(s, ServerMessage.class);
          notificationHandler.notify(message);
        }
      });
    }
    catch (URISyntaxException|DeploymentException|IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void onOpen(Session session, EndpointConfig endpointConfig) {
    System.out.println("WS connection established");
  }

  public void joinGame(String auth, Integer gameID) throws ResponseException {
    try {
      UserGameCommand command=new UserGameCommand(UserGameCommand.CommandType.CONNECT, auth, gameID);
      this.session.getBasicRemote().sendText(new Gson().toJson(command));
    }
    catch(Exception e){
      throw new ResponseException(500, e.getMessage());
    }
  }

  public void leaveGame(String auth, Integer id) throws ResponseException {
    try {
      UserGameCommand command=new UserGameCommand(UserGameCommand.CommandType.LEAVE, auth, id);
      this.session.getBasicRemote().sendText(new Gson().toJson(command));
    }
    catch(Exception e){
      throw new ResponseException(500, e.getMessage());
    }
  }

}
