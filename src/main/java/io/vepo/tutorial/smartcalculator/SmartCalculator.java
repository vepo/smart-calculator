package io.vepo.tutorial.smartcalculator;

import static java.lang.System.in;
import static java.lang.System.out;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "calc", mixinStandardHelpOptions = true, description = "A Calculator with some hookpoints")
public class SmartCalculator implements Callable<Integer> {

    public static void main(String[] args) {
        System.exit(new CommandLine(new SmartCalculator()).execute(args));
    }

    @Option(names = { "--hookpoint" }, description = "Hookpoint implementation")
    Path hookpointImplementation;

    @Override
    public Integer call() throws Exception {
        start(in);
        return CommandLine.ExitCode.OK;
    }

    private void start(InputStream input) {
        var parser = new CalculatorParser(new CalculatorHookpoints(hookpointImplementation));
        var scanner = new Scanner(input);
        Runtime.getRuntime()
                .addShutdownHook(new Thread() {
                    @Override
                    public void run() {
                        if (parser.isRunning()) {
                            out.println();
                            out.println("Exit by system call!!!");
                        }
                    }
                });
        out.println("Welcome to SmartCalculator! Type \\h for help, \\q to quit, or start entering your calculations.");
        do {
            out.print("> "); // Prompt
            parser.feed(scanner.nextLine());
        } while (parser.isRunning());

        out.println("Exit by command!");
        out.println("Goodbye!");
        scanner.close();
    }
}