grammar SmartCalculator;

start: expression;

expression
    :
    '(' innerExpression=expression ')'
        expression     (operator=(MOST_IMPORTANT_OPERATOR | LESS_IMPORTANT_OPERATOR)      expression    )+ | 
    '(' expression ')' (operator=(MOST_IMPORTANT_OPERATOR | LESS_IMPORTANT_OPERATOR)      expression    )+ | 
    '(' expression ')' (operator=(MOST_IMPORTANT_OPERATOR | LESS_IMPORTANT_OPERATOR) '('  expression ')')+ |  
        expression     (operator=(MOST_IMPORTANT_OPERATOR | LESS_IMPORTANT_OPERATOR) '('  expression ')')+ | 
    NUMBER             (operator=(MOST_IMPORTANT_OPERATOR | LESS_IMPORTANT_OPERATOR)      expression    )+ |

    NUMBER
    ;

command
    : '\\' COMMAND
    ;


MOST_IMPORTANT_OPERATOR: '*' | '/' | '^';
LESS_IMPORTANT_OPERATOR: '+' | '-';

NUMBER: INT '.' [0-9]+ EXP? | '-'? INT EXP | '-'? INT;
COMMAND: [A-Z]+;

fragment HEX: [0-9a-fA-F]+;

fragment INT: '0' | [1-9] [0-9]*; // no leading zeros

fragment EXP: [Ee] [+\-]? INT; // \- since - means "range" inside [...]

// Just ignore WhiteSpaces
NEWLINE:'\r'? '\n' ;     // return newlines to parser (is end-statement signal)
WS  :   [ \t]+ -> skip ; // toss out whitespace