package ui;

import com.google.gson.Gson;
import exception.*;

import java.io.*;
import java.net.*;

public class ServerFacade {
  private String serverUrl;
  public ServerFacade(String url){
    serverUrl = url;
  }



  private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
    try {
      URL url=(new URI(serverUrl + path)).toURL();
      HttpURLConnection http = (HttpURLConnection) url.openConnection();
      http.setRequestMethod(method);

      //writeBody(request, http)
      http.connect();

      return readBody(http, responseClass);
    }
    catch (Exception e) {
      throw new ResponseException(500, e.getMessage());
    }
  }

  private static void writeBody(HttpURLConnection http, Object request) throws ResponseException {
    if(request != null) {
      http.addRequestProperty("Content-Type", "application/json");
      String requestData=new Gson().toJson(request);
      try(OutputStream o = http.getOutputStream()){
        o.write(requestData.getBytes());
      }
      catch (Exception e){
        throw new ResponseException(500, e.getMessage());
      }
    }
  }

  private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws ResponseException {
    T response = null;
    if(responseClass != null){
      if(http.getContentLength() > 0) {
        try (InputStream responseBody=http.getInputStream()) {
          InputStreamReader reader=new InputStreamReader(responseBody);
          response = new Gson().fromJson(reader, responseClass);
        } catch (Exception e) {
          throw new ResponseException(500, e.getMessage());
        }
      }
    }
    return response;
  }

  private boolean isSuccessful(int status) {
    return status / 100 == 2;
  }
}
