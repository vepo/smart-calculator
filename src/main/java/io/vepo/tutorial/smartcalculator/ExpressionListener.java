package io.vepo.tutorial.smartcalculator;

public interface ExpressionListener {
    void onCommand(String command);

    boolean onExpression(String expression, double value);

    void onResult(String expression, double value);

    boolean onDivisionByZero(String expression, double quocient, double dividend);
}
