package net.ssehub.rightsmanagement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests the {@link StringUtils}.
 * @author El-Sharkawy
 * @author Kunold
 *
 */
class StringUtilsTests {

    /**
     * Test {@link StringUtils#normalizePath(String)}.
     * @param name The name of the tests (to be displayed by the IDE or in the test report)
     * @param unnormalized The input string for the test.
     * @param expectedNormalization The expected normalization.
     */
    @ParameterizedTest(name = "Path normalization #{index} ({0}): {1} -> {2}")
    @MethodSource("pathNormalizationTestdata")
    void testNormalizePath(String name, String unnormalized, String expectedNormalization) {
        assertEquals(expectedNormalization, StringUtils.normalizePath(unnormalized));
    }

    /**
     * Parameters for the {@link #testNormalizePath(String, String, String)} tests.
     * @return The parameters (name, input data, expected result) for the test.
     */
    static Stream<Arguments> pathNormalizationTestdata() {
        return Stream.of(
            arguments("Unproblematic path w/o normalization", "/A_path/to_somewhere", "/A_path/to_somewhere"),
            arguments("Whitespace", "/A path/to somewhere", "/A%20path/to%20somewhere"),
            arguments("Tilde", "/A~path/with~tilde", "/A%7epath/with%7etilde"),
            arguments("At", "/A@path/with@at", "/A%40path/with%40at"),
            arguments("And", "/A&path/with&and", "/A%26path/with%26and"),
            arguments("Asterisk", "/A*path/with*asterisk", "/A%2apath/with%2aasterisk"),
            arguments("Hash", "/A#path/with#hash", "/A%23path/with%23hash"),
            arguments("Less-Than Sign", "/A<path/with<less", "/A%3cpath/with%3cless"),
            arguments("Greater-Than Sign", "/A>path/with>greater", "/A%3epath/with%3egreater"),
            arguments("Percent", "/A%path/with%percent", "/A%25path/with%25percent"),
            arguments("Vertikal Line", "/A|path/with|vertikal|line", "/A%7cpath/with%7cvertikal%7cline"),
            arguments("Left Curly Bracket", "/A{path/with{left{bracket", "/A%7bpath/with%7bleft%7bbracket"),
            arguments("Right Curly Bracket", "/A}path/with}right}bracket", "/A%7dpath/with%7dright%7dbracket"),
            arguments("Circumflex Accent", "/A^path/with^circumflex^accent", "/A%5epath/with%5ecircumflex%5eaccent"),
            arguments("Grave Accent", "/A`path/with`grave`accent", "/A%60path/with%60grave%60accent")
        );
    }
    
    /**
     * Test {@link StringUtils#normalizeName(String)}.
     * @param name The name of the tests (to be displayed by the IDE or in the test report)
     * @param unnormalized The input string for the test.
     * @param expectedNormalization The expected normalization.
     */
    @ParameterizedTest(name = "Name normalization #{index} ({0}): {1} -> {2}")
    @MethodSource("nameNormalizationTestdata")
    void testNomalizeName(String name, String unnormalized, String expectedNormalization) {
        assertEquals(expectedNormalization, StringUtils.normalizeName(unnormalized));
    }
    
    /**
     * Parameters for the {@link #testNomalizeName(String, String, String)} test.
     * @return The parameters (name, input data, expected result) for the test.
     */
    static Stream<Arguments> nameNormalizationTestdata() {
        return Stream.of(
            arguments("Unproblematic name w/o normalization", "Group_1", "Group_1"),
            arguments("Whitespace", "Group 1", "Group_1")
        );
    }
    
}
