import chess.*;
import ui.chessBoardUI;
import ui.preloginUI;

public class Main {
    public static void main(String[] args) {
//        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
//        System.out.println("♕ 240 Chess Client: " + piece);

        int port = 8080;
//        if (args.length == 1) {
//            port = Integer.parseInt(args[0]);
//        }
        System.out.println("Welcome to Chess! Type 'help' to begin.");
//        new chessBoardUI(new ChessGame(), "BLACK");

        new preloginUI(port);
    }
}