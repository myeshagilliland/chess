package chess;

import java.util.Collection;

/**
 * Represents moving a Rook piece on a chessboard
 */
public class RookMove extends PieceMove {

    public RookMove(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
    }

    /**
     * @return ChessPosition of ending location
     */
    public Collection<ChessMove> getMoves() {
        int col = myPosition.getColumn();
        int row = myPosition.getRow();
        ChessGame.TeamColor team = chessBoard.getPiece(myPosition).getTeamColor();

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
