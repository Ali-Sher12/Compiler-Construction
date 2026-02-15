/* ========================================================================
   Scanner.flex - JFlex Specification for Compiler Scanner
   CS4031 - Compiler Construction Assignment 01
   ======================================================================== */

/* ========================================================================
   USER CODE SECTION
   ======================================================================== */
import java.io.*;

%%

/* ========================================================================
   OPTIONS AND DECLARATIONS
   ======================================================================== */
%class Scanner
%unicode
%line
%column
%type Token

%{
    /* Helper method to create tokens */
    private Token token(String type) {
        return new Token(type, yytext(), yyline + 1, yycolumn + 1);
    }
    
    private Token token(String type, Object value) {
        return new Token(type, yytext(), value, yyline + 1, yycolumn + 1);
    }
    
    /* Helper method to create error tokens */
    private Token error(String message) {
        return Token.error(message, yytext(), yyline + 1, yycolumn + 1);
    }

    /* Token class to represent lexical tokens */
    public static class Token {
        public String type;
        public String lexeme;
        public Object value;
        public int line;
        public int column;
        public boolean isError;
        public String errorMessage;

        public Token(String type, String lexeme, int line, int column) {
            this.type = type;
            this.lexeme = lexeme;
            this.value = lexeme;
            this.line = line;
            this.column = column;
            this.isError = false;
        }

        public Token(String type, String lexeme, Object value, int line, int column) {
            this.type = type;
            this.lexeme = lexeme;
            this.value = value;
            this.line = line;
            this.column = column;
            this.isError = false;
        }

        // Static factory method for error tokens
        public static Token error(String errorMessage, String lexeme, int line, int column) {
            Token token = new Token("ERROR", lexeme, line, column);
            token.isError = true;
            token.errorMessage = errorMessage;
            return token;
        }

        @Override
        public String toString() {
            if (isError) {
                return String.format("ERROR: %s [%s] | Line: %d | Column: %d",
                                   errorMessage, lexeme, line, column);
            }
            return String.format("<%s, '%s', Line: %d, Col: %d>",
                               type, lexeme, line, column);
        }
    }
%}

/* ========================================================================
   MACRO DEFINITIONS
   ======================================================================== */

/* Basic character classes */
DIGIT           = [0-9]
UPPERCASE       = [A-Z]
LOWERCASE       = [a-z]
LETTER          = [a-zA-Z]
UNDERSCORE      = _

/* Whitespace */
WHITESPACE      = [ \t\r\n]+
NEWLINE         = \r|\n|\r\n

/* Keywords (case-sensitive) */
KEYWORD         = start|finish|loop|condition|declare|output|input|function|return|break|continue|else

/* Identifiers: Start with uppercase, followed by uppercase/lowercase/digits/underscores (max 31 chars) */
IDENTIFIER      = {UPPERCASE}[a-zA-Z0-9_]{0,30}

/* Integer Literals */
INTEGER         = [+-]?{DIGIT}+

/* Floating-Point Literals */
FLOAT_BASIC     = {DIGIT}+\.{DIGIT}{1,6}
FLOAT_SIGNED    = [+-]?{FLOAT_BASIC}
EXPONENT        = [eE][+-]?{DIGIT}+
FLOAT           = {FLOAT_SIGNED}{EXPONENT}?

