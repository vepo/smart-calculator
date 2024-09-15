package io.vepo.tutorial.smartcalculator;

public interface CalculatorListener {
    void onCommand(String command);

    boolean onExpression(String expression, double value);

    void onResult(String expression, double value);
}
