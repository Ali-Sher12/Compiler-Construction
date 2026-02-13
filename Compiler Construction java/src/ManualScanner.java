import javax.sound.sampled.Line;
import java.io.*;
import java.util.*;
import java.nio.file.*;

public class ManualScanner
{
    TokenType Tokens_Dict;
    int line = 1;
    int start = 0;
    int curr = 0;
    int column = 1;
    ArrayList<Token> Tokens_List;
    String source;
    boolean error_found = false;

    ManualScanner()
    {
        Tokens_List = new ArrayList<Token>();
        Tokens_Dict = new TokenType();
        try
        {
            Path path = Path.of("/home/jarvis2026/Documents/Repos/compiler construction/Compiler Construction java/src/input.txt");
            source = Files.readString(path);
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    boolean Identifier_Eligibility(){
        if(is_lowercase(source.charAt(start))){
            System.out.println("SyntaxError Identifier must begin with uppercase letter. "+source.substring(start,curr));
            error_found = true;
            return false;
        }
        if((curr-start) > 30)
        {
            System.out.println("SyntaxError Identifier length too long. "+source.substring(start,curr));
            error_found = true;
            return false;
        }
        return true;
    }

    String isKEYWORD(String lexeme) {
        if(Tokens_Dict.token_map.containsKey(lexeme.toLowerCase()))
            return Tokens_Dict.token_map.get(lexeme.toLowerCase());
        if(Identifier_Eligibility())
            return Tokens_Dict.TOK_IDENTIFIER;

        System.out.println("SyntaxError Substring neither keyword nor identifier. "+source.substring(start,curr));
        return Tokens_Dict.NOT_A_TOKEN;
    }

    boolean is_digit(char num){
        return num >= '0' && num <= '9';
    }

    char advance(){
        if(curr >= source.length())
            return '\0';
        column+=1;
        char ch = source.charAt(curr);
        curr += 1;
        return ch;
    }

    void add_token(String input_type){
        // add validations using input_type
        if(Objects.equals(input_type, Tokens_Dict.TOK_IDENTIFIER)){
            Identifier_Eligibility();
        }
        else if(Objects.equals(input_type, Tokens_Dict.NOT_A_TOKEN)) return;
        Token tempToken = new Token(input_type, source.substring(start, curr),line,column-(curr-start));
        Tokens_List.add(tempToken);
    }

    char peek(){
        if(curr >= source.length())
            return '\0';
        return source.charAt(curr);
    }

    char lookahead(){
        if(curr+1 >= source.length())
            return '\0';
        return source.charAt(curr+1);
    }

    boolean is_uppercase(char ch){
        return ('A' <= ch && ch <= 'Z');
    }

    boolean is_lowercase(char ch){
        return ('a' <= ch && ch <= 'z');
    }

    boolean is_alphabet(char ch){
        return is_uppercase(ch) || is_lowercase(ch);
    }

    void tokenise(){
        while (curr < source.length()) {
            start = curr;
            char ch = advance();
            if(ch =='\0') return;
            if(ch =='\n'){
                line = line + 1;
                column = 1;
            }
            else if (ch == '\t') {
                column+=3;
                continue;
            }
            else if (ch == '\r') continue;
            else if (ch ==' ') continue;
            else if (ch == '#'){
                if( peek() == '#' ){
                    while (peek() != '\n'){
                        if (peek() == '\0')
                            return;
                        advance();
                    }
                }
                else if( peek() == '*' ){
                    advance();
                    while (!(peek() == '*' && lookahead() == '#')){
                        if (peek() == '\0') {
                            System.out.println("SyntaxError Multi-line comments not properly closed (* missing). Line "+line);
                            return;
                        }
                        advance();
                    }
                    advance(); advance();
                }
                else System.out.println("SyntaxError Single-line comments not properly closed. Line "+line);
            }
            else if (ch =='(') add_token(Tokens_Dict.TOK_LPAREN);
            else if (ch ==')') add_token(Tokens_Dict.TOK_RPAREN);
            else if (ch =='{') add_token(Tokens_Dict.TOK_LCURLY);
            else if (ch =='}') add_token(Tokens_Dict.TOK_RCURLY);
            else if (ch =='[') add_token(Tokens_Dict.TOK_LSQUAR);
            else if (ch ==']') add_token(Tokens_Dict.TOK_RSQUAR);
            else if (ch ==',') add_token(Tokens_Dict.TOK_COMMA);
            else if (ch ==';') add_token(Tokens_Dict.TOK_SEMICOLON);
            else if (ch =='?') add_token(Tokens_Dict.TOK_QUESTION);
            else if (ch =='%') add_token(Tokens_Dict.TOK_MOD);
            else if (ch ==':') add_token(Tokens_Dict.TOK_COLON);
            else if (ch =='.') add_token(Tokens_Dict.TOK_DOT);
            else if (ch =='^') add_token(Tokens_Dict.TOK_LOGICALXOR);
            else if (ch =='=') {
                if (peek() == '=') {
                    advance();
                    add_token(Tokens_Dict.TOK_EQEQ);
                }
                else add_token(Tokens_Dict.TOK_EQ);
            }
            else if (ch =='&') {
                if (peek() == '&') {
                    advance();
                    add_token(Tokens_Dict.TOK_LOGICALAND);
                }
                else add_token(Tokens_Dict.TOK_AND);
            }
            else if (ch =='|') {
                if (peek() == '|') {
                    advance();
                    add_token(Tokens_Dict.TOK_LOGICALOR);
                }
                else add_token(Tokens_Dict.TOK_OR);
            }
            else if (ch =='*') {
                if (peek() == '=') {
                    advance();
                    add_token(Tokens_Dict.TOK_MULASSIGN);
                }
                else if (peek() == '*') {
                    advance();
                    add_token(Tokens_Dict.TOK_EXPONENT);
                }
                else add_token(Tokens_Dict.TOK_STAR);
            }

            else if (ch =='/') {
                if (peek() == '=') {
                    advance();
                    add_token(Tokens_Dict.TOK_DIVASSIGN);
                }
                else add_token(Tokens_Dict.TOK_DIV);
            }
            else if (ch =='-') {
                if (peek() == '=') {
                    advance();
                    add_token(Tokens_Dict.TOK_MINUSASSIGN);
                }
                else if (peek() == '-') {
                    advance();
                    add_token(Tokens_Dict.TOK_DEC);
                }
                else if (is_digit(peek())) {
                    while (is_digit(peek()))
                        advance();
                    if (peek() == '.' && is_digit (lookahead())) {
                        advance(); //consumes the '.'
                        while (is_digit(peek())) {
                            advance();
                        }
                        if(!exponent_condition_helper()){
                            add_token(Tokens_Dict.TOK_FLOAT);
                        }
                    }
                    else if (peek() == '.' && !is_digit (lookahead())) {
                        error_found = true;
                        System.out.println("SyntaxError Nothing after decimal point. "+line);
                    }
                    else if(!exponent_condition_helper())
                    {
                        add_token(Tokens_Dict.TOK_INTEGER);
                    }
                    else add_token(Tokens_Dict.TOK_MINUS);
                }
            }

            else if (ch =='+') {
                if (peek() == '=') {
                    advance();
                    add_token(Tokens_Dict.TOK_PLUSASSIGN);
                }
                else if (peek() == '+') {
                    advance();
                    add_token(Tokens_Dict.TOK_INC);
                }
                else if (is_digit(peek())) {
                    while (is_digit(peek()))
                        advance();
                    if (peek() == '.' && is_digit (lookahead())) {
                        advance(); //consumes the '.'
                        while (is_digit(peek())) {
                            advance();
                        }
                        if(!exponent_condition_helper()){
                            add_token(Tokens_Dict.TOK_FLOAT);
                        }
                    }
                    else if (peek() == '.' && !is_digit (lookahead())) {
                        System.out.println("SyntaxError Nothing after decimal point. "+line);
                        error_found = true;
                    }
                    else if(!exponent_condition_helper())
                    {
                        add_token(Tokens_Dict.TOK_INTEGER);
                    }
                }
                else add_token(Tokens_Dict.TOK_PLUS);
            }
            else if (ch =='!') {
                if (peek() == '=') {
                    advance();
                    add_token(Tokens_Dict.TOK_NE);
                }
                else add_token(Tokens_Dict.TOK_NOT);
            }

            else if (ch =='<'){
                if (peek() == '=') {
                    advance();
                    add_token(Tokens_Dict.TOK_LE);
                }
                else add_token(Tokens_Dict.TOK_LT);
            }

            else if(ch =='>') {
                if (peek() == '=') {
                    advance();
                    add_token(Tokens_Dict.TOK_GE);
                }
                else add_token(Tokens_Dict.TOK_GT);
            }

            else if (ch =='\'') {
                advance();
                if (peek() != '\''){
                    System.out.println("Syntax error Invalid char at line  "+ line);
                    error_found = true;                }
                else{
                    advance();
                    add_token(Tokens_Dict.TOK_CHAR);
                }
            }

            else if (ch =='\"') {
                while (peek() != '\"') {
                    if (peek() == '\0')
                        break;
                    advance();
                }
                if (peek() == '\"') {
                    advance();
                    add_token(Tokens_Dict.TOK_STRING);
                }
                else{
                    System.out.println("Syntax error  Unclosed string at line  "+ line);
                    error_found = true;                }
            }

            else if (is_digit(ch)) {
                while (is_digit(peek()))
                    advance();
                if (peek() == '.' && is_digit (lookahead())) {
                    advance(); //consumes the '.'
                    while (is_digit(peek())) {
                        advance();
                    }
                    if(!exponent_condition_helper()){
                        add_token(Tokens_Dict.TOK_FLOAT);
                    }
                }
                else if (peek() == '.' && !is_digit (lookahead())) {
                    System.out.println("SyntaxError Nothing after decimal point. "+line);
                    error_found = true;
                }
                else if(!exponent_condition_helper()) {
                    add_token(Tokens_Dict.TOK_INTEGER);
                }
            }
            else if (is_alphabet (ch)) {
                while (is_alphabet(peek()) || peek() == '_' || is_digit(peek())) {
                    if (peek() == '\0')
                        break;
                    advance ();
                }
                String token_lexeme = source.substring(start, curr);
                add_token(isKEYWORD(token_lexeme));
            }
        }
    }

    private boolean exponent_condition_helper()
    {
        if (peek() == 'e' || peek() == 'E'){
            advance();
            if (peek() == '+' || peek() == '-') {
                advance();
                if (!is_digit(peek())) {
                    System.out.println("SyntaxError Exponent is invalid. "+line);
                    error_found = true;
                }
                while (is_digit(peek()))
                    advance();
                if(!error_found) add_token(Tokens_Dict.TOK_FLOAT);
            }
            else if (is_digit (peek())){
                while (is_digit(peek()))
                    advance();
                add_token(Tokens_Dict.TOK_FLOAT);                        }

            else{
                System.out.println("SyntaxError Exponent is invalid. "+line);
                error_found = true;
            }
            return true;
        }
        return false;
    }

    void printList()
    {
        for (int i=0;i<Tokens_List.size();i++)
        {
            Tokens_List.get(i).print();
        }
    }

}
