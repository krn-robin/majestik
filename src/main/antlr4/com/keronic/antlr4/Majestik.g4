grammar Majestik;


options { caseInsensitive = true; }
prog
    : expression_list EOF
    ;

expression_list
   : (expression terminator)+ expression?
   | expression terminator?
    ;

expression
    : assign
    | block
    | invoke
   | boolean
   | if_expression
    ;

boolean
   : FALSE
   | TRUE
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
   | boolean
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
   : BLOCK CRLF* expression_list? ENDBLOCK
    ;

invoke
    : name = VAR LEFT_RBRACKET argss = arguments RIGHT_RBRACKET
    | name = VAR LEFT_RBRACKET RIGHT_RBRACKET
    ;

assign
   : lhs ASSIGN CRLF* rhs
   ;

if_expression
   : IF CRLF* expression CRLF* THEN CRLF* expression_list? ELSE CRLF* expression_list? ENDIF
   ;

terminator
   : (SEMICOLON | CRLF)+
   ;

WHITESPACE
   : (' ' | '\t') -> skip
   ;

CRLF
   : '\r'? '\n'
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
   : [A-Z] [A-Z0-9_]*
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

ASSIGN
   : '<<'
   ;

NUMBER
   : [0-9]+
   | [0-9]+ '.' [0-9]+
       ;

BLOCK
   : '_block'
   ;

ENDBLOCK
   : '_endblock'
   ;

ELIF
   : '_elif'
   ;

ELSE
   : '_else'
   ;

ENDIF
   : '_endif'
   ;

FALSE
   : '_false'
   ;

IF
   : '_if'
   ;

THEN
   : '_then'
   ;

TRUE
   : '_true'
   ;

