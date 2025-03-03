package chess;

import java.util.Collection;

/**
 * Represents moving a King piece on a chessboard
 */
public class KingMove extends PieceMove {

    public KingMove(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
    }

    /**
     * @return ChessPosition of ending location
     */
    @Override
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
