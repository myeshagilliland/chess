package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents moving a Bishop piece on a chessboard
 */
public class QueenMove {

    private ChessBoard chessBoard;
    private ChessPosition myPosition;
    private Collection<ChessMove> moves = new ArrayList<ChessMove>();

    public QueenMove(ChessBoard board, ChessPosition myPosition) {
        this.chessBoard = board;
        this.myPosition = myPosition;
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
