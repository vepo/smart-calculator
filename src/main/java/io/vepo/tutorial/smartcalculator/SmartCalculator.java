package io.vepo.tutorial.smartcalculator;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import io.vepo.tutorial.smartcalculator.generated.SmartCalculatorBaseListener;
import io.vepo.tutorial.smartcalculator.generated.SmartCalculatorLexer;
import io.vepo.tutorial.smartcalculator.generated.SmartCalculatorParser;
import io.vepo.tutorial.smartcalculator.generated.SmartCalculatorParser.ExpressionContext;

public class SmartCalculator {
    public static void main(String[] args) throws Exception {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");
        engine.eval("""
                    function onExpression(value1, operator, value2) {
                        print("EXPR:" + value1 + operator + value2);
                        return eval(value1 + operator + value2)
                    }
                """);

        SmartCalculatorParser parser = new SmartCalculatorParser(
                new CommonTokenStream(new SmartCalculatorLexer(CharStreams.fromString("5 + 23 + 7 * 8"))));
        ParseTreeWalker walker = new ParseTreeWalker();
        var memory = new HashMap<ExpressionContext, Integer>();
        walker.walk(new SmartCalculatorBaseListener() {
            @Override
            public void exitExpression(ExpressionContext ctx) {
                if (Objects.nonNull(ctx.NUMBER())) {
                    memory.put(ctx, Integer.parseInt(ctx.NUMBER().getText()));
                    System.out.println("Found number! " + ctx.NUMBER());
                } else {
                    var leftValue = memory.get(ctx.expression(0));
                    var rightValue = memory.get(ctx.expression(1));
                    switch (ctx.OPERATOR().getText()) {
                        case "+":
                            memory.put(ctx, leftValue + rightValue);
                            break;
                        case "-":
                            memory.put(ctx, leftValue + rightValue);
                            break;
                        case "*":
                            memory.put(ctx, leftValue * rightValue);
                            break;
                        case "/":
                            memory.put(ctx, leftValue / rightValue);
                            break;
                        default:
                            System.out.println("Operator not Found!! " + ctx.OPERATOR());
                            break;
                    }
                    try {
                        ((Invocable) engine).invokeFunction("onExpression", leftValue, ctx.OPERATOR().toString(),
                                rightValue);
                    } catch (NoSuchMethodException | ScriptException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Evaluated: " + memory.get(ctx));
                }
                System.out.println("Number: " + ctx.NUMBER());
                System.out.println("Operator: " + ctx.OPERATOR());
                System.out.println("Expression: " + ctx.expression());
            }
        }, parser.expression());
        // SuiteCreator creator = new SuiteCreator();
        // walker.walk(creator, parser.suite());
        // Suite suite = creator.getTestSuite();

    }
}