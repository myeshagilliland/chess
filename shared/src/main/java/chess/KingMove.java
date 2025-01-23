package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents moving a Bishop piece on a chessboard
 */
public class KingMove {

    private ChessBoard chessBoard;
    private ChessPosition myPosition;
    private Collection<ChessMove> moves = new ArrayList<ChessMove>(); //handle null case

    public KingMove(ChessBoard board, ChessPosition myPosition) {
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

        //right
        checkAddPosition(row, col+1, team);
        //left
        checkAddPosition(row, col-1, team);
        //up
        checkAddPosition(row+1, col, team);
        //down
        checkAddPosition(row-1, col, team);

        //to top right
        checkAddPosition(row+1, col+1, team);
        //to bottom left
        checkAddPosition(row-1, col-1, team);
        //to top left
        checkAddPosition(row+1, col-1, team);
        //to bottom right
        checkAddPosition(row-1, col+1, team);

        return moves;
    }

}