/* String Literals */
STRING_CHAR     = [^\"\\\n]|\\[\"\\ntr]
STRING          = \"{STRING_CHAR}*\"

/* Character Literals */
CHAR_CONTENT    = [^\'\\\n]|\\[\'\\ntr]
CHAR            = \'{CHAR_CONTENT}\'

/* Boolean Literals */
BOOLEAN         = true|false

/* Comments */
SINGLE_COMMENT  = ##[^\n]*
MULTI_COMMENT   = #\*([^*]|\*+[^*#])*\*+#

/* Note: Multi-character operators and punctuators are defined directly in rules section
   to avoid macro expansion issues */

/* ========================================================================
   LEXICAL RULES
   Priority: Comments > Multi-char ops > Keywords > Booleans > Identifiers
             > Floats > Integers > Strings/Chars > Single-char ops > Punctuators
   ======================================================================== */

%%

/* Priority 1: Multi-line comments (highest priority) */
{MULTI_COMMENT}         { /* Skip multi-line comments */ }

/* Priority 2: Single-line comments */
{SINGLE_COMMENT}        { /* Skip single-line comments */ }

/* Priority 3: Multi-character operators */
"**"                    { return token("TOK_EXPONENT"); }
"=="                    { return token("TOK_EQEQ"); }
"!="                    { return token("TOK_NE"); }
"<="                    { return token("TOK_LE"); }
">="                    { return token("TOK_GE"); }
"&&"                    { return token("TOK_LOGICALAND"); }
"||"                    { return token("TOK_LOGICALOR"); }
"++"                    { return token("TOK_INC"); }
"--"                    { return token("TOK_DEC"); }
"+="                    { return token("TOK_PLUSASSIGN"); }
"-="                    { return token("TOK_MINUSASSIGN"); }
"*="                    { return token("TOK_MULASSIGN"); }
"/="                    { return token("TOK_DIVASSIGN"); }

/* Priority 4: Keywords (must come before identifiers) */
"start"                 { return token("TOK_START"); }
"finish"                { return token("TOK_FINISH"); }
"loop"                  { return token("TOK_LOOP"); }
"condition"             { return token("TOK_CONDITION"); }
"declare"               { return token("TOK_DECLARE"); }
"output"                { return token("TOK_OUTPUT"); }
"input"                 { return token("TOK_INPUT"); }
"function"              { return token("TOK_FUNCTION"); }
"return"                { return token("TOK_RETURN"); }
"break"                 { return token("TOK_BREAK"); }
"continue"              { return token("TOK_CONTINUE"); }
"else"                  { return token("TOK_ELSE"); }

/* Priority 5: Boolean literals */
"true"                  { return token("TOK_TRUE"); }
"false"                 { return token("TOK_FALSE"); }

/* Priority 6: Identifiers (after keywords and booleans) */
{IDENTIFIER}            { return token("TOK_IDENTIFIER"); }

/* Priority 7: Floating-point literals (before integers to avoid partial match) */
{FLOAT}                 { return token("TOK_FLOAT", Double.parseDouble(yytext())); }

/* Priority 8: Integer literals */
{INTEGER}               { return token("TOK_INTEGER", Integer.parseInt(yytext())); }

/* Priority 9: String and Character literals */
{STRING}                { return token("TOK_STRING", yytext().substring(1, yytext().length()-1)); }
{CHAR}                  { return token("TOK_CHAR", yytext().charAt(1)); }

/* Priority 10: Single-character operators */
"+"                     { return token("TOK_PLUS"); }
"-"                     { return token("TOK_MINUS"); }
"*"                     { return token("TOK_STAR"); }
"/"                     { return token("TOK_DIV"); }
"%"                     { return token("TOK_MOD"); }
"!"                     { return token("TOK_NOT"); }
">"                     { return token("TOK_GT"); }
"<"                     { return token("TOK_LT"); }
"="                     { return token("TOK_EQ"); }
"^"                     { return token("TOK_LOGICALXOR"); }

/* Priority 11: Punctuators */
"("                     { return token("TOK_LPAREN"); }
")"                     { return token("TOK_RPAREN"); }
"{"                     { return token("TOK_LCURLY"); }
"}"                     { return token("TOK_RCURLY"); }
"["                     { return token("TOK_LSQUAR"); }
"]"                     { return token("TOK_RSQUAR"); }
","                     { return token("TOK_COMMA"); }
";"                     { return token("TOK_SEMICOLON"); }
":"                     { return token("TOK_COLON"); }
"."                     { return token("TOK_DOT"); }

/* Priority 12: Whitespace (skip but track line numbers) */
{WHITESPACE}            { /* Skip whitespace, line/column tracking is automatic */ }

/* Error handling: Any unmatched character - return error token and continue */
.                       { return error("Illegal character"); }