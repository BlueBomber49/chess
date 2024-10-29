package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import model.GameData;
import service.exception.AuthFailedException;
import service.exception.BadInputException;
import service.exception.ColorTakenException;

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

  public void joinGame(String authToken, Integer gameID, ChessGame.TeamColor color)
          throws AuthFailedException, BadInputException, ColorTakenException {
    String userName = verifyAuth(authToken);
    if(gameID != null) {
      GameData game=data.getGame(gameID);
      GameData newGame;
      if (game != null) {
        if (color == ChessGame.TeamColor.WHITE) {
          if (game.whiteUsername() != null) {
            throw new ColorTakenException("Error: White already taken");
          }
          newGame=new GameData(game.gameId(), userName, game.blackUsername(), game.gameName(), game.game());
        } else if (color == ChessGame.TeamColor.BLACK) {
          if (game.blackUsername() != null) {
            throw new ColorTakenException("Error: Black already taken");
          }
          newGame=new GameData(game.gameId(), game.whiteUsername(), userName, game.gameName(), game.game());
        } else {
          throw new BadInputException("Error: Invalid Color");
        }
      } else {
        throw new BadInputException("Error: Invalid GameID");
      }
      data.updateGame(newGame);
    }
    else{
      throw new BadInputException("Error: GameID is null");
    }
  }

  public String verifyAuth(String authToken) throws AuthFailedException {
    if(data.getAuth(authToken) == null){
      throw new AuthFailedException("Error: Unauthorized");
    }
    return data.getAuth(authToken).username();
  }
}
