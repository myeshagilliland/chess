package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class RookMove {

    private ChessBoard chessBoard;
    private ChessPosition myPosition;
    private Collection<ChessMove> moves = new ArrayList<ChessMove>(); //handle null case

    public RookMove(ChessBoard board, ChessPosition myPosition) {
        this.chessBoard = board;
        this.myPosition = myPosition;
//        this.moves = new ArrayList<ChessMove>();
    }

    /**
     * @return ChessPosition of starting location
     */
//    public ChessPosition getStartPosition() {
//        return startPosition;
//    }

//    public void checkAddPosition (int row, int col, ChessGame.TeamColor team) {
//        ChessPosition possiblePosition = new ChessPosition(row, col);
//        if (row < 1 || row > 8 || col < 1 || col > 8) {
//            return;
//        }
//        var pieceAtPosition = chessBoard.getPiece(possiblePosition);
////        var pieceType = pieceAtPosition.getPieceType();
//        if (pieceAtPosition == null) {
//            moves.add(new ChessMove(myPosition, possiblePosition, null));
//        } else if (chessBoard.getPiece(possiblePosition).getTeamColor() != chessBoard.getPiece(myPosition).getTeamColor()) {
//            moves.add(new ChessMove(myPosition, possiblePosition, null));
//            return;
//        } //|| team == chessBoard.getPiece(possiblePosition).getTeamColor()
//    }

    /**
     * @return ChessPosition of ending location
     */
    public Collection<ChessMove> getMoves() {
//        this.chessBoard = myBoard;
//        this.myPosition = myPosition;
//        int col = myPosition.getColumn()-1;
//        int row = 7-myPosition.getRow();
//        moves.clear();

        int col = myPosition.getColumn();
        int row = myPosition.getRow();
        ChessGame.TeamColor team = chessBoard.getPiece(myPosition).getTeamColor();

//        if (chessBoard.getPiece(myPosition).getPieceType() != ChessPiece.PieceType.ROOK) {
//            throw new IllegalArgumentException("Piece type must be Rook");
//        }

//        for (int i = col+1; i <=8; i++) {
//            checkAddPosition(row, i, team);
//        }
//        for (int i = col-1; i >=1; i--) {
//            checkAddPosition(row, i, team);
//        }
//        for (int i = row+1; i <=8; i++) {
//            checkAddPosition(row, i, team);
//        }
//        for (int i = row-1; i >=1; i--) {
//            checkAddPosition(row, i, team);
//        }

//
        // right
        for (int i = col+1; i <= 8; i++) {
            ChessPosition possiblePosition = new ChessPosition(row, i);
            if (chessBoard.getPiece(possiblePosition) == null) {
                moves.add(new ChessMove(myPosition, possiblePosition, null));
            } else if (chessBoard.getPiece(possiblePosition).getTeamColor() != chessBoard.getPiece(myPosition).getTeamColor()) {
                moves.add(new ChessMove(myPosition, possiblePosition, null));
                break;
            } else {break;}
        }
        // left
        for (int i = col-1; i >= 1; i--) {
            ChessPosition possiblePosition = new ChessPosition(row, i);
            if (chessBoard.getPiece(possiblePosition) == null) {
                moves.add(new ChessMove(myPosition, possiblePosition, null));
            } else if (chessBoard.getPiece(possiblePosition).getTeamColor() != chessBoard.getPiece(myPosition).getTeamColor()) {
                moves.add(new ChessMove(myPosition, possiblePosition, null));
                break;
            } else {break;}
        }
        // up
        for (int i = row+1; i <= 8; i++) {
            ChessPosition possiblePosition = new ChessPosition(i, col);
            if (chessBoard.getPiece(possiblePosition) == null) {
                moves.add(new ChessMove(myPosition, possiblePosition, null));
            } else if (chessBoard.getPiece(possiblePosition).getTeamColor() != chessBoard.getPiece(myPosition).getTeamColor()) {
                moves.add(new ChessMove(myPosition, possiblePosition, null));
                break;
            } else {break;}
        }
        // down
        for (int i = row-1; i >= 1; i--) {
            ChessPosition possiblePosition = new ChessPosition(i, col);
            if (chessBoard.getPiece(possiblePosition) == null) {
                moves.add(new ChessMove(myPosition, possiblePosition, null));
            } else if (chessBoard.getPiece(possiblePosition).getTeamColor() != chessBoard.getPiece(myPosition).getTeamColor()) {
                moves.add(new ChessMove(myPosition, possiblePosition, null));
                break;
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
