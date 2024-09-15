package io.vepo.tutorial.smartcalculator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CalculatorParserTest {

    @Test
    void expressionTest() {
        var parser = new CalculatorParser(new CalculatorHookpoints());
        assertThat(parser.feed("3")).isEqualTo(3.0);
        assertThat(parser.feed("-3")).isEqualTo(-3.0);
        assertThat(parser.feed("2*-3")).isEqualTo(-6.0);
        assertThat(parser.feed("2*(-3)")).isEqualTo(-6.0);
        assertThat(parser.feed("3 + 5")).isEqualTo(8.0);
        assertThat(parser.feed("3 ^ 5")).isEqualTo(243.0);
        assertThat(parser.feed("((6 + 3) * (1 + 3)) - 2")).isEqualTo(34.0);
        // assertThat(parser.feed("(6 + 3) * (1 + 3) - 2")).isEqualTo(34.0);
    }
}
