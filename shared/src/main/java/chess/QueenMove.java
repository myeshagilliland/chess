package chess;

import java.util.Collection;

/**
 * Represents moving a Queen piece on a chessboard
 */
public class QueenMove extends PieceMove {

    public QueenMove(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
    }

    /**
     * @return ChessPosition of ending location
     */
    public Collection<ChessMove> getMoves() {
        Collection<ChessMove> bishopMoves = new BishopMove(chessBoard, myPosition).getMoves();
        Collection<ChessMove> rookMoves = new RookMove(chessBoard, myPosition).getMoves();

        moves.addAll(bishopMoves);
        moves.addAll(rookMoves);

        return moves;
    }

}
