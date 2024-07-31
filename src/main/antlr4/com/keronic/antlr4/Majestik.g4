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

ASSIGN
   : '<<'
   ;

NUMBER
   : [0-9]+
       | [0-9]* '.' [0-9]+
       ;

ABSTRACT
   : '_abstract'
   ;

ALLRESULTS
   : '_allresults'
   ;

BLOCK
   : '_block'
   ;

CATCH
   : '_catch'
   ;

CF
   : '_cf'
   ;

CLASS
   : '_class'
   ;

CONSTANT
   : '_constant'
   ;

CONTINUE
   : '_continue'
   ;

DEFAULT
   : '_default'
   ;

DYNAMIC
   : '_dynamic'
   ;

ELIF
   : '_elif'
   ;

ELSE
   : '_else'
   ;

ENDBLOCK
   : '_endblock'
   ;

ENDCATCH
   : '_endcatch'
   ;

ENDIF
   : '_endif'
   ;

ENDLOCK
   : '_endlock'
   ;

ENDLOOP
   : '_endloop'
   ;

ENDMETHOD
   : '_endmethod'
   ;

ENDPROC
   : '_endproc'
   ;

ENDPROTECT
   : '_endprotect'
   ;

ENDTRY
   : '_endtry'
   ;

FINALLY
   : '_finally'
   ;

FOR
   : '_for'
   ;

GATHER
   : '_gather'
   ;

GLOBAL
   : '_global'
   ;

HANDLING
   : '_handling'
   ;

IF
   : '_if'
   ;

IMPORT
   : '_import'
   ;

ITER
   : '_iter'
   ;

LEAVE
   : '_leave'
   ;

LOCAL
   : '_local'
   ;

LOCK
   : '_lock'
   ;

LOCKING
   : '_locking'
   ;

LOOP
   : '_loop'
   ;

LOOPBODY
   : '_loopbody'
   ;

METHOD
   : '_method'
   ;

OPTIONAL
   : '_optional'
   ;

OVER
   : '_over'
   ;

PACKAGE
   : '_package'
   ;

PRIMITIVE
   : '_primitive'
   ;

PRIVATE
   : '_private'
   ;

PROC
   : '_proc'
   ;

PROTECT
   : '_protect'
   ;

PROTECTION
   : '_protection'
   ;

RETURN
   : '_return'
   ;

SCATTER
   : '_scatter'
   ;

THEN
   : '_then'
   ;

THISTHREAD
   : '_thisthread'
   ;

THROW
   : '_throw'
   ;

TRY
   : '_try'
   ;

WHEN
   : '_when'
   ;

WHILE
   : '_while'
   ;

WITH
   : '_with'
   ;
