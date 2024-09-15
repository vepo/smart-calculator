package io.vepo.tutorial.smartcalculator;

import static java.lang.System.err;
import static java.lang.System.out;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class CalculatorHookpoints {
    private static boolean invokeBooleanFunction(Invocable engine, String functionName, Object... fnArgs) {
        try {
            var value = engine.invokeFunction(functionName, fnArgs);
            if (value instanceof Boolean bValue) {
                return bValue;
            } else {
                err.println("Command does not return a boolean! retValue=" + value);
                return true;
            }
        } catch (ScriptException e) {
            err.println(String.format("Error while executing the function onCommand! %d:%d error=%s",
                    e.getLineNumber(), e.getColumnNumber(), e.getMessage()));
            return true;
        } catch (NoSuchMethodException e1) {
            return true;
        }
    }

    private Optional<ScriptEngine> engine;

    public CalculatorHookpoints(Path hookpointImplementation) {
        if (Objects.nonNull(hookpointImplementation)) {
            ScriptEngineManager manager = new ScriptEngineManager();
            System.setProperty("nashorn.args", "--language=es6");
            engine = Optional.of(manager.getEngineByName("js")).map(e -> {
                try {
                    out.println("Loading hookpoints: " + hookpointImplementation);
                    e.eval(Files.readString(hookpointImplementation));
                } catch (ScriptException | IOException ex) {
                    throw new IllegalStateException("Cannot read file! path=" + hookpointImplementation, ex);
                }
                return e;
            });
        } else {
            engine = Optional.empty();
        }
    }

    public CalculatorHookpoints() {
        engine = Optional.empty();
    }

    public boolean onCommand(String command, CalculatorMemory memory) {
        return engine.map(e -> invokeBooleanFunction((Invocable) e, "onCommand", command, memory))
                .orElse(true);
    }

    public boolean onExpression(String expression, double value,
            CalculatorMemory memory) {
        return engine.map(e -> invokeBooleanFunction((Invocable) e, "onExpression", expression, value, memory))
                .orElse(true);
    }

    public void onResult(String expression, double value, CalculatorMemory memory) {
        engine.ifPresent(e -> invokeBooleanFunction((Invocable) e, "onResult", expression, value, memory));
    }

    public boolean onDivisionByZero(String expression, double quocient, double dividend, CalculatorMemory memory) {
        return engine.map(
                e -> invokeBooleanFunction((Invocable) e, "onDivisionByZero", expression, quocient, dividend, memory))
                .orElse(true);
    }
}
