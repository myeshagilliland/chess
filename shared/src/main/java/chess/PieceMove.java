package chess;

import java.util.ArrayList;
import java.util.Collection;

public abstract class PieceMove {
    protected ChessBoard chessBoard;
    protected ChessPosition myPosition;
    protected Collection<ChessMove> moves = new ArrayList<ChessMove>();

    public PieceMove(ChessBoard board, ChessPosition myPosition) {
        this.chessBoard = board;
        this.myPosition = myPosition;
    }

    /**
     * Adds a move to moves if it is valid
     * @return true if ok to check the next move
     */
    protected boolean checkAddPosition (int row, int col, ChessGame.TeamColor team) {
        ChessPosition possiblePosition = new ChessPosition(row, col);
        if (row < 1 || row > 8 || col < 1 || col > 8) {
            return false;
        } else if (chessBoard.getPiece(possiblePosition) == null) {
            moves.add(new ChessMove(myPosition, possiblePosition, null));
            return true;
        } else if (chessBoard.getPiece(possiblePosition).getTeamColor() != chessBoard.getPiece(myPosition).getTeamColor()) {
            moves.add(new ChessMove(myPosition, possiblePosition, null));
            return false;
        } else {
            return false;
        }
    }

    public abstract Collection<ChessMove> getMoves();
}
