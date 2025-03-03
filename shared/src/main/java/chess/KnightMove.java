package chess;

import java.util.Collection;

/**
 * Represents moving a Knight piece on a chessboard
 */
public class KnightMove extends PieceMove {

    public KnightMove(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
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
