package cs2110;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import java.util.Scanner;

/**
 * A console-based implementation of the popular "Wordle" word game.
 */
public class Wordell {

    /**
     * Returns a random entry from the valid word list in "words.txt".
     */
    static String getRandomValidWord() throws IOException {
        String[] validWords = Files.readString(Path.of("words.txt")).split("\n");
        Random rand = new Random();
        return validWords[rand.nextInt(validWords.length)];
    }

    /**
     * Returns a String that outputs a green tile containing the given character `c` to the console,
     * followed by a trailing space. Requires that `c` is an uppercase letter.
     */
    static String greenTile(char c) {
        assert 'A' <= c && c <= 'Z'; // make sure the parameter is an uppercase letter
        return "\u001B[102m " + c + " \u001B[0m ";
    }

    /**
     * Returns a String that outputs a yellow tile containing the given character `c` to the
     * console, followed by a trailing space. Requires that `c` is an uppercase letter.
     */
    static String yellowTile(char c) {
        assert 'A' <= c && c <= 'Z'; // make sure the parameter is an uppercase letter
        return "\u001B[103m " + c + " \u001B[0m ";
    }

    /**
     * Returns a String that outputs a gray tile containing the given character `c` to the console,
     * followed by a trailing space. Requires that `c` is an uppercase letter.
     */
    static String grayTile(char c) {
        assert 'A' <= c && c <= 'Z'; // make sure the parameter is an uppercase letter
        return "\u001B[47m " + c + " \u001B[0m ";
    }

    /**
     * Returns `false` and prints an explanatory message if the given `guess` is not valid,
     * otherwise returns `true`. A guess is not valid if (1) it contains a number of characters
     * besides 5, in which case the message "Your guess must have 5 letters. Try again.", or (2) it
     * contains the correct number of characters, but one of these is outside 'A'-'Z', in which case
     * the message "Your guess cannot include the character '*'. Try again." with * replaced by the
     * first illegal character should be printed. Both messages should end with a newline. Requires
     * that `guess != null`.
     */
    static boolean isValidGuess(String guess) {
        assert guess != null;

        // checks that guess is 5 characters
        if (guess.length() != 5) {
            System.out.println("Your guess must have 5 letters. Try again.");
            return false;
        }

        // Checks that every character must be an uppercase A–Z
        // And stops at the first character violating the specs to report it
        for (int i = 0; i < guess.length(); i++) {
            char l = guess.charAt(i);
            if (l < 'A' || l > 'Z') {
                System.out.println(
                        "Your guess cannot include the character '" + l + "'. Try again.");
                return false;
            }
        }

        return true;
    }

    /**
     * Prints the output for the given `guess`, which consists of `guess.length()` colored tiles
     * containing the characters in the guess (in order) colored according to the corresponding
     * entries in the given `colors` array (0=gray, 1=yellow, 2=green), followed by a trailing space
     * and a newline.
     */
    static void printGuessOutput(String guess, int[] colors) {
        assert guess != null;
        assert colors != null;
        assert guess.length() == colors.length;

        // Emit 1 colored tile per character using colors[i] with 0 = gray, 1 = yellow, 2 = green
        // A trailing space comes after each tile
        for (int i = 0; i < guess.length(); i++) {
            char l = guess.charAt(i);
            int color = colors[i];
            assert color >= 0 && color <= 2;

            if (color == 0) {
                System.out.print(grayTile(l));
            } else if (color == 1) {
                System.out.print(yellowTile(l));
            } else if (color == 2) {
                System.out.print(greenTile(l));
            }
        }

        System.out.println();
    }

    /**
     * Returns an `int[5]` array where the value at index `i` corresponds to the color of the `i`th
     * character in the given `guess` (0=gray, 1=yellow, 2=green). A character in the `guess` is
     * colored green if that character appears in the same position in the actual `word`. A
     * character is colored yellow if it appears in a different position in the actual `word` that
     * is not already associated with another yellow or green tile. Otherwise, a character is
     * colored gray. All yellow tiles with a given letter appear to the left of all gray tiles with
     * that same letter.
     */
    public static int[] getColorArray(String guess, String word) {
        int[] colors = new int[5];
        int[] track = new int[26];

        for (int i = 0; i < 5; i++) {
            track[word.charAt(i) - 'A']++;
        }

        // Marks the greens and takes in those letters from track
        for (int i = 0; i < 5; i++) {
            char g = guess.charAt(i);
            if (g == word.charAt(i)) {
                colors[i] = 2;
                track[g - 'A']--;
            }
        }

        // For not green positions it assigns yellow if any of that letter are left.
        // Left-to-right makes sure that all yellows for a letter appear before any grays
        for (int i = 0; i < 5; i++) {
            if (colors[i] == 0) {
                int index = guess.charAt(i) - 'A';
                if (track[index] > 0) {
                    colors[i] = 1;
                    track[index]--;
                }
            }
        }
        return colors;
    }

