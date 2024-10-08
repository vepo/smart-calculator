package io.vepo.tutorial.smartcalculator;

import static java.lang.System.err;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import io.vepo.tutorial.smartcalculator.generated.SmartCalculatorBaseListener;
import io.vepo.tutorial.smartcalculator.generated.SmartCalculatorLexer;
import io.vepo.tutorial.smartcalculator.generated.SmartCalculatorParser;
import io.vepo.tutorial.smartcalculator.generated.SmartCalculatorParser.CommandContext;

public class CalculatorParser implements ExpressionListener {

    private AtomicBoolean running;
    private CalculatorMemory memory;
    private CalculatorHookpoints hookpoints;

    public CalculatorParser(CalculatorHookpoints hookpoints) {
        this.running = new AtomicBoolean(true);
        this.memory = new CalculatorMemory();
        this.hookpoints = Objects.requireNonNull(hookpoints, "hookpoints cannot be null!");
    }

    public double feed(String content) {
        var tokens = new CommonTokenStream(new SmartCalculatorLexer(CharStreams.fromString(content)));
        var parser = new SmartCalculatorParser(tokens);

        parser.setErrorHandler(new DefaultErrorStrategy() {
            @Override
            public void recover(Parser recognizer, RecognitionException e) {
                err.println("Error: expected=" + e.getExpectedTokens() + " found=" + e.getOffendingToken());
            }
        });
        var walker = new ParseTreeWalker();
        if (content.trim().startsWith("\\")) {
            walker.walk(new SmartCalculatorBaseListener() {
                @Override
                public void exitCommand(CommandContext ctx) {
                    onCommand(ctx.COMMAND().getText());
                }
            }, parser.command());
        } else {
            CalculatorTreeListener listener = new CalculatorTreeListener(this);
            walker.walk(listener, parser.start());
            memory.put(listener.getResult());
        }
        return memory.current();
    }

    public boolean isRunning() {
        return running.get();
    }

    @Override
    public void onCommand(String command) {
        if (hookpoints.onCommand(command, memory)) {
            switch (command) {
                case "QUIT":
                case "EXIT":
                    this.running.set(false);
                    break;
                default:
                    err.println("Unrecognized command: " + command);
                    break;
            }
        } else {
            err.println("Command execution ignored by hookpoint! command=" + command);
        }
    }

    @Override
    public boolean onExpression(String expression, double value) {
        return hookpoints.onExpression(expression, value, memory);
    }

    @Override
    public void onResult(String expression, double value) {
        hookpoints.onResult(expression, value, memory);
    }

    @Override
    public boolean onDivisionByZero(String expression, double quocient, double dividend) {
        return hookpoints.onDivisionByZero(expression, quocient, dividend, memory);
    }
}
