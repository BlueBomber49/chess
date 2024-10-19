package service;

import chess.ChessGame;
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

  public Integer createGame(String authToken, String gameName) throws AuthFailedException {
    verifyAuth(authToken);
    return data.createGame(gameName);
  }

  public void joinGame(String authToken, Integer gameID, String userName, ChessGame.TeamColor color) throws AuthFailedException, BadInputException {
    verifyAuth(authToken);
    GameData game = data.getGame(gameID);
    GameData newGame;
    if(game != null){
      if(color == ChessGame.TeamColor.WHITE){
        if(game.whiteUsername() != null){
          throw new BadInputException("White already taken");
        }
        newGame = new GameData(game.gameId(), userName, game.blackUsername(), game.gameName(), game.game());
      }
      else{
        if(game.blackUsername() != null){
          throw new BadInputException("Black already taken");
        }
        newGame = new GameData(game.gameId(), game.whiteUsername(), userName, game.gameName(), game.game());
      }
    } else{
      throw new BadInputException("Invalid GameID");
    }
    data.updateGame(newGame);
  }

  public void verifyAuth(String authToken) throws AuthFailedException {
    if(data.getAuth(authToken) == null){
      throw new AuthFailedException("Unauthorized");
    }
  }
}
