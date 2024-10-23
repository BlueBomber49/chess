package chess.movecalculators;

import chess.*;

import java.util.ArrayList;

public class PawnMoveCalculator extends MoveCalculator{
  /**
   * @param board
   * @param pieceType
   * @param color
   * @param position
   */
  public PawnMoveCalculator(ChessBoard board, ChessPiece.PieceType pieceType, ChessGame.TeamColor color, ChessPosition position) {
    super(board, pieceType, color, position);
  }

  public ArrayList<ChessMove> calculateValidMoves(){
    ChessPosition start = super.position;
    ChessPosition end = start;
    ChessGame.TeamColor color = super.color;
    ArrayList<ChessMove> moveList = new ArrayList<>();

    if(color == ChessGame.TeamColor.WHITE){ //White pawn moves
      //White: moves by increasing row.
      //If row = 2, can move up 2
      //Can capture if enemy piece is diagonally forward (Col +1/-1)
      //If row = 7, valid moves = all promotion pieces

      //Edge cases
      if(start.getRow() == 2){ //Double move option
        end = new ChessPosition(start.getRow() + 2, start.getColumn());
        if(board.getPiece(end) == null && board.getPiece(new ChessPosition(start.getRow()+1, start.getColumn())) == null) {
          ChessMove moveToAdd = new ChessMove(start, end, null);
          moveList.add(moveToAdd);
        }
      } else if(start.getRow() == 5){ //En Passant

      }


      if(start.getColumn() != 1){ //Left capture possible
        end = new ChessPosition(start.getRow() + 1, start.getColumn()-1);
        if(board.getPiece(end) != null && board.getPiece(end).getTeamColor() != color) {
          ChessMove moveToAdd = new ChessMove(start, end, null);
          moveList.add(moveToAdd);
        }
      }
      if(start.getColumn() != 8){ //Right capture possible
        end = new ChessPosition(start.getRow() + 1, start.getColumn()+1);
        if(board.getPiece(end) != null && board.getPiece(end).getTeamColor() != color) {
          ChessMove moveToAdd = new ChessMove(start, end, null);
          moveList.add(moveToAdd);
        }
      }

      //Default move
      end = new ChessPosition(start.getRow() + 1, start.getColumn());
      if(board.getPiece(end) == null) {
        ChessMove moveToAdd = new ChessMove(start, end, null);
        moveList.add(moveToAdd);
      }

      if(start.getRow() == 7){ //Promotion pieces need added to all moves
        ArrayList<ChessMove> promotionMoveList = new ArrayList<>();
        for(ChessMove move : moveList){
          for(var type : ChessPiece.PieceType.values()){
            if(type != ChessPiece.PieceType.KING && type != ChessPiece.PieceType.PAWN) {
              ChessMove moveToAdd=new ChessMove(move.getStartPosition(), move.getEndPosition(), type);
              promotionMoveList.add(moveToAdd);
            }
          }

        }
        return promotionMoveList;
      }

    } else{ //Black pawn moves
      //Black: moves by decreasing row
      //If row = 7, can move down 2
      //Can capture if enemy piece is diagonally down (Col +1/-1)
      //If row = 2, valid moves = all promotion pieces


      //Edge cases
      if(start.getRow() == 7){ //Double move option
        end = new ChessPosition(start.getRow() - 2, start.getColumn());
        if(board.getPiece(end) == null && board.getPiece(new ChessPosition(start.getRow()-1, start.getColumn())) == null) {
          ChessMove moveToAdd = new ChessMove(start, end, null);
          moveList.add(moveToAdd);
        }
      } else if(start.getRow() == 4){ //En Passant

      }


      if(start.getColumn() != 1){ //Left capture possible
        end = new ChessPosition(start.getRow() - 1, start.getColumn()-1);
        if(board.getPiece(end) != null && board.getPiece(end).getTeamColor() != color) {
          ChessMove moveToAdd = new ChessMove(start, end, null);
          moveList.add(moveToAdd);
        }
      }
      if(start.getColumn() != 8){ //Right capture possible
        end = new ChessPosition(start.getRow() - 1, start.getColumn()+1);
        if(board.getPiece(end) != null && board.getPiece(end).getTeamColor() != color) {
          ChessMove moveToAdd = new ChessMove(start, end, null);
          moveList.add(moveToAdd);
        }
      }

      //Default move
      end = new ChessPosition(start.getRow() - 1, start.getColumn());
      if(board.getPiece(end) == null) {
        ChessMove moveToAdd = new ChessMove(start, end, null);
        moveList.add(moveToAdd);
      }

      if(start.getRow() == 2){ //Promotion pieces need added to all moves
        ArrayList<ChessMove> promotionMoveList = new ArrayList<>();
        for(ChessMove move : moveList){
          for(var type : ChessPiece.PieceType.values()){
            if(type != ChessPiece.PieceType.KING && type != ChessPiece.PieceType.PAWN) {
              ChessMove moveToAdd=new ChessMove(move.getStartPosition(), move.getEndPosition(), type);
              promotionMoveList.add(moveToAdd);
            }
          }

        }
        return promotionMoveList;
      }

    }

    return moveList;
  }
}
