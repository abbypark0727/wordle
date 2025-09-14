package cs2110;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PrintGuessTest {

    /* *******************************************************************************************
     * The code at the top of this file is used to capture the console output, so we can check
     * that it is correct.
     ******************************************************************************************* */

    /**
     * The original `System.out`.
     */
    PrintStream systemOut;

    /**
     * Replacement for `System.out` during test execution.
     */
    PrintStream out;
    ByteArrayOutputStream outBytes;

    @BeforeEach
    void setUpSimulator() {
        outBytes = new ByteArrayOutputStream();
        out = new PrintStream(outBytes);
        systemOut = System.out;
        System.setOut(out);
        clearOutputStream();
    }

    /**
     * Resets the output stream so we can capture the print output from processing one command
     */
    void clearOutputStream() {
        out.flush();
        outBytes.reset();
    }

    @AfterEach
    void restoreOutput() {
        out.close();
        System.setOut(systemOut);
    }

    /**
     * Asserts that the line captured in the OutputStream is equal to `expected`
     */
    void assertOutput(String expected) {
        out.flush();
        assertEquals(expected + System.lineSeparator(), outBytes.toString());
        outBytes.reset();
    }

    /* *******************************************************************************************
     * Here is where the tests begin.
     ******************************************************************************************* */

    @DisplayName("The guess output message should end with a newline.")
    @Test
    void testForNewline() {
        Wordell.printGuessOutput("HELLO", new int[]{0, 0, 0, 0, 0});
        out.flush(); // capture the program output
        assertTrue(outBytes.toString().endsWith(System.lineSeparator()));
    }

    @DisplayName("The output includes exactly 5 tiles.")
    @Test
    void testCorrectNumberTiles() {
        Wordell.printGuessOutput("HELLO", new int[]{0, 0, 0, 0, 0});
        out.flush(); // capture the program output
        String output = outBytes.toString();
        int tileEndIndex = -1; // the index of the final character in a tile's String representation
        int numTiles = 0;
        do {
            tileEndIndex = output.indexOf("\u001B[0m", tileEndIndex + 1);
            if (tileEndIndex >= 0) {
                numTiles++;
            }
        } while (tileEndIndex >= 0); // we haven't reached the end of the output String
        assertEquals(5, numTiles);
    }

    @DisplayName("There is exactly one uncolored space character between each pair of consecutive tiles.")
    @Test
    void testForSeparatingSpaces() {
        Wordell.printGuessOutput("HELLO", new int[]{0, 0, 0, 0, 0});
        out.flush(); // capture the program output
        String output = outBytes.toString();
        int tileEndIndex = -1; // the index of the final character in a tile's String representation
        do {
            tileEndIndex = output.indexOf("\u001B[0m", tileEndIndex + 1);

            if (tileEndIndex >= 0) {
                assertEquals(' ', output.charAt(tileEndIndex + 4));
                assertNotEquals(' ', output.charAt(tileEndIndex + 5));
            }
        } while (tileEndIndex >= 0); // we haven't reached the end of the output String
    }

    @DisplayName("Each of the tile colors is outputted correctly.")
    @Test
    void testTileColors() {
        // green
        Wordell.printGuessOutput("ABCDE", new int[]{2, 2, 2, 2, 2});
        out.flush(); // capture the program output
        String output = outBytes.toString();
        assertEquals("102", output.substring(2, 5));
        outBytes.reset();

        // yellow
        Wordell.printGuessOutput("ABCDE", new int[]{1, 1, 1, 1, 1});
        out.flush(); // capture the program output
        output = outBytes.toString();
        assertEquals("103", output.substring(2, 5));
        outBytes.reset();

        // gray
        Wordell.printGuessOutput("ABCDE", new int[]{0, 0, 0, 0, 0});
        out.flush(); // capture the program output
        output = outBytes.toString();
        assertEquals("47", output.substring(2, 4));
    }

    @DisplayName("The output for a guess containing all three colors of tiles is produced correctly.")
    @Test
    void testFullOutput() {
        Wordell.printGuessOutput("TIGER", new int[]{2, 1, 1, 0, 2});
        out.flush(); // capture the program output
        String output = outBytes.toString();
        assertEquals("\u001B[102m T \u001B[0m \u001B[103m I \u001B[0m \u001B[103m G \u001B[0m "
                        + "\u001B[47m E \u001B[0m \u001B[102m R \u001B[0m " + System.lineSeparator()
                , output);
    }
}
