package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import exception.BadInputException;
import exception.ResponseException;
import model.AuthData;
import responseclasses.GameResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

import static ui.EscapeSequences.*;

public class Client {
  private ServerFacade facade;
  private String auth;
  private String currentUser;
  public State state;
  private ArrayList<GameResponse> games;
  public Client(String url){
    facade = new ServerFacade(url);
    auth = null;
    currentUser = null;
    state = State.LOGGED_OUT;
    games = new ArrayList<>();
  }

  public void run(){
    System.out.println("Welcome to TerminalChess!  Login to start.");
    System.out.printf(this.help());
    while(state != State.QUIT){
      new Repl(this).run();
    }
    System.out.println("See ya later!");
  }

  public String eval(String input){
    var tokens = input.toLowerCase().split(" ");
    String cmd;
    if(tokens.length > 0){
      cmd = tokens[0];
    }
    else{
      cmd = "";
    }
    var params = Arrays.copyOfRange(tokens, 1, tokens.length);
    if(Objects.equals(cmd, "help")){
      return this.help();
    }
    else {
      switch (state) {
        case LOGGED_OUT -> {
          return loggedOutEval(cmd, params);
        }
        case LOGGED_IN -> {
          return loggedInEval(cmd, params);
        }
        case PLAYING_GAME -> {
          return inGameEval(cmd, params);
        }
        default -> {return "Broke";}
      }
    }
  }



  public String loggedOutEval(String cmd, String[] params){
    switch (cmd) {
      case "help" -> {
        return this.help();
      }
      case "quit" -> {
        this.quit();
        return "quit";
      }
      case "register" -> {
        var response=this.register(params);
        try {
          games=facade.listGames(auth);
        }
        catch(Exception ignored){
        }
        return response;
      }
      case "login" -> {
        var response=this.login(params);
        try {
          games=facade.listGames(auth);
        }
        catch(Exception ignored){
        }
        return response;
      }
      default -> {
        return "Command not recognized.  Type 'help' for a list of commands";
      }
    }
  }

