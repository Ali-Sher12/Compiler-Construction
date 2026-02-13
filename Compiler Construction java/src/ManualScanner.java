import java.io.*;
import java.util.*;
import java.nio.file.*;

public class ManualScanner
{
    TokenType Tokens_Dict;
    int line = 1;
    int start = 0;
    int curr = 0;
    ArrayList<Token> Tokens_List;
    String source;

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

    String isKEYWORD(String lexeme) {
        if(Tokens_Dict.token_map.containsKey(lexeme))
            return Tokens_Dict.token_map.get(lexeme);
        return Tokens_Dict.TOK_IDENTIFIER;
    }

    boolean is_digit(char num){
        return num >= '0' && num <= '9';
    }

    char advance(){
        if(curr >= source.length())
            return '\0';
        char ch = source.charAt(curr);
        curr += 1;
        return ch;
    }

    boolean match(char expected){
        if (curr >= source.length())
            return false;
        if (source.charAt(curr) != expected)
            return false;
        curr += 1;
        return true;
    }

    void add_token(String input_type){
        Token tempToken = new Token(input_type, source.substring(start, curr),line,start);
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
    boolean is_alphanum(char ch){
        return (('a' <= ch && ch <= 'z') ||
                ('A' <= ch && ch <= 'Z') ||
                ('0' <= ch && ch <= '9'));
    }

    void tokenise(){
        while (curr < source.length()) {
            start = curr;
            char ch = advance();
            if (ch == '+') add_token(Tokens_Dict.TOK_PLUS);
            else if(ch =='-') add_token(Tokens_Dict.TOK_MINUS);
            else if(ch =='\n')
                line = line + 1;
            else if (ch =='*') add_token(Tokens_Dict.TOK_STAR);
            else if (ch ==' ') continue;
            else if (ch == '\t') continue;
            else if (ch == '\r') continue;
            else if (ch == '#'){
                while (peek() != '\n'){
                    if (peek() == '\0')
                        return;
                    advance();
                }
                advance();
            }
            else if (ch =='(') add_token(Tokens_Dict.TOK_LPAREN);
            else if (ch ==')') add_token(Tokens_Dict.TOK_RPAREN);
            else if (ch =='{') add_token(Tokens_Dict.TOK_LCURLY);
            else if (ch =='}') add_token(Tokens_Dict.TOK_RCURLY);
            else if (ch =='[') add_token(Tokens_Dict.TOK_LSQUAR);
            else if (ch ==']') add_token(Tokens_Dict.TOK_RSQUAR);
            else if (ch ==',') add_token(Tokens_Dict.TOK_COMMA);
            else if (ch =='^') add_token(Tokens_Dict.TOK_CARET);
            else if (ch =='/') add_token(Tokens_Dict.TOK_SLASH);
            else if (ch ==';') add_token(Tokens_Dict.TOK_SEMICOLON);
            else if (ch =='?') add_token(Tokens_Dict.TOK_QUESTION);
            else if (ch =='%') add_token(Tokens_Dict.TOK_MOD);
            else if (ch =='=') {
                if (peek() == '=')
                    advance();
                add_token(Tokens_Dict.TOK_EQEQ);
            }
            else if (ch =='~') {
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
            else if (ch ==':' ) {
                if (peek() == '=') {
                    advance();
                    add_token(Tokens_Dict.TOK_ASSIGN);
                }
                else add_token(Tokens_Dict.TOK_COLON);
            }
            else if (ch =='.') {
                if (!is_digit(peek()))
                    add_token(Tokens_Dict.TOK_DOT);
                else{
                    while (true){
                        if(is_digit(peek()))
                            advance();
                        else {
                            add_token(Tokens_Dict.TOK_FLOAT);
                            break;
                        }
                    }
                }
            }

            else if (is_digit(ch)) {
                while (is_digit(peek()))
                    advance();
                if (peek() == '.' && is_digit (lookahead())) {
                    advance(); //consumes the '.'
                    while (is_digit(peek())) {
                        advance();
                    }
                    add_token(Tokens_Dict.TOK_FLOAT);
                }
                else
                    add_token(Tokens_Dict.TOK_INTEGER);
            }

            else if (ch =='\'') {
                while (peek() != '\'') {
                    if (peek() == '\0')
                        break;
                    advance();
                }
                if (peek() == '\'') {
                    advance();
                    add_token(Tokens_Dict.TOK_STRING);
                }
                else
                    System.out.println("Syntax error  Unclosed string at line  "+ line);
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
                else
                    System.out.println("Syntax error  Unclosed string at line  "+ line);
            }

            else if (is_alphanum (ch)) {
                while (is_alphanum(peek()) || peek() == '_') {
                    if (peek() == '\0')
                        break;
                    advance ();
                }
                String token_lexeme = source.substring(start, curr);
                add_token(isKEYWORD(token_lexeme));
            }
        }
        return Tokens_List;
    }

    void printList()
    {
        for (int i=0;i<Tokens_List.size();i++)
        {
            Tokens_List.get(i).print();
        }

    }

}
