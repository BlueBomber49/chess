package ui.evaluators;

import chess.ChessGame;
import exception.ResponseException;
import responseclasses.GameResponse;
import ui.Client;
import ui.ServerFacade;
import ui.State;
import websocket.WebsocketFacade;

import java.util.ArrayList;
import java.util.Objects;

public class LoggedInEvaluator {

  private ServerFacade facade;
  private ArrayList<GameResponse> games;
  private String auth;
  private State state;
  private ChessGame.TeamColor color;
  private Integer currentGameID;
  private WebsocketFacade ws;
  private String url;
  private Client client;
  public LoggedInEvaluator(ServerFacade facade, ArrayList<GameResponse> games, State state, String url, String auth, Client client){
    this.facade = facade;
    this.games = games;
    this.state = state;
    this.color = ChessGame.TeamColor.WHITE;
    this.url = url;
    this.client = client;
    this.auth = auth;
  }

  public String eval(String cmd, String[] params){
    switch (cmd) {
      case "logout" -> {
        return this.logout();
      }
      case "create" -> {
        return this.createGame(params);
      }
      case "list" -> {
        return this.listGames();
      }
      case "join" -> {
        return this.joinGame(params);
      }
      case "observe" -> {
        return this.observeGame(params);
      }
      default -> {
        return "Command not recognized.  Type 'help' for a list of commands";
      }
    }
  }

  public String logout(){
    try {
      facade.logout(auth);
      auth = null;
      state = State.LOGGED_OUT;
      return "Logged out.  See ya later!";
    }
    catch(Exception e) {
      if(e.getMessage().startsWith("failure: 401")){
        return "AuthToken not found.  This shouldn't happen";
      }
      return "Error: " + e.getMessage(); //Change at some point
    }
  }

  public String createGame(String[] params){
    try {
      String name = "";
      if(params.length > 1){
        for(String s : params){
          name += s + " ";
        }
        name = name.substring(0, name.length()-1);
      }
      else if(params.length < 1){
        return "Please try again, and input a game name";
      }
      else {
        name=params[0];
      }
      facade.createGame(auth, name);
      this.listGames();
      return "Game '" + name + "' created successfully!" ;
    }
    catch(Exception e) {
      return "Error: " + e.getMessage(); //Change at some point
    }
  }

  public String listGames(){
    try{
      ArrayList<GameResponse> gameList = facade.listGames(auth);
      var result = "";
      games = gameList;
      if(gameList.isEmpty()){
        return "No games are currently in progress";
      }
      for(var i = 1; i <= gameList.size(); i ++){
        var game = gameList.get(i-1);
        var white = (game.whiteUsername() != null) ? game.whiteUsername(): "None";
        var black = (game.blackUsername() != null) ? game.blackUsername(): "None";
        result += i + ") " + game.gameName() + ", White player: " + white + ", Black player: " + black + "%n";
      }
      return result;
    }
    catch(Exception e){
      return "Error: " + e.getMessage();
    }
  }

  public String joinGame(String[] params){
    ChessGame.TeamColor color;
    try{
      if(games.isEmpty()){
        facade.listGames(auth);
        if(games.isEmpty()){
          return "No games currently exist.  Use 'create [gameName]' to start a new game";
        }
      }
      if(params.length != 2){
        return "Please use format 'join [ID] [WHITE|BLACK]'";
      }
      Integer id = Integer.valueOf(params[0]);
      if(id > games.size() || id < 1){
        return "Invalid Game ID.  Use 'list' to get a list of games with their id's";
      }
      if(Objects.equals(params[1], "white")){
        color = ChessGame.TeamColor.WHITE;
      }
      else if(Objects.equals(params[1], "black")){
        color = ChessGame.TeamColor.BLACK;
      }
      else{
        return "Please ensure that your color is either WHITE or BLACK";
      }
      id = games.get(id - 1).gameID();
      facade.joinGame(auth, id, color);
      ws = new WebsocketFacade(url, client);
      ws.joinGame(auth, id, color);
      this.state = State.PLAYING_GAME;
      this.color = color;
      this.currentGameID = id;
    }
    catch(NumberFormatException n){
      return "Please ensure that ID is an integer";
    }
    catch(ResponseException e){
      if(e.getMessage().startsWith("failure: 403")) {
        return "Sorry, that color is taken";
      }
      else{
        return "Error: " + e.errorCode() + " " + e.getMessage();
      }
    }
    return "";
  }

  public String observeGame(String[] params) {
    try {
      if (games.isEmpty()) {
        facade.listGames(auth);
        if (games.isEmpty()) {
          return "No games currently exist.  Use 'create [gameName]' to start a new game";
        }
      }
      if (params.length != 1) {
        return "Please use format 'observe [ID]'";
      }
      int id;
      try {
        id=Integer.parseInt(params[0]);
      }
      catch(Exception e){
        return "Please use the format 'observe [ID]'.  (ID must be an integer)";
      }
      if (id > games.size() || id < 1) {
        return "Invalid Game ID.  Use 'list' to get a list of games with their id's";
      }
      ws = new WebsocketFacade(url, client);
      ws.observeGame(auth, id);
      this.state = State.OBSERVING_GAME;
      this.currentGameID = id;
    } catch (Exception e) {
      return "Error: " + e.getMessage();
    }
    return "";
  }

  public Integer getCurrentGameID() {
    return currentGameID;
  }

  public State getState() {
    return state;
  }

  public ChessGame.TeamColor getColor() {
    return color;
  }

  public String getAuth() {
    return auth;
  }

  public ServerFacade getFacade() {
    return facade;
  }

  public String getUrl() {
    return url;
  }

  public ArrayList<GameResponse> getGames() {
    return games;
  }

  public WebsocketFacade getWs() {
    return ws;
  }
}
