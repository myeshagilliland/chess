package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class RookMove {

    private ChessBoard chessBoard;
    private ChessPosition startPosition;
    private Collection<ChessPosition> moves; //handle null case

//    public RookMove(ChessPosition startPosition, ChessPosition endPosition,
//                    ChessPiece.PieceType promotionPiece) {
//        this.startPosition = startPosition;
//        this.endPosition = endPosition;
//        this.promotionPiece = promotionPiece;
//    }

    /**
     * @return ChessPosition of starting location
     */
//    public ChessPosition getStartPosition() {
//        return startPosition;
//    }

    public void checkPosition (ChessPosition myPosition, ChessPosition possiblePosition) {
        if (chessBoard.getPiece(possiblePosition) != null) {
            moves.add(possiblePosition);
        } else if (chessBoard.getPiece(possiblePosition).getTeamColor() == chessBoard.getPiece(myPosition).getTeamColor()) {
            moves.add(possiblePosition);
        } //else {break;} THINK THIS THROUGH
    }

    /**
     * @return ChessPosition of ending location
     */
    public Collection<ChessPosition> getMoves(ChessBoard myBoard, ChessPosition myPosition) {
        this.chessBoard = myBoard;
        this.startPosition = myPosition;
        int col = myPosition.getColumn()-1;
        int row = myPosition.getRow()-1;

//        if (chessBoard.getPiece(myPosition).getPieceType() != ChessPiece.PieceType.ROOK) {
//            throw new IllegalArgumentException("Piece type must be Rook");
//        }

        // forward
        for (int i = col+1; i <= 8; i++) {
            ChessPosition possiblePosition = new ChessPosition(row, i);
            if (chessBoard.getPiece(possiblePosition) != null) {
                moves.add(possiblePosition);
            } else if (chessBoard.getPiece(possiblePosition).getTeamColor() == chessBoard.getPiece(myPosition).getTeamColor()) {
                moves.add(possiblePosition);
            } else {break;}
        }
        // backward
        for (int i = col-1; i >= 1; i--) {
            ChessPosition possiblePosition = new ChessPosition(row, i);
            if (chessBoard.getPiece(possiblePosition) != null) {
                moves.add(possiblePosition);
            } else if (chessBoard.getPiece(possiblePosition).getTeamColor() == chessBoard.getPiece(myPosition).getTeamColor()) {
                moves.add(possiblePosition);
            } else {break;}
        }
        // right
        for (int i = row+1; i <= 8; i++) {
            ChessPosition possiblePosition = new ChessPosition(i, col);
            if (chessBoard.getPiece(possiblePosition) != null) {
                moves.add(possiblePosition);
            } else if (chessBoard.getPiece(possiblePosition).getTeamColor() == chessBoard.getPiece(myPosition).getTeamColor()) {
                moves.add(possiblePosition);
            } else {break;}
        }
        // left
        for (int i = row-1; i >= 1; i--) {
            ChessPosition possiblePosition = new ChessPosition(i, col);
            if (chessBoard.getPiece(possiblePosition) != null) {
                moves.add(possiblePosition);
            } else if (chessBoard.getPiece(possiblePosition).getTeamColor() == chessBoard.getPiece(myPosition).getTeamColor()) {
                moves.add(possiblePosition);
            } else {break;}
        }
        return moves;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
//    public ChessPiece.PieceType getPromotionPiece() {
//        return promotionPiece;
//    }

    }
