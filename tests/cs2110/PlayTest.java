package cs2110;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PlayTest {

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

    /* *******************************************************************************************
     * Here is where the tests begin.
     ******************************************************************************************* */

    @DisplayName("WHEN the `play()` method is called, THEN the user receives the correct input "
            + "prompt.")
    @Test
    void testCorrectPrompt() {
        Wordell.play("HELLO", new Scanner("HELLO\n"));
        out.flush();
        String prompt = outBytes.toString().split("\u001B")[0];
        assertEquals("1. Enter a guess: ", prompt);
    }

    @DisplayName("WHEN the `play()` method is called AND the user guesses the correct word, "
            + "THEN the correct win message is printed.")
    @Test
    void testWinMessage() {
        // 1 guess
        Wordell.play("HELLO", new Scanner("HELLO\n"));
        out.flush();
        String message = outBytes.toString().split(System.lineSeparator())[1];
        assertEquals("Congratulations! You won in 1 guesses.", message);
        outBytes.reset();

        // 2 guesses
        Wordell.play("HELLO", new Scanner("MELON\nHELLO\n"));
        out.flush();
        message = outBytes.toString().split(System.lineSeparator())[2];
        assertEquals("Congratulations! You won in 2 guesses.", message);
    }

    @DisplayName("WHEN the `play()` method is called AND the user makes six guesses without "
            + "guessing the target word, THEN the correct loss message is printed.")
    @Test
    void testLossMessage() {
        Wordell.play("HELLO", new Scanner("FRUIT\nAPPLE\nLEMON\nMELON\nGRAPE\nMANGO"));
        out.flush();
        String message = outBytes.toString().split(System.lineSeparator())[6];
        assertEquals("Better luck next time. The word was HELLO.", message);
    }

    @DisplayName("WHEN the `play()` method is called AND the user inputs a guess with 5 lowercase "
            + "letters, THEN it is converted to uppercase and considered valid.")
    @Test
    void testLowercaseInput() {
        Wordell.play("HELLO", new Scanner("hello\n"));
        out.flush();
        String message = outBytes.toString().split(System.lineSeparator())[1];
        assertEquals("Congratulations! You won in 1 guesses.", message);
        outBytes.reset();
    }

    /**
     * Returns a String array whose entries are lines of the console from `play()`.
     */
    String[] reconstructConsole(String inputs) {
        out.flush();
        String[] outLines = outBytes.toString().split(System.lineSeparator());
        String[] inLines = inputs.split("\n");
        assert outLines.length == inLines.length + 1;
        String[] console = new String[2 * outLines.length - 1];
        for (int i = 0; i < outLines.length - 1; i++) {
            int splitIndex = outLines[i].indexOf(":") + 2; // separate prompt from message
            console[2 * i] = outLines[i].substring(0, splitIndex) + inLines[i];
            console[2 * i + 1] = outLines[i].substring(splitIndex);
        }
        console[console.length - 1] = outLines[outLines.length - 1];
        return console;
    }

    @DisplayName("WHEN the `play()` method is called AND the user makes an invalid guess, THEN "
            + "the correct message is printed AND they are prompted to enter a new guess without "
            + "the guess number increasing.")
    @Test
    void testInvalidGuess() {
        // 1 guess, 2 attempts
        String inputs = "HELLO!\nHELLO";
        Wordell.play("HELLO", new Scanner(inputs));

        String[] expected = {
                "1. Enter a guess: HELLO!",
                "Your guess must have 5 letters. Try again.",
                "1. Enter a guess: HELLO",
                "\u001B[102m H \u001B[0m \u001B[102m E \u001B[0m \u001B[102m L \u001B[0m "
                        + "\u001B[102m L \u001B[0m \u001B[102m O \u001B[0m ",
                "Congratulations! You won in 1 guesses."
        };

        assertArrayEquals(expected, reconstructConsole(inputs));
        outBytes.reset();

        // 1 guess, 3 attempts
        inputs = "HELLO!\nHELO!\nHELLO";
        Wordell.play("HELLO", new Scanner(inputs));

        expected = new String[]{
                "1. Enter a guess: HELLO!",
                "Your guess must have 5 letters. Try again.",
                "1. Enter a guess: HELO!",
                "Your guess cannot include the character '!'. Try again.",
                "1. Enter a guess: HELLO",
                "\u001B[102m H \u001B[0m \u001B[102m E \u001B[0m \u001B[102m L \u001B[0m "
                        + "\u001B[102m L \u001B[0m \u001B[102m O \u001B[0m ",
                "Congratulations! You won in 1 guesses."
        };

        assertArrayEquals(expected, reconstructConsole(inputs));
    }

    @DisplayName("A full game of Wordell has the correct console outputs.")
    @Test
    void testFullGame() {
        String inputs = "least\nflash\nclasp";
        Wordell.play("CLASP", new Scanner(inputs));

        String[] expected = new String[]{
                "1. Enter a guess: least",
                "\u001B[103m L \u001B[0m \u001B[47m E \u001B[0m \u001B[102m A \u001B[0m "
                        + "\u001B[102m S \u001B[0m \u001B[47m T \u001B[0m ",
                "2. Enter a guess: flash",
                "\u001B[47m F \u001B[0m \u001B[102m L \u001B[0m \u001B[102m A \u001B[0m "
                        + "\u001B[102m S \u001B[0m \u001B[47m H \u001B[0m ",
                "3. Enter a guess: clasp",
                "\u001B[102m C \u001B[0m \u001B[102m L \u001B[0m \u001B[102m A \u001B[0m "
                        + "\u001B[102m S \u001B[0m \u001B[102m P \u001B[0m ",
                "Congratulations! You won in 3 guesses."
        };

        assertArrayEquals(expected, reconstructConsole(inputs));
    }
}
