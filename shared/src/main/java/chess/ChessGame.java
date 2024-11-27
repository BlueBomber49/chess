package chess;

import org.junit.platform.commons.util.BlacklistedExceptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard gameBoard;
    private TeamColor currentTurn;
    private boolean whiteCanCastle;
    private boolean blackCanCastle;
    public boolean isFinished;

    public ChessGame() {
        gameBoard = new ChessBoard();
        gameBoard.resetBoard();
        currentTurn = TeamColor.WHITE;
        isFinished = false;
        whiteCanCastle = true;
        blackCanCastle = true;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTurn;
    }

    public boolean isFinished(){return isFinished;}

    public void finish(){isFinished = true;}

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition){
        //setup
        ArrayList<ChessMove> validPieceMoves = new ArrayList<>();
        ChessPiece piece = gameBoard.getPiece(startPosition);
        if(piece == null){
            return null;
        }
        Collection<ChessMove> chessMoves = piece.pieceMoves(gameBoard, startPosition);
        TeamColor team = piece.getTeamColor();
        for(ChessMove testMove : chessMoves){
            try {
                ChessPiece capturedPiece = gameBoard.getPiece(testMove.getEndPosition());
                gameBoard.movePiece(testMove); //Setting the theoretical board up
                if(!isInCheck(team)) {
                    validPieceMoves.add(testMove);
                }
                ChessMove moveBack;
                if(testMove.getPromotionPiece() != null){
                    moveBack = new ChessMove(testMove.getEndPosition(), testMove.getStartPosition(), ChessPiece.PieceType.PAWN);
                }
                else{
                    moveBack = new ChessMove(testMove.getEndPosition(), testMove.getStartPosition(),null);
                }
                gameBoard.movePiece(moveBack);
                gameBoard.addPiece(testMove.getEndPosition(), capturedPiece);
            }
            catch(InvalidMoveException e){
                continue;
            }
        }
        return validPieceMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = gameBoard.getPiece(move.getStartPosition());
        if(piece == null){
            throw new InvalidMoveException("No piece selected");
        }
        if(piece.getTeamColor() != getTeamTurn()){
            throw new InvalidMoveException("Not your turn");
        }

        if(validMoves(move.getStartPosition()).contains(move)){
                gameBoard.movePiece(move);
        }
        else{
            throw new InvalidMoveException("Invalid Move");
        }
        if(getTeamTurn() == TeamColor.WHITE) {
            if(piece.getPieceType() == ChessPiece.PieceType.KING){
                whiteCanCastle = false;
            }
            setTeamTurn(TeamColor.BLACK);
        } else{
            if(piece.getPieceType() == ChessPiece.PieceType.KING){
                blackCanCastle = false;
            }
          setTeamTurn(TeamColor.WHITE);
        }

    }

    public Collection<ChessMove> getAllTeamMoves(TeamColor color){
        ArrayList<ChessMove> teamMoves = new ArrayList<>();
        if (isFinished){
            return teamMoves;
        }
        for(int row = 1; row < 9; row ++) {
            for(int col = 1; col < 9; col++){
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = gameBoard.getPiece(pos);
                if(piece != null && piece.getTeamColor() == color){
                    Collection<ChessMove> moves = piece.pieceMoves(gameBoard, pos);
                    teamMoves.addAll(moves);
                }
            }
        }
        return teamMoves;
        //For each piece of a given team, get all of their moves.
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        Collection<ChessMove> enemyMoves;
        if(teamColor == TeamColor.WHITE){
            enemyMoves = getAllTeamMoves(TeamColor.BLACK);
        }
        else{
            enemyMoves = getAllTeamMoves(TeamColor.WHITE);
        }
        ChessPosition kingPos = gameBoard.getKingPosition(teamColor);
        for(ChessMove enemyMove : enemyMoves){
            if(enemyMove.getEndPosition().equals(kingPos)){
                return true;
            }
        }
        return false;


    }


    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if(!checkForValidMoves(teamColor) && isInCheck(teamColor)){
            finish();
            return true;
        }
        return false;
    }

    public boolean checkForValidMoves(TeamColor teamColor){
        for(int row = 1; row < 9; row++){
            for(int col = 1; col < 9; col++){
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = gameBoard.getPiece(new ChessPosition(row, col));
                if(piece != null && piece.getTeamColor() == teamColor){
                    if(!validMoves(pos).isEmpty()){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(!checkForValidMoves(teamColor) && !isInCheck(teamColor)){
            finish();
            return true;
        }
        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        gameBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameBoard;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame=(ChessGame) o;
        return Objects.deepEquals(gameBoard, chessGame.gameBoard) && currentTurn == chessGame.currentTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameBoard, currentTurn);
    }
}
