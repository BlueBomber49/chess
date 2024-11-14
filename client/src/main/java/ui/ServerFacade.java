package ui;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.*;
import model.*;
import requestclasses.CreateGameRequest;
import requestclasses.JoinGameRequest;
import requestclasses.LoginRequest;
import responseclasses.GameIdResponse;
import responseclasses.GameListResponse;
import responseclasses.GameResponse;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ServerFacade {
  private String serverUrl;
  public ServerFacade(String url){
    serverUrl = url;
  }

  public AuthData register(String username, String password, String email) throws ResponseException {
    var user = new UserData(username, password, email);
    return this.makeRequest("POST", "/user", user, AuthData.class, null);
  }

  public AuthData login(String username, String password) throws ResponseException {
    var request = new LoginRequest(username, password);
    return this.makeRequest("POST", "/session", request, AuthData.class, null);
  }

  public void logout(String auth) throws ResponseException {
    this.makeRequest("DELETE", "/session", null, null, auth);
  }

  public ArrayList<GameResponse> listGames(String authToken) throws ResponseException {
    GameListResponse list = this.makeRequest("GET", "/game", null, GameListResponse.class, authToken);
    return list.games();
  }

  public GameIdResponse createGame(String authToken, String gameName) throws ResponseException {
    var request = new CreateGameRequest(gameName);
    return this.makeRequest("POST", "/game", request, GameIdResponse.class, authToken);
  }

  public void joinGame(String authToken, Integer gameId, ChessGame.TeamColor color) throws ResponseException{
    this.makeRequest("PUT", "/game", new JoinGameRequest(color, gameId), null, authToken);
  }

  public void clear() throws ResponseException {
    this.makeRequest("DELETE", "/db", null, null, null);
  }

  private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authHeader) throws ResponseException {
    try {
      URL url=(new URI(serverUrl + path)).toURL();
      HttpURLConnection http = (HttpURLConnection) url.openConnection();
      if(authHeader != null){
        http.setRequestProperty("Authorization", authHeader);
      }
      http.setRequestMethod(method);
      http.setDoOutput(true);

      writeBody(http, request);
      http.connect();
      throwIfNotSuccessful(http);
      return readBody(http, responseClass);
    }
    catch (Exception e) {
      throw new ResponseException(500, e.getMessage());
    }
  }

  private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
    var status = http.getResponseCode();
    if (!isSuccessful(status)) {
      throw new ResponseException(status, "failure: " + status);
    }
  }

  private static void writeBody(HttpURLConnection http, Object request) throws ResponseException {
    if(request != null) {
      http.addRequestProperty("Content-Type", "application/json");
      String reqData=new Gson().toJson(request);
      try (OutputStream reqBody = http.getOutputStream()) {
        reqBody.write(reqData.getBytes());
      }
      catch (Exception e){
        throw new ResponseException(500, e.getMessage());
      }
    }
  }

  private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws ResponseException {
    T response = null;
    if(responseClass != null){
        try (InputStream responseBody=http.getInputStream()) {
          InputStreamReader reader=new InputStreamReader(responseBody);
          response = new Gson().fromJson(reader, responseClass);
        } catch (Exception e) {
          throw new ResponseException(500, e.getMessage());
      }
    }
    return response;
  }

  private boolean isSuccessful(int status) {
    return status / 100 == 2;
  }


}
