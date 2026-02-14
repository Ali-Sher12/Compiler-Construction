/* =====================================================================
   JFlex Scanner Specification for CS4031 Compiler Construction
   Assignment 01 - Lexical Analyzer
   ===================================================================== */

/* =====================================================================
   USER CODE SECTION
   ===================================================================== */
import java.util.HashMap;

%%

/* =====================================================================
   OPTIONS AND DECLARATIONS
   ===================================================================== */

%class Scanner
%unicode
%line
%column
%type Token

%{
    // Token type definitions matching manual scanner
    public static final String NOT_A_TOKEN = "NOT_A_TOKEN";
    public static final String TOK_LPAREN = "TOK_LPAREN";
    public static final String TOK_RPAREN = "TOK_RPAREN";
    public static final String TOK_LCURLY = "TOK_LCURLY";
    public static final String TOK_RCURLY = "TOK_RCURLY";
    public static final String TOK_LSQUAR = "TOK_LSQUAR";
    public static final String TOK_RSQUAR = "TOK_RSQUAR";
    public static final String TOK_COMMA = "TOK_COMMA";
    public static final String TOK_DOT = "TOK_DOT";
    public static final String TOK_PLUS = "TOK_PLUS";
    public static final String TOK_MINUS = "TOK_MINUS";
    public static final String TOK_STAR = "TOK_STAR";
    public static final String TOK_EXPONENT = "TOK_EXPONENT";
    public static final String TOK_DIV = "TOK_DIV";
    public static final String TOK_MOD = "TOK_MOD";
    public static final String TOK_COLON = "TOK_COLON";
    public static final String TOK_SEMICOLON = "TOK_SEMICOLON";
    public static final String TOK_QUESTION = "TOK_QUESTION";
    public static final String TOK_NOT = "TOK_NOT";
    public static final String TOK_GT = "TOK_GT";
    public static final String TOK_LT = "TOK_LT";
    public static final String TOK_EQ = "TOK_EQ";
    public static final String TOK_LOGICALAND = "TOK_LOGICALAND";
    public static final String TOK_LOGICALOR = "TOK_LOGICALOR";
    public static final String TOK_LOGICALXOR = "TOK_LOGICALXOR";
    public static final String TOK_GE = "TOK_GE";
    public static final String TOK_LE = "TOK_LE";
    public static final String TOK_NE = "TOK_NE";
    public static final String TOK_EQEQ = "TOK_EQEQ";
    public static final String TOK_ASSIGN = "TOK_ASSIGN";  // Used for ++, --, +=, -=, *=, /=
    public static final String TOK_IDENTIFIER = "TOK_IDENTIFIER";
    public static final String TOK_STRING = "TOK_STRING";
    public static final String TOK_CHAR = "TOK_CHAR";
    public static final String TOK_INTEGER = "TOK_INTEGER";
    public static final String TOK_FLOAT = "TOK_FLOAT";
    public static final String TOK_CONDITION = "TOK_CONDITION";
    public static final String TOK_START = "TOK_START";
    public static final String TOK_FINISH = "TOK_FINISH";
    public static final String TOK_TRUE = "TOK_TRUE";
    public static final String TOK_FALSE = "TOK_FALSE";
    public static final String TOK_LOOP = "TOK_LOOP";
    public static final String TOK_DECLARE = "TOK_DECLARE";
    public static final String TOK_OUTPUT = "TOK_OUTPUT";
    public static final String TOK_INPUT = "TOK_INPUT";
    public static final String TOK_FUNCTION = "TOK_FUNCTION";
    public static final String TOK_RETURN = "TOK_RETURN";
    public static final String TOK_BREAK = "TOK_BREAK";
    public static final String TOK_CONTINUE = "TOK_CONTINUE";
    public static final String TOK_ELSE = "TOK_ELSE";

    // Helper class to represent tokens
    public static class Token {
        public String type;
        public String lexeme;
        public int line;
        public int column;

        public Token(String type, String lexeme, int line, int column) {
            this.type = type;
            this.lexeme = lexeme;
            this.line = line;
            this.column = column;
        }

        @Override
        public String toString() {
            return String.format("Token type: %s | Lexeme: %s | Line: %d | Column: %d",
                               type, lexeme, line, column);
        }
    }

    // Helper method to create tokens
    private Token createToken(String type) {
        return new Token(type, yytext(), yyline + 1, yycolumn + 1);
    }

    // Error handling
    private void error(String message) {
        System.err.println("Lexical error at line " + (yyline + 1) +
                          ", column " + (yycolumn + 1) + ": " + message);
    }
%}

/* =====================================================================
   MACRO DEFINITIONS
   ===================================================================== */

// Basic character classes
DIGIT = [0-9]
LETTER = [a-zA-Z]
WHITESPACE = [ \t\r\n]

