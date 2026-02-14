/* ========================= */
/*        USER CODE          */
/* ========================= */

import java.io.*;
import java.util.HashMap;

%%

%public
%class Scanner
%unicode
%line
%column
%type String

%{

    TokenType tokenType = new TokenType();

    private String keywordCheck(String lexeme) {
        if(tokenType.token_map.containsKey(lexeme))
            return tokenType.token_map.get(lexeme);
        return "TOK_IDENTIFIER";
    }

%}

/* ========================= */
/*          MACROS           */
/* ========================= */

DIGIT           = [0-9]
UPPER           = [A-Z]
LOWER           = [a-z]
ID_CHAR         = [a-z0-9_]
SIGN            = [+-]

WHITESPACE      = [ \t\r\n]+

SINGLE_COMMENT  = "##"[^\n]*
MULTI_COMMENT   = "#*"([^*]|\*+[^*#])*\*+#

FLOAT_LITERAL   = {SIGN}?{DIGIT}+\.{DIGIT}{1,6}([eE]{SIGN}?{DIGIT}+)?
INT_LITERAL     = {SIGN}?{DIGIT}+

STRING_LITERAL  = \"([^\"\\\n]|\\[\"\\ntr])*\"
CHAR_LITERAL    = \'([^\'\\\n]|\\[\'\\ntr])\'

IDENTIFIER      = {UPPER}{ID_CHAR}{0,30}

MULTI_OP        = (\*\*|==|!=|<=|>=|&&|\|\||\+\+|--|\+=|-=|\*=|/=)

SINGLE_OP       = [+\-*/%<>=!]

PUNCTUATOR      = [(){}\[\],;:]

KEYWORD         = (start|finish|loop|condition|declare|output|input|function|return|break|continue|else)
BOOLEAN         = (true|false)

%%

/* ================================================= */
/*          PATTERN MATCHING PRIORITY ORDER         */
/* ================================================= */

/* 1. Multi-line comments */
{MULTI_COMMENT}        { /* Skip multi-line comment */ }

/* 2. Single-line comments */
{SINGLE_COMMENT}       { /* Skip single-line comment */ }

/* 3. Multi-character operators */
{MULTI_OP}             { return yytext(); }

/* 4. Keywords */
{KEYWORD}              { return tokenType.token_map.get(yytext()); }

/* 5. Boolean literals */
{BOOLEAN}              {
                            if(yytext().equals("true"))
                                return "TOK_TRUE";
                            else
                                return "TOK_FALSE";
                        }

/* 6. Identifiers */
{IDENTIFIER}           { return "TOK_IDENTIFIER"; }

/* 7. Floating-point literals */
{FLOAT_LITERAL}        { return "TOK_FLOAT"; }

/* 8. Integer literals */
{INT_LITERAL}          { return "TOK_INTEGER"; }

/* 9. String literals */
{STRING_LITERAL}       { return "TOK_STRING"; }

/* 9b. Character literals */
{CHAR_LITERAL}         { return "TOK_CHAR"; }

/* 10. Single-character operators */
"+"                    { return "TOK_PLUS"; }
"-"                    { return "TOK_MINUS"; }
"*"                    { return "TOK_STAR"; }
"/"                    { return "TOK_DIV"; }
"%"                    { return "TOK_MOD"; }
"<"                    { return "TOK_LT"; }
">"                    { return "TOK_GT"; }
"="                    { return "TOK_EQ"; }
"!"                    { return "TOK_NOT"; }

/* 11. Punctuators */
"("                    { return "TOK_LPAREN"; }
")"                    { return "TOK_RPAREN"; }
"{"                    { return "TOK_LCURLY"; }
"}"                    { return "TOK_RCURLY"; }
"["                    { return "TOK_LSQUAR"; }
"]"                    { return "TOK_RSQUAR"; }
","                    { return "TOK_COMMA"; }
";"                    { return "TOK_SEMICOLON"; }
":"                    { return "TOK_COLON"; }

/* 12. Whitespace */
{WHITESPACE}           { /* Skip but line number auto-tracked */ }

/* Error Handling */
.                      { return "NOT_A_TOKEN"; }
