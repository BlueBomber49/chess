package websocket;

import messages.ServerMessage;

public interface NotificationHandler {
  public void notify(ServerMessage message);
}
