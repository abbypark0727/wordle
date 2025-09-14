package cs2110;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ColorArrayTest {

    @DisplayName("WHEN the correct word is guessed, THEN an array of all green tiles is returned.")
    @Test
    void testCorrectGuess() {
        int[] colors = Wordell.getColorArray("SMART", "SMART");
        assertArrayEquals(new int[]{2,2,2,2,2}, colors);
    }

    @DisplayName("WHEN the guess contains only letters that are not in the target word, THEN an "
            + "array of all gray tiles is returned.")
    @Test
    void testAllWrongLetters() {
        int[] colors = Wordell.getColorArray("LYING", "SMART");
        assertArrayEquals(new int[]{0,0,0,0,0}, colors);
    }

    @DisplayName("WHEN the guess contains all three types of tiles, THEN the correct color array "
            + "is returned.")
    @Test
    void testAllColors() {
        int[] colors = Wordell.getColorArray("MOIRE", "SMART");
        assertArrayEquals(new int[]{1,0,0,2,0}, colors);
    }

    @DisplayName("WHEN the guess is BLOND and the word is APPLE, THEN the color array {0,1,0,0,0}"
            + "is returned.")
    @Test
    void testBlondApple() {
        int[] colors = Wordell.getColorArray("BLOND", "APPLE");
        assertArrayEquals(new int[]{0,1,0,0,0}, colors);
    }

    @DisplayName("WHEN the guess is HEDGE and the word is APPLE, THEN the color array {0,0,0,0,2}"
            + "is returned.")
    @Test
    void testHedgeApple() {
        int[] colors = Wordell.getColorArray("HEDGE", "APPLE");
        assertArrayEquals(new int[]{0,0,0,0,2}, colors);
    }

    @DisplayName("WHEN the guess is SHEET and the word is APPLE, THEN the color array {0,0,1,0,0}"
            + "is returned.")
    @Test
    void testSheetApple() {
        int[] colors = Wordell.getColorArray("SHEET", "APPLE");
        assertArrayEquals(new int[]{0,0,1,0,0}, colors);
    }

    @DisplayName("WHEN the guess is LAPEL and the word is APPLE, THEN the color array {1,1,2,1,0}"
            + "is returned.")
    @Test
    void testLapelApple() {
        int[] colors = Wordell.getColorArray("LAPEL", "APPLE");
        assertArrayEquals(new int[]{1,1,2,1,0}, colors);
    }

}
