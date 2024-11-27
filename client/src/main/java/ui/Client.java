package ui;

import chess.*;
import exception.ResponseException;
import ui.evaluators.InGameEvaluator;
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
  private ChessGame game;
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
          InGameEvaluator inGameEvaluator = new InGameEvaluator(auth, currentGameID, state, game, color, this, ws);
          String message = inGameEvaluator.eval(cmd, params);
          auth = inGameEvaluator.getAuth();
          currentGameID = inGameEvaluator.getCurrentGameID();
          state = inGameEvaluator.getState();
          game = inGameEvaluator.getGame();
          color = inGameEvaluator.getColor();
          ws = inGameEvaluator.getWs();
          return message;
        }
        default -> {return "Broke";}
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
        this.game = game;
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
