grammar Majestik;


options { caseInsensitive = true; }
prog
    : expression_list EOF
    ;

TERMINATOR
    : (SEMICOLON | CRLF)+
    ;

expression_list
    : (expression TERMINATOR)+ expression?
    | expression TERMINATOR?
    ;

expression
    : assign
    | block
    | invoke
    ;

number
    : NUMBER
    ;

string
    : STRING
    ;

var
    : VAR
    ;

lhs
    : var
    ;

rhs
    : var
    | number
    | string
    ;

argument
    : var
    | number
    | string
    ;

arguments
    : argument
    | (argument COMMA)+ argument
    ;

block
    : BLOCK TERMINATOR? expression_list? ENDBLOCK
    ;

invoke
    : name = VAR LEFT_RBRACKET argss = arguments RIGHT_RBRACKET
    | name = VAR LEFT_RBRACKET RIGHT_RBRACKET
    ;

assign
   : lhs ASSIGN rhs
   ;

CRLF
   : '\r'? '\n' -> skip
   ;

WHITESPACE
   : (' ' | '\t') -> skip
   ;

LEFT_RBRACKET
   : '('
   ;

RIGHT_RBRACKET
   : ')'
   ;

COMMA
   : ','
   ;

SEMICOLON
   : ';'
   ;

VAR
   : [a-zA-Z] [a-zA-Z0-9_]*
   ;

DOUBLEQUOTE
   : '"'
   ;

SINGLEQUOTE
   : '\''
   ;

STRING
   : DOUBLEQUOTE (~ [\\"\r\n])*? DOUBLEQUOTE
   | SINGLEQUOTE (~ [\\'\r\n])*? SINGLEQUOTE
   ;

BLOCK
   : '_block'
   ;

ENDBLOCK
   : '_endblock'
   ;

ASSIGN
   : '<<'
   ;

NUMBER
   : [0-9]+
       | [0-9]* '.' [0-9]+
       ;

