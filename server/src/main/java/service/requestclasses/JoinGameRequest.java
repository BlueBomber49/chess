package service.requestclasses;

import chess.ChessGame;

public record JoinGameRequest(ChessGame.TeamColor playerColor, Integer gameID) {
}