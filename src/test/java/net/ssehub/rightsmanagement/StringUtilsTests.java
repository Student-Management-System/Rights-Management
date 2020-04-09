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
            arguments("Whitespace", "/A path/to somewhere", "/A%20path/to%20somewhere")
        );
    }
}
