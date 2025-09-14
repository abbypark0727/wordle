package cs2110;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class IsValidGuessTest {

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

    @DisplayName(
            "WHEN a guess is made with too few characters, THEN `isValidGuess()` returns `false` "
                    + "and prints the correct message.")
    @Test
    void testGuessTooShort() {
        boolean b = Wordell.isValidGuess("CAT");
        assertFalse(b);
        assertOutput("Your guess must have 5 letters. Try again.");
    }

    @DisplayName(
            "WHEN a guess is made with too many characters, THEN `isValidGuess()` returns `false` "
                    + "and prints the correct message.")
    @Test
    void testGuessTooLong() {
        boolean b = Wordell.isValidGuess("PLATYPUS");
        assertFalse(b);
        assertOutput("Your guess must have 5 letters. Try again.");
    }

    @DisplayName(
            "WHEN a guess is made with illegal characters, THEN `isValidGuess()` returns `false` "
                    + "and prints the correct message.")
    @Test
    void testGuessBadChar() {
        boolean b = Wordell.isValidGuess("DUCK3");
        assertFalse(b);
        assertOutput("Your guess cannot include the character '3'. Try again.");
    }

    @DisplayName("WHEN a valid guess is made, THEN `isValidGuess()` returns `true` and does not "
            + "print anything.")
    @Test
    void testValidGuess() {
        boolean b = Wordell.isValidGuess("ROBOT");
        assertTrue(b);
        out.flush();
        assertEquals("", outBytes.toString());
    }
}