// Identifiers - Must start with uppercase, max 31 chars total
// NO SPACES - spaces are delimiters
IDENTIFIER = [A-Z][A-Za-z0-9_]{0,30}

// Integer literals
INTEGER = [+-]?{DIGIT}+

// Floating-point literals
FLOAT = [+-]?{DIGIT}+\.{DIGIT}{1,6}([eE][+-]?{DIGIT}+)?

// Comments
SINGLE_LINE_COMMENT = ##[^\n]*
MULTI_LINE_COMMENT = "#*"([^*]|"*"+[^*#])*"*"+"#"

%%

/* =====================================================================
   LEXICAL RULES
   ===================================================================== */

/* Priority 1: Multi-line comments */
{MULTI_LINE_COMMENT}        { /* Skip */ }

/* Priority 2: Single-line comments */
{SINGLE_LINE_COMMENT}       { /* Skip */ }

/* Priority 3: Multi-character operators */
"**"                        { return createToken(TOK_EXPONENT); }
"=="                        { return createToken(TOK_EQEQ); }
"!="                        { return createToken(TOK_NE); }
"<="                        { return createToken(TOK_LE); }
">="                        { return createToken(TOK_GE); }
"&&"                        { return createToken(TOK_LOGICALAND); }
"||"                        { return createToken(TOK_LOGICALOR); }
"++"                        { return createToken(TOK_ASSIGN); }
"--"                        { return createToken(TOK_ASSIGN); }
"+="                        { return createToken(TOK_ASSIGN); }
"-="                        { return createToken(TOK_ASSIGN); }
"*="                        { return createToken(TOK_ASSIGN); }
"/="                        { return createToken(TOK_ASSIGN); }

/* Priority 4: Keywords */
"start"                     { return createToken(TOK_START); }
"finish"                    { return createToken(TOK_FINISH); }
"loop"                      { return createToken(TOK_LOOP); }
"condition"                 { return createToken(TOK_CONDITION); }
"declare"                   { return createToken(TOK_DECLARE); }
"output"                    { return createToken(TOK_OUTPUT); }
"input"                     { return createToken(TOK_INPUT); }
"function"                  { return createToken(TOK_FUNCTION); }
"return"                    { return createToken(TOK_RETURN); }
"break"                     { return createToken(TOK_BREAK); }
"continue"                  { return createToken(TOK_CONTINUE); }
"else"                      { return createToken(TOK_ELSE); }

/* Priority 5: Boolean literals */
"true"                      { return createToken(TOK_TRUE); }
"false"                     { return createToken(TOK_FALSE); }

/* Priority 6: Identifiers */
{IDENTIFIER}                { return createToken(TOK_IDENTIFIER); }

/* Priority 7: Floating-point literals */
{FLOAT}                     { return createToken(TOK_FLOAT); }

/* Priority 8: Integer literals */
{INTEGER}                   { return createToken(TOK_INTEGER); }

/* Priority 9: String literals - match directly in rules */
\"([^\"\\\n]|\\[\"\\\ntr])*\"   { return createToken(TOK_STRING); }

/* Priority 9: Character literals - match directly in rules */
'([^'\\\n]|\\['\\\ntr])'    { return createToken(TOK_CHAR); }

/* Priority 10: Single-character operators */
"+"                         { return createToken(TOK_PLUS); }
"-"                         { return createToken(TOK_MINUS); }
"*"                         { return createToken(TOK_STAR); }
"/"                         { return createToken(TOK_DIV); }
"%"                         { return createToken(TOK_MOD); }
"<"                         { return createToken(TOK_LT); }
">"                         { return createToken(TOK_GT); }
"="                         { return createToken(TOK_EQ); }
"!"                         { return createToken(TOK_NOT); }
"&"                         { return createToken(TOK_LOGICALAND); }
"|"                         { return createToken(TOK_LOGICALOR); }
"^"                         { return createToken(TOK_LOGICALXOR); }

/* Priority 11: Punctuators */
"("                         { return createToken(TOK_LPAREN); }
")"                         { return createToken(TOK_RPAREN); }
"{"                         { return createToken(TOK_LCURLY); }
"}"                         { return createToken(TOK_RCURLY); }
"["                         { return createToken(TOK_LSQUAR); }
"]"                         { return createToken(TOK_RSQUAR); }
","                         { return createToken(TOK_COMMA); }
";"                         { return createToken(TOK_SEMICOLON); }
":"                         { return createToken(TOK_COLON); }

/* Priority 12: Whitespace */
{WHITESPACE}+               { /* Skip */ }

/* Error handling */
.                           {
                              error("Unrecognized character: '" + yytext() + "'");
                              return createToken(NOT_A_TOKEN);
                            }