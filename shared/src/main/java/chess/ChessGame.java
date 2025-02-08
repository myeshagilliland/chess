package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    private TeamColor teamTurn;
    private Collection<ChessPosition> whitePiecePositions = new ArrayList<ChessPosition>();
    private Collection<ChessPosition> blackPiecePositions = new ArrayList<ChessPosition>();
    private ChessPosition whiteKingPosition;
    private ChessPosition blackKingPosition;

    public ChessGame() {
       ChessBoard defaultBoard = new ChessBoard();
       defaultBoard.resetBoard();
       setBoard(defaultBoard);
       setTeamTurn(TeamColor.WHITE);
//       resetPiecesCollections();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if (board.getPiece(startPosition) == null) {
            return null;
        }

        Collection<ChessMove> possibleMoves = board.getPiece(startPosition).pieceMoves(board, startPosition);

        //remove if puts in check
        TeamColor team = board.getPiece(startPosition).getTeamColor();
        TeamColor otherTeam = board.getPiece(startPosition).getOtherTeamColor();

        Collection<ChessMove> validMoves = new ArrayList<ChessMove>();

        for (ChessMove move:possibleMoves){
            //create new ChessBoard object
            ChessBoard testBoard = board.clone();
            ChessPiece myPiece = testBoard.getPiece(startPosition);
            testBoard.addPiece(startPosition, null); //remove old piece
            testBoard.addPiece(move.getEndPosition(), myPiece); //add to new

            //create new ChessGame object
            ChessGame testGame = new ChessGame();
            testGame.setBoard(testBoard);
            testGame.setTeamTurn(otherTeam);
            testGame.resetPiecesCollections();

            if (!testGame.isInCheck(team)) {
                validMoves.add(move);
            }

//            possibleMoves.removeIf(testGame.isInCheck(team));

        }

//        //if currently in check, remove move if it doesn't escape check
//        if (isInCheck(team)) {
//            for (ChessMove move:possibleMoves){
//                //create new ChessBoard object
//                ChessBoard testBoard = board.clone();
//                ChessPiece myPiece = testBoard.getPiece(startPosition);
//                testBoard.addPiece(startPosition, null); //remove old piece
//                testBoard.addPiece(move.getEndPosition(), myPiece); //add to new
//
//                //create new ChessGame object
//                ChessGame testGame = new ChessGame();
//                testGame.setBoard(testBoard);
//                testGame.setTeamTurn(otherTeam);
//                testGame.resetPiecesCollections();
//
//                if (testGame.isInCheck(team)) {
//                    possibleMoves.remove(move);
//                }
//
//            }
//        }

//        return possibleMoves;
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece myPiece = board.getPiece(move.getStartPosition());
        Collection<ChessMove> validPieceMoves = validMoves(move.getStartPosition());
        if (validPieceMoves == null) {
            throw new InvalidMoveException("No piece at start position");
        }

        TeamColor team = myPiece.getTeamColor();
        if (team != teamTurn) {
            throw new InvalidMoveException("Other team's turn");
        }

//        if (isInCheck(team)) {
//            throw new InvalidMoveException("In check");
//        }

        if (!validPieceMoves.contains(move)) {
            throw new InvalidMoveException("Invalid move");
        } else {
            board.addPiece(move.getStartPosition(), null); //remove old piece

            if (myPiece.getPieceType() == ChessPiece.PieceType.PAWN && move.getPromotionPiece() != null) {
//                ChessPiece.PieceType promotionType = move.getPromotionPiece();
                ChessPiece promotionPiece = new ChessPiece(team, move.getPromotionPiece());
                board.addPiece(move.getEndPosition(), promotionPiece);
            } else {
                board.addPiece(move.getEndPosition(), myPiece);
            }

            updatePieces(move);

            //switch team turn
            if (teamTurn == TeamColor.WHITE) {
                teamTurn = TeamColor.BLACK;
            } else {
                teamTurn = TeamColor.WHITE;
            }
        }

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition;
        Collection<ChessPosition> otherTeamPositions;
        if (teamColor == TeamColor.WHITE) {
            kingPosition = whiteKingPosition;
            otherTeamPositions = blackPiecePositions;
        } else {
            kingPosition = blackKingPosition;
            otherTeamPositions = whitePiecePositions;
        }

        for (ChessPosition position:otherTeamPositions) {
            Collection<ChessMove> moves = board.getPiece(position).pieceMoves(board, position);
//            Collection<ChessMove> moves = validMoves(position);

            for (ChessMove move:moves) {
                if (move.getEndPosition().equals(kingPosition)) {
                    return true;
                }
            }
        }

        return false;

//        for (int row = 1; row <= 8; row++) {
//            for (int col = 1; col <= 8; col++) {
//                ChessPosition position = new ChessPosition(row, col);
//                ChessPiece piece = board.getPiece(position);
//                if (piece.getTeamColor() != teamColor) {
//                    Collection<ChessMove> moves = piece.pieceMoves(board, position);
//                    for (ChessMove move : moves) {
//                        if (move.getEndPosition() == kingPosition) {
//                            return true;
//                        }
//                    }
//                }
//            }
//        }
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }

        Collection<ChessPosition> piecePositions;
        if (teamColor == TeamColor.WHITE) {
            piecePositions = whitePiecePositions;
        } else {
            piecePositions = blackPiecePositions;
        }

        for (ChessPosition piecePosition:piecePositions) {
            if (validMoves(piecePosition) != null && !validMoves(piecePosition).isEmpty()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
        resetPiecesCollections();
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    /**
     * initialize black/whitePieces and black/whiteKingPosition for default board
     */
    public void resetPiecesCollections() {
        whitePiecePositions.clear();
        blackPiecePositions.clear();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPiece currentPiece = board.getPiece(new ChessPosition(row,col));
                if (currentPiece != null) {
                    if (currentPiece.getTeamColor() == TeamColor.WHITE) {
                        whitePiecePositions.add(new ChessPosition(row, col));
                        if (currentPiece.getPieceType() == ChessPiece.PieceType.KING) {
                            whiteKingPosition = new ChessPosition(row, col);
                        }
                    } else {
                        blackPiecePositions.add(new ChessPosition(row, col));
                        if (currentPiece.getPieceType() == ChessPiece.PieceType.KING) {
                            blackKingPosition = new ChessPosition(row, col);
                        }
                    }

                }
            }
        }
//        for (int row = 1; row <= 2; row++) {
//            for (int col = 1; col <=8; col++) {
//                whitePiecePositions.add(new ChessPosition(row, col));
//                blackPiecePositions.add(new ChessPosition(9-row, col));
//            }
//        }
    }

    /**
     * update black/whitePieces and black/whiteKingPosition after a move
     */
    public void updatePieces(ChessMove move) {
        //update pieces list
        if (blackPiecePositions.remove(move.getStartPosition())) {
            blackPiecePositions.remove(move.getStartPosition());
            blackPiecePositions.add(move.getEndPosition());
        } else if (whitePiecePositions.remove(move.getStartPosition())) {
            whitePiecePositions.remove(move.getStartPosition());
            whitePiecePositions.add(move.getEndPosition());
        }

        //update kingPosition
        if (move.getStartPosition() == whiteKingPosition) {
            whiteKingPosition = move.getEndPosition();
        } else if (move.getStartPosition() == blackKingPosition) {
            blackKingPosition = move.getEndPosition();
        }
    }
}
