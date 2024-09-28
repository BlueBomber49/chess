package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static chess.ChessPiece.PieceType.*;
import static java.util.Objects.isNull;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] squares = new ChessPiece[8][8];
    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
    return squares[position.getRow()-1][position.getColumn()-1];
    }

    public ChessPosition getKingPosition(ChessGame.TeamColor color){
        for(int row = 1; row < 9; row ++) {
            for(int col = 1; col < 9; col++){
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = getPiece(pos);
                if(piece != null && piece.getTeamColor() == color && piece.getPieceType() == KING){
                    return new ChessPosition(row, col);
                }
            }
        }
        return null;
    }

    public void movePiece(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece.PieceType promotionPiece = move.getPromotionPiece();

        if(squares[start.getRow()-1][start.getColumn()-1] == null){
            throw new InvalidMoveException("No piece in start position for move");
        }

        ChessGame.TeamColor color = getPiece(start).getTeamColor();

        if(promotionPiece == null){
            addPiece(end, getPiece(start));
        }
        else{
            addPiece(end, new ChessPiece(color, promotionPiece));
        }
        addPiece(start, null);
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        squares = new ChessPiece[8][8];
        ChessPiece K = new ChessPiece(ChessGame.TeamColor.WHITE, KING);
        ChessPiece Q = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        ChessPiece B = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        ChessPiece N = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        ChessPiece R = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        ChessPiece P = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);

        ChessPiece k = new ChessPiece(ChessGame.TeamColor.BLACK, KING);
        ChessPiece q = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        ChessPiece b = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        ChessPiece n = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        ChessPiece r = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        ChessPiece p = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);

        ChessPiece [] whiteBackRow = new ChessPiece[]{R, N, B, Q, K, B, N, R};
        ChessPiece [] blackBackRow = new ChessPiece[]{r, n, b, q, k, b, n, r};

        for(int i = 0; i < squares.length; i++){
            squares[0][i] = whiteBackRow[i];
        }

        for(int i = 0; i < squares.length; i++){
            squares[1][i] = P;
        }

        for(int i = 0; i < squares.length; i++){
            squares[7][i] = blackBackRow[i];
        }

        for(int i = 0; i < squares.length; i++){
            squares[6][i] = p;
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that=(ChessBoard) o;
        return Arrays.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    @Override
    public String toString() {
        String board = "";
        for(var row : squares){
            board += "|";
            for(var columnPiece : row){
                if(isNull(columnPiece)){
                    board += " |";
                } else{
                    board += columnPiece.toString() + "|";
                }
            }
            board += "\n";
        }
        return board;
    }
}
