grammar SmartCalculator;

expression
    : 
    NUMBER | 
    expression OPERATOR expression;

OPERATOR: '+' | '-' | '*' | '/';

NUMBER: '-'? INT '.' [0-9]+ EXP? | '-'? INT EXP | '-'? INT;

fragment HEX: [0-9a-fA-F];

fragment INT: '0' | [1-9] [0-9]*; // no leading zeros

fragment EXP: [Ee] [+\-]? INT; // \- since - means "range" inside [...]

// Just ignore WhiteSpaces
WS: [ \t\r\n]+ -> skip;