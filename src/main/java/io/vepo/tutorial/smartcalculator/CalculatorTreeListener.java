package io.vepo.tutorial.smartcalculator;

import static java.lang.System.err;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.vepo.tutorial.smartcalculator.generated.SmartCalculatorBaseListener;
import io.vepo.tutorial.smartcalculator.generated.SmartCalculatorParser.ExpressionContext;
import io.vepo.tutorial.smartcalculator.generated.SmartCalculatorParser.StartContext;

public class CalculatorTreeListener extends SmartCalculatorBaseListener {
    private final ExpressionListener expressionListener;
    private final Map<ExpressionContext, Double> expressionMemory;
    private double result;

    public CalculatorTreeListener(ExpressionListener expressionListener) {
        this.expressionListener = Objects.requireNonNull(expressionListener, "expressionListener cannot be null!");
        this.expressionMemory = new HashMap<>();
        this.result = 0.0;
    }

    public double getResult() {
        return result;
    }

    @Override
    public void exitStart(StartContext ctx) {
        result = expressionMemory.get(ctx.expression());
    }

    @Override
    public void exitExpression(ExpressionContext ctx) {
        var previousValue = previousValue(ctx);
        if (expressionListener.onExpression(ctx.getText(), previousValue)) {
            if (Objects.isNull(ctx.operator) && Objects.nonNull(ctx.NUMBER())) {
                double currValue = Double.parseDouble(ctx.NUMBER().getText());
                expressionMemory.put(ctx, currValue);
            } else if (Objects.nonNull(ctx.operator)) {
                evalOperation(ctx, previousValue);
            }
        }
    }

    private void evalOperation(ExpressionContext ctx, double previousValue) {
        var rightValue = previousValue;
        var leftValue = Objects.isNull(ctx.NUMBER()) ? expressionMemory.get(ctx.expression(0).getRuleContext())
                : Double.parseDouble(ctx.NUMBER().getText());
        if (!isDivision(ctx) ||
                !isZero(rightValue)
                || !expressionListener.onDivisionByZero(ctx.getText(), leftValue, rightValue)) {
            var operationResult = switch (ctx.operator.getText()) {
                case "+" -> leftValue + rightValue;
                case "-" -> leftValue - rightValue;
                case "*" -> leftValue * rightValue;
                case "/" -> leftValue / rightValue;
                case "^" -> Math.pow(leftValue, rightValue);
                default -> Double.NaN;
            };
            expressionMemory.put(ctx, operationResult);
            if (operationResult == Double.NaN) {
                err.println("Operator not Found!! " + ctx.operator);
            }
            expressionListener.onResult(ctx.getText(), operationResult);
        }
    }

    private double previousValue(ExpressionContext ctx) {
        if (Objects.isNull(ctx.operator)) {
            return 0.0;
        } else if (ctx.expression().size() == 2) {
            return expressionMemory.get(ctx.expression(1).getRuleContext());
        } else {
            return expressionMemory.get(ctx.expression(0).getRuleContext());
        }
    }

    private static boolean isDivision(ExpressionContext ctx) {
        return ctx.operator.getText().equals("/");
    }

    private static boolean isZero(Double value) {
        return Math.abs(value) < 2 * Double.MIN_VALUE;
    }
}
