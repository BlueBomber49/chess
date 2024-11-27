package ui;

import chess.*;
import exception.ResponseException;
import ui.evaluators.LoggedInEvaluator;
import ui.evaluators.LoggedOutEvaluator;
import websocket.messages.ServerMessage;
import model.AuthData;
import responseclasses.GameResponse;
import websocket.NotificationHandler;
import websocket.WebsocketFacade;

import java.util.*;

import static ui.EscapeSequences.*;

public class Client implements NotificationHandler {
  private ServerFacade facade;
  private String auth;
  public State state;
  private ArrayList<GameResponse> games;
  private WebsocketFacade ws;
  private String url;
  private ChessGame.TeamColor color;
  private Integer currentGameID;
  private LoggedOutEvaluator loggedOutEvaluator;
  public Client(String url){
    facade = new ServerFacade(url);
    state = State.LOGGED_OUT;
    games = new ArrayList<>();
    this.url = url;
    this.color = ChessGame.TeamColor.WHITE;
    this.loggedOutEvaluator = new LoggedOutEvaluator(facade, games, state);
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
          String returnString = loggedOutEvaluator.eval(cmd, params);
          state = loggedOutEvaluator.getState();
          auth = loggedOutEvaluator.getAuth();
          games = loggedOutEvaluator.getGames();
          return returnString;
        }
        case LOGGED_IN -> {
          LoggedInEvaluator loggedInEvaluator = new LoggedInEvaluator(facade, games, state, url, auth, this);
          String returnString = loggedInEvaluator.eval(cmd, params);
          facade = loggedInEvaluator.getFacade();
          state = loggedInEvaluator.getState();
          auth = loggedInEvaluator.getAuth();
          games = loggedInEvaluator.getGames();
          url = loggedInEvaluator.getUrl();
          ws = loggedInEvaluator.getWs();
          return returnString;
        }
        case PLAYING_GAME, OBSERVING_GAME -> {
          return inGameEval(cmd, params);
        }
        default -> {return "Broke";}
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
    switch(cmd) {
      case "leave" -> {
        return leaveGame();
      }
      case "redraw" -> {
        return "Chessboard goes here";
        //drawBoard();
      }
      case "resign" -> {
        return resignGame();
      }
      case "highlight" -> {
        return "I would love to highlight the board right now";
      }
      case "move" -> {
        return makeMove(params);
      }
      default -> {
        return "Not a recognized command.  Type 'help' for a list of commands";
      }
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
      case PLAYING_GAME -> """
              %nhelp:  Gets a list of available commands
              resign:  Admit defeat, ending the current game
              leave:  Leaves the current game
              redraw:  Redraws the chess board
              highlight [column & row]:  Highlights squares that the selected piece can move to
              move [starting column & row] [ending column & row]:  Moves the piece from the starting position to the end position
              """;
      case OBSERVING_GAME -> """
              %nhelp:  Gets a list of available commands
              leave:  Leaves the current game
              redraw:  Redraws the chess board
              highlight [column & row]:  Highlights squares that the selected piece can move to
              """;
      default -> "Something broke";
    };
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
      ws = new WebsocketFacade(url, this);
      ws.joinGame(auth, id);
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
      ws = new WebsocketFacade(url, this);
      this.state = State.OBSERVING_GAME;
    } catch (Exception e) {
      return "Error: " + e.getMessage();
    }
    return "";
  }

  public String leaveGame(){
    try {
      state=State.LOGGED_IN;
      ws.leaveGame(auth, currentGameID);
      return "You have left the game";
    }
    catch (Exception e){
      return "Error: " + e.getMessage();
    }
  }

  public String resignGame(){
    try{
      ws.resignGame(auth, currentGameID);
    } catch (ResponseException e) {
      return "Error: " + e.getMessage();
    }
    return "You have resigned";
  }

  public String makeMove(String[] params){
    try {
      if (params.length < 2 || params.length > 3) {
        return "Please use format 'move [start column][start row] [end column][end row] [Promotion R|B|N|Q]";
      }
      var colMap=Map.of("a", 1, "b", 2, "c", 3, "d", 4,
              "e", 5, "f", 6, "g", 7, "h", 8);
      String start=params[0];
      String end=params[1];
      if (!start.matches("\\b[a-h][1-8]\\b") || !end.matches("\\b[a-h][1-8]\\b")) {
        return "Please use format 'move [start column][start row] [end column][end row]";
      }
      int startRow=Integer.parseInt(start.substring(1));
      int startCol=colMap.get(start.substring(0, 1));
      int endRow=Integer.parseInt(end.substring(1));
      int endCol=colMap.get(end.substring(0, 1));
      ChessPosition startPos=new ChessPosition(startRow, startCol);
      ChessPosition endPos=new ChessPosition(endRow, endCol);
      ChessMove move;
      if (params.length == 3) {
        String promotionParam=params[2];
        ChessPiece.PieceType promotionPiece;
        switch (promotionParam.toUpperCase()) {
          case "R" -> {
            promotionPiece=ChessPiece.PieceType.ROOK;
          }
          case "N" -> {
            promotionPiece=ChessPiece.PieceType.KNIGHT;
          }
          case "B" -> {
            promotionPiece=ChessPiece.PieceType.BISHOP;
          }
          case "Q" -> {
            promotionPiece=ChessPiece.PieceType.QUEEN;
          }
          default -> {
            return "Invalid Promotion Piece";
          }
        }
        move=new ChessMove(startPos, endPos, promotionPiece);
      } else {
        move=new ChessMove(startPos, endPos, null);
      }
      ws.makeMove(auth, currentGameID, move);
    }
    catch(Exception e){
      return "Error: " + e.getMessage();
    }
    return "";
  }

  public String drawBoard(ChessGame game, ChessGame.TeamColor perspective){
    boolean flipped;
    String header;
    if(perspective == ChessGame.TeamColor.WHITE){
      flipped = true;
      header =  SET_BG_COLOR_LIGHT_GREY + SET_TEXT_BOLD + SET_TEXT_COLOR_BLACK +
              "    a  b  c  d  e  f  g  h    " + RESET_BG_COLOR + RESET_TEXT_COLOR + "%n";
    }
    else{
      flipped = false;
      header =  SET_BG_COLOR_LIGHT_GREY + SET_TEXT_BOLD + SET_TEXT_COLOR_BLACK +
              "    h  g  f  e  d  c  b  a    " + RESET_BG_COLOR + RESET_TEXT_COLOR + "%n";
    }
    String boardString = "%n" + header;
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
    boardString += header + "%n";
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

  @Override
  public void notify(ServerMessage message) {
    ServerMessage.ServerMessageType type = message.getServerMessageType();
    switch(type) {
      case LOAD_GAME -> {
        ChessGame game = message.getGame();
        drawBoard(game, color);
      }
      case NOTIFICATION -> {
        System.out.println("Notification: " + message.getMessage());
      }
      case ERROR -> {
        System.out.println("Error: " + message.getErrorMessage());
      }
    }

  }
}
