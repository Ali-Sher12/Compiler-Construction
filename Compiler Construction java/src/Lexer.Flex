/* ============================= */
/* Lexer.flex for CS4031 Assignment */
/* Compatible with your TokenType.java */
/* ============================= */

/* User code section */
%class Lexer
%unicode
%public
%cup
%line
%column

%{

import java_cup.runtime.Symbol;
import java.io.IOException;

import java.util.HashMap;

%}

/* ============================= */
/* Macro Definitions */
/* ============================= */
DIGIT       = [0-9]
LETTER      = [A-Za-z]
ID_START    = [A-Z]
ID_BODY     = [a-z0-9_]{0,30}
INT         = [+-]?{DIGIT}+
FLOAT       = [+-]?{DIGIT}+\.{DIGIT}{1,6}([eE][+-]?{DIGIT}+)?
STRING      = \"([^\"\\\n]|\\["\\ntr])*\"
CHAR        = \'([^\'\\\n]|\\[\'\\ntr])\'
WHITESPACE  = [ \t\r\n]+
SINGLE_COMMENT = \#\#[^\n]*
MULTI_COMMENT  = \#\*([^*]|\*+[^*#])*\*+\#
MULTI_OP    = \*\*|==|!=|<=|>=|&&|\|\||\+\+|--|\+=|-=|\*=|/=
SINGLE_OP   = \+|-|\*|/|%|=|!|<|>|\&|\||\^
PUNCTUATOR  = [\(\)\{\}\[\],;:.?]

/* ============================= */
/* Rules Section */
/* ============================= */
%%

/* ---- Whitespace ---- */
{WHITESPACE}       { /* skip but track lines */ }

/* ---- Comments ---- */
{MULTI_COMMENT}    { /* skip multi-line comments */ }
{SINGLE_COMMENT}   { /* skip single-line comments */ }

/* ---- Multi-character Operators ---- */
{MULTI_OP}         {
                        String text = yytext();
                        switch(text) {
                            case "**": return new Token("TOK_EXPONENT", text, yyline+1, yycolumn+1);
                            case "==": return new Token("TOK_EQEQ", text, yyline+1, yycolumn+1);
                            case "!=": return new Token("TOK_NE", text, yyline+1, yycolumn+1);
                            case "<=": return new Token("TOK_LE", text, yyline+1, yycolumn+1);
                            case ">=": return new Token("TOK_GE", text, yyline+1, yycolumn+1);
                            case "&&": return new Token("TOK_AND", text, yyline+1, yycolumn+1);
                            case "||": return new Token("TOK_OR", text, yyline+1, yycolumn+1);
                            case "++": return new Token("TOK_INC", text, yyline+1, yycolumn+1);
                            case "--": return new Token("TOK_DEC", text, yyline+1, yycolumn+1);
                            case "+=": return new Token("TOK_PLUSASSIGN", text, yyline+1, yycolumn+1);
                            case "-=": return new Token("TOK_MINUSASSIGN", text, yyline+1, yycolumn+1);
                            case "*=": return new Token("TOK_MULASSIGN", text, yyline+1, yycolumn+1);
                            case "/=": return new Token("TOK_DIVASSIGN", text, yyline+1, yycolumn+1);
                            default: return new Token("NOT_A_TOKEN", text, yyline+1, yycolumn+1);
                        }
                   }

