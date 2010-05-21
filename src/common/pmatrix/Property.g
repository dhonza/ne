grammar Property;


@header {
package common.pmatrix;

import common.pmatrix.ParameterMatrixBuilder;
import common.pmatrix.Parameter;
}

@lexer::header {
package common.pmatrix;
}

@members {
// NOT GENERATED
private ParameterMatrixBuilder builder = null;
private String name = null;

public void setBuilder(ParameterMatrixBuilder builder) {
	this.builder = builder;
}

public void setName(String name) {
	this.name = name;
}
// END NOT GENERATED
}

expr
	: object (','  object)* ;
object
	: number | range | bool | string;
range
	: '(' from=INT ';' to=INT ';' step=INT ')' {builder.addRange(name, Integer.parseInt($from.text), Integer.parseInt($to.text), Integer.parseInt($step.text));}
	| '(' from=DBL ';' to=DBL ';' step=DBL ')' {builder.addRange(name, Double.parseDouble($from.text), Double.parseDouble($to.text), Double.parseDouble($step.text));};		
number
	: INT {builder.add(new Parameter<Integer>(name, Integer.parseInt($INT.text)));}
	| DBL {builder.add(new Parameter<Double>(name, Double.parseDouble($DBL.text)));} ;
bool
	: BOOL 	{builder.add(new Parameter<Boolean>(name, Boolean.valueOf($BOOL.text)));};
string	
	: STRING {String s = $STRING.text; builder.add(new Parameter<String>(name, s.substring(1, s.length()-1)));};

// START:tokens
INT 	:   ('-'|'+')?'0'..'9'+ ;
DBL 	:   ('-'|'+')?'0'..'9'*'.''0'..'9'+(('e'|'E')('-'|'+')?'0'..'9'+)?;
STRING	:   '"'.*'"' ;
WS  	:   (' '|'\t')+ {skip();} ;BOOL	:   'true'|'TRUE'|'false'|'FALSE' ;

// END:tokens
