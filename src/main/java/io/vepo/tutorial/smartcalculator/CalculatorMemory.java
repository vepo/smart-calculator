package io.vepo.tutorial.smartcalculator;

import static java.lang.System.out;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class CalculatorMemory {

    private Deque<Double> internalMemory;

    public CalculatorMemory() {
        this.internalMemory = new LinkedList<>();
    }

    public void put(double value) {
        this.internalMemory.offer(value);
    }

    public double current() {
        return internalMemory.isEmpty() ? 0.0 : internalMemory.peekLast();
    }

    public void rollback() {
        internalMemory.pop();
    }

    public void print() {
        List<Double> m = internalMemory.stream().toList();
        for (int i = 0; i < internalMemory.size(); ++i) {
            out.printf("Memory[%d] = %f%n", i, m.get(i));
        }
    }

}
