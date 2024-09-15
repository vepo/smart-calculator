let counter = 0;

function onResult(expression, result) {
    counter++;
    print("==== ON RESULT ====");
    print("Expression: " + expression);
    print("Result: " + result);
    print("Counter: " + counter);
    return true; // accept the result
}

function onExpression(expression, value) {
    counter++;
    print("==== ON Expression ====");
    print("Expression: " + expression);
    print("Previous value: " + value);
    print("Counter: " + counter);
    return true;
}

function onDivisionByZero(expression, previousValue) {
    counter++;
    print("==== DIVISION BY ZERO ====");
    print("Expression: " + expression);
    print("Previous Value: " + previousValue);
    print("Counter: " + counter);
    return false; // ignore the exception
}

function onCommand(command, memory) {
    counter++;
    print("==== COMMAND ====");
    print("Command: " + command);
    print("Counter: " + counter);
    if (command == 'ROLLBACK') {
        memory.rollback();
        return false; // ignore the command
    } else if (command == 'PRINT') {
        memory.print();
        return false; // ignore the command
    } else {
        return true; // execute the custom command
    } 
}