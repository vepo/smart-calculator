grammar SmartCalculator;

expression
    : 
    '(' expression ')' OPERATOR expression | 
    NUMBER OPERATOR expression | 
    NUMBER
    ;

command
    : '\\' COMMAND
    ;


OPERATOR: '+' | '-' | '*' | '/';

NUMBER: '-'? INT '.' [0-9]+ EXP? | '-'? INT EXP | '-'? INT;
COMMAND: [A-Z]+;

fragment HEX: [0-9a-fA-F]+;

fragment INT: '0' | [1-9] [0-9]*; // no leading zeros

fragment EXP: [Ee] [+\-]? INT; // \- since - means "range" inside [...]

// Just ignore WhiteSpaces
NEWLINE:'\r'? '\n' ;     // return newlines to parser (is end-statement signal)
WS  :   [ \t]+ -> skip ; // toss out whitespace