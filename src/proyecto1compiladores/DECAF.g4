
grammar DECAF ;



// ********* LEXER ******************

//Palabras reservadas
CLASS: 'class' ;
STRUCT: 'struct' ;
TRUE : 'true' ;
FALSE: 'false' ;
VOID: 'void' ;
IF: 'if' ;
ELSE: 'else' ;
WHILE : 'while' ;
RETURN: 'return' ;
INT: 'int' ;
CHARS : 'char' ;
BOOLEAN : 'boolean' ;
AND: '&&';
OR: '||';


fragment LETTER: ('a'..'z'|'A'..'Z') ;
fragment DIGIT : '0'..'9' ;
ID : LETTER( LETTER | DIGIT)* ;
NUM: DIGIT(DIGIT)* ;
COMMENTS: '//' ~('\r' | '\n' )*  -> channel(HIDDEN);
WS : [ \t\r\n\f]+  ->channel(HIDDEN);

CHAR: '\''(LETTER|DIGIT|' '| '!' | '"' | '#' | '$' | '%' | '&' | '\'' | '(' | ')' | '*' | '+' 

| ',' | '-' | '.' | '/' | ':' | ';' | '<' | '=' | '>' | '?' | '@' | '[' | '\\' | ']' | '^' | '_' | '`'| '{' | '|' | '}' | '~' 
'\t'| '\\n' | '\"' | '\'')'\'';


// ********** PARSER *****************


//REGLAS

program : CLASS ID '{' (declaration)* '}'  ;

declaration: structDeclaration| varDeclaration | methodDeclaration  ;

varDeclaration: varType ID ';'  #simpleType
				|  varType ID '[' NUM ']' ';' #arrayType ; 

structDeclaration : STRUCT ID '{' (varDeclaration)* '}'  ;


varType: INT #intType
		| CHARS #charType
		| BOOLEAN  #booleanType
		| VOID #voidType
		| STRUCT ID #structType
		| structDeclaration #structDeclType ;

methodDeclaration : methodType ID '(' (parameter2)? ')' block  ;

parameter2: (parameter) (',' parameter)* ;

methodType : INT | CHARS | BOOLEAN | VOID ;

parameter : parameterType ID | parameterType ID '[' ']' ;

parameterType: INT | CHARS | BOOLEAN | ID  ;

block : '{' (statement)* '}' ;

statement : 
		    ifStatement
           | whileStatement			   
           | returnStatement ';'						   
           | methodCall ';' 
           | assignStatement ';'							   
           | varDeclaration
           | expressionA ';'  
           | block;
ifStatement:  IF '(' expression ')' block #simpleIf|IF '(' expression ')' block ( ELSE  block ) #ifElse ;
	
			
			
whileStatement: WHILE '(' expression ')' block	 ;     
returnStatement: RETURN expressionA  ;
assignStatement: location '=' expression  ; 
         
expressionA: expression | ;

expression : andExpr| expression OR andExpr  ;

andExpr: eqExpr | andExpr AND eqExpr ;

eqExpr: relationExpr | eqExpr eq_op relationExpr ;

relationExpr: addExpr | relationExpr rel_op addExpr ;

addExpr: multExpr | addExpr add_op multExpr ;

multExpr: unaryExpr | multExpr mult_op unaryExpr ;

unaryExpr:  '('(INT|CHARS)')'  value| '-' value | '!' value | value   ;

location: simpleLocation| structLocation ;

structLocation: simpleVariable ('.' location) #variableStruct
			| arrayVariable ('.' location) #structArray;
	
simpleLocation: simpleVariable | arrayVariable ;

simpleVariable: 	ID;			

arrayVariable: ID '[' expression ']' ;

value: location | methodCall |literal | '('expression')' ;

methodCall :	ID '(' (arg2)? ')' ;
	
arg2 	:	(arg) (',' arg)* ;

arg	:	expression;

mult_op :  '*' | '/' | '%' ;

add_op : '+'|'-' ;

rel_op : '<' | '>' | '<=' | '>=' ;

eq_op : '==' | '!=' ;

cond_op : AND | OR ;

literal : int_literal | char_literal | bool_literal ;

int_literal : NUM ;

char_literal : CHAR  ;

bool_literal : TRUE | FALSE  ;

