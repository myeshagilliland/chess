package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents moving a Bishop piece on a chessboard
 */
public class KnightMove {

    private ChessBoard chessBoard;
    private ChessPosition myPosition;
    private Collection<ChessMove> moves = new ArrayList<ChessMove>(); //handle null case

    public KnightMove(ChessBoard board, ChessPosition myPosition) {
        this.chessBoard = board;
        this.myPosition = myPosition;
//        this.moves = new ArrayList<ChessMove>();
    }

    /**
     * Adds a move to moves if it is valid
     * @return true if ok to check the next move
     */
    public boolean checkAddPosition (int row, int col, ChessGame.TeamColor team) {
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

    /**
     * @return ChessPosition of ending location
     */
    public Collection<ChessMove> getMoves() {
        int col = myPosition.getColumn();
        int row = myPosition.getRow();
        ChessGame.TeamColor team = chessBoard.getPiece(myPosition).getTeamColor();

        //up 2 left 1
        checkAddPosition(row+2, col-1, team);
        //up 2 right 1
        checkAddPosition(row+2, col+1, team);
        //right 2 up 1
        checkAddPosition(row+1, col+2, team);
        //right 2 down 1
        checkAddPosition(row-1, col+2, team);

        //down 2 right 1
        checkAddPosition(row-2, col+1, team);
        //down 2 left 1
        checkAddPosition(row-2, col-1, team);
        //left 2 down 1
        checkAddPosition(row-1, col-2, team);
        //left 2 up 1
        checkAddPosition(row+1, col-2, team);

        return moves;
    }

}
