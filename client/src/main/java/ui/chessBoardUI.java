package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import static ui.EscapeSequences.*;

public class chessBoardUI {

    private static final int numSquares = 8;
    private static final int squareSize = 3;
    private static final int lineWidth = 1;

//    private static final String EMPTY =

    public void chessBoardUI() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

//        drawHeaders(out);

//        drawChessBoard(out);


    }
}