  public String loggedInEval(String cmd, String[] params){
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

  private String inGameEval(String cmd, String[] params) {
    if(Objects.equals(cmd, "leave")){
      state = State.LOGGED_IN;
      return "You have left the game";
    }
    else{
      return "Not implemented";
    }
  }

  public String help(){
    return switch(state){
      case LOGGED_OUT -> """
              %nhelp:  Gets a list of available commands
              register [username] [password] [email]: Register as a new user
              login [username] [password]: Login as an existing user
              quit:  Terminates the program
              """;
      case LOGGED_IN -> """
              %nhelp:  Gets a list of available commands
              logout:  Logs out and returns you to the previous page
              list:  Gets a list of available games with their id's
              create [game name]:  Creates a new game with the given name
              join [ID] [WHITE|BLACK]:  Joins the game as the chosen color
              observe [ID]:  Joins the game as an observer
              """;
      case PLAYING_GAME -> "Phase 6 stuff";
      default -> "Something broke";
    };
  }

  public String quit(){
    state = State.QUIT;
    return "quit";
  }

  public String register(String[] params){
    try{
      if(params.length != 3){
        return "Incorrect register format.  Please use the format 'register [username] [password] [email]'";
      }
      var username = params[0];
      var password = params[1];
      var email = params[2];
      AuthData authData = facade.register(username, password, email);
      auth = authData.authToken();
      currentUser = authData.username();
      state = State.LOGGED_IN;
      return "Registration successful.  Welcome, " + username + "!";
    }
    catch (Exception e){
      if(e.getClass() == ResponseException.class){
        return "That username is taken.  Please select a different username";
      }
      return "Error: " + e.getMessage(); //Change this?
    }
  }

  public String login(String[] params ){
    try {
      if(params.length != 2){
        return "Incorrect login format.  Please use the format 'login [username] [password] ";
      }
      var username = params[0];
      var password = params[1];
      var authData = facade.login(username, password);
      auth = authData.authToken();
      currentUser = authData.username();
      state = State.LOGGED_IN;
      return "Login successful.  Welcome, " + username + "!";
    }
    catch(Exception e){
      if(e.getMessage().startsWith("failure: 401")){
        return "Username or password incorrect";
      }
      return "Error: " + e.getMessage(); //Change this at some point
    }
  }

  public String logout(){
    try {
      facade.logout(auth);
      auth = null;
      currentUser = null;
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
      this.state = State.PLAYING_GAME;
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
    drawBoard(new ChessGame(), ChessGame.TeamColor.WHITE);
    System.out.println();
    drawBoard(new ChessGame(), ChessGame.TeamColor.BLACK);
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
      this.state = State.PLAYING_GAME;
    } catch (Exception e) {
      return "Error: " + e.getMessage();
    }
    drawBoard(new ChessGame(), ChessGame.TeamColor.WHITE);
    System.out.println();
    drawBoard(new ChessGame(), ChessGame.TeamColor.BLACK);
    return "";
  }
  public String drawBoard(ChessGame game, ChessGame.TeamColor perspective){
    boolean flipped;
    String header;
    if(perspective == ChessGame.TeamColor.WHITE){
      flipped = true;
      header = SET_BG_COLOR_LIGHT_GREY + SET_TEXT_BOLD + SET_TEXT_COLOR_BLACK +
              "    a  b  c  d  e  f  g  h    " + RESET_BG_COLOR + RESET_TEXT_COLOR + "%n";
    }
    else{
      flipped = false;
      header = SET_BG_COLOR_LIGHT_GREY + SET_TEXT_BOLD + SET_TEXT_COLOR_BLACK +
              "    h  g  f  e  d  c  b  a    " + RESET_BG_COLOR + RESET_TEXT_COLOR + "%n";
    }
    String boardString = header;
    ChessBoard board = game.getBoard();
    if(!flipped) {
      for (var row=1; row <= 8; row++) {
        boardString += drawRow(board, row, flipped);
      }
    }
    else {
      for (var row=8; row >= 1; row--) {
        boardString += drawRow(board, row, flipped);
      }
    }
    boardString += header;
    System.out.printf(boardString);
    return boardString;
  }

  public String drawRow(ChessBoard board, int row, boolean flipped){
    String rowString = SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + " " + row + " ";
    rowString += SET_TEXT_COLOR_LIGHT_GREY;

    var whiteSquareRow = 0;
    if(flipped){
      whiteSquareRow = 1;
    }
    for(var col = 1; col <= 8; col ++){
      if(row % 2 == whiteSquareRow && col % 2 == 1){
        rowString += SET_BG_COLOR_BLACK;
      }
      else if(row % 2 != whiteSquareRow && col % 2 == 0){
        rowString += SET_BG_COLOR_BLACK;
      }
      else {
        rowString += SET_BG_COLOR_WHITE;
      }
      String pieceString = "";
      ChessPiece piece = board.getPiece(new ChessPosition(row, col));
      if(!flipped){
        int newcol = 9 - col;
        piece = board.getPiece(new ChessPosition(row, newcol));
      }
      ChessPiece.PieceType type = null;
      if(piece != null){
        type = piece.getPieceType();
      }

      switch(type) {
        case ROOK -> pieceString += " r";
        case BISHOP -> pieceString += " b";
        case KNIGHT -> pieceString += " n";
        case QUEEN -> pieceString += " q";
        case KING -> pieceString += " k";
        case PAWN -> pieceString += " p";
        case null -> pieceString += "  ";
      }

      if(piece != null && piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)){
        pieceString = pieceString.toUpperCase(Locale.ROOT);
      }
      pieceString += " ";
      rowString += pieceString;
    }
    rowString += SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK;
    rowString += " " + row + " ";
    rowString +=  RESET_TEXT_COLOR + RESET_BG_COLOR + "%n";
    return rowString;
  }
}
