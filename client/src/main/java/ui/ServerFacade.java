package ui;

import com.google.gson.Gson;
import exception.*;
import model.*;
import requestclasses.LoginRequest;

import java.io.*;
import java.net.*;

public class ServerFacade {
  private String serverUrl;
  private String authToken;
  public ServerFacade(String url){
    serverUrl = url;
    authToken = null;
  }

  public AuthData register(String username, String password, String email) throws ResponseException {
    var user = new UserData(username, password, email);
    var auth = this.makeRequest("POST", "/user", user, AuthData.class, null);
    authToken = auth.authToken();
    return auth;
  }

  public AuthData login(String username, String password) throws ResponseException {
    var request = new LoginRequest(username, password);
    var auth = this.makeRequest("POST", "/session", request, AuthData.class, null);
    authToken = auth.authToken();
    return auth;
  }

  public void logout(String authToken) throws ResponseException {
    this.makeRequest("DELETE", "/session", null, null, authToken);
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
