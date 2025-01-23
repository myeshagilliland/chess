package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents moving a Bishop piece on a chessboard
 */
public class PawnMove {

    private ChessBoard chessBoard;
    private ChessPosition myPosition;
    private ChessPiece.PieceType promotionType;
    private Collection<ChessMove> moves = new ArrayList<ChessMove>(); //handle null case

    public PawnMove(ChessBoard board, ChessPosition myPosition) {
        this.chessBoard = board;
        this.myPosition = myPosition;
//        this.moves = new ArrayList<ChessMove>();
    }

    /**
     * Adds a move to moves if it is valid
     * @return true if ok to check the next move
     */
//    public boolean checkAddPosition (int row, int col, ChessGame.TeamColor team) {
//        ChessPosition possiblePosition = new ChessPosition(row, col);
//        if (row < 1 || row > 8 || col < 1 || col > 8) {
//            return false;
//        } else if (chessBoard.getPiece(possiblePosition) == null) {
//            moves.add(new ChessMove(myPosition, possiblePosition, null));
//            return true;
//        } else if (chessBoard.getPiece(possiblePosition).getTeamColor() != chessBoard.getPiece(myPosition).getTeamColor()) {
//            moves.add(new ChessMove(myPosition, possiblePosition, null));
//            return false;
//        } else {
//            return false;
//        }
//    }

    public boolean onBoard (ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();
        if (row < 1 || row > 8 || col < 1 || col > 8) {
            return false;
        }
        return true;
    }

    public void moveCheckPromote (ChessPosition possiblePosition, ChessGame.TeamColor team, ChessPiece.PieceType promotionType) {
        int row = possiblePosition.getRow();
        if ((row == 8 && team == ChessGame.TeamColor.WHITE) || (row == 1 && team == ChessGame.TeamColor.BLACK)) {
            moves.add(new ChessMove(myPosition, possiblePosition, ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(myPosition, possiblePosition, ChessPiece.PieceType.KNIGHT));
            moves.add(new ChessMove(myPosition, possiblePosition, ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(myPosition, possiblePosition, ChessPiece.PieceType.QUEEN));
        } else { moves.add(new ChessMove(myPosition, possiblePosition, null));}
    }

    /**
     * @return ChessPosition of ending location
     */
    public Collection<ChessMove> getMoves() {

        int col = myPosition.getColumn();
        int row = myPosition.getRow();
        ChessGame.TeamColor team = chessBoard.getPiece(myPosition).getTeamColor();

//        checkAddPosition(row, col, team);

        ChessPosition possiblePosition;
        //forward 1
        if (team == ChessGame.TeamColor.WHITE) {
            possiblePosition = new ChessPosition(row + 1, col);
        } else {
            possiblePosition = new ChessPosition(row - 1, col);
        }
        if (onBoard(possiblePosition) && chessBoard.getPiece(possiblePosition) == null) {
            moveCheckPromote(possiblePosition, team, null);
            //forward 2
            if ((team == ChessGame.TeamColor.WHITE && row == 2)) {
                possiblePosition = new ChessPosition(row + 2, col);
                if (onBoard(possiblePosition) && chessBoard.getPiece(possiblePosition) == null) {
                    moveCheckPromote(possiblePosition, team, null);
                }
            } else if (team == ChessGame.TeamColor.BLACK && row == 7) {
                possiblePosition = new ChessPosition(row - 2, col);
                if (onBoard(possiblePosition) && chessBoard.getPiece(possiblePosition) == null) {
                    moveCheckPromote(possiblePosition, team, null);
                }
            }

        }

        //left diagonal capture
        if (team == ChessGame.TeamColor.WHITE) {
            possiblePosition = new ChessPosition(row + 1, col - 1);
        } else {
            possiblePosition = new ChessPosition(row - 1, col - 1);
        }
        if (onBoard(possiblePosition) && chessBoard.getPiece(possiblePosition) != null && chessBoard.getPiece(possiblePosition).getTeamColor() != team) {
            moveCheckPromote(possiblePosition, team, null);
        }
        //right diagonal capture
        if (team == ChessGame.TeamColor.WHITE) {
            possiblePosition = new ChessPosition(row + 1, col + 1);
        } else {
            possiblePosition = new ChessPosition(row - 1, col + 1);
        }
        if (onBoard(possiblePosition) && chessBoard.getPiece(possiblePosition) != null && chessBoard.getPiece(possiblePosition).getTeamColor() != team) {
            moveCheckPromote(possiblePosition, team, null);
        }

        return moves;
    }

}
