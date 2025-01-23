package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents moving a Bishop piece on a chessboard
 */
public class QueenMove {

    private ChessBoard chessBoard;
    private ChessPosition myPosition;
    private Collection<ChessMove> moves = new ArrayList<ChessMove>(); //handle null case

    public QueenMove(ChessBoard board, ChessPosition myPosition) {
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

        //to top right
        for (int i = 1; i<8; i++) {
            int new_row = row + i;
            int new_col = col + i;
            boolean valid = checkAddPosition(new_row, new_col, team);
            if (!valid) {break;}
        }
        //to bottom left
        for (int i = 1; i<8; i++) {
            int new_row = row - i;
            int new_col = col - i;
            boolean valid = checkAddPosition(new_row, new_col, team);
            if (!valid) {break;}
        }
        //to top left
        for (int i = 1; i<8; i++) {
            int new_row = row + i;
            int new_col = col - i;
            boolean valid = checkAddPosition(new_row, new_col, team);
            if (!valid) {break;}
        }
        //to bottom right
        for (int i = 1; i<8; i++) {
            int new_row = row - i;
            int new_col = col + i;
            boolean valid = checkAddPosition(new_row, new_col, team);
            if (!valid) {break;}
        }

        //right
        for (int i = col+1; i <=8; i++) {
            boolean valid = checkAddPosition(row, i, team);
            if (!valid) {break;}
        }
        //left
        for (int i = col-1; i >=1; i--) {
            boolean valid = checkAddPosition(row, i, team);
            if (!valid) {break;}
        }
        //up
        for (int i = row+1; i <=8; i++) {
            boolean valid = checkAddPosition(i, col, team);
            if (!valid) {break;}
        }
        //down
        for (int i = row-1; i >=1; i--) {
            boolean valid = checkAddPosition(i, col, team);
            if (!valid) {break;}
        }

        return moves;
    }

}
