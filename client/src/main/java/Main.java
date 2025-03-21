import chess.*;
import ui.chessBoardUI;
import ui.preloginUI;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        new chessBoardUI(new ChessGame(), "BLACK");

        new preloginUI(args);
    }
}