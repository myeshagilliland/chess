package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents moving a Bishop piece on a chessboard
 */
public class PawnMove {

    private ChessBoard chessBoard;
    private ChessPosition myPosition;
    private Collection<ChessMove> moves = new ArrayList<ChessMove>();

    public PawnMove(ChessBoard board, ChessPosition myPosition) {
        this.chessBoard = board;
        this.myPosition = myPosition;
    }

    public boolean onBoard (ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();
        if (row < 1 || row > 8 || col < 1 || col > 8) {
            return false;
        }
        return true;
    }

    public void moveCheckPromote (ChessPosition possiblePosition, ChessGame.TeamColor team) {
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

        ChessPosition possiblePosition;
        //forward 1
        if (team == ChessGame.TeamColor.WHITE) {
            possiblePosition = new ChessPosition(row + 1, col);
        } else {
            possiblePosition = new ChessPosition(row - 1, col);
        }
        if (onBoard(possiblePosition) && chessBoard.getPiece(possiblePosition) == null) {
            moveCheckPromote(possiblePosition, team);
            //forward 2
            if ((team == ChessGame.TeamColor.WHITE && row == 2)) {
                possiblePosition = new ChessPosition(row + 2, col);
                if (onBoard(possiblePosition) && chessBoard.getPiece(possiblePosition) == null) {
                    moveCheckPromote(possiblePosition, team);
                }
            } else if (team == ChessGame.TeamColor.BLACK && row == 7) {
                possiblePosition = new ChessPosition(row - 2, col);
                if (onBoard(possiblePosition) && chessBoard.getPiece(possiblePosition) == null) {
                    moveCheckPromote(possiblePosition, team);
                }
            }

        }

        //left diagonal capture
        if (team == ChessGame.TeamColor.WHITE) {
            possiblePosition = new ChessPosition(row + 1, col - 1);
        } else {
            possiblePosition = new ChessPosition(row - 1, col - 1);
        }
        if (onBoard(possiblePosition) && chessBoard.getPiece(possiblePosition) != null
                && chessBoard.getPiece(possiblePosition).getTeamColor() != team) {
            moveCheckPromote(possiblePosition, team);
        }
        //right diagonal capture
        if (team == ChessGame.TeamColor.WHITE) {
            possiblePosition = new ChessPosition(row + 1, col + 1);
        } else {
            possiblePosition = new ChessPosition(row - 1, col + 1);
        }
        if (onBoard(possiblePosition) && chessBoard.getPiece(possiblePosition) != null
                && chessBoard.getPiece(possiblePosition).getTeamColor() != team) {
            moveCheckPromote(possiblePosition, team);
        }

        return moves;
    }

}
