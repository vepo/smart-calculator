package io.vepo.tutorial.smartcalculator;

import static java.lang.System.err;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.vepo.tutorial.smartcalculator.generated.SmartCalculatorBaseListener;
import io.vepo.tutorial.smartcalculator.generated.SmartCalculatorParser.CommandContext;
import io.vepo.tutorial.smartcalculator.generated.SmartCalculatorParser.ExpressionContext;

public class CalculatorTreeListener extends SmartCalculatorBaseListener {
    private final CalculatorListener commandListener;
    private final CalculatorMemory memory;
    private final Map<ExpressionContext, Double> expressionMemory;

    public CalculatorTreeListener(CalculatorListener commandListener, CalculatorMemory memory) {
        this.commandListener = Objects.requireNonNull(commandListener, "Command listener cannot be null!");
        this.memory = Objects.requireNonNull(memory, "memory cannot be null!!");
        this.expressionMemory = new HashMap<>();
    }

    @Override
    public void exitExpression(ExpressionContext ctx) {
        if (commandListener.onExpression(ctx.getText(), memory.current())) {
            if (Objects.isNull(ctx.OPERATOR()) && Objects.nonNull(ctx.NUMBER())) {
                memory.put(Double.parseDouble(ctx.NUMBER().getText()));
                expressionMemory.put(ctx, Double.parseDouble(ctx.NUMBER().getText()));
            } else if (Objects.nonNull(ctx.OPERATOR())) {
                var leftValue = memory.current();
                var rightValue = Objects.isNull(ctx.NUMBER()) ? expressionMemory.get(ctx.expression(0).getRuleContext())
                        : Double.parseDouble(ctx.NUMBER().getText());
                var result = switch (ctx.OPERATOR().getText()) {
                    case "+" -> leftValue + rightValue;
                    case "-" -> leftValue + rightValue;
                    case "*" -> leftValue * rightValue;
                    case "/" -> leftValue / rightValue;
                    default -> Double.NaN;
                };
                expressionMemory.put(ctx, result);
                if (result != Double.NaN) {
                    memory.put(result);
                } else {
                    err.println("Operator not Found!! " + ctx.OPERATOR());
                }
                commandListener.onResult(ctx.getText(), memory.current());
            } else {
                System.out.println(ctx);
                System.out.println(ctx.getText());
            }
        }
    }

    @Override
    public void exitCommand(CommandContext ctx) {
        this.commandListener.onCommand(ctx.COMMAND().getText());
    }
}
