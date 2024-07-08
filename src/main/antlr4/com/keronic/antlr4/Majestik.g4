grammar Majestik;

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
    | string
    ;

argument
    : var
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
    : lhs ASSIGN rhs;

fragment A : [aA];
fragment B : [bB];
fragment C : [cC];
fragment D : [dD];
fragment E : [eE];
fragment F : [fF];
fragment G : [gG];
fragment H : [hH];
fragment I : [iI];
fragment J : [jJ];
fragment K : [kK];
fragment L : [lL];
fragment M : [mM];
fragment N : [nN];
fragment O : [oO];
fragment P : [pP];
fragment Q : [qQ];
fragment R : [rR];
fragment S : [sS];
fragment T : [tT];
fragment U : [uU];
fragment V : [vV];
fragment W : [wW];
fragment X : [xX];
fragment Y : [yY];
fragment Z : [zZ];

CRLF : '\r'? '\n'  -> skip;
WHITESPACE: (' ' | '\t') -> skip;

LEFT_RBRACKET : '(';
RIGHT_RBRACKET : ')';

COMMA : ',';

SEMICOLON: ';';

VAR : [a-zA-Z][a-zA-Z0-9_]*;

DOUBLEQUOTE: '"';
SINGLEQUOTE: '\'';

STRING : DOUBLEQUOTE (~[\\"\r\n])*? DOUBLEQUOTE
       | SINGLEQUOTE (~[\\'\r\n])*? SINGLEQUOTE;

BLOCK : '_' B L O C K;
ENDBLOCK : '_' E N D B L O C K;

ASSIGN : '<<';