    /**
     * Simulates a game of "Wordell" with the given target `word`, using the given Scanner `sc` to
     * get inputs (guessed words) from the user. Over the course of 6 rounds, the game should prompt
     * the user for a guess with the console output "#. Enter a guess: " where # is the current
     * valid guess number (starting from 1), read the user's console input, and convert it to
     * uppercase. If the user gives a valid guess, a color array is printed and the prompt is made
     * for the next guess. If the user gives an invalid guess, the application should prompt the
     * user for another guess with the same guess number. If the user guesses the correct word, the
     * game should print "Congratulations! You won in # guesses." with `#` replaced by the number of
     * valid guesses, and the method should return. Otherwise, if the user runs out of guesses, the
     * game should print "Better luck next time. The word was *****.", with `*****` replaced by the
     * `word`.
     */
    static void play(String word, Scanner sc) {
        int guessNum = 1;

        while (guessNum <= 6) {
            // Prompt for a guess and then put it to uppercase
            System.out.print(guessNum + ". Enter a guess: ");
            String guess = sc.next().toUpperCase();

            // Rejects any invalid guesses without counting it as an attempt
            if (isValidGuess(guess)) {
                int[] colors = getColorArray(guess, word);
                printGuessOutput(guess, colors);

                // If the word is guessed correctly, then the win message is printed
                // and game stops
                if (guess.equals(word)) {
                    System.out.println("Congratulations! You won in " + guessNum + " guesses.");
                    return;
                }

                // Counts a valid attempt
                guessNum += 1;
            }
        }

        // Prints out when the player is out of their attempts
        System.out.println("Better luck next time. The word was " + word + ".");
    }


    /**
     * Simulates a game of "Wordell" in hard mode with the given target `word`, using the given
     * Scanner `sc` to get inputs (guessed words) from the user. Over the course of 6 rounds, the
     * game should prompt * the user for a guess with the console output "#. Enter a guess: " where
     * # is the current * valid guess number (starting from 1), read the user's console input, and
     * convert it to * uppercase. If the user gives a valid guess, a color array is printed and the
     * prompt is made for the next guess. If the user gives an invalid guess, the application should
     * prompt the user for another guess with the same guess number. In the case that a guess
     * conflicts with information from a previous guess, the message "Your guess conflicts with
     * information from the guess *****. Try again." with ***** replaced by the first guessed word
     * that causes a conflict is printed. If the user guesses the correct word, the game should
     * print "Congratulations! You won in # guesses." with the correct number of guesses filled in,
     * and the method should return. Otherwise, if the user runs out of guesses, the game should
     * print "Better luck next time. The word was *****.", with `word` in place of the *s.
     */
    static void playHardMode(String word, Scanner sc) {
        String[] previousGuesses = new String[6];
        int[][] previousColors = new int[6][5];
        int validGuessCount = 0;    // number of prior valid guesses
        int guessValidNum = 1;      // counts only valid guesses

        while (guessValidNum <= 6) {
            System.out.print(guessValidNum + ". Enter a guess: ");
            String guess = sc.next().toUpperCase();

            if (isValidGuess(guess)) {
                // Treats the new guess as if it is the word
                // and requires every previous guess to recolor exactly the same
                boolean conflict = false;
                String conflictsWith = null;

                int i = 0;
                while (i < validGuessCount && !conflict) {
                    // makes it so that the guess is treated as the word
                    int[] recolor = getColorArray(previousGuesses[i], guess);
                    int j = 0;
                    while (j < 5) {
                        if (recolor[j] != previousColors[i][j]) {
                            // occurrence of the first conflict with prior guess
                            conflict = true;
                            conflictsWith = previousGuesses[i];
                            break;
                        }
                        j += 1;
                    }
                    i += 1;
                }

                if (conflict) {
                    //
                    System.out.println(
                            "Your guess conflicts with information from the guess " + conflictsWith
                                    +
                                    ". Try again.");
                } else {
                    int[] colors = getColorArray(guess, word);
                    printGuessOutput(guess, colors);

                    if (guess.equals(word)) {
                        System.out.println(
                                "Congratulations! You won in " + guessValidNum + " guesses.");
                        return;
                    }

                    // store this valid guess and its colors for future hard-mode checks
                    previousGuesses[validGuessCount] = guess;
                    int k = 0;
                    while (k < 5) { // copy colors into the history row
                        previousColors[validGuessCount][k] = colors[k];
                        k += 1;
                    }
                    validGuessCount += 1;
                    guessValidNum += 1; // only increments on a valid and non-conflicting guess
                }
            }
            // else means re-prompting with same guessNum
        }

        System.out.println("Better luck next time. The word was " + word + ".");
    }

    /**
     * Creates a new "Wordell" game with a random target word. Uses hard mode if the "hard" command
     * line argument is supplied.
     */
    public static void main(String[] args) throws IOException {
        boolean hardMode = (args.length == 1 && args[0].equals("hard"));

        try (Scanner sc = new Scanner(System.in)) {
            if (hardMode) {
                playHardMode(getRandomValidWord(), sc);
            } else {
                play(getRandomValidWord(), sc);
            }
        }
    }
}
