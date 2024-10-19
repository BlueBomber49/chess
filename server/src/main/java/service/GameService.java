package service;

import dataaccess.DataAccess;
import model.GameData;

import java.util.ArrayList;

public class GameService {
  private DataAccess data;


  public GameService(DataAccess data){
    this.data = data;
  }

  public ArrayList<GameData> listGames(String authToken) throws AuthFailedException {
    verifyAuth(authToken);
    return data.getAllGames();
  }

  public void createGame(String authToken) throws AuthFailedException {
    verifyAuth(authToken);

  }

  public void joinGame(String authToken) throws AuthFailedException {
    verifyAuth(authToken);
  }

  public void verifyAuth(String authToken) throws AuthFailedException {
    if(data.getAuth(authToken) == null){
      throw new AuthFailedException("Unauthorized");
    }
  }
}
