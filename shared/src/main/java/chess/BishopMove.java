package chess;

import java.util.Collection;

/**
 * Represents moving a Bishop piece on a chessboard
 */
public class BishopMove extends PieceMove {

    public BishopMove(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
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
            int newRow = row + i;
            int newCol = col + i;
            boolean valid = checkAddPosition(newRow, newCol, team);
            if (!valid) {break;}
        }
        //to bottom left
        for (int i = 1; i<8; i++) {
            int newRow = row - i;
            int newCol = col - i;
            boolean valid = checkAddPosition(newRow, newCol, team);
            if (!valid) {break;}
        }
        //to top left
        for (int i = 1; i<8; i++) {
            int newRow = row + i;
            int newCol = col - i;
            boolean valid = checkAddPosition(newRow, newCol, team);
            if (!valid) {break;}
        }
        //to bottom right
        for (int i = 1; i<8; i++) {
            int newRow = row - i;
            int newCol = col + i;
            boolean valid = checkAddPosition(newRow, newCol, team);
            if (!valid) {break;}
        }

        return moves;
    }

}