/* ---- Keywords ---- */
"start"        { return new Token("TOK_START", yytext(), yyline+1, yycolumn+1); }
"finish"       { return new Token("TOK_FINISH", yytext(), yyline+1, yycolumn+1); }
"loop"         { return new Token("TOK_LOOP", yytext(), yyline+1, yycolumn+1); }
"condition"    { return new Token("TOK_CONDITION", yytext(), yyline+1, yycolumn+1); }
"declare"      { return new Token("TOK_DECLARE", yytext(), yyline+1, yycolumn+1); }
"output"       { return new Token("TOK_OUTPUT", yytext(), yyline+1, yycolumn+1); }
"input"        { return new Token("TOK_INPUT", yytext(), yyline+1, yycolumn+1); }
"function"     { return new Token("TOK_FUNCTION", yytext(), yyline+1, yycolumn+1); }
"return"       { return new Token("TOK_RETURN", yytext(), yyline+1, yycolumn+1); }
"break"        { return new Token("TOK_BREAK", yytext(), yyline+1, yycolumn+1); }
"continue"     { return new Token("TOK_CONTINUE", yytext(), yyline+1, yycolumn+1); }
"true"         { return new Token("TOK_TRUE", yytext(), yyline+1, yycolumn+1); }
"false"        { return new Token("TOK_FALSE", yytext(), yyline+1, yycolumn+1); }

/* ---- Identifiers ---- */
{ID_START}{ID_BODY}?  { return new Token("TOK_IDENTIFIER", yytext(), yyline+1, yycolumn+1); }

/* ---- Literals ---- */
{FLOAT}        { return new Token("TOK_FLOAT", yytext(), yyline+1, yycolumn+1); }
{INT}          { return new Token("TOK_INTEGER", yytext(), yyline+1, yycolumn+1); }
{STRING}       { return new Token("TOK_STRING", yytext(), yyline+1, yycolumn+1); }
{CHAR}         { return new Token("TOK_CHAR", yytext(), yyline+1, yycolumn+1); }

/* ---- Single-character Operators ---- */
"("  { return new Token("TOK_LPAREN", yytext(), yyline+1, yycolumn+1); }
")"  { return new Token("TOK_RPAREN", yytext(), yyline+1, yycolumn+1); }
"{"  { return new Token("TOK_LCURLY", yytext(), yyline+1, yycolumn+1); }
"}"  { return new Token("TOK_RCURLY", yytext(), yyline+1, yycolumn+1); }
"["  { return new Token("TOK_LSQUAR", yytext(), yyline+1, yycolumn+1); }
"]"  { return new Token("TOK_RSQUAR", yytext(), yyline+1, yycolumn+1); }
","  { return new Token("TOK_COMMA", yytext(), yyline+1, yycolumn+1); }
"."  { return new Token("TOK_DOT", yytext(), yyline+1, yycolumn+1); }
"+"  { return new Token("TOK_PLUS", yytext(), yyline+1, yycolumn+1); }
"-"  { return new Token("TOK_MINUS", yytext(), yyline+1, yycolumn+1); }
"*"  { return new Token("TOK_STAR", yytext(), yyline+1, yycolumn+1); }
"/"  { return new Token("TOK_DIV", yytext(), yyline+1, yycolumn+1); }
"%"  { return new Token("TOK_MOD", yytext(), yyline+1, yycolumn+1); }
":"  { return new Token("TOK_COLON", yytext(), yyline+1, yycolumn+1); }
";"  { return new Token("TOK_SEMICOLON", yytext(), yyline+1, yycolumn+1); }
"?"  { return new Token("TOK_QUESTION", yytext(), yyline+1, yycolumn+1); }
"!"  { return new Token("TOK_NOT", yytext(), yyline+1, yycolumn+1); }
"<"  { return new Token("TOK_LT", yytext(), yyline+1, yycolumn+1); }
">"  { return new Token("TOK_GT", yytext(), yyline+1, yycolumn+1); }
"="  { return new Token("TOK_EQ", yytext(), yyline+1, yycolumn+1); }
"&"  { return new Token("TOK_LOGICALAND", yytext(), yyline+1, yycolumn+1); }
"|"  { return new Token("TOK_LOGICALOR", yytext(), yyline+1, yycolumn+1); }
"^"  { return new Token("TOK_LOGICALXOR", yytext(), yyline+1, yycolumn+1); }

/* ---- Error Handling ---- */
. { return new Token("NOT_A_TOKEN", yytext(), yyline+1, yycolumn+1); }

